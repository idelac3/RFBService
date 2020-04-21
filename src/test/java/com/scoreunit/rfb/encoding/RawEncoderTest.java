package com.scoreunit.rfb.encoding;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.junit.Test;

import com.scoreunit.rfb.image.TrueColorImage;
import com.scoreunit.rfb.service.SetPixelFormat;

public class RawEncoderTest {

	@Test
	public void test_01_encodeDecode() throws IOException {
		
		final RawEncoder encoder = new RawEncoder();
		assertEquals(Encodings.RAW, encoder.getType());
		
		final int width = 640, height = 480;
		final int size = width * height;
		
		final int[] raw = new int[size];

		// Create test image with random data.
		for (int i = 0 ; i < size ; i++) {
		
			// Fill with random data.
			raw[i] = i + (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
		}
		
		final SetPixelFormat pixelFormat = SetPixelFormat.default32bit();
		
		final byte[] encodedImage = encoder.encode(new TrueColorImage(raw, width, height), pixelFormat);
		
		// Try to decode and compare with original.

		final DataInputStream in = new DataInputStream(new ByteArrayInputStream(encodedImage));
		
		final TrueColorImage img = new TrueColorImage(new int[size], width, height);
		
		int index = 0;
		while (in.available() > 0) {
			
			int pixel = in.readInt();
					
			// reconstruct decoded image in TrueColorImage buffer.
			img.raw[index] = pixel;
			
			index++;
		}
		
		// Do final pixel-by-pixel comparison.
		final int[] colorImageBuffer = img.raw;
		assertEquals(raw.length, colorImageBuffer.length);
		for (int pixelIndex = 0 ; pixelIndex < raw.length ; pixelIndex++) {
			
			int originalPixel = Integer.reverseBytes(raw[pixelIndex] & 0x00FFFFFF);
			int decodedPixel = colorImageBuffer[pixelIndex];
			
			final String msg = (String.format("Original pixel value: '%x', expected value '%x'.", originalPixel, decodedPixel));
			
			assertEquals(msg, originalPixel, decodedPixel);
		}
	}

}
