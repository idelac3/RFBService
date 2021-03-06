package com.scoreunit.rfb.service;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import com.scoreunit.rfb.encoding.Encodings;
import com.scoreunit.rfb.screen.ScreenClip;
import com.scoreunit.rfb.ssl.SSLUtil;

/**
 * Configuration class, to carry some information
 * from {@link RFBService} to {@link ClientHandler}
 * and to {@link FramebufferUpdater} instances.
 * 
 * @author igor.delac@gmail.com
 *
 */
class RFBConfig {

	/**
	 * Used for VNC auth.
	 */
	private String password;
		
	/**
	 * If set, this object will define which area of screen should be 
	 * presented to VNC client.
	 * <p>
	 * This is useful if only primary screen should be shared, in multi-monitor setups, etc. 
	 */
	private ScreenClip screenClip;
	
	/**
	 * A list of preferred encodings by RFB service.
	 * If set, client encoding list will be ignored in favor of this list.
	 */
	private int[] preferredEncodings;
	
	/**
	 * SSL server socket factory instance is used to create {@link SSLServerSocket}.
	 * Instead of using plain {@link ServerSocket} and accept VNC clients,
	 * it is possible to add SSL layer and encrypt packets between RFB service and VNC client.
	 * <p>
	 * See {@link SSLUtil#newInstance(String, java.io.InputStream, String)} method how to create
	 * instance of {@link SSLServerSocketFactory}.
	 */
	private SSLServerSocketFactory sslServerSocketFactory;
	
	/**
	 * If set, a password that VNC client must provide for authentication.
	 * 
	 * @return	secret password
	 */
	public String getPassword() {
		
		return password;
	}
	
	/**
	 * If set, a password that VNC client must provide for authentication.
	 * 
	 * @param password	-	secret password
	 */
	public void setPassword(String password) {
		
		this.password = password;
	}
	
	/**
	 * If set, this object will define which area of screen should be 
	 * presented to VNC client.
	 * <p>
	 * This is useful if only primary screen should be shared, in multi-monitor setups, etc.
	 * 
	 * @return	instance of {@link ScreenClip} object
	 */
	public ScreenClip getScreenClip() {
		
		return screenClip;
	}
	
	/**
	 * If set, this object will define which area of screen should be 
	 * presented to VNC client.
	 * <p>
	 * This is useful if only primary screen should be shared, in multi-monitor setups, etc.
	 * 
	 * @param screenClip	-	instance of {@link ScreenClip} object, or null value
	 */
	public void setScreenClip(ScreenClip screenClip) {
		
		this.screenClip = screenClip;
	}
	
	/**
	 * A list of preferred encodings. See {@link Encodings} class for
	 * possible encodings.
	 * 
	 * @return	preferred encodings
	 */
	public int[] getPreferredEncodings() {
		
		return preferredEncodings;
	}

	/**
	 * A list of preferred encodings. See {@link Encodings} class for
	 * possible encodings.
	 * 
	 * @param preferredEncodings	-	preferred encodings, or null value to used VNC client list of encodings 
	 */
	public void setPreferredEncodings(int[] preferredEncodings) {
		
		this.preferredEncodings = preferredEncodings;
	}

	/**
	 * Enable SSL secure layer, by providing instance of {@link SSLServerSocketFactory}.
	 * <p>
	 * Use helper method {@link SSLUtil#newInstance(String, java.io.InputStream, String)} to create {@link SSLServerSocketFactory}.
	 * 
	 * @param	sslServerSocketFactory		-	{@link SSLServerSocketFactory} instance, or null value if SSL communication should be turned off
	 */
	public void setSSLServerSocketFactory(final SSLServerSocketFactory sslServerSocketFactory) {
		
		this.sslServerSocketFactory = sslServerSocketFactory;
	}
	
	/**
	 * Check if this {@link RFBConfig} contains an instance of {@link SSLServerSocketFactory}.
	 * <p>
	 * If it contains, then secure communication using SSL layer.
	 * 
	 * @return null value if SSL is not desired, or {@link SSLServerSocketFactory} instance
	 */
	public SSLServerSocketFactory getSSLServerSocketFactory() {
		
		return this.sslServerSocketFactory;
	}
}
