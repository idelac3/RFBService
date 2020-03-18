package com.scoreunit.rfb.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scoreunit.rfb.encoding.Encodings;
import com.scoreunit.rfb.screen.ScreenClip;

/**
 * RFB service is Java implementation of VNC server.
 * <p>
 * <b>RFB</b> stands for <i>remote frame buffer</i> protocol.
 * <p>
 * This is simple implementations, mainly to demonstrate implementation of protocol.
 * It is not efficient as other implementations, nor does it compete with them.
 *  
 * @author igor.delac@gmail.com
 *
 */
public class RFBService implements Runnable {

	public final static Logger log = LoggerFactory.getLogger(RFBService.class);
	
	public final static int DEFAULT_PORT = 5900;

	private int port;
	
	private ServerSocket socket = null;
	
	private boolean running;
	
	private final List<ClientHandler> clientHandlers;
	
	private RFBConfig rfbConfig;
	
	/**
	 * Create default instance, with TCP port set to {@link #DEFAULT_PORT} value.
	 */
	public RFBService() {
		
		this(DEFAULT_PORT);
	}
	
	/**
	 * Create new instance with given TCP port value.
	 * 
	 * @param port	-	TCP port on which to listen
	 */
	public RFBService(final int port) {
	
		this.port = port;
		
		this.running = false;
		
		this.clientHandlers = new ArrayList<>();
		
		this.rfbConfig = new RFBConfig();
	}

	/**
	 * Set listening TCP port. Set value before {@link #start()} method is invoked.
	 * 
	 * @param port		-	TCP port to bind to
	 */
	public void setPort(final int port) {
		
		this.port = port;
	}

	/**
	 * TCP port value. 
	 * <p>
	 * Note that this information is 
	 * not useful to determine if RFB service has connected
	 * to VNC client, or RFB service has bind and waits for VNC
	 * client connection.
	 * 
	 * @return	TCP port value
	 */
	public int getPort() {
		
		return this.port;
	}
	
	/**
	 * If password is set, then VNC auth. is enabled. Client will be asked
	 * to provide secret password.
	 *  
	 * @param pwd	-	secret password that VNC client must provide to authenticate
	 */
	public void setPassword(final String pwd) {
		
		this.rfbConfig.setPassword(pwd);
	}
	
	/**
	 * Configure a list of encodings which are preferred, instead
	 * of list provided by VNC client.
	 * <p>
	 * If set to null value, then client encoding list is considered.
	 *  
	 * @param encodings		-	eg. {@link Encodings#ZLIB}, {@link Encodings#HEXTILE}, etc.
	 */
	public void setPreferredEncodings(final int[] encodings) {
	
		this.rfbConfig.setPreferredEncodings(encodings);
	}
	
	/**
	 * Check if it's running.
	 * 
	 * @return	true if RFB service is running
	 */
	public boolean isRunning() {
		
		return this.running;
	}

	/**
	 * Start service. This method will create new thread.
	 */
	public void start() {
		
		final Thread thread = new Thread(this, this.toString());
		thread.start();		
	}

	/**
	 * Terminate existing RFB service thread.
	 */
	public void terminate() {
	
		try {
		
			this.running = false;
			
			this.socket.close(); // Do not accept new connections.
			
			for (final ClientHandler handler : this.clientHandlers) {
				
				handler.terminate(); // terminate currently open sessions.
			}
			
			this.clientHandlers.clear();
		} catch (final IOException exception) {

			log.error("Unable to terminate RFB service socket.", exception);
		}
	}
	
	/**
	 * Get list of client handlers. Note that 
	 * this list might contain clients that were disconnected as well.
	 * 
	 * @return	list of {@link ClientHandler} instances
	 */
	public List<ClientHandler> getClientHandlers() {
		
		return new ArrayList<>(this.clientHandlers);
	}
	
	/**
	 * Set {@link ScreenClip}. Screen clip object defines which area of screen should be 
	 * presented to VNC client.
	 * <p>
	 * This is useful if only primary screen should be shared, in multi-monitor setups, etc.
	 * 
	 * @param	clip	-	new {@link ScreenClip} object
	 */
	public void setScreenClip(final ScreenClip clip) {
		
		this.rfbConfig.setScreenClip(clip);
	}

	/**
	 * Instead of binding to TCP port, and waiting for VNC client to connect,
	 * it is possible to establish TCP connection to VNC client.
	 * 
	 * @param hostname	-	VNC client IP address, or host name
	 * @param port		-	TCP port value on which VNC client is listening
	 * 
	 * @throws IOException		if VNC client is unreachable  
	 * @throws UnknownHostException 
	 */
	public void connect(final String hostname, final int port) throws UnknownHostException, IOException {
		
		final Socket socket = new Socket(hostname, port);
		final ClientHandler clientHandler = new ClientHandler(socket, this.rfbConfig);
		
		final Thread clientThread = new Thread(clientHandler
				, String.format("%s-[%s:%d]", ClientHandler.class.getSimpleName(), hostname, port));
		clientThread.start();
	}
	
	/**
	 * Set {@link ScreenClip}. Screen clip object defines which area of screen should be 
	 * presented to VNC client.
	 * <p>
	 * This is useful if only primary screen should be shared, in multi-monitor setups, etc.
	 * <p>
	 * <b>NOTE</b>
	 * <p>
	 * Ensure that <i>(xPos, yPos)</i> offset value does not cross real screen dimension.
	 * Also ensure that <i>(width, height)</i> with given offset does not fall off screen.
	 * 
	 * @param	xPos		-	x offset, top-left corner is at (0, 0), in pixel
	 * @param	yPos		-	y offset, top-left corner is at (0, 0), in pixel
	 * @param	width		-	width of screen image region, in pixel
	 * @param	height		-	height of screen image region, in pixel
	 */
	public void setScreenClip(final int xPos, final int yPos,
			final int width, final int height) {
		
		this.rfbConfig.setScreenClip(new ScreenClip(xPos, yPos, width, height));
	}

	public void run() {
		
		//
		// Prepare server socket, bind to TCP port (eg. 5900).
		//
		
		if (this.socket == null) {
			
			try {
			
				this.socket = new ServerSocket(this.port);
				
				log.info(
						String.format("RFB service (VNC server) started at TCP port '%d'.",
								this.socket.getLocalPort())
						);
			} catch (final IOException exception) {

				log.error(
						String.format("Unable to open TCP port '%d'. RFB service terminated."
								, this.port)
						, exception
						);
				
				return;
			}
		}
		
		this.running = true;
		
		//
		// Start accepting client connections.
		//
		
		while (this.running == true) {
			
			try {
			
				//
				// Handle each client connection in separate thread, and 
				// store each client handler object into list. Later it 
				// will be used to terminate all client connections.
				//
				
				final Socket clientSocket = this.socket.accept();
				
				final ClientHandler handler = new ClientHandler(clientSocket, this.rfbConfig);
				
				final Thread clientThread = new Thread(handler, handler.toString());
				clientThread.start();
				
				this.clientHandlers.add(handler);
			} catch (final IOException exception) {
				
				if (this.running == true) {
					
					log.error("Problem occured while waiting for client connection.", exception);
				}
			}
		}
		
		this.running = false;
	}
	
	@Override
	public String toString() {
		
		return String.format("%s-[:%d]", RFBService.class.getSimpleName(), this.port);
	}
}
