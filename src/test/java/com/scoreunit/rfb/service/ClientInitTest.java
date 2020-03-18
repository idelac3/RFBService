package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class ClientInitTest {

	@Test
	public void test() throws IOException {
		
		final byte[] buf = new byte[] {
				0,  }; // shared desktop = false
		
		final InputStream inputStream = new ByteArrayInputStream(buf);
		ClientInit clientInit = ClientInit.readClientInit(inputStream);
		
		assertFalse(clientInit.sharedDesktop);
	}

}
