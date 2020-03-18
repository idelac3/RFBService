package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class SecurityTypesTest {

	@Test
	public void test_01_read() throws IOException {
		
		final byte[] buf = {
				1	//  None
		};
		
		final InputStream inputStream = new ByteArrayInputStream(buf);
		SecurityTypes request = SecurityTypes.read(inputStream);
		
		assertEquals(1, request.securityType);
	}

	@Test
	public void test_02_securirtyResult() throws IOException {
		
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		SecurityTypes.sendSecurityResult(out, null); // result = OK.
		assertArrayEquals(new byte[] {0, 0, 0, 0}, out.toByteArray());

		out.reset();
		
		SecurityTypes.sendSecurityResult(out, "bad password"); // result = "bad password".
		assertArrayEquals(new byte[] {
				0, 0, 0, 1		// failure result code.
				, 0, 0, 0, 12		// message length.
				, 'b', 'a', 'd', ' '	// "bad "
				, 'p', 'a', 's', 's'	// "pass"
				, 'w', 'o', 'r', 'd'	// "word"
		}, out.toByteArray()); 
	}
}
