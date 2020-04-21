package com.scoreunit.rfb.encoding;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.junit.Test;

import com.scoreunit.rfb.image.TrueColorImage;
import com.scoreunit.rfb.service.SetPixelFormat;

public class ZlibEncoderTest {

	@Test
	public void test_01_encodeDecode() throws IOException, DataFormatException {
		
		final ZlibEncoder encoder = new ZlibEncoder();
		assertEquals(Encodings.ZLIB, encoder.getType());
		
		final int width = 640, height = 480;
		final int size = width * height;
		
		final int[] image = new int[size];

		// Create test image with random data.
		for (int i = 0 ; i < size ; i++) {
		
			// Fill with random data.
			image[i] = i + (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
		}
		
		final SetPixelFormat pixelFormat = SetPixelFormat.default32bit();
		
		final byte[] encodedImage = encoder.encode(new TrueColorImage(image, width, height), pixelFormat);
		
		// Try to decode and compare with original.
		final byte[] decodedImage = new byte[size * 4];
		
		final Inflater inflater = new Inflater();
		inflater.setInput(encodedImage, 4, encodedImage.length - 4); // Length is stored in first 4 bytes.

		int len = inflater.inflate(decodedImage);
		assertEquals(size * 4, len);
		
		int[] colorImageBuffer = new int[size];
		ByteBuffer buff = ByteBuffer.wrap(decodedImage);
		int index = 0;
		while (buff.remaining() > 0) {
		
			colorImageBuffer[index] = buff.getInt();
			
			index++;
		}
		
		// Do final pixel-by-pixel comparison.
		assertEquals(image.length, colorImageBuffer.length);
		for (int pixelIndex = 0 ; pixelIndex < image.length ; pixelIndex++) {
			
			int originalPixel = Integer.reverseBytes(image[pixelIndex] & 0x00FFFFFF);
			int decodedPixel = colorImageBuffer[pixelIndex];
			
			final String msg = (String.format("Original pixel [%d] value: '%x', expected value '%x'.", pixelIndex, originalPixel, decodedPixel));
			
			assertEquals(msg, originalPixel, decodedPixel);
		}
	}

}
