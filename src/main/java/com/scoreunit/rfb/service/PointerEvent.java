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

	/**
	 * Button mask constants, for {@link #isButtonPressed()} method.
	 */
	public static int BUTTON1 = 1, BUTTON2 = 2, BUTTON3 = 4;
	
	/**
	 * Button mask bits for mouse wheel up and down pointer events.
	 */
	public static int WHEEL_UP = 8, WHEEL_DOWN = 16;
	
	/**
	 * Access to raw button mask byte received in pointer message.
	 */
	public byte buttonMask;
	
	/**
	 * Mouse coordinates received in pointer message.
	 */
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
	 * Check if event contains a bit set indicating that mouse button is pressed.
	 * If this function returns false, it means that mouse button is released.
	 * 
	 * @param button	-	one of {@link #BUTTON1}, {@link #BUTTON2}, etc.
	 * 
	 * @return	true if pointer event is received with button mask indicating that mouse button is pressed, otherwise false
	 */
	public boolean isButtonPressed(final int button) {
		
		return (buttonMask & button) == button;
	}

	/**
	 * Check if this pointer event message contains a bit set indicating wheel up action request.
	 * 
	 * @return	true if user did wheel up button move
	 */
	public boolean isWheelUp() {
		
		return (buttonMask & WHEEL_UP) == WHEEL_UP;
	}

	/**
	 * Check if this pointer event message contains a bit set indicating wheel down action request.
	 * 
	 * @return	true if user did wheel down button move
	 */
	public boolean isWheelDown() {
		
		return (buttonMask & WHEEL_DOWN) == WHEEL_DOWN;
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
