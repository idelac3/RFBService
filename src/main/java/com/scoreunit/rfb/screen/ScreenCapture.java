package com.scoreunit.rfb.screen;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Java routines to capture current screen.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class ScreenCapture {
	
	/**
	 * This method will fill image buffer with part of screen.
	 * Buffer is always filled with ARGB values (32-bit).
	 * 
	 * @param x upper left value
	 * @param y upper left value
	 * @param width width in pixel
	 * @param height height in pixel
	 * 
	 * @return	array of int's representing pixels of current screen, or part of screen
	 * 
	 * @throws AWTException	if running in headless mode, eg. without X11, or any display 
	 */
	public static int[] getScreenshot(int x, int y, int width, int height) throws AWTException {
	
		final Robot robot = new Robot();
		
		final Rectangle screenRect = new Rectangle(x, y, width, height);
		final BufferedImage colorImage = robot.createScreenCapture(screenRect);
	
	    final int[] colorImageBuffer = ((DataBufferInt) colorImage.getRaster().getDataBuffer()).getData();

	    return colorImageBuffer;
	}
	
	/**
	 * This method will fill image buffer with complete screen (primary screen).
	 * Buffer is always filled with ARGB values (32-bit).
	 * 
	 * @return	array of int's representing pixels of current screen
	 * 
	 * @throws AWTException	if running in headless mode, eg. without X11, or any display 
	 */
	public static int[] getScreenshot() throws AWTException {
		
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		return getScreenshot(0, 0, screenSize.width, screenSize.height);
	}
	
	public static int getScreenWidth() {
		
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		return screenSize.width;
	}
	
	public static int getScreenHeight() {
		
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		return screenSize.height;
	}
}
