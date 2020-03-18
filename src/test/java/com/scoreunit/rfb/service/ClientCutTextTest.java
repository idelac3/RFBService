package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class ClientCutTextTest {

	@Test
	public void test() throws IOException {
		
		final byte[] buf = new byte[] {
				0, 0, 0 // padding
				, 0, 0, 0, 5 // length of value
				, 'a', 'b', 'c', 'd', 'e', }; // value
		
		final InputStream inputStream = new ByteArrayInputStream(buf);
		ClientCutText clientCutText = ClientCutText.read(inputStream);
		
		assertEquals("abcde", clientCutText.text);		
	}

}
