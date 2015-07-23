package RobotService;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class RobotMouse {

	public static RobotMouse robo;

	private Robot robot;

	public RobotMouse() throws AWTException {
		this.robot = new Robot();
	}

	/**
	 * {@link Robot#mousePress(int)}
	 * @param button
	 */
	public void mousePress(int button) {
		robot.mousePress(button);
	}
	
	/**
	 *  {@link Robot#mouseRelease(int)}
	 * @param button
	 */
	public void mouseRelease(int button) {
		robot.mouseRelease(button);
	}
	
	/**
	 *  {@link Robot#mouseMove(int, int)}
	 * @param x
	 * @param y
	 */
	public void mouseMove(int x, int y) {
		robot.mouseMove(x, y);
	}
	
	public void mouseClick(int x, int y) {
		mouseMove(x, y);
		mousePress(InputEvent.BUTTON1_DOWN_MASK);
		mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public void mouseRightClick(int x, int y) {
		mouseMove(x, y);
		mousePress(InputEvent.BUTTON2_DOWN_MASK);
		mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
	}
}
