package com.scoreunit.rfb.encoding;

import static org.junit.Assert.*;

import org.junit.Test;

import com.scoreunit.rfb.service.SetPixelFormat;

public class PixelTransformTest {

	@Test
	public void test_01_transform_default32() {		

		final SetPixelFormat pixelFormat = SetPixelFormat.default32bit();
		final int pixel = 0xAA112233; // Test pattern, [A R G B] format of pixel.
		int newPixel = PixelTransform.transform(pixel, pixelFormat);
		
		assertEquals(0x33221100, newPixel); // transformation should result in R G B components reversed.
	}

	@Test
	public void test_01_transform_8bit() {		

		final byte bitsPerPixel = 8, depth = 8;
		final byte bigEndianFlag = 0, trueColorFlag = 1;
		final short redMax = 3, greenMax = 3, blueMax = 3;
		final byte redShift = 4, greenShift = 2, blueShift = 0;
		
		// Pixel format where only 1 byte is pixel. First two bits are for blue, next two for green color, ... etc.
		final SetPixelFormat pixelFormat = new SetPixelFormat(bitsPerPixel, depth
				, bigEndianFlag, trueColorFlag
				, redMax, greenMax, blueMax
				, redShift, greenShift, blueShift);
		
		final int pixel = 0xAA000000 // Test pattern, [A R G B] format of pixel.
				| (240 << 16)	// will result in 0b11 for red component
				| (100 << 8)	// will result in 0b01 for green component
				| (50 << 0); 	// will result in 0b00 for blue component
		
		int newPixel = PixelTransform.transform(pixel, pixelFormat);
		
		// transformation should result in R G B components reduced to 8 bits.
		assertEquals(0b00110100, newPixel);
	}
}
