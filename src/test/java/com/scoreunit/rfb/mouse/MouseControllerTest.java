package com.scoreunit.rfb.mouse;

import static org.junit.Assert.*;

import java.awt.event.InputEvent;

import org.junit.Test;

public class MouseControllerTest {

	@Test
	public void test_01_basicMethodCalls() {
		
		final MouseController controller = new MouseController();
		assertNotNull(controller.toString());
		
		MouseController.mouseClick(0, 0);
		MouseController.mouseMiddleClick(1, 1);
		MouseController.mouseRightClick(0, 0);
		MouseController.mouseMove(500, 500);
		MouseController.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		MouseController.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		MouseController.mouseWheel(100);
	}

}
