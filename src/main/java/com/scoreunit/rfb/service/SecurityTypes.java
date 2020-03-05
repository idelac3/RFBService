package com.scoreunit.rfb.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 'The RFB Protocol' documentation, page 9,
 * by Tristan Richardson, RealVNC Ltd.
 * <p>
 * Version 3.8, Last updated 26 November 2010
 *
 * @author igor.delac@gmail.com
 *
 */
public class SecurityTypes {

	public final static int NONE = 1
			, VNC_AUTH = 2;
	
	/**
	 * Values:
	 * <ul>
	 *  <li> 1 - none</li>
	 *  <li> 2 - VNC auth. security type</li>
	 * </ul> 
	 */
	public final int securityType;
	
	/**
	 * Create new security type object.
	 * 
	 * @param type	-	selected by VNC client security type value
	 */
	public SecurityTypes(int type) {
		
		this.securityType = type;
	}
	
	/**
	 * Write supported security types.
	 * 
	 * @param out				-	instance of {@link OutputStream} where to write, typically obtained from {@link Socket#getOutputStream()} method
	 * 
	 * @param securityTypes		-	array of security types, eg. 1 - None, 2 - VNC auth. etc
	 * @throws IOException	if connection is broken
	 */
	public static void send(final OutputStream out, final byte[] securityTypes) throws IOException {
		
		final int numberOfSecurityTypes = securityTypes.length;
		
		out.write(numberOfSecurityTypes);
		out.write(securityTypes);
		out.flush();
	}
	
	/**
	 * Read client protocol version string value.
	 * 
	 * @param in			-	instance of {@link InputStream} used to read, typically obtained from {@link Socket#getInputStream()} method
	 * @return	instance of {@link SecurityTypes} object with values stored in {@link major} and {@link #minor} members
	 * 
	 * @throws IOException	if version string probided by client does not look right, see {@link #ver} how value should look
	 */
	public static SecurityTypes read(final InputStream in) throws IOException {
				
		int securityType = in.read();

		return new SecurityTypes(securityType);
	}
	

	/**
	 * Write security result.
	 * 
	 * @param out				-	instance of {@link OutputStream} where to write, typically obtained from {@link Socket#getOutputStream()} method
	 * 
	 * @param failureReason		-	optional, if set, then write SecurityResult message with failed status and description, 
	 * 								if set to null, then write SecurityResult message with OK status
	 * 
	 * @throws IOException	if connection is broken
	 */
	public static void sendSecurityResult(final OutputStream out, final String failureReason) throws IOException {
		
		byte[] ok = {0, 0, 0, 0};
		byte[] failure = {0, 0, 0, 1}; // Little-endian or Big-endian byte order ?
		
		if (failureReason == null) {
			
			out.write(ok);
		}
		else {
			
			final DataOutputStream dOut = new DataOutputStream(out);
			
			dOut.write(failure);
			dOut.writeInt(failureReason.length()); // Big-endian (network) byte order if DataOutputStream is used.
			dOut.write(failureReason.getBytes());
		}
		
		out.flush();
	}
	
}
