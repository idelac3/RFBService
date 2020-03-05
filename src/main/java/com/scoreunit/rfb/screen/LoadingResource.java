package com.scoreunit.rfb.screen;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Few methods that will help to load 'Loading ...' image,
 * stored in raw format (32-bit, RGB0 pixel format).
 * 
 * @author igor.delac@gmail.com
 *
 */
public class LoadingResource {
	
	/**
	 * Load image as resource.
	 * 
	 * @return	image raw data, RGB0 pixel format
	 * 
	 * @throws IOException	if resource file 'loading.raw' is not on class path
	 */
	public static byte[] raw() throws IOException {
		
		final InputStream inputStream = 
				LoadingResource.class.getClassLoader().getResourceAsStream("loading.raw");
		
		if (inputStream == null) {
			
			throw new IOException("Resource 'loading.raw' not found on class path.");
		}
								
		final byte[] result = new byte[187200]; // Exact size of file 'loading.raw'
		final DataInputStream in = new DataInputStream(inputStream);
		
		in.readFully(result);
		
		return result;
	}
	
	/**
	 * Obtain width of 'loading.raw' image.
	 * 
	 * @return	width in pixel
	 */
	public static short getWidth() {
		
		return 260;
	}
	
	/**
	 * Obtain height of 'loading.raw' image.
	 * 
	 * @return	height in pixel
	 */
	public static short getHeight() {
		
		return 180;
	}
}
