package com.scoreunit.rfb.mouse;

import static org.junit.Assert.*;

import org.junit.Test;

public class MouseControllerTest {

	@Test
	public void test_01_basicMethodCalls() {
		
		final MouseController controller = new MouseController();
		assertNotNull(controller.toString());
		
		controller.mouseMove(0, 0);
		controller.mousePress(MouseController.BUTTON1);
		controller.mouseRelease(MouseController.BUTTON1);
		controller.mouseWheel(100);
	}

}
