package com.scoreunit.rfb.screen;

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

	/**
	 * Read given top-left starting position of rectangle <i>(xPos, yPos)</i>,
	 * and dimension <i>(width, height)</i>, all in pixel. 
	 */
	public final short xPos, yPos, width, height;

	/**
	 * Create new screen image clip.
	 * 
	 * @param xPos		-	top-left starting position, x-axis, in pixel
	 * @param yPos		-	top-left starting position, y-axis, in pixel
	 * @param width		-	width, dimension <i>(width, height)</i>, in pixel
	 * @param height	-	height, dimension <i>(width, height)</i>, in pixel
	 */
	public ScreenClip(short xPos, short yPos, short width, short height) {

		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
	}
	
	
}
