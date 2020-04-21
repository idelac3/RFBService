package com.scoreunit.rfb.encoding;

import java.nio.ByteBuffer;

import com.scoreunit.rfb.image.TrueColorImage;
import com.scoreunit.rfb.service.SetPixelFormat;

/**
 * Raw encoder just aligns bytes within 32-bit true color image of screen.
 * <p>
 * This encoder is stateless.
 *  
 * @author igor.delac@gmail.com
 *
 */
public class RawEncoder implements EncodingInterface {

	/**
	 * Raw encoding will just adjust each 32-bit ARGB pixel to
	 * BGRA pixel compatible with most VNC client implementations.
	 */
	@Override
	public byte[] encode(final TrueColorImage image, final SetPixelFormat pixelFormat) {

		//
		// Take pixel format information.
		//
		
		final int imageLength = image.raw.length;
		final int[] raw = image.raw;
		
		// Only bits per pixel are used here. Possible values are: 8, 16 or 32.
		final byte bitsPerPixel = pixelFormat.bitsPerPixel;
		
		final byte[] result = new byte[bitsPerPixel / 8 * imageLength];
		
		// No need to set byte order, since PixelTransform method will
		// set proper order.
		final ByteBuffer buffer = ByteBuffer.wrap(result);
		
		for (int pixel : raw) {

			// Build new pixel according to given pixel format.
			int newPixel = PixelTransform.transform(pixel, pixelFormat);
			
			// Write new pixel to result buffer.
			// Only 8-bit, 16-bit and 32-bit pixel formats are supported.
			if (bitsPerPixel == 8) {
				
				buffer.put((byte) newPixel);
			}
			else if (bitsPerPixel == 16) {
				
				buffer.putShort((short) newPixel);
			}
			else if (bitsPerPixel == 32) {
				
				buffer.putInt(newPixel);
			}
		}
		
		return result;
	}

	@Override
	public int getType() {

		return Encodings.RAW;
	}

}
