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
		
		
		int x1 = 0, y1 = 0, x2 = 0, y2 = 0;

		for (int col = 4; col < width - 5; col++) {
			/*
			 * Look for different columns ...
			 */
			if (! colMatch(previousImageBuffer, col, colorImageBuffer, col, width)) {																
				x1 = col;
				break;
			}
		}
		
		for (int row = 4; row < height - 5; row++) {
			/*
			 * Look for different rows ...
			 */
			if (! rowMatch(previousImageBuffer, row, colorImageBuffer, row, width)) {																
				y1 = row;
				break;
			}
		}

		for (int col = width - 5; col >= 4; col--) {
			/*
			 * Look for different columns ...
			 */
			if (! colMatch(previousImageBuffer, col, colorImageBuffer, col, width)) {																
				x2 = col;
				break;
			}
		}
		
		for (int row = height - 5; row >= 4; row--) {
			/*
			 * Look for different rows ...
			 */
			if (! rowMatch(previousImageBuffer, row, colorImageBuffer, row, width)) {																
				y2 = row;
				break;
			}
		}
		
		if (x1 < x2 && y1 < y2) {
			
			int[] delta = subBuffer(colorImageBuffer, width, x1, y1, x2, y2);
		
			deltaX = x1;
			deltaY = y1;
			deltaWidth = x2 - x1 + 1;
			deltaHeight = y2 - y1 + 1;
			
			return delta;
			
		}
		else {

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
		
		for (int y = y1; y <= h1; y++) {
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
