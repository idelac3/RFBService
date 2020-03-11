package com.scoreunit.rfb.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 'The RFB Protocol' documentation, page 25,
 * by Tristan Richardson, RealVNC Ltd.
 * <p>
 * Version 3.8, Last updated 26 November 2010
 *
 * @author igor.delac@gmail.com
 *
 */
class PointerEvent {
	
	public byte buttonMask;
	
	public short xPos, yPos;
	
	/**
	 * Create new PointerEvent object.
	 * 
	 * @param xPos		-	position of cursor, X axis
	 * @param yPos		-	position of cursor, Y axis
	 * 
	 * @param buttonMask -	defines which buttons are pressed or released
	 */
	public PointerEvent(final short xPos, final short yPos, final byte buttonMask) {

		this.xPos = xPos;
		this.yPos = yPos;
		this.buttonMask = buttonMask;
	}
	
	/**
	 * Indicates either pointer movement or a pointer button press or release. 
	 * The pointer is now at (x-position, y-position),
	 * and the current state of buttons 1 to 8 are represented by bits 0 to 7 of button-mask respectively, 
	 * 0 meaning up, 1 meaning down (pressed).
	 * <p>
	 * On a conventional mouse, buttons 1, 2 and 3 correspond to the left, middle and right buttons on the mouse. 
	 * On a wheel mouse, each step of the wheel is represented by a press and release of a certain button. 
	 * Button 4 means up, button 5 means down, button 6 means left and button 7 means right.
	 * <p>
	 * 
	 * @param inputStream		-	{@link InputStream} to read raw data from
	 * 
	 * @return	instance of {@link PointerEvent} message
	 * 
	 * @throws IOException	if connections breaks
	 */
	public static PointerEvent read(final InputStream inputStream) throws IOException {
		
		final DataInputStream in = new DataInputStream(inputStream);
		
		byte buttonMask = in.readByte();
		short x = in.readShort();
		short y = in.readShort();
		
		return new PointerEvent(x, y, buttonMask);
	}
}
