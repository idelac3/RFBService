package com.scoreunit.rfb.encoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.scoreunit.rfb.image.TrueColorImage;

/**
 * Holds 16x16 pixel part of image.
 * <p>
 * Contains function to divide image into 16x16 pixel tiles,
 * suitable for {@link HextileEncoder}.
 * See {@link #build(int[], int, int)} method.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class Tile {

	private final int[] pixels;
	
	/**
	 * Currently tiles are limited to 16x16 pixel.
	 */
	public final short width = 16, height = 16;

	/**
	 * (x, y) position of the tile, taken from the main image.
	 */
	public final short xPos, yPos;
	
	/**
	 * Provide 16x16 array of pixels.
	 * 
	 * @param pixels 	-	32-bit ARGB pixels
	 * 
	 * @param	xPos	-	x offset, position of tile, relative to source image from which tile was obtained, in pixel
	 * @param	yPos	-	y offset, position of tile, relative to source image from which tile was obtained, in pixel
	 */
	public Tile(final int[] pixels, final short xPos, final short yPos) {
	
		this.pixels = pixels;
		
		this.xPos = xPos;
		this.yPos = yPos;
	}
	
	/**
	 * Access directly array of pixels that this tile instance holds.
	 * 
	 * @return	an 32-bit ARGB pixels
	 */
	public int[] raw() {
		
		return this.pixels;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + Arrays.hashCode(pixels);
		result = prime * result + width;
		result = prime * result + xPos;
		result = prime * result + yPos;
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
		Tile other = (Tile) obj;
		if (height != other.height)
			return false;
		if (!Arrays.equals(pixels, other.pixels))
			return false;
		if (width != other.width)
			return false;
		if (xPos != other.xPos)
			return false;
		if (yPos != other.yPos)
			return false;
		return true;
	}

	@Override
	public String toString() {
		
		return String.format("%s-[%d-%d]", Tile.class.getSimpleName(), this.xPos, this.yPos);
	}

	/**
	 * Divide image into tiles.
	 * 
	 * @param image		-	{@link TrueColorImage} instance
	 * 
	 * @return	list of {@link Tile}s
	 */
	public static List<Tile> build(final TrueColorImage image) {
		
		return Tile.build(image.raw, image.width, image.height);
	}
	
	/**
	 * Divide image into tiles.
	 * 
	 * @param image		-	raw image data, 32-bit pixels	
	 * @param width		-	width in pixel
	 * @param height	-	height in pixel
	 * 
	 * @return	list of {@link Tile}s
	 */
	public static List<Tile> build(final int[] image
			, final int width, final int height) {
				
		final List<Tile> tiles = new ArrayList<>();
		
		// Scan over image, and create a copy of 16x16 tiles.
		int x = 0, y = 0;
		while (x < width && y < height) {
		
			final int[] tileData = new int[256]; // Use always 16x16, 
													// even if bottom part of image is not divisible by 16.

			final int firstLine = x + y * width;	// Offset in source image, at which pixel tile begins. 
			for (int i = 0 ; i < 16 ; i++) {
			
				int destPos = i * 16;
				int line = firstLine + (i * width);
				System.arraycopy(image, line, tileData, destPos, 16);
			}
			
			tiles.add(new Tile(tileData, (short) x, (short) y));
			
			x = x + 16;
			
			if (x + 16 > width) {
				
				x = 0;
				
				y = y + 16;
				
				if (y + 16 > height) {
					
					break;
				}
			}			
		}
		
		return tiles;
	}
}
