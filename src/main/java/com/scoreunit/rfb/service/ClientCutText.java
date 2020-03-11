package com.scoreunit.rfb.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 'The RFB Protocol' documentation, page 26,
 * by Tristan Richardson, RealVNC Ltd.
 * <p>
 * Version 3.8, Last updated 26 November 2010
 *
 * @author igor.delac@gmail.com
 *
 */
class ClientCutText {
	
	public String text;
	
	/**
	 * Create new ClientCutText object.
	 * 
	 */
	public ClientCutText(final String text) {

		this.text = text;
	}
	
	/**
	 * The client has new ISO 8859-1 (Latin-1) text in its cut buffer.
	 * Ends of lines are represented by the linefeed / newline character (value 10) alone. No carriage-return (value 13) is needed.
	 * 
	 * @param inputStream		-	{@link InputStream} to read raw data from
	 * 
	 * @return	instance of {@link ClientCutText} message
	 * 
	 * @throws IOException	if connections breaks
	 */
	public static ClientCutText read(final InputStream inputStream) throws IOException {
		
		final DataInputStream in = new DataInputStream(inputStream);
		
		in.readByte(); // 3 padding bytes.
		in.readByte();
		in.readByte();
		
		int len = in.readInt();
		byte[] buff = new byte[len];
		
		in.read(buff);
		
		return new ClientCutText(new String(buff));
	}
}
