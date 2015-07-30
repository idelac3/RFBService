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
	
	private  int[] colorImageBuffer, previousImageBuffer;
	
	private int width, height;
	
	private int deltaX, deltaY, deltaWidth, deltaHeight;
	
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
	
		previousImageBuffer = colorImageBuffer;
		
	    colorImageBuffer = ((DataBufferInt) colorImage.getRaster().getDataBuffer()).getData();

	    if (previousImageBuffer == null || 
	    		previousImageBuffer.length != colorImageBuffer.length) {
	    	previousImageBuffer = colorImageBuffer;
	    }

	    this.width = width;
	    this.height = height;
	    
	}
	
	public int[] getDeltaImageBuffer() {
		
		int startRowChange = 0, endRowChange = 0;

		/*
		 * Determine first and last rows of screen delta.
		 */
		
		for (int row = 4; row < height - 5; row++) {
			/*
			 * Look for first different row.
			 */
			if (! rowMatch(previousImageBuffer, row, colorImageBuffer, row, width)) {																
				startRowChange = row;
				break;
			}
		}

		for (int row = height - 5; row >= 4; row--) {
			/*
			 * Look for last different row.
			 */
			if (! rowMatch(previousImageBuffer, row, colorImageBuffer, row, width)) {																
				endRowChange = row;
				break;
			}
		}
		
		/*
		 * If delta area of screen is found ... 
		 */
		if (startRowChange < endRowChange) {
			
			/*
			 * Calculate row count and pixel count.
			 */
			int rowCount = endRowChange - startRowChange + 1;
			int pixelCount = rowCount * width;
			
			/*
			 * Allocate delta buffer for screen area between starting
			 * and ending row.
			 */
			int[] deltaScreen = new int[pixelCount];
			
			/*
			 * Copy from image buffer only different pixels.
			 */
			System.arraycopy(colorImageBuffer, startRowChange * width, deltaScreen, 0, pixelCount);
			
			deltaX = 0;
			deltaY = startRowChange;
			deltaWidth = width;
			deltaHeight = rowCount;
			
			return deltaScreen;
		}
		else {
			
			/*
			 * If no difference between previous and current image buffer,
			 * return null value and set delta coordinates to 0 value.
			 */
			deltaX = 0;
			deltaY = 0;
			deltaWidth = 0;
			deltaHeight = 0;
			
			return null;
		}
		
	}
	
	public int getDeltaX() {
		return deltaX;
	}

	public int getDeltaY() {
		return deltaY;
	}

	public int getDeltaWidth() {
		return deltaWidth;
	}

	public int getDeltaHeight() {
		return deltaHeight;
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

	/**
	 * Row matcher function. It compares two rows and returns positive result if they are equal.
	 * This function does not make sense if buffers are not the same dimension.<BR>
	 *  <B>NOTE:</B> This function does not check for index bounds in arrays!
	 *  
	 * @param buf1 first image buffer
	 * @param row1 row in first image buffer
	 * @param buf2 second image buffer
	 * @param row2 row in second image buffer
	 * @param width width of row
	 * @return <I>true</I> if equal, otherwise <I>false</I>
	 */
    private boolean rowMatch(int[] buf1, int row1, int[] buf2, int row2, int width) {
        boolean match = true;
        
        for (int i = row1 * width, j = row2 * width;
                i < (row1 + 1) * width && j < (row2 + 1) * width;
                i++, j++) {
            if (buf1[i] != buf2[j]) {
                match = false;
                break;
            }
        }
        
        return match;
    }

    private boolean colMatch(int[] buf1, int col1, int[] buf2, int col2, int width) {
        boolean match = true;
        
        for (int i = col1, j = col2;
                i < buf1.length && j < buf2.length;
                i = i + width, j = j + width) {
            if (buf1[i] != buf2[j]) {
                match = false;
                break;
            }
        }
        
        return match;
    }
    
    private int[] subBuffer(int[] buffer, int width, int x1, int y1, int x2, int y2) {
    	
		int w1 = x2 - x1 + 1;
		int h1 = y2 - y1 + 1;
		
		int[] newBuffer = new int[w1 * h1];
		
		for (int y = y1; y < h1; y++) {
			int srcPos = y * width + x1;
			int destPos = (y - y1) * w1;
			int length = w1;
			
			try {
				System.arraycopy(buffer, srcPos, newBuffer, destPos, length);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				System.err.println("srcPos: " + srcPos);
				System.err.println("destPos: " + destPos);
				System.err.println("length: " + length);
			}

		}
		return newBuffer;
	}
    
}
