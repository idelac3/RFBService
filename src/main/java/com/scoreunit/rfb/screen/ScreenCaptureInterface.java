package com.scoreunit.rfb.screen;

import java.awt.image.BufferedImage;

import com.scoreunit.rfb.image.TrueColorImage;

public interface ScreenCaptureInterface {

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
	 * @throws Exception	if running in headless mode, eg. without X11, or any display, or screen image is not available, etc.  
	 */
	public TrueColorImage getScreenshot(int x, int y, int width, int height) throws Exception;
	
	/**
	 * This method will fill image buffer with complete screen (primary screen).
	 * Buffer is always filled with ARGB values (32-bit), see {@link BufferedImage#TYPE_INT_ARGB}.
	 * 
	 * @return	{@link TrueColorImage} which contains array of int's representing pixels of current screen
	 * 
	 * @throws Exception	if running in headless mode, eg. without X11, or any display, or screen image is not available, etc. 
	 */
	public TrueColorImage getScreenshot() throws Exception;
	
	/**
	 * Width of screen.
	 * 
	 * @return width in pixel, or -1 if screen is not available
	 */
	public int getScreenWidth();		
	
	/**
	 * Screen height.
	 * 
	 * @return height in pixel, or -1 if screen is not available 
	 */
	public int getScreenHeight();
}