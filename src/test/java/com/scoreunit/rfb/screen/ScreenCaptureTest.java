package com.scoreunit.rfb.screen;

import static org.junit.Assert.*;

import java.awt.AWTException;

import org.junit.Test;

import com.scoreunit.rfb.image.TrueColorImage;

public class ScreenCaptureTest {

	@Test
	public void test_01_screenCapture() {
		
		assertTrue( ScreenCapture.getScreenHeight() > 0 );
		assertTrue( ScreenCapture.getScreenWidth() > 0 );
		
		int x = 320, y = 320, width = 240, height = 240;
		
		try {
		
			final TrueColorImage img = ScreenCapture.getScreenshot(x, y, width, height);
			
			final int[] raw = img.raw;
			assertEquals(width * height, raw.length);
		} catch (final AWTException ex) {
			
		}
	}
	
	@Test
	public void test_02_screenDimension() {
		
		assertTrue( ScreenCapture.getScreenHeight() > 0 );
		assertTrue( ScreenCapture.getScreenWidth() > 0 );
		
		try {
		
			final TrueColorImage img = ScreenCapture.getScreenshot();
			
			assertEquals(ScreenCapture.getScreenHeight(), img.height);
			assertEquals(ScreenCapture.getScreenWidth(), img.width);
		} catch (final AWTException ex) {
			
		}
	}

}
