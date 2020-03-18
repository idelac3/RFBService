package com.scoreunit.rfb.screen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds actual screen coordinates, or a rectangle.
 * <p>
 * Used when region of screen image is captured.
 * <p>
 * Note this object is immutable.
 *  
 * @author igor.delac@gmail.com
 *
 */
public class ScreenClip {
	
	public final static Logger log = LoggerFactory.getLogger(ScreenClip.class);

	/**
	 * Read given top-left starting position of rectangle <i>(xPos, yPos)</i>,
	 * and dimension <i>(width, height)</i>, all in pixel. 
	 */
	public final int xPos, yPos, width, height;

	/**
	 * Create default screen clip, which is full screen size and offset is set to (0, 0).
	 */
	public ScreenClip() {
		
		this.xPos = 0;
		this.yPos = 0;
		this.width = ScreenCapture.getScreenWidth();
		this.height = ScreenCapture.getScreenHeight();		
	}
	
	/**
	 * Create new screen image clip.
	 * 
	 * @param xPos		-	top-left starting position, x-axis, in pixel
	 * @param yPos		-	top-left starting position, y-axis, in pixel
	 * @param width		-	width, dimension <i>(width, height)</i>, in pixel
	 * @param height	-	height, dimension <i>(width, height)</i>, in pixel
	 */
	public ScreenClip(final int xPos, final int yPos, final int width, final int height) {

		if (validate(xPos, yPos, width, height) == true) {
			
			this.xPos = xPos;
			this.yPos = yPos;
			this.width = width;
			this.height = height;
			
			return;
		}
		
		this.xPos = 0;
		this.yPos = 0;
		this.width = ScreenCapture.getScreenWidth();
		this.height = ScreenCapture.getScreenHeight();
	}

	/**
	 * Check if given offset <i>(xPos, yPos)</i> and dimension <i>(width, height)</i> are correct
	 * and possible on host display system.
	 * 
	 * @param xPos		-	offset <i>(xPos, yPos)</i>, in pixel
	 * @param yPos		-	offset <i>(xPos, yPos)</i>, in pixel
	 * @param width		-	dimension <i>(width, height)</i>, in pixel
	 * @param height	-	dimension <i>(width, height)</i>, in pixel
	 * 
	 * @return	returns true if given values are correct, otherwise false
	 */
	public boolean validate(final int xPos, final int yPos, final int width, final int height) {
		
		//
		// Basic check if screen region is not outside 
		//  available screen image.
		//
		
		if (xPos < 0 || xPos > ScreenCapture.getScreenWidth()) {
			
			log.error(String.format("Screen clip (x ,y) offset is set to wrong value: ('%d', '%d'). Screen size is: ('%d', '%d')."
					, xPos, yPos, ScreenCapture.getScreenWidth(), ScreenCapture.getScreenHeight()));
			
			return false;
		}
		
		if (yPos < 0 || yPos > ScreenCapture.getScreenHeight()) {
			
			log.error(String.format("Screen clip (x ,y) offset is set to wrong value: ('%d', '%d'). Screen size is: ('%d', '%d')."
					, xPos, yPos, ScreenCapture.getScreenWidth(), ScreenCapture.getScreenHeight()));
			
			return false;
		}
		
		if (width < 0 || width > ScreenCapture.getScreenWidth()) {
			
			log.error(String.format("Screen clip dimension (width, height) is set to wrong value: ('%d', '%d'). Screen size is: ('%d', '%d')."
					, width, height, ScreenCapture.getScreenWidth(), ScreenCapture.getScreenHeight()));
			
			return false;
		}
		
		if (height < 0 || height > ScreenCapture.getScreenHeight()) {
			
			log.error(String.format("Screen clip dimension (width, height) is set to wrong value: ('%d', '%d'). Screen size is: ('%d', '%d')."
					, width, height, ScreenCapture.getScreenWidth(), ScreenCapture.getScreenHeight()));
			
			return false;
		}
		
		if (xPos + width > ScreenCapture.getScreenWidth()) {
		
			log.error(String.format("Screen clip dimension (width, height) is set to wrong value: ('%d', '%d'). Screen size is: ('%d', '%d')."
					, width, height, ScreenCapture.getScreenWidth(), ScreenCapture.getScreenHeight()));
			
			return false;
		}

		if (yPos + height > ScreenCapture.getScreenHeight()) {
			
			log.error(String.format("Screen clip dimension (width, height) is set to wrong value: ('%d', '%d'). Screen size is: ('%d', '%d')."
					, width, height, ScreenCapture.getScreenWidth(), ScreenCapture.getScreenHeight()));
			
			return false;
		}
			
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		result = prime * result + xPos;
		result = prime * result + yPos;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScreenClip other = (ScreenClip) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		if (xPos != other.xPos)
			return false;
		if (yPos != other.yPos)
			return false;
		return true;
	}
}
