package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.scoreunit.rfb.encoding.EncodingInterface;
import com.scoreunit.rfb.encoding.Encodings;
import com.scoreunit.rfb.encoding.HextileEncoder;
import com.scoreunit.rfb.encoding.RawEncoder;
import com.scoreunit.rfb.encoding.RichCursorEncoder;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SelectEncoderTest {

	@Test
	public void test_01_lastEncoderNotSet() {
		
		EncodingInterface lastEncoder = null;
		final int[] clientEncodings = new int[]{Encodings.ZRLE, Encodings.HEXTILE, Encodings.RAW}; // Typical list of VNC client program.
		final int[] preferredEncodings = null;
		
		lastEncoder = SelectEncoder.selectEncoder(lastEncoder, clientEncodings, preferredEncodings);
		
		assertNotNull(lastEncoder);
		assertTrue(lastEncoder instanceof HextileEncoder); // Hextile is first supported encoding in client list.
	}

	@Test
	public void test_02_lastEncoderSet() {
		
		EncodingInterface lastEncoder = new RawEncoder();
		final int[] clientEncodings = new int[]{Encodings.ZRLE, Encodings.HEXTILE, Encodings.RAW}; // Typical list of VNC client program.
		final int[] preferredEncodings = null;
		
		lastEncoder = SelectEncoder.selectEncoder(lastEncoder, clientEncodings, preferredEncodings);
		
		assertNotNull(lastEncoder);
		assertTrue(lastEncoder instanceof RawEncoder); // Raw encoder is reused, since client supports it.
	}
	
	@Test
	public void test_03_lastEncoderSetButUnsupported() {
		
		EncodingInterface lastEncoder = new RichCursorEncoder();
		final int[] clientEncodings = new int[]{Encodings.ZRLE, Encodings.HEXTILE, Encodings.RAW}; // Typical list of VNC client program.
		final int[] preferredEncodings = null;
		
		lastEncoder = SelectEncoder.selectEncoder(lastEncoder, clientEncodings, preferredEncodings);
		
		assertNotNull(lastEncoder);
		assertTrue(lastEncoder instanceof HextileEncoder); // First supported encoder by client.
	}
	
	@Test
	public void test_04_fallbackEncoder() {
		
		EncodingInterface lastEncoder = new RichCursorEncoder();
		final int[] clientEncodings = new int[]{Encodings.ZRLE}; // VNC client does not list any supported encoder.
		final int[] preferredEncodings = null;
		
		lastEncoder = SelectEncoder.selectEncoder(lastEncoder, clientEncodings, preferredEncodings);
		
		assertNotNull(lastEncoder);
		assertTrue(lastEncoder instanceof RawEncoder); // fall-back solution.
	}
	
	@Test
	public void test_05_preferRawEncoding() {
		
		EncodingInterface lastEncoder = new RichCursorEncoder();
		final int[] clientEncodings = new int[]{Encodings.ZRLE, Encodings.HEXTILE, Encodings.RAW}; // Typical list of VNC client program.
		final int[] preferredEncodings = new int[]{Encodings.ZLIB, Encodings.RAW}; // RFB service will prefer ZLIB and RAW encodings.
		
		lastEncoder = SelectEncoder.selectEncoder(lastEncoder, clientEncodings, preferredEncodings);
		
		assertNotNull(lastEncoder);
		assertTrue(lastEncoder instanceof RawEncoder); // First supported encoder by client.
	}
	
}
