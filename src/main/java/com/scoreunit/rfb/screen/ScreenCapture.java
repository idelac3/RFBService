package com.scoreunit.rfb.screen;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scoreunit.rfb.image.TrueColorImage;

/**
 * Java routines to capture current screen.
 * <p>
 * Note that on 32-bit true color systems,
 * image of screen is usually in the following byte order:
 * <pre>
 * [A R G B]
 * </pre>
 * while VNC clients might expect following byte order:
 * <pre>
 * [B G R 0]
 * </pre>
 * <p>
 * 
 * @author igor.delac@gmail.com
 *
 */
public class ScreenCapture {
	
	public final static Logger log = LoggerFactory.getLogger(ScreenCapture.class);
	
	/**
	 * This method will fill image buffer with part of screen.
	 * Buffer is always filled with ARGB values (32-bit).
	 * 
	 * @param x upper left value
	 * @param y upper left value
	 * @param width width in pixel
	 * @param height height in pixel
	 * 
	 * @return	{@link TrueColorImage} which contains array of int's representing pixels of current screen, or part of screen
	 * 
	 * @throws AWTException	if running in headless mode, eg. without X11, or any display 
	 */
	public static TrueColorImage getScreenshot(int x, int y, int width, int height) throws AWTException {
	
		final Robot robot = new Robot();
		
		final Rectangle screenRect = new Rectangle(x, y, width, height);
		final BufferedImage colorImage = robot.createScreenCapture(screenRect);

	    final int[] colorImageBuffer = ((DataBufferInt) colorImage.getRaster().getDataBuffer()).getData();

	    return new TrueColorImage(colorImageBuffer, colorImage.getWidth(), colorImage.getHeight());
	}
	
	/**
	 * This method will fill image buffer with complete screen (primary screen).
	 * Buffer is always filled with ARGB values (32-bit).
	 * 
	 * @return	{@link TrueColorImage} which contains array of int's representing pixels of current screen
	 * 
	 * @throws AWTException	if running in headless mode, eg. without X11, or any display 
	 */
	public static TrueColorImage getScreenshot() throws AWTException {
		
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		return getScreenshot(0, 0, screenSize.width, screenSize.height);
	}
	
	/**
	 * Width of screen.
	 * 
	 * @return width in pixel
	 */
	public static int getScreenWidth() {
		
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		return screenSize.width;
	}
	
	/**
	 * Screen height.
	 * 
	 * @return height in pixel
	 */
	public static int getScreenHeight() {
		
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		return screenSize.height;
	}
}
