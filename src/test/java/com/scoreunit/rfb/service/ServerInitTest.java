package com.scoreunit.rfb.service;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;

public class ServerInitTest {


	@Test
	public void test() throws IOException {
		
		short width = 0x1234, height = 0x2345;
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		ServerInit.send(out, width, height);
		
		assertEquals(0x12, out.toByteArray()[0]);
		assertEquals(0x34, out.toByteArray()[1]);
		assertEquals(0x23, out.toByteArray()[2]);
		assertEquals(0x45, out.toByteArray()[3]);
	}

}
