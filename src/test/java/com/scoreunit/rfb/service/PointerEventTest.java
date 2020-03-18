package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class PointerEventTest {


	@Test
	public void test() throws IOException {
		
		final byte[] buf = new byte[] {
				1 // button mask
				, 0, 0 // x
				, 5, 5 // y
				}; 
		
		final InputStream inputStream = new ByteArrayInputStream(buf);
		PointerEvent request = PointerEvent.read(inputStream);
		
		assertEquals(1, request.buttonMask);
		assertEquals(0, request.xPos);
		assertEquals(0x0505, request.yPos);
	}

}
