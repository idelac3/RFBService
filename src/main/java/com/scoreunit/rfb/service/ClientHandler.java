package com.scoreunit.rfb.service;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is socket handler that will handle client connection.
 * <p>
 * Instance of this class is created for each client, for each new session.
 * <p>
 * Goal is to perform initial handshake, authorization (ask client to provide password / secret) if any, etc.
 * 
 * @author igor.delac@gmail.com
 *
 */
class ClientHandler implements Runnable {
	
	public final static Logger log = LoggerFactory.getLogger(ClientHandler.class);
	
	private final Socket socket;
	
	private boolean running;
	
	private String password;
	
	public ClientHandler(final Socket socket) {
		
		this.socket = socket;
		
		this.running = false;
	}
	
	/**
	 * Set new password, for VNC auth. method.
	 * If null, then authentication is disabled (default).
	 * 
	 * @param secret	-	new password to challenge VNC client with
	 */
	public void setPassword(final String secret) {
		
		this.password = secret;
	}
	
	public boolean isRunning() {
		
		return this.running;
	}
	
	public void terminate() {
	
		if (this.socket == null) {
			
			try {
			
				this.running = false;
				
				this.socket.close();
			} catch (final IOException exception) {

				log.error("Client handler termination failure.", exception);
			}
		}
	}
	
	@Override
	public void run() {

		//
		// Some fixed values after handshake.
		//
		
		final ProtocolVersion ver;
		final SecurityTypes sec;
		final ClientInit clientInit;
		
		//
		// Variable values which VNC client might send a request to change.
		//
		
		SetPixelFormat setPixelFormat;
		SetEncodings setEncodings;
		
		//
		// Check & prepare TCP socket object.
		//
		
		if (this.socket == null) {
		
			return;
		}
				
		this.running = true;
		
		FramebufferUpdater frameBufferUpdater = null;
		
		try {
			
			final InputStream in   = this.socket.getInputStream();
			final OutputStream out = new BufferedOutputStream(this.socket.getOutputStream());
		
			// This is updater for frame buffer, running it its own thread.
			// Updater will receive frame buffer update requests from this thread,
			//  and it will write response message back to socket.
			frameBufferUpdater = new FramebufferUpdater(this.toString(), out);	
			frameBufferUpdater.start();
			
			//
			// RFB protocol starts by sending version string
			//  and waiting for VNC client to reply with its version string.
			//
			
			ProtocolVersion.sendProtocolVersion(out);
			ver = ProtocolVersion.readProtocolVersion(in);			
			log.info("RFB protocol: " + ver);
			
			//
			// Send supported security types.
			//
			
			final byte[] securityTypes;
			
			if (this.password == null) {
				
				securityTypes = new byte[] {SecurityTypes.NONE};
			}
			else {
				
				securityTypes = new byte[] {SecurityTypes.VNC_AUTH};
			}
			
			SecurityTypes.send(out, securityTypes);
			sec = SecurityTypes.read(in);
			
			if (sec.securityType == SecurityTypes.VNC_AUTH) {
				
				// Send challenge data if VNC auth. is used.
				final VNCAuth vncAuth = new VNCAuth(this.password);
				vncAuth.sendChallenge(out);
				vncAuth.readChallenge(in);
				
				if (vncAuth.isValid() == false) {
					
					// Wrong password received from VNC client!
					SecurityTypes.sendSecurityResult(out, "Wrong password.");
					
					this.running = false;
					
					this.socket.close();
					
					return;
				}
			}
						
			// SecurityResult message should be sent to VNC client.
			// 'The RFB Protocol' documentation, page 10.
			SecurityTypes.sendSecurityResult(out, null);
			
			//
			// Wait for a ClientInit message.
			//
			
			clientInit = ClientInit.readClientInit(in);
			
			if (clientInit.sharedDesktop == false) {
			
				log.info("Client requested exclusive access to desktop. We won't kick other VNC clients for now.");
			}
			
			//
			// ServerInit prepare and send.
			//
			
			ServerInit.send(out);
			
			//
			// Run in loop, wait for some requests from client. 
			//

			while (this.running == true) {
				
				if (frameBufferUpdater.isRunning() == false) {
					
					break; // Stop this client handler, if updater is not running anymore.
				}
				
				//
				// 'The RFB Protocol' documentation, page 19,
				// by Tristan Richardson, RealVNC Ltd.
				//
				// Version 3.8, Last updated 26 November 2010
				//
				
				final int SET_PIXEL_FORMAT = 0
						, SET_ENCODINGS = 2
						, FRAMEBUFFER_UPDATE_REQUEST = 3
						, KEY_EVENT = 4
						, POINTER_EVENT = 5
						, CLIENT_CUT_TEXT = 6;
				
				//
				// Read VNC client messages and handle them.
				//
				
				int msgType = in.read();
				
				if (msgType == SET_PIXEL_FORMAT) {
					
					in.read(new byte[3]); // padding.
					setPixelFormat = SetPixelFormat.read(in);
				}
				else if (msgType == SET_ENCODINGS) {
					
					in.read(); // padding.					
					setEncodings = SetEncodings.read(in);
				}
				else if (msgType == FRAMEBUFFER_UPDATE_REQUEST) {
					
					final FramebufferUpdateRequest request = 
							FramebufferUpdateRequest.read(in);

					frameBufferUpdater.update(request);
				}
			}			
			
			in.close();
			out.close();
		} catch (final IOException | InterruptedException exception) {

			if (this.running == true) {
			
				log.error("Client connection closed.");
			}
		}
		
		this.running = false;
		
		if (frameBufferUpdater != null) {
		
			frameBufferUpdater.terminate();
		}
	}

	@Override
	public String toString() {
		
		if (this.socket == null) {
			
			return ClientHandler.class.getSimpleName();
		}
		
		return String.format("%s-[%s]", ClientHandler.class.getSimpleName(), this.socket.getRemoteSocketAddress());
	}
}
