package com.scoreunit.rfb.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 'The RFB Protocol' documentation, page 14,
 * by Tristan Richardson, RealVNC Ltd.
 * <p>
 * Version 3.8, Last updated 26 November 2010
 *
 * @author igor.delac@gmail.com
 *
 */
public class VNCAuth {
	
	/**
	 * A VNC password, for DES encrypted challenge response from VNC client.
	 */
	final private String password;
	
	/**
	 * Challenge data sent to VNC client.
	 */
	private byte[] challengeData;
	
	/**
	 * DES encrypted challenge response from VNC client.
	 */
	private byte[] challengeResponse;
	
	/**
	 * Create new VNC auth.
	 */
	public VNCAuth(final String password) {
		
		this.password = password;
	}
	
	/**
	 * Write challenge message.
	 * 
	 * @param out				-	instance of {@link OutputStream} where to write, typically obtained from {@link Socket#getOutputStream()} method
	 * 
	 * @throws IOException	if connection is broken
	 */
	public void sendChallenge(final OutputStream out) throws IOException {
				
		final byte[] randomChallenge =  new byte[16];
		
		long value = System.currentTimeMillis();
		
		for (int i = 0 ; i < 16 ; i++) {
		
			randomChallenge[i] = (byte) (value % 256);
			value = value / 10;
			
			if (value <= 0) {
				
				value = Long.MAX_VALUE - 1;
			}
		}
		
		out.write(randomChallenge);
		out.flush();
		
		this.challengeData = randomChallenge;
	}
	
	/**
	 * Read client challenge response value.
	 * 
	 * @param in			-	instance of {@link InputStream} used to read, typically obtained from {@link Socket#getInputStream()} method
	 * 
	 * @throws IOException	if version string provided by client does not look right, see {@link #ver} how value should look
	 */
	public void readChallenge(final InputStream in) throws IOException {
				
		final byte[] challengeResponse = new byte[16];
		
		in.read(challengeResponse);

		this.challengeResponse = challengeResponse;
	}
	
	/**
	 * Verify VNC auth.
	 * 
	 * @return	true if challenge data is encrypted with right password
	 */
	public boolean isValid() {
		
		byte[] expected = DESCipher.enc(password, challengeData);
		
		// Note that DES encryption might result in longer byte[] array. 
		// We compare only 16 bytes as per VNC auth standard.
		for (int i = 0 ; i < 16 ; i++) {
			
			if (this.challengeResponse[i] != expected[i]) {
				
				return false;
			}
		}
		
		return true;
	}
}
