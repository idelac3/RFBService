package com.scoreunit.rfb.encoding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.scoreunit.rfb.service.SetPixelFormat;

public class RichCursorEncoderTest {

	@Test
	public void test_01_basic() {
		
		
		final RichCursorEncoder encoder = new RichCursorEncoder();
		assertEquals(Encodings.RICH_CURSOR, encoder.getType());
		
		SetPixelFormat pixelFormat = SetPixelFormat.default32bit();
		
		assertNotNull(encoder.encode(null, pixelFormat));
	}

}
