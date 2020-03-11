package com.scoreunit.rfb.encoding;

import java.nio.ByteOrder;

import com.scoreunit.rfb.screen.ScreenCapture;
import com.scoreunit.rfb.service.SetPixelFormat;

/**
 * Helper class to transform pixel from source image of screen
 * into suitable format advertised by VNC client.
 * <p>
 * Note that source image is usually 32-bit ARGB image with
 * little-endian byte order ({@link ByteOrder#LITTLE_ENDIAN}).
 * <p>
 * 
 * @author igor.delac@gmail.com
 *
 */
public class PixelTransform {

	/**
	 * Convert given 32-bit pixel into suitable pixel (8-bit, 16-bit, or 32-bit)
	 * according to given pixel format argument.
	 * 
	 * @param pixel			-		pixel, as obtained from {@link ScreenCapture} image source
	 * @param pixelFormat	-		format as received from VNC client, see {@link SetPixelFormat} message
	 * 
	 * @return	new pixel value, transformed
	 */
	public static int transform(final int pixel,
			final SetPixelFormat pixelFormat) {
		
		//
		// Read desired pixel format.
		//
		
		byte bitsPerPixel = pixelFormat.bitsPerPixel, depth = pixelFormat.depth;		
		byte bigEndianFlag = pixelFormat.bigEndianFlag, trueColorFlag = pixelFormat.trueColorFlag;
		
		short redMax = pixelFormat.redMax, greenMax = pixelFormat.greenMax, blueMax = pixelFormat.blueMax;
		byte redShift = pixelFormat.redShift, greenShift = pixelFormat.greenShift, blueShift = pixelFormat.blueShift;

		// Pixel byte order from source image is in following order:
		// [ A, R, G, B ]
	
		// Extract R G B components of pixel from 32-bit source image.		
		int	red   = (pixel >> 16) & 0xFF;
		int	green = (pixel >>  8) & 0xFF;
		int	blue  = (pixel >>  0) & 0xFF;
		
		// Translate each component value (0..255) to
		// (0..redMax), (0..greenMax) and (0..blueMax).
		int redDivider = 256 / (redMax + 1)
				, greenDivider = 256 / (greenMax + 1)
				, blueDivider = 256 / (blueMax + 1);
		
		red = red / redDivider;
		green = green / greenDivider;
		blue = blue / blueDivider;
				
		// Build new pixel according to given pixel format.
		int newPixel = (red << redShift)
				| (green << greenShift)
				| (blue << blueShift);
	
		// Make proper byte order, eg. for non-big endian systems.
		if (bigEndianFlag == 0) {
		
			// Now check if pixel is 2 or 4 bytes long.
			if (bitsPerPixel > 8) {
				
				newPixel = Integer.reverseBytes(newPixel);
			}
		}
		
		return newPixel;
	}
}
