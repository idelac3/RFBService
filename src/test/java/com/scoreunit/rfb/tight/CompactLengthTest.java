package com.scoreunit.rfb.tight;

import static org.junit.Assert.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CompactLengthTest {

	@Test
	public void test_01_under128() {
		
		byte[] result = CompactLength.calc(123);
		assertArrayEquals(new byte[]{123}, result);		
	}

	@Test
	public void test_02_between128and16383() {
		
		byte[] result = CompactLength.calc(10000);
		assertArrayEquals(new byte[]{ (byte)0x90, 0x4E }, result);		
	}
	
	@Test
	public void test_03_above16383() {
		
		byte[] result = CompactLength.calc(104583);
		assertArrayEquals(new byte[]{ (byte)0x87, (byte)0xB1, 0x06 }, result);		
	}
}
