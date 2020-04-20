package com.scoreunit.rfb.tight;

/**
 * Helper class to calculate length value in compact form. Used for tight encoding.
 * 
 * @author igor.delac@gmail.com
 *
 */
class CompactLength {

	/**
	 * Calculate length value in compact form. Used for tight encoding.	  	
	 * <p>
	 * Length is compactly represented in one, two or three bytes, according to the following scheme:
	 * <table border=1>
	 *  <tr><th>Value</th><th>Description</th></tr>
	 *  <tr><td>0xxxxxxx</td><td>for values 0..127</td></tr>
	 *  <tr><td>1xxxxxxx 0yyyyyyy</td><td>for values 128..16383</td></tr>
	 *  <tr><td>1xxxxxxx 1yyyyyyy zzzzzzzz</td><td>for values 16384..4194303</td></tr>
	 * </table>
	 * Here each character denotes one bit, xxxxxxx are the least significant 7 bits of the value (bits 0-6), 
	 * yyyyyyy are bits 7-13, and zzzzzzzz are the most significant 8 bits (bits 14-21). 
	 * <p>
	 * For example, decimal value 10000 should be represented as two bytes: binary 10010000 01001110, or hexadecimal 90 4E.
	 * 
	 * @param length		-	length value
	 * 
	 * @return	length in compact form
	 */
	public static byte[] calc(int length) {
		
		if (length < 128) {
			
			return new byte[]{ (byte) length };
		}
		else if (length >= 128 && length <= 16383) {
			
			byte b1 = (byte) ((length & 0b01111111) | 0b10000000);
			byte b2 = (byte) ((length >> 7) & 0b01111111);
			
			return new byte[] {b1, b2};
		}

		byte b1 = (byte) ((length & 0b01111111) | 0b10000000);
		byte b2 = (byte) (((length >> 7) & 0b01111111) | 0b10000000);
		byte b3 = (byte) (length >> 14);
		
		return new byte[] {b1, b2, b3};
	}

}
