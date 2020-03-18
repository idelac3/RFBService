package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

public class SetEncodingsTest {

	@Test
	public void test() throws IOException {
		
		final byte[] buf = {
				0, 3,	// number of encodings
				0, 0, 0, 1,	// first encoding
				0, 0, 0, 2,	// second encoding
				0, 0, 0, 3,	// third encoding
		};
		
		final SetEncodings encodings = SetEncodings.read(
				new ByteArrayInputStream(buf));
		
		assertArrayEquals(new int[] {1, 2, 3}, encodings.encodingType);
	}

}
