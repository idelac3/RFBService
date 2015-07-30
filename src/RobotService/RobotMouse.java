package RobotService;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

/**
 * This Robot class can send mouse events 
 * to desktop, like mouse move, click etc.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class RobotMouse {

	/**
	 * Single instance on application level.
	 */
	public static RobotMouse robo;

	private Robot robot;

	/**
	 * Instance of Robot for generator of pointer events.
	 * 
	 * @throws AWTException
	 */
	public RobotMouse() throws AWTException {
		this.robot = new Robot();
	}

	/**
	 * Generate mouse press.
	 * 
	 * {@link Robot#mousePress(int)}
	 * @param button
	 */
	public void mousePress(int button) {
		robot.mousePress(button);
	}
	
	/**
	 * Generate mouse release.
	 * 
	 *  {@link Robot#mouseRelease(int)}
	 * @param button
	 */
	public void mouseRelease(int button) {
		robot.mouseRelease(button);
	}
	
	/**
	 * Cause mouse pointer to move on screen.
	 * 
	 *  {@link Robot#mouseMove(int, int)}
	 * @param x
	 * @param y
	 */
	public void mouseMove(int x, int y) {
		robot.mouseMove(x, y);
	}
	
	/**
	 * Produce mouse left-click on given position.
	 * Note that multi-display systems may have negative values for x, y as starting points.
	 * 
	 * @param x value in pixel, starting from upper left area
	 * @param y value in pixel, starting from upper left area
	 */
	public void mouseClick(int x, int y) {
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
	public void mouseRightClick(int x, int y) {
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
	public void mouseMiddleClick(int x, int y) {
		mouseMove(x, y);
		mousePress(InputEvent.BUTTON3_DOWN_MASK);
		mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
	}

	/**
	 * Generate mouse wheel, mainly for scrolling.
	 * 
	 * @param amt number of "notches" to move the mouse wheel.
	 * Negative values indicate movement up/away from the user, positive values indicate movement down/towards the user.
	 */
	public void mouseWheel(int amt) {
		robot.mouseWheel(amt);
	}
	
}
