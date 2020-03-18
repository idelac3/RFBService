package com.scoreunit.rfb.encoding;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.junit.Test;

import com.scoreunit.rfb.image.TrueColorImage;
import com.scoreunit.rfb.service.SetPixelFormat;

public class HextileEncoderTest {

	@Test
	public void test_01_encodeDecode() throws IOException {
		
		final HextileEncoder encoder = new HextileEncoder();
		assertEquals(Encodings.HEXTILE, encoder.getType());
		
		final int width = 640, height = 480;
		final int size = width * height;
		
		final int[] image = new int[size];

		// Create test image with random data.
		for (int i = 0 ; i < size ; i++) {
		
			// Fill with random data.
			image[i] = i + (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
		}
		
		// Start from second tile. Leave first tile with single color ( 0x00 0x00 0x00 )
		//  to test background specific hextile encoding.
		for (int y = 0 ; y < 16 ; y++) {
			for (int x = 0 ; x < 16 ; x++) {
				
				image[x + 16 * y] = 0x00;
			}
		}
		
		final SetPixelFormat pixelFormat = SetPixelFormat.default32bit();
		
		final byte[] encodedImage = encoder.encode(image, width, height, pixelFormat);
		
		// Try to decode and compare with original.

		final DataInputStream in = new DataInputStream(new ByteArrayInputStream(encodedImage));
		
		TrueColorImage img = new TrueColorImage(new int[size], width, height);
		
		int tileNo = 0, xOffset = 0, yOffset = 0;
		
		while (in.available() > 0) {
			
			int subencodingMask = in.read();
						
			if (subencodingMask == HextileEncoder.MASK_BACKGROUND_SPECIFIED) {
				
				int singlePixelValue = in.readInt();

				// reconstruct tile in byte buffer.
				
				for (int y = 0 ; y < 16 ; y++) {
					for (int x = 0 ; x < 16 ; x++) {
										
						img.setPixel(xOffset + x, yOffset + y, singlePixelValue);
					}
				}				
			}
			else if (subencodingMask == HextileEncoder.MASK_RAW) {

				// reconstruct tile in byte buffer.
				
				for (int y = 0 ; y < 16 ; y++) {
					for (int x = 0 ; x < 16 ; x++) {
					
						int singlePixelValue = in.readInt(); // read pixel-by-pixel.
						
						img.setPixel(xOffset + x, yOffset + y, singlePixelValue);
					}
				}
			}
						
			xOffset = xOffset + 16;
			
			if (xOffset >= width) {
				
				xOffset = 0;
				yOffset = yOffset + 16;
			}
			
			tileNo++;
		}
		
		// Each tile is 16x16 pixel, in size. Calculate how many tiles were decoded.
		assertEquals(size / (16 * 16), tileNo);
		
		// Do final pixel-by-pixel comparison.
		final int[] colorImageBuffer = img.raw;
		assertEquals(image.length, colorImageBuffer.length);
		for (int pixelIndex = 0 ; pixelIndex < image.length ; pixelIndex++) {
			
			int originalPixel = Integer.reverseBytes(image[pixelIndex] & 0x00FFFFFF);
			int decodedPixel = colorImageBuffer[pixelIndex];
			
			final String msg = (String.format("Original pixel value: '%x', expected value '%x'.", originalPixel, decodedPixel));
			
			assertEquals(msg, originalPixel, decodedPixel);
		}
	}

}
