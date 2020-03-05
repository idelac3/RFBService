package com.scoreunit.rfb.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class ProtocolVersion {

	/**
	 * 'The RFB Protocol' documentation, page 8,
	 * by Tristan Richardson, RealVNC Ltd.
	 * <p>
	 * Version 3.8, Last updated 26 November 2010
	 */
	public static final String ver = "RFB 003.008\n";
	
	/**
	 * Should be 3.3, 3.7 or 3.8.
	 */
	public int major, minor;
	
	/**
	 * Create new protocol version object.
	 * 
	 * @param major			-	major version, always value 3
	 * @param minor			-	minor version, should be 8 by most modern VNC clients
	 */
	public ProtocolVersion(int major, int minor) {
		
		this.major = major;
		this.minor = minor;
	}
	
	/**
	 * Write {@link #ver} protocol version.
	 * 
	 * @param out				-	instance of {@link OutputStream} where to write, typically obtained from {@link Socket#getOutputStream()} method
	 * 
	 * @throws IOException	if connection is broken
	 */
	public static void sendProtocolVersion(final OutputStream out) throws IOException {
		
		out.write(ver.getBytes());
		out.flush();
	}
	
	/**
	 * Read client protocol version string value.
	 * 
	 * @param in			-	instance of {@link InputStream} used to read, typically obtained from {@link Socket#getInputStream()} method
	 * @return	instance of {@link ProtocolVersion} object with values stored in {@link major} and {@link #minor} members
	 * 
	 * @throws IOException	if version string probided by client does not look right, see {@link #ver} how value should look
	 */
	public static ProtocolVersion readProtocolVersion(final InputStream in) throws IOException {
		
		byte[] buff = new byte[12];
		
		in.read(buff);
		
		if (buff[0] == 'R' && buff[1] == 'F' && buff[2] == 'B' && buff[3] == ' '
			&& buff[4] == '0' && buff[5] == '0' && buff[6] == '3' && buff[7] == '.'
			&& buff[8] == '0' && buff[9] == '0' && (buff[10] >= '3' || buff[10] <= '8') && buff[11] == '\n'
				) {
			
			return new ProtocolVersion(3, buff[10] - '0');
		}
		
		throw new IOException("Unsupported RFB version value: " + Arrays.toString(buff));
	}
	
	@Override
	public String toString() {
		
		return String.format("%d.%d", this.major, this.minor);
	}
}
