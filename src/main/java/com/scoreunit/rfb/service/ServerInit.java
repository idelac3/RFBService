package com.scoreunit.rfb.service;

import java.awt.Toolkit;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 'The RFB Protocol' documentation, page 17,
 * by Tristan Richardson, RealVNC Ltd.
 * <p>
 * Version 3.8, Last updated 26 November 2010
 *
 * @author igor.delac@gmail.com
 *
 */
public class ServerInit {

	/**
	 * Write server init. message.
	 * 
	 * @param outputStream				-	instance of {@link OutputStream} where to write, typically obtained from {@link Socket#getOutputStream()} method
	 * 
	 * @throws IOException	if connection is broken
	 */
	public static void send(final OutputStream outputStream) throws IOException {
		
		final DataOutputStream out = new DataOutputStream(outputStream);
		
		short width  = (short) Toolkit.getDefaultToolkit().getScreenSize().width;
		short height = (short) Toolkit.getDefaultToolkit().getScreenSize().height;
		
		out.writeShort(width);
		out.writeShort(height);
		
		final SetPixelFormat pixelFormat = new SetPixelFormat(); // Default should be ok.
		SetPixelFormat.write(outputStream, pixelFormat);

		final String title = 
				String.format("%s\\%s", System.getProperty("host.name")
						, System.getProperty("user.name"));
		
		out.writeInt(title.length());
		out.write(title.getBytes());			

		out.flush();
	}
	
}
