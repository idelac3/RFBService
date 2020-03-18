package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import org.junit.Test;

import com.scoreunit.rfb.screen.ScreenClip;

public class RFBConfigTest {

	@Test
	public void test() {
		
		final RFBConfig config = new RFBConfig();
		config.setPassword("password");
		config.setPreferredEncodings(new int[]{1,2,3,4});
		config.setScreenClip(new ScreenClip((short) 1, (short) 2, (short) 3, (short) 4));
		
		assertEquals("password", config.getPassword());
		assertArrayEquals(new int[]{1,2,3,4}, config.getPreferredEncodings());
		assertEquals(new ScreenClip((short) 1, (short) 2, (short) 3, (short) 4)
				, config.getScreenClip());
	}

}
