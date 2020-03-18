package com.scoreunit.rfb.image;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class TrueColorImageTest {

	@Test
	public void test_01_basics() {
		
		int width = 640, height = 480;
		int size = width * height;
		
		int[] raw = new int[size];
		Arrays.fill(raw, 1234);
		
		final TrueColorImage img = new TrueColorImage(raw, width, height);
		assertEquals(width, img.width);
		assertEquals(height, img.height);
		assertArrayEquals(raw, img.raw);
		
		assertEquals(1234, img.getPixel(1, 1));
		
		img.setPixel(2, 2, 2345);
		assertEquals(2345, img.getPixel(2, 2));
		assertEquals(2345, img.raw[2 + 2 * width]);
		
		assertEquals(new TrueColorImage(raw, width, height),
				new TrueColorImage(raw, width, height));
		
		assertNotNull(img.toString());
		
		assertEquals(img.hashCode(), (new TrueColorImage(raw, width, height)).hashCode());
		
		assertTrue(img.equals(img));
		assertTrue(img.equals(null) == false);
		assertTrue(img.equals("123") == false);
		assertTrue(img.equals(new TrueColorImage(raw, width, height + 1)) == false);
		assertTrue(img.equals(new TrueColorImage(raw, width + 1, height)) == false);
		assertTrue(img.equals(new TrueColorImage(new int[size], width, height)) == false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_02_negative() {
		
		
		int width = 640, height = 480;
		int size = width * height;
		
		int[] raw = new int[size];
		Arrays.fill(raw, 1234);
		
		final TrueColorImage img = new TrueColorImage(raw, width, height);

		img.setPixel(width + 3, height - 3, 1234);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_03_negative() {
		
		int width = 640, height = 480;
		int size = width * height;
		
		int[] raw = new int[size];
		Arrays.fill(raw, 1234);
		
		final TrueColorImage img = new TrueColorImage(raw, width, height);

		img.getPixel(width - 3, height + 3);
	}
}
