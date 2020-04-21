package com.scoreunit.rfb.screen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.scoreunit.rfb.image.TrueColorImage;

public class ScreenCaptureTest {

	@Test
	public void test_01_screenCapture() throws Exception {
		
		final ScreenCaptureInterface screenCapture = new ScreenCapture();
		
		assertTrue( screenCapture.getScreenHeight() > 0 );
		assertTrue( screenCapture.getScreenWidth() > 0 );
		
		int x = 320, y = 320, width = 240, height = 240;

		final TrueColorImage img = screenCapture.getScreenshot(x, y, width, height);

		final int[] raw = img.raw;
		assertEquals(width * height, raw.length);
	}
	
	@Test
	public void test_02_screenCaptureFullScreen() throws Exception {
		
		final ScreenCaptureInterface screenCapture = new ScreenCapture();
		
		int width = screenCapture.getScreenWidth(), height = screenCapture.getScreenHeight();

		final TrueColorImage img = screenCapture.getScreenshot();

		final int[] raw = img.raw;
		assertEquals(width * height, raw.length);
	}
	
	@Test
	public void test_03_screenDimension() throws Exception {
		
		final ScreenCaptureInterface screenCapture = new ScreenCapture();
		
		assertTrue( screenCapture.getScreenHeight() > 0 );
		assertTrue( screenCapture.getScreenWidth() > 0 );

		final TrueColorImage img = screenCapture.getScreenshot();

		assertEquals(screenCapture.getScreenHeight(), img.height);
		assertEquals(screenCapture.getScreenWidth(), img.width);
	}

}
