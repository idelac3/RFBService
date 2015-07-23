package RobotService;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * This class allows access to pixels on screen.
 * To take a screenshot, use {@link #getScreenshot(int, int, int, int)}
 * and {@link #getColorImageBuffer()} methods.<BR>
 * <BR>
 * 
 * @author igor.delac@gmail.com
 *
 */
public class RobotScreen {

	/**
	 * Instance of RoboScreen for accessing image buffer.
	 */
	public static RobotScreen robo;
	
	private  int[] colorImageBuffer;

	private Robot robot;
	
	/**
	 * Create new {@link RobotScreen} instance.
	 * 
	 * @throws AWTException
	 */
	public RobotScreen() throws AWTException {
		robot = new Robot();
	}
	
	/**
	 * Get current image buffer. May return <I>null</I> value
	 * if it's not filled with pixels. To fill image buffer use
	 * {@link #getScreenshot(int, int, int, int)} and {@link #setMinimizedImageBuffer(int, int)}
	 * methods.<BR>
	 * It's up to user of this class to control when will application use methods to fill
	 * image buffer.<BR>
	 * 
	 * @return image buffer
	 */
	public int[] getColorImageBuffer() {
		return colorImageBuffer;
	}

	/**
	 * This method will fill image buffer with part of screen.
	 * Buffer is always filled with ARGB values (32-bit).
	 * 
	 * @param x upper left value
	 * @param y upper left value
	 * @param width width in pixel
	 * @param height height in pixel
	 */
	public void getScreenshot(int x, int y, int width, int height) {
		
		Rectangle screenRect = new Rectangle(x, y, width, height);
		BufferedImage colorImage = robot.createScreenCapture(screenRect);
	    		
	    colorImageBuffer = ((DataBufferInt) colorImage.getRaster().getDataBuffer()).getData();

	}
	
	/**
	 * Produce black and white vertical bars in color image buffer.
	 * This is usually called when main application window is minimized and buffer
	 * should not contain screen capture, instead some generated pattern.
	 * 
	 * @param width dimension used to calculate buffer length
	 * @param height dimension used to calculate buffer length
	 */
	public void setMinimizedImageBuffer(int width, int height) {
		
		/*
		 * Vertical bar width in pixel.
		 */
		final int barWidth = 16;
		
		/*
		 * Color value in ARGB format.
		 */
		final int color = 0x00FFFFFF;
		
		/*
		 * New allocation of image buffer for vertical bars.
		 */
		colorImageBuffer = new int[width * height];
		
		/*
		 * Fill image buffer with bars.
		 */
		for (int i = 0; i < colorImageBuffer.length; i = i + 2 * barWidth) {
		 	
			for (int j = 0; j < barWidth; j++) {
				if (i + j < colorImageBuffer.length) {
					colorImageBuffer[i + j] = color;
				}				
			}
			
			
		}
		
	}
	
}
