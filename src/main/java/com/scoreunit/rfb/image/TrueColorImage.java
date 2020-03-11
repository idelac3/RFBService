package com.scoreunit.rfb.image;

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
	
	public TrueColorImage(final int[] raw, final int width, final int height) {
	
		this.raw = raw;
		
		this.width = width;
		this.height = height;
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
	
	
}
