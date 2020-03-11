package com.scoreunit.rfb.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 'The RFB Protocol' documentation, page 23,
 * by Tristan Richardson, RealVNC Ltd.
 * <p>
 * Version 3.8, Last updated 26 November 2010
 *
 * @author igor.delac@gmail.com
 *
 */
class KeyEvent {

	public int key;
	
	public byte downFlag;
	
	/**
	 * Create new KeyEvent object.
	 * 
	 * @param key		-	value of key, eg. for Enter key value is 0xff0d,
	 * 						see <i>X11/keysymdef.h</i> file
	 * @param downFlag	-	flag is set if key is pressed, not yet released
	 */
	public KeyEvent(final int key, final byte downFlag) {

		this.key = key;
		this.downFlag = downFlag;
	}
	
	/**
	 * Read key event message.
	 * <p>
	 * 
	 * @param inputStream		-	{@link InputStream} to read raw data from
	 * 
	 * @return	instance of {@link KeyEvent} message
	 * 
	 * @throws IOException	if connections breaks
	 */
	public static KeyEvent read(final InputStream inputStream) throws IOException {
		
		final DataInputStream in = new DataInputStream(inputStream);
		
		byte downFlag = in.readByte();
		in.readShort(); // padding
		int key = in.readInt();
		
		return new KeyEvent(key, downFlag);
	}
}
