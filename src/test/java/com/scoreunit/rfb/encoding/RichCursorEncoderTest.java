package com.scoreunit.rfb.encoding;

import static org.junit.Assert.*;

import org.junit.Test;

import com.scoreunit.rfb.service.SetPixelFormat;

public class RichCursorEncoderTest {

	@Test
	public void test_01_basic() {
		
		
		final RichCursorEncoder encoder = new RichCursorEncoder();
		assertEquals(Encodings.RICH_CURSOR, encoder.getType());
		
		int[] image = null;
		int width = 0, height = 0;
		SetPixelFormat pixelFormat = SetPixelFormat.default32bit();
		
		assertNotNull(encoder.encode(image, width, height, pixelFormat));
	}

}
