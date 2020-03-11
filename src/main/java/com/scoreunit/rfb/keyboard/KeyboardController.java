package com.scoreunit.rfb.keyboard;

import static java.awt.event.KeyEvent.VK_ALT;
import static java.awt.event.KeyEvent.VK_BACK_SPACE;
import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_END;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_F1;
import static java.awt.event.KeyEvent.VK_F10;
import static java.awt.event.KeyEvent.VK_F11;
import static java.awt.event.KeyEvent.VK_F12;
import static java.awt.event.KeyEvent.VK_F2;
import static java.awt.event.KeyEvent.VK_F3;
import static java.awt.event.KeyEvent.VK_F4;
import static java.awt.event.KeyEvent.VK_F5;
import static java.awt.event.KeyEvent.VK_F6;
import static java.awt.event.KeyEvent.VK_F7;
import static java.awt.event.KeyEvent.VK_F8;
import static java.awt.event.KeyEvent.VK_F9;
import static java.awt.event.KeyEvent.VK_HOME;
import static java.awt.event.KeyEvent.VK_INSERT;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_PAGE_DOWN;
import static java.awt.event.KeyEvent.VK_PAGE_UP;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_TAB;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

/**
 * Class to control keyboard on system level.
 * <p>
 * With this class it is possible to send key events,
 * just as would local user hit keys on keyboard.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class KeyboardController {

	/**
	 * Send key to system. Special key codes have 0xff00 bit mask. This 
	 * is according to RFB specification.
	 * 
	 * @param keyCode	-	see {@link KeyEvent}
	 * @param state 	-	key button pressed or released on keyboard, 
	 * 						value 0 for key released or 1 for key pressed
	 */
	public static void sendKey(int keyCode, int state) {
		
		switch (keyCode) {
		case 0xff08:
			doType(VK_BACK_SPACE, state);
			break;
		case 0xff09:
			doType(VK_TAB, state);
			break;
		case 0xff0d: case 0xff8d:
			doType(VK_ENTER, state);
			break;
		case 0xff1b:
			doType(VK_ESCAPE, state);
			break;
		case 0xff63:
			doType(VK_INSERT, state);
			break;
		case 0xffff:
			doType(VK_DELETE, state);
			break;
		case 0xff50:
			doType(VK_HOME, state);
			break;
		case 0xff57:
			doType(VK_END, state);
			break;
		case 0xff55:
			doType(VK_PAGE_UP, state);
			break;
		case 0xff56:
			doType(VK_PAGE_DOWN, state);
			break;
		case 0xff51:
			doType(VK_LEFT, state);
			break;
		case 0xff52:
			doType(VK_UP, state);
			break;
		case 0xff53:
			doType(VK_RIGHT, state);
			break;
		case 0xff54:
			doType(VK_DOWN, state);
			break;
		case 0xffbe:
			doType(VK_F1, state);			
			break;
		case 0xffbf:
			doType(VK_F2, state);			
			break;
		case 0xffc0:
			doType(VK_F3, state);			
			break;
		case 0xffc1:
			doType(VK_F4, state);			
			break;
		case 0xffc2:
			doType(VK_F5, state);			
			break;
		case 0xffc3:
			doType(VK_F6, state);			
			break;
		case 0xffc4:
			doType(VK_F7, state);			
			break;									
		case 0xffc5:
			doType(VK_F8, state);			
			break;		
		case 0xffc6:
			doType(VK_F9, state);			
			break;			
		case 0xffc7:
			doType(VK_F10, state);			
			break;
		case 0xffc8:
			doType(VK_F11, state);			
			break;		
		case 0xffc9:
			doType(VK_F12, state);			
			break;		
		case 0xffe1: case 0xffe2:
			doType(VK_SHIFT, state);			
			break;				
		case 0xffe3: case 0xffe4:
			doType(VK_CONTROL, state);			
			break;			
		case 0xffe9: case 0xffea:
			doType(VK_ALT, state);			
			break;			
		default:
			
			/*
			 * Translation of a..z keys.
			 */
			if (keyCode >= 97 && keyCode <= 122) {
				/*
				 * Turn lower-case a..z key codes into upper-case A..Z key codes.
				 */
				keyCode = keyCode - 32;
			}
			
			doType(keyCode, state);

		}
	}

	/**
	 * Send key event to system.
	 * 
	 * @param keyCode		-	a key code to send, see {@link KeyEvent} list of codes
	 * @param state 		-	if value <i>0</i>, key is released, otherwise key is pressed
	 */
	private static void doType(int keyCode, int state) {
		
		try {
			
			final Robot robot = new Robot();
			
			if (state == 0) {
				robot.keyRelease(keyCode);
			}
			else {
				robot.keyPress(keyCode);
			}
		} catch (final AWTException ex) {
			
		}
	}

}
