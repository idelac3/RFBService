package com.scoreunit.rfb.service;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scoreunit.rfb.keyboard.KeyboardController;
import com.scoreunit.rfb.mouse.MouseController;
import com.scoreunit.rfb.screen.ScreenCapture;

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
	
	private final RFBConfig config;
	
	public ClientHandler(final Socket socket, final RFBConfig config) {
		
		this.socket = socket;
		
		this.running = false;
		
		this.config = config;
	}
	
	/**
	 * Check if client thread is running.
	 * 
	 * @return	true if client thread is running
	 */
	public boolean isRunning() {
		
		return this.running;
	}
	
	/**
	 * Terminate connection with VNC client.
	 */
	public void terminate() {
	
		if (this.socket != null) {
			
			try {
			
				this.running = false;
				
				this.socket.close();
			} catch (final IOException exception) {

				log.error("Client handler termination failure.", exception);
			}
		}
	}

	/**
	 * Fetch screen (clip) width in pixel.
	 * 
	 * @return	width of screen, or region of screen that is presented to VNC client
	 */
	private short getWidth() {
	
		if (this.config.getScreenClip() != null) {
			
			return this.config.getScreenClip().width;
		}
		
		return (short) ScreenCapture.getScreenWidth();
	}
	
	/**
	 * Fetch screen (clip) height in pixel.
	 * 
	 * @return	height of screen, or region of screen that is presented to VNC client
	 */
	private short getHeight() {
	
		if (this.config.getScreenClip() != null) {
			
			return this.config.getScreenClip().height;
		}
		
		return (short) ScreenCapture.getScreenHeight();
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
			frameBufferUpdater = new FramebufferUpdater(this, out);
			frameBufferUpdater.setScreenClip(this.config.getScreenClip()); // Forward information about screen region, if set.
			frameBufferUpdater.setPreferredEncodings(this.config.getPreferredEncodings()); // If set, favor encodings of RFB service, instead of VNC client encoding list.
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
			
			if (this.config.getPassword() == null) {
				
				securityTypes = new byte[] {SecurityTypes.NONE};
			}
			else {
				
				securityTypes = new byte[] {SecurityTypes.VNC_AUTH};
			}
			
			SecurityTypes.send(out, securityTypes);
			sec = SecurityTypes.read(in);
			
			if (sec.securityType == SecurityTypes.VNC_AUTH) {
				
				// Send challenge data if VNC auth. is used.
				final VNCAuth vncAuth = new VNCAuth(this.config.getPassword());
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
			
			ServerInit.send(out, this.getWidth(), this.getHeight());
			
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
					
					frameBufferUpdater.setPixelFormat(setPixelFormat);
				}
				else if (msgType == SET_ENCODINGS) {
					
					in.read(); // padding.					
					setEncodings = SetEncodings.read(in);
					frameBufferUpdater.setClientEncodings(setEncodings);
				}
				else if (msgType == FRAMEBUFFER_UPDATE_REQUEST) {
					
					final FramebufferUpdateRequest request = 
							FramebufferUpdateRequest.read(in);

					frameBufferUpdater.update(request);
				}
				else if (msgType == KEY_EVENT) {
				
					final KeyEvent keyEvent = KeyEvent.read(in);
					KeyboardController.sendKey(keyEvent.key, keyEvent.downFlag);
				}
				else if (msgType == POINTER_EVENT) {
					
					final PointerEvent pointerEvent = PointerEvent.read(in);
					
					/*
					 * Button mask:
					 * 1 - left button
					 * 2 - middle button
					 * 4 - right button
					 * 8 - wheel up
					 * 16 - wheel down
					 */
					int buttonMask = pointerEvent.buttonMask;
					
					int x = pointerEvent.xPos;
					int y = pointerEvent.yPos;
					
					switch (buttonMask) {
					case 1: MouseController.mouseClick(x, y); break;
					case 2: MouseController.mouseMiddleClick(x, y); break;
					case 4: MouseController.mouseRightClick(x, y); break;
					case 8: MouseController.mouseWheel(100); break;
					case 16: MouseController.mouseWheel(-100); break;
					}
				}
				else if (msgType == CLIENT_CUT_TEXT) {
					
					final ClientCutText event = ClientCutText.read(in);
					
					try {
					
						final Clipboard clipboard = 
								Toolkit.getDefaultToolkit().getSystemClipboard();
						final StringSelection selection = new StringSelection(event.text);
						clipboard.setContents(selection, selection);
					}
					catch (final Exception ex) {
						
						log.error("Unable to copy to clipboard text.", ex);
					}
				}
			}			
			
			in.close();
			out.close();
		} catch (final IOException | InterruptedException exception) {

			if (this.running == true) {
			
				log.info(
						String.format("Client connection '%s' closed.", this.socket.getRemoteSocketAddress())
						);
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
