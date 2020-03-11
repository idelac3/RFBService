package com.scoreunit.rfb.mouse;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

/**
 * This Robot class can send mouse events 
 * to desktop, like mouse move, click etc.
 * <p>
 * Methods in this class will silently fail on {@link AWTException},
 * if AWT toolkit is not possible to create, 
 * eg. in case when X11 subsystem is not available, etc.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class MouseController {

	/**
	 * Generate mouse press.
	 * 
	 * @param button	-	button mask, see {@link Robot#mousePress(int)} method
	 */
	public static void mousePress(int button) {
		
		try {
		
			final Robot robot = new Robot();
			robot.mousePress(button);
		} catch (final AWTException exception) {

		}		
	}
	
	/**
	 * Generate mouse release.
	 * 
	 * @param button	-	button mask, see {@link Robot#mouseRelease(int)} method
	 */
	public static void mouseRelease(int button) {
		
		try {
		
			final Robot robot = new Robot();
			robot.mouseRelease(button);
		} catch (final AWTException exception) {

		}		
	}
	
	/**
	 * Generate mouse move event. Move cursor on screen
	 * 
	 * @param x	-	x position
	 * @param y	-	y position
	 */
	public static void mouseMove(int x, int y) {
		
		try {
		
			final Robot robot = new Robot();
			robot.mouseMove(x, y);
		} catch (final AWTException exception) {

		}		
	}
	
	/**
	 * Produce mouse left-click on given position.
	 * Note that multi-display systems may have negative values for x, y as starting points.
	 * 
	 * @param x value in pixel, starting from upper left area
	 * @param y value in pixel, starting from upper left area
	 */
	public static void mouseClick(int x, int y) {
		mouseMove(x, y);
		mousePress(InputEvent.BUTTON1_DOWN_MASK);
		mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	/**
	 * Produce mouse right-click on given position.
	 * Note that multi-display systems may have negative values for x, y as starting points.
	 * 
	 * @param x value in pixel, starting from upper left area
	 * @param y value in pixel, starting from upper left area
	 */	
	public static void mouseRightClick(int x, int y) {
		mouseMove(x, y);
		mousePress(InputEvent.BUTTON2_DOWN_MASK);
		mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
	}
	
	/**
	 * Produce mouse middle-click on given position.
	 * Note that multi-display systems may have negative values for x, y as starting points.
	 * 
	 * @param x value in pixel, starting from upper left area
	 * @param y value in pixel, starting from upper left area
	 */	
	public static void mouseMiddleClick(int x, int y) {
		mouseMove(x, y);
		mousePress(InputEvent.BUTTON3_DOWN_MASK);
		mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
	}

	/**
	 * Generate mouse wheel, mainly for scrolling.
	 * 
	 * @param amt 		-	number of "notches" to move the mouse wheel.
	 * 						Negative values indicate movement up/away from the user, 
	 * 						positive values indicate movement down/towards the user.
	 * 						See {@link Robot#mouseWheel(int)} method.
	 */
	public static void mouseWheel(int amt) {
		
		try {
			
			final Robot robot = new Robot();
			robot.mouseWheel(amt);
		} catch (final AWTException exception) {

		}	
	}
	
}
