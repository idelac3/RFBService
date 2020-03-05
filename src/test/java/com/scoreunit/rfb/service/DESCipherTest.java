package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class DESCipherTest {

	@Test
	public void test_01_encDec() {
		
		final String key = "12345678";
		final byte[] data = new byte[100];
		
		for (int i = 0 ; i < 100 ; i++) {
		
			data[i] = (byte) i;
		}
		
		byte[] encrypted = DESCipher.enc(key, data);
		
		byte[] decrypted = DESCipher.dec(key, encrypted);
		
		assertFalse(Arrays.equals(data, encrypted));
		assertArrayEquals(data, decrypted);
	}

	@Test
	public void test_02_realExample() {
		
		String password = "password1";
		
		byte[] challenge = {
				  (byte) 0x83, 0x3f, (byte) 0xb1, (byte) 0xe1, (byte) 0xa6, 0x03, (byte) 0x8d, (byte) 0xfd,
				  0x5e, 0x49, 0x38, 0x0e, 0x5c, (byte) 0xec, (byte) 0x9b, 0x21
				};
		
		byte[] response = {
				  (byte) 0xb4, (byte) 0xd4, (byte) 0x8a, (byte) 0xf7, (byte) 0xfe, (byte) 0xa0, (byte) 0xde, 0x6d,
				  (byte) 0xee, 0x2c, (byte) 0x9e, 0x00, (byte) 0xae, 0x62, 0x33, (byte) 0x87
				};
		
		byte[] enc = DESCipher.enc(password, challenge);
		
		for (int i = 0 ; i < 16 ; i++) {
		
			assertEquals(response[i], enc[i]);
		}		
	}
	
	@Test
	public void test_03_byteReverse() {
	
		byte input1    = (byte) 0b00001111;
		byte expected1 = (byte) 0b11110000;
		assertEquals(expected1, DESCipher.reverseByte(input1));
		
		byte input2    = (byte) 0b00000001;
		byte expected2 = (byte) 0b10000000;
		assertEquals(expected2, DESCipher.reverseByte(input2));
		
		byte input3    = (byte) 0b10000001;
		byte expected3 = (byte) 0b10000001;
		assertEquals(expected3, DESCipher.reverseByte(input3));
		
		byte input4    = (byte) 0b00000000;
		byte expected4 = (byte) 0b00000000;
		assertEquals(expected4, DESCipher.reverseByte(input4));
		
		byte input5    = (byte) 0b11111111;
		byte expected5 = (byte) 0b11111111;
		assertEquals(expected5, DESCipher.reverseByte(input5));
	}
}
