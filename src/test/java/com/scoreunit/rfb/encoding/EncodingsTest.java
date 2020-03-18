package com.scoreunit.rfb.encoding;

import static org.junit.Assert.*;

import org.junit.Test;

public class EncodingsTest {

	@Test
	public void test_01_newInstance() {
		
		final int[] encodings = {Encodings.RAW, Encodings.HEXTILE, Encodings.ZLIB, Encodings.RICH_CURSOR};
		
		for (final int encodingType : encodings) {
		
			assertNotNull(Encodings.newInstance(encodingType));
		}
		
		assertNull(Encodings.newInstance(1234)); // does not exist 1234 encoding type.
	}

}
