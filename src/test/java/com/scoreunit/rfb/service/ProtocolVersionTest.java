package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class ProtocolVersionTest {

	@Test
	public void test_01() throws IOException {
		
		final byte[] buf = "RFB 003.008\n".getBytes();
		
		final InputStream inputStream = new ByteArrayInputStream(buf);
		ProtocolVersion request = ProtocolVersion.readProtocolVersion(inputStream);
		
		assertEquals(3, request.major);
		assertEquals(8, request.minor);
	}

	@Test(expected = IOException.class)
	public void test_02() throws IOException {
		
		final byte[] buf = "RFB 004.004\n".getBytes();
		
		final InputStream inputStream = new ByteArrayInputStream(buf);
		ProtocolVersion request = ProtocolVersion.readProtocolVersion(inputStream);
		
		assertEquals(3, request.major);
		assertEquals(8, request.minor);
	}
}
