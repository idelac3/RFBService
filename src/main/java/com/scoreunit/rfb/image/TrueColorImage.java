package com.scoreunit.rfb.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.scoreunit.rfb.encoding.RawEncoder;
import com.scoreunit.rfb.screen.ScreenCapture;

/**
 * Stores true color (32-bit) image.
 * <p>
 * Note that here is not described byte order for a pixel.
 * When using {@link ScreenCapture} methods to capture image of screen,
 * byte order is {@link ByteOrder#LITTLE_ENDIAN}, and when encoding image
 * for VNC client, byte order should be {@link ByteOrder#BIG_ENDIAN}.
 * See {@link RawEncoder} how to switch byte order.
 * <p>
 * 
 * @author igor.delac@gmail.com
 *
 */
public class TrueColorImage {

	public final int[] raw;
	
	public final int width, height;
	
	/**
	 * Encapsulate raw array of pixels (32-bit ARGB format) into {@link TrueColorImage} object.
	 * 
	 * @param raw			-	array of pixels		
	 * @param width			-	width of image
	 * @param height		-	height of image
	 */
	public TrueColorImage(final int[] raw, final int width, final int height) {
	
		this.raw = raw;
		
		this.width = width;
		this.height = height;
	}

	/**
	 * Change single pixel color value.
	 * Position (0, 0) is left, top pixel.
	 * 
	 * @param x			-	x position 
	 * @param y			-	y position
	 * @param value		-	new color value of pixel
	 * 
	 * @throws	IllegalArgumentException	if position is out of range
	 */
	public void setPixel(int x, int y, int value) {
		
		if (x >= width || y >= height) {
			
			throw new IllegalArgumentException(String.format("Pixel value out of range: (%d, %d)", x, y));
		}
		
		this.raw[x + y * width] = value;
	}

	/**
	 * Read single pixel color value.
	 * Position (0, 0) is left, top pixel.
	 * 
	 * @param x			-	x position 
	 * @param y			-	y position
	 * @return	current color value of pixel
	 * 
	 * @throws	IllegalArgumentException if position is out of range
	 */
	public int getPixel(int x, int y) {
		
		if (x >= width || y >= height) {
			
			throw new IllegalArgumentException(String.format("Pixel value out of range: (%d, %d)", x, y));
		}
		
		return this.raw[x + y * width];
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + Arrays.hashCode(raw);
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrueColorImage other = (TrueColorImage) obj;
		if (height != other.height)
			return false;
		if (!Arrays.equals(raw, other.raw))
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrueColorImage [width=");
		builder.append(width);
		builder.append(", height=");
		builder.append(height);
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * Convert {@link TrueColorImage} instance to {@link BufferedImage}.
	 * Here is assumed pixel arrangement to be {@link BufferedImage#TYPE_INT_ARGB}. 
	 * 
	 * @param		trueColorImage		-	instance of {@link TrueColorImage} object
	 * 
	 * @return	{@link BufferedImage} of type {@link BufferedImage#TYPE_INT_ARGB}, or null if input argument is null
	 */
	public static BufferedImage toBufferedImage(final TrueColorImage trueColorImage) {
		
		if (trueColorImage == null) {
			
			return null;
		}
		
		final int width = trueColorImage.width,
				height = trueColorImage.height;
		
		final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final int[] array = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
		System.arraycopy(trueColorImage.raw, 0, array, 0, array.length);
		
		return bufferedImage;
	}
	
	/**
	 * Convert {@link TrueColorImage} instance to byte array where
	 * pixels are in BGR order. This also reduce size from 32-bit pixel to 24-bit pixel.
	 * 
	 * @param trueColorImage	-	instance of {@link TrueColorImage}
	 * 
	 * @return	raw byte array
	 */
	public static byte[] toBGR(final TrueColorImage trueColorImage) {
		
		int[] src = trueColorImage.raw;
		
		final byte[] raw = new byte[src.length * 3];
		
		final ByteBuffer dstBuffer = ByteBuffer.wrap(raw);
				
		for (int i = 0 ; i < src.length ; i++) {
			
			int pixel = src[i];
			
			// Convert from ARGB into BGR.
			dstBuffer.put( (byte) (pixel & 0xFF)); // Blue component.
			pixel = pixel >> 8;
		
			dstBuffer.put( (byte) (pixel & 0xFF)); // Green component.
			pixel = pixel >> 8;

			dstBuffer.put( (byte) (pixel & 0xFF)); // Red component.
			pixel = pixel >> 8;
		}
		
		return raw;
	}
}
