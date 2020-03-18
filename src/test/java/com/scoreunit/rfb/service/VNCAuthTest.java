package com.scoreunit.rfb.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class VNCAuthTest {

	@Test
	public void test_01_wrongPassword() throws IOException {
	
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final ByteArrayInputStream in = new ByteArrayInputStream(new byte[16]);
		
		final String password = "blabla123";
		
		final VNCAuth auth = new VNCAuth(password);
		auth.sendChallenge(out);
		auth.readChallenge(in);
		
		assertFalse(auth.isValid());
	}

	@Test
	public void test_02_correctPassword() throws IOException {
		
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		final String password = "blabla123";
		
		final VNCAuth auth = new VNCAuth(password);
		auth.sendChallenge(out);
	
		final byte[] challengeResponse = DESCipher.enc(password, out.toByteArray());
		auth.readChallenge(new ByteArrayInputStream(challengeResponse));
		
		assertTrue(auth.isValid());
	}
}
