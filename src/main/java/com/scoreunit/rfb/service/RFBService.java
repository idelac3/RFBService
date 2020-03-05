package com.scoreunit.rfb.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RFBService implements Runnable {

	public final static Logger log = LoggerFactory.getLogger(RFBService.class);
	
	public final static int DEFAULT_PORT = 5900;

	private int port;
	
	private ServerSocket socket = null;
	
	private boolean running;
	
	private final List<ClientHandler> clientHandlers;
	
	/**
	 * Used for VNC auth.
	 */
	private String secret;
	
	public RFBService() {
		
		this(DEFAULT_PORT);
	}
	
	public RFBService(final int port) {
	
		this.port = port;
		
		this.running = false;
		
		this.clientHandlers = new ArrayList<>();
	}

	public void setPassword(final String pwd) {
		
		this.secret = pwd;
	}
	
	public boolean isRunning() {
		
		return this.running;
	}

	public void start() {
		
		final Thread thread = new Thread(this, this.toString());
		thread.start();		
	}

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
	
	public List<ClientHandler> getClientHandlers() {
		
		return new ArrayList<>(this.clientHandlers);
	}
	
	public void run() {
		
		//
		// Prepare server socket, bind to TCP port (eg. 5900).
		//
		
		if (this.socket == null) {
			
			try {
			
				this.socket = new ServerSocket(this.port);
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
				
				final ClientHandler handler = new ClientHandler(clientSocket);
				handler.setPassword(secret);
				
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
