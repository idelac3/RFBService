package com.scoreunit.rfb.mouse;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class use {@link Robot} to send mouse events 
 * to desktop, like mouse move, button press, release etc.
 * <p>
 * Methods in this class will silently fail on {@link AWTException},
 * if AWT toolkit is not possible to create, 
 * eg. in case when X11 subsystem is not available, etc.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class MouseController {
	
	public final static Logger log = LoggerFactory.getLogger(MouseController.class);
	
	/**
	 * Use with methods {@link #mousePress(int)} and {@link #mouseRelease(int)}
	 * to select mouse button (left, right or middle).
	 */
	public static int BUTTON1 = InputEvent.BUTTON1_DOWN_MASK
			, BUTTON2 = InputEvent.BUTTON2_DOWN_MASK
			, BUTTON3 = InputEvent.BUTTON3_DOWN_MASK;
	
	private Robot robot;
	
	public MouseController() {
			
		try {
		
			this.robot = new Robot();
		} catch (final AWTException exception) {

			this.robot = null;
		}	
	}
	
	/**
	 * Generate mouse press system event.
	 * 
	 * @param button	-	button mask, see {@link Robot#mousePress(int)} method
	 */
	public void mousePress(int button) {
		
		if (this.robot != null) {
		
			this.robot.mousePress(button);
		}
	}
	
	/**
	 * Generate mouse release system event.
	 * 
	 * @param button	-	button mask, see {@link Robot#mouseRelease(int)} method
	 */
	public void mouseRelease(int button) {
		
		if (this.robot != null) {
			
			this.robot.mouseRelease(button);
		}	
	}
	
	/**
	 * Generate mouse move event. Move cursor on screen.
	 * 
	 * @param x	-	x position
	 * @param y	-	y position
	 */
	public void mouseMove(int x, int y) {

		if (this.robot != null) {
		
			this.robot.mouseMove(x, y);
		}
	}

	/**
	 * Generate mouse wheel, mainly for scrolling.
	 * 
	 * @param amt 		-	number of "notches" to move the mouse wheel.
	 * 						Negative values indicate movement up/away from the user, 
	 * 						positive values indicate movement down/towards the user.
	 * 						See {@link Robot#mouseWheel(int)} method.
	 */
	public void mouseWheel(int amt) {

		if (this.robot != null) {
	
			this.robot.mouseWheel(amt);
		} 
	}
	
}
