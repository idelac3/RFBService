package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class KeyEventTest {

	@Test
	public void test() throws IOException {
		
		final byte[] buf = new byte[] {
				1 // down flag = true
				, 0, 0 // padding
				, -1, -1, 5, 5 // key scan code
				}; 
		
		final InputStream inputStream = new ByteArrayInputStream(buf);
		KeyEvent request = KeyEvent.read(inputStream);
		
		assertEquals(1, request.downFlag);
		assertEquals(0xFFFF0505, request.key);
	}

}
