package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class FramebufferUpdateRequestTest {

	@Test
	public void test() throws IOException {
		
		final byte[] buf = new byte[] {
				0 // incremental = false
				, 0, 10 // x position
				, 0, 20 // y position
				, 0, 30 // width in pixel
				, 0, 40 // height in pixel
				}; 
		
		final InputStream inputStream = new ByteArrayInputStream(buf);
		FramebufferUpdateRequest request = FramebufferUpdateRequest.read(inputStream);
		
		assertEquals(0, request.incremental);
		assertEquals(10, request.xPosition);
		assertEquals(20, request.yPosition);
		assertEquals(30, request.width);
		assertEquals(40, request.height);
	}

}
