package com.scoreunit.rfb.screen;

import static org.junit.Assert.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScreenClipTest {

	@Test
	public void test_01_basic() {
		
		final int xPos = 1, yPos = 2, width = 3, height = 4;
		
		final ScreenClip clip = new ScreenClip(xPos, yPos, width, height);
		
		assertEquals(xPos, clip.xPos);
		assertEquals(yPos, clip.yPos);
		assertEquals(width, clip.width);
		assertEquals(height, clip.height);
		
		assertTrue( clip.validate(xPos, yPos, width, height) == true );
		
		assertEquals(new ScreenClip(xPos, yPos, width, height)
				, clip);
		assertEquals((new ScreenClip(xPos, yPos, width, height)).hashCode(),
				clip.hashCode());
		
		assertTrue( clip.equals(clip) );
		assertTrue( clip.equals(null) == false );
		assertTrue( clip.equals("abc") == false );
		assertTrue( clip.equals(new ScreenClip(xPos + 1, yPos, width, height)) == false );
		assertTrue( clip.equals(new ScreenClip(xPos, yPos + 1, width, height)) == false );
		assertTrue( clip.equals(new ScreenClip(xPos, yPos, width + 1, height)) == false );
		assertTrue( clip.equals(new ScreenClip(xPos, yPos, width, height + 1)) == false );
	}

	@Test
	public void test_02_badOffsetX() {
		
		final int xPos = -1, yPos = 2, width = 3, height = 4;
		
		final ScreenClip clip = new ScreenClip(xPos, yPos, width, height);
		
		assertEquals(new ScreenClip(), clip);
		
		assertTrue( clip.validate(xPos, yPos, width, height) == false );
	}
	
	@Test
	public void test_03_badOffsetX() {
		
		final int xPos = Integer.MAX_VALUE, yPos = 2, width = 3, height = 4;
		
		final ScreenClip clip = new ScreenClip(xPos, yPos, width, height);
		
		assertEquals(new ScreenClip(), clip);
		
		assertTrue( clip.validate(xPos, yPos, width, height) == false );
	}
	
	@Test
	public void test_04_badOffsetY() {
		
		final int xPos = 1, yPos = -2, width = 3, height = 4;
		
		final ScreenClip clip = new ScreenClip(xPos, yPos, width, height);
		
		assertEquals(new ScreenClip(), clip);
		
		assertTrue( clip.validate(xPos, yPos, width, height) == false );
	}
	
	@Test
	public void test_05_badOffsetY() {
		
		final int xPos = 2, yPos = Integer.MAX_VALUE, width = 3, height = 4;
		
		final ScreenClip clip = new ScreenClip(xPos, yPos, width, height);
		
		assertEquals(new ScreenClip(), clip);
		
		assertTrue( clip.validate(xPos, yPos, width, height) == false );
	}
	
	@Test
	public void test_06_badWidth() {
		
		final int xPos = 1, yPos = 2, width = -3, height = 4;
		
		final ScreenClip clip = new ScreenClip(xPos, yPos, width, height);
		
		assertEquals(new ScreenClip(), clip);
		
		assertTrue( clip.validate(xPos, yPos, width, height) == false );
	}
	
	@Test
	public void test_07_badWidth() {
		
		final int xPos = 0, yPos = 0, width = Integer.MAX_VALUE, height = 4;
		
		final ScreenClip clip = new ScreenClip(xPos, yPos, width, height);
		
		assertEquals(new ScreenClip(), clip);
		
		assertTrue( clip.validate(xPos, yPos, width, height) == false );
	}
	
	
	@Test
	public void test_08_badHeight() {
		
		final int xPos = 1, yPos = 2, width = 33, height = -4;
		
		final ScreenClip clip = new ScreenClip(xPos, yPos, width, height);
		
		assertEquals(new ScreenClip(), clip);
		
		assertTrue( clip.validate(xPos, yPos, width, height) == false );
	}
	
	@Test
	public void test_09_badHeight() {
		
		final int xPos = 0, yPos = 0, width = 22, height = Integer.MAX_VALUE;
		
		final ScreenClip clip = new ScreenClip(xPos, yPos, width, height);
		
		assertEquals(new ScreenClip(), clip);
		
		assertTrue( clip.validate(xPos, yPos, width, height) == false );
	}	
	
	@Test
	public void test_10_bad_OffsetX_Width() {
		
		final int xPos = ScreenCapture.getScreenWidth() / 2, yPos = 0, width = ScreenCapture.getScreenWidth(), height = ScreenCapture.getScreenHeight();
		
		final ScreenClip clip = new ScreenClip(xPos, yPos, width, height);
		
		assertEquals(new ScreenClip(), clip);
		
		assertTrue( clip.validate(xPos, yPos, width, height) == false );
	}
	
	@Test
	public void test_10_bad_OffsetY_Height() {
		
		final int xPos = 0, yPos = ScreenCapture.getScreenHeight() / 2, width = ScreenCapture.getScreenWidth(), height = ScreenCapture.getScreenHeight();
		
		final ScreenClip clip = new ScreenClip(xPos, yPos, width, height);
		
		assertEquals(new ScreenClip(), clip);
		
		assertTrue( clip.validate(xPos, yPos, width, height) == false );
	}
}
