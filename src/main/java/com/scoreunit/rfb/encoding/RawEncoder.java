package com.scoreunit.rfb.encoding;

import java.nio.ByteBuffer;

public class RawEncoder implements EncodingInterface {

	/**
	 * Raw encoding will just adjust each 32-bit ARGB pixel to
	 * RGB0 pixel compatibile with most VNC client implementations.
	 */
	@Override
	public byte[] encode(final int[] image
			, final int width, final int height) {

		final byte[] result = new byte[4 * image.length];
		
		final ByteBuffer buffer = ByteBuffer.wrap(result);
		
		for (int pixel : image) {

			pixel = pixel << 8; // shift ARGB into RGB0.
			
			buffer.putInt(pixel);
		}
		
		return result;
	}

}
