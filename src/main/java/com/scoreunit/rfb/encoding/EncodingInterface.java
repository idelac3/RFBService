package com.scoreunit.rfb.encoding;

import com.scoreunit.rfb.screen.ScreenCapture;
import com.scoreunit.rfb.service.SetPixelFormat;

/**
 * Every framebuffer encoding algo. should implement this inferface.
 * <p>
 * General rule is to accept 32-bit image in ARGB format, as obtained from
 * {@link ScreenCapture#getScreenshot(int, int, int, int)} method,
 * and encode it to byte array, specific to each encoder.
 * <p>
 * Eg. raw encoder will take each pixel of 32-bit ARGB image, and convert
 * to RGB0 pixels.
 * 
 * @author igor.delac@gmail.com
 *
 */
public interface EncodingInterface {

	/**
	 * Encode image, typically obtained from {@link ScreenCapture#getScreenshot()} method,
	 * and produce encoded data, eg. <i>raw, hextile, zrle, tight, ...</i>.
	 * 
	 * @param image		-	an 32-bit ARGB image, or part of image
	 * @param width		-	width in pixel
	 * @param height	-	height in pixel
	 * 
	 * @param pixelFormat		-	desired pixel format, since VNC client might request 8-bit pixel encoding, and source image is 32-bit
	 * 								this parameter will help encoder to properly determine pixel format and transformation
	 * 
	 * @return	encoded image, or part of image
	 */
	public byte[] encode(final int[] image, final int width, final int height, final SetPixelFormat pixelFormat);

	/**
	 * An encoding type value, eg. {@link Encodings#RAW}, {@link Encodings#HEXTILE}, etc.
	 * 
	 * @return	encoding type value, depends on encoding algo.
	 */
	public int getType();
}
