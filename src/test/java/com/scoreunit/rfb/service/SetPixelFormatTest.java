package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class SetPixelFormatTest {

	@Test
	public void test_00_readMethod() throws IOException {

		final byte[] buf = {
				8,		// bits per pixel
				8,		// depth
				0,		// big endian
				1,		// true color
				0, 3,	// red max
				0, 3,	// green max
				0, 3,	// blue max
				4,		// red shift
				2,		// green shift
				0,		// blue shift
				0, 0, 0, // padding  
		};
		
		final ByteArrayInputStream in = new ByteArrayInputStream(buf);
		final SetPixelFormat pixelFormat = SetPixelFormat.read(in);
		
		assertEquals(8, pixelFormat.bitsPerPixel);
		assertEquals(8, pixelFormat.depth);
		assertEquals(0, pixelFormat.bigEndianFlag);
		assertEquals(1, pixelFormat.trueColorFlag);
		assertEquals(3, pixelFormat.redMax);
		assertEquals(3, pixelFormat.greenMax);
		assertEquals(3, pixelFormat.blueMax);
		assertEquals(4, pixelFormat.redShift);
		assertEquals(2, pixelFormat.greenShift);
		assertEquals(0, pixelFormat.blueShift);
	}
	
	@Test
	public void test_01_emptyConstructor() throws IOException {
	
		final SetPixelFormat pixelFormat = new SetPixelFormat();
		
		assertEquals(32, pixelFormat.bitsPerPixel);
		assertEquals(32, pixelFormat.depth);
		assertEquals(0, pixelFormat.bigEndianFlag);
		assertEquals(1, pixelFormat.trueColorFlag);
		assertEquals(255, pixelFormat.redMax);
		assertEquals(255, pixelFormat.greenMax);
		assertEquals(255, pixelFormat.blueMax);
		assertEquals(16, pixelFormat.redShift);
		assertEquals(8, pixelFormat.greenShift);
		assertEquals(0, pixelFormat.blueShift);
	}
	
	@Test
	public void test_02_definedConstructor() throws IOException {
	
		final SetPixelFormat pixelFormat = new SetPixelFormat(
				(byte) 32,
				(byte) 32,
				(byte) 0,
				(byte) 1,
				(short) 255, (short) 255, (short) 255,
				(byte) 16, (byte) 8, (byte) 0				
				);
		
		assertEquals(32, pixelFormat.bitsPerPixel);
		assertEquals(32, pixelFormat.depth);
		assertEquals(0, pixelFormat.bigEndianFlag);
		assertEquals(1, pixelFormat.trueColorFlag);
		assertEquals(255, pixelFormat.redMax);
		assertEquals(255, pixelFormat.greenMax);
		assertEquals(255, pixelFormat.blueMax);
		assertEquals(16, pixelFormat.redShift);
		assertEquals(8, pixelFormat.greenShift);
		assertEquals(0, pixelFormat.blueShift);
	}
	
	@Test
	public void test_03_default32() throws IOException {
	
		final SetPixelFormat pixelFormat = SetPixelFormat.default32bit();
		
		assertEquals(32, pixelFormat.bitsPerPixel);
		assertEquals(24, pixelFormat.depth);
		assertEquals(0, pixelFormat.bigEndianFlag);
		assertEquals(1, pixelFormat.trueColorFlag);
		assertEquals(255, pixelFormat.redMax);
		assertEquals(255, pixelFormat.greenMax);
		assertEquals(255, pixelFormat.blueMax);
		assertEquals(16, pixelFormat.redShift);
		assertEquals(8, pixelFormat.greenShift);
		assertEquals(0, pixelFormat.blueShift);
	}
	
	@Test
	public void test_04_write() throws IOException {
	
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		SetPixelFormat.write(out, SetPixelFormat.default32bit());
		
		final SetPixelFormat pixelFormat = SetPixelFormat.read(new ByteArrayInputStream(out.toByteArray()));
		assertEquals(32, pixelFormat.bitsPerPixel);
		assertEquals(24, pixelFormat.depth);
		assertEquals(0, pixelFormat.bigEndianFlag);
		assertEquals(1, pixelFormat.trueColorFlag);
		assertEquals(255, pixelFormat.redMax);
		assertEquals(255, pixelFormat.greenMax);
		assertEquals(255, pixelFormat.blueMax);
		assertEquals(16, pixelFormat.redShift);
		assertEquals(8, pixelFormat.greenShift);
		assertEquals(0, pixelFormat.blueShift);
	}
}
