package com.scoreunit.rfb.screen;

import java.awt.AWTError;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scoreunit.rfb.image.TrueColorImage;

/**
 * Java routines to capture current screen.
 * <br>
 * Note that on 32-bit true color systems,
 * image of screen is usually in the following byte order:
 * <pre>
 * [A R G B]
 * </pre>
 * while VNC clients might expect following byte order:
 * <pre>
 * [B G R 0]
 * </pre>
 * This implementation use AWT library.
 *
 * 
 * @author igor.delac@gmail.com
 *
 */
public class ScreenCapture implements ScreenCaptureInterface {

	/**
	 * Default {@link Logger} instance.
	 */
	public final static Logger log = LoggerFactory.getLogger(ScreenCapture.class);
	
	/**
	 * A default width and height in pixel if AWT toolkit is unavailable on host system,
	 * or headless mode is on.
	 */
	public final static int DEFAULT_WIDTH = 800, DEFAULT_HEIGHT = 600;
	
	/**
	 * Used to get screen dimension.
	 */
	private Toolkit toolkit;
	
	/**
	 * Used to capture image of screen or part of screen.
	 */
	private Robot robot;

	/**
	 * New screen capture instance.
	 */
	public ScreenCapture() {
	
		try {
			
			this.toolkit = Toolkit.getDefaultToolkit();
			this.robot = new Robot();
		}
		catch (final AWTError | AWTException error) {
			
			log.error("Unable to initialize AWT Toolkit.", error);
			
			this.toolkit = null;
			this.robot = null;
		}
	}
	
	/**
	 * This method will fill image buffer with part of screen.
	 * Buffer is always filled with ARGB values (32-bit).
	 * <p>
	 * Position <i>(x, y)</i> always start from top-left corner of screen.
	 * 
	 * @param x			-	horizontal value
	 * @param y			-	vertical value
	 * @param width		-	width in pixel
	 * @param height	-	height in pixel
	 * 
	 * @return	{@link TrueColorImage} which contains array of int's representing pixels of current screen, or part of screen
	 * 
	 * @throws AWTException	if running in headless mode, eg. without X11, or any display 
	 */
	@Override
	public TrueColorImage getScreenshot(int x, int y, int width, int height) throws AWTException {	
		
		if (this.robot == null) {
			
			try {
			
				// Try to provide at least visual indicator that host system is unable to capture screen image.
				return LoadingResource.get(DEFAULT_WIDTH, DEFAULT_HEIGHT
						, new ByteArrayInputStream("AWT unavailable. Check if headless mode is off and graphics is available on host system.".getBytes()));
			} catch (IOException e) {
			
				throw new AWTException(e.getMessage());
			}
		}
		
		final Rectangle screenRect = new Rectangle(x, y, width, height);
		final BufferedImage colorImage = this.robot.createScreenCapture(screenRect);

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
	@Override
	public TrueColorImage getScreenshot() throws AWTException {
		
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		return getScreenshot(0, 0, screenSize.width, screenSize.height);
	}
	
	/**
	 * Width of screen.
	 * 
	 * @return width in pixel, or {@link #DEFAULT_WIDTH} if AWT {@link Toolkit#getDefaultToolkit()} is not available
	 */
	@Override
	public int getScreenWidth() {
		
		if (this.toolkit == null) {
			
			return DEFAULT_WIDTH;
		}
		
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		return screenSize.width;
	}
	
	/**
	 * Screen height.
	 * 
	 * @return height in pixel, or or {@link #DEFAULT_HEIGHT}  if AWT {@link Toolkit#getDefaultToolkit()} is not available
	 */
	@Override
	public int getScreenHeight() {
		
		if (this.toolkit == null) {
			
			return DEFAULT_HEIGHT;
		}
		
		final Dimension screenSize = this.toolkit.getScreenSize();
		
		return screenSize.height;
	}
}
