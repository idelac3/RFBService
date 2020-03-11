package com.scoreunit.rfb.encoding;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TileTest {

	final int[] image16x16 = {
			1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4,		// first line, 16 pixels
			5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8,		// next line, 16 pixels, ...
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4,
			5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4,
			5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4,
			5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8,
			8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
			9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9				
	};
	
	final int[] image20x20 = {
			1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 9, 9, 9, 9,		// first line, 20 pixels
			5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 9, 9, 9, 9,		// next line, 20 pixels, ...
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 9, 9,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 9, 9,
			1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 9, 9, 9, 9,
			5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 9, 9, 9, 9,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 9, 9,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 9, 9,
			1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 9, 9, 9, 9,
			5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 9, 9, 9, 9,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 9, 9,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 9, 9,
			1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 9, 9, 9, 9,
			5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 9, 9, 9, 9,
			8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9,
			9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,		// 16-th line, extra 4 to make image 20x20 pixel.
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	
	@Test
	public void test_01_singleTile() {

		final int width = 16, height = 16;
		
		final List<Tile> tiles = 
				Tile.build(image16x16, width, height);
		
		assertEquals(1, tiles.size());
		assertArrayEquals(image16x16, tiles.get(0).raw());
	}

	@Test
	public void test_02_singleTile() {

		final int width = 20, height = 20;

		final List<Tile> tiles = 
				Tile.build(image20x20, width, height);
		
		assertEquals(1, tiles.size());
		assertArrayEquals(image16x16, tiles.get(0).raw());	// Note that this first tile should be 16x16 !
	}

	@Test
	public void test_03_twoTiles() {

		final int width = 16, height = 16 * 2;
		
		final int[] image = new int[image16x16.length * 2];	// This is example image 16x32 pixel.
		System.arraycopy(image16x16, 0, image, 0, image16x16.length);
		System.arraycopy(image16x16, 0, image, image16x16.length, image16x16.length);

		final List<Tile> tiles = 
				Tile.build(image, width, height);
		
		assertEquals(2, tiles.size());
		assertArrayEquals(image16x16, tiles.get(0).raw());
		assertArrayEquals(image16x16, tiles.get(1).raw());
	}
	
	@Test
	public void test_04_twoTiles() {

		final int width = 16 * 2, height = 16;
		
		final int[] image = new int[image16x16.length * 2];	// This is example image 32x16 pixel.
		for (int i = 0 ; i < 32 ; i++) {
			
			System.arraycopy(image16x16, 16 * (i / 2), image, i * 16, 16);
		}

		final List<Tile> tiles = 
				Tile.build(image, width, height);
		
		assertEquals(2, tiles.size());
		assertArrayEquals(image16x16, tiles.get(0).raw());
		assertArrayEquals(image16x16, tiles.get(1).raw());
	}
	
	@Test
	public void test_05_threeTiles() {

		final int width = 16, height = 16 * 3;
		
		final int[] image = new int[image16x16.length * 3];	// This is example image 16x48 pixel.
		for (int i = 0 ; i < 3 ; i++) {
		
			System.arraycopy(image16x16, 0, image, i * image16x16.length, image16x16.length);
		}

		final List<Tile> tiles = 
				Tile.build(image, width, height);
		
		assertEquals(3, tiles.size());
		for (int i = 0 ; i < 3 ; i++) {
		
			assertArrayEquals(image16x16, tiles.get(i).raw());
		}
	}
	
	@Test
	public void test_06_threeTiles() {

		final int width = 16 * 3, height = 16;
		
		final int[] image = new int[image16x16.length * 3];	// This is example image 48x16 pixel.
		for (int i = 0, j = 0 ; i < 16 ; i++, j = j + 3) {
			
			System.arraycopy(image16x16, 16 * i, image, j * 16, 16);
			System.arraycopy(image16x16, 16 * i, image, (j + 1) * 16, 16);
			System.arraycopy(image16x16, 16 * i, image, (j + 2) * 16, 16);
		}


		final List<Tile> tiles = 
				Tile.build(image, width, height);
		
		assertEquals(3, tiles.size());
		for (int i = 0 ; i < 3 ; i++) {
		
			assertArrayEquals(image16x16, tiles.get(i).raw());
		}
	}
	
	@Test
	public void test_07_fourTiles() {

		final int width = 16 * 2, height = 16 * 2;
		
		final int[] image = new int[image16x16.length * 4];	// This is example image 32x32 pixel.

		for (int i = 0, j = 0 ; i < 16 ; i++, j = j + 2) {
			
			System.arraycopy(image16x16, 16 * i, image, j * 16, 16);
			System.arraycopy(image16x16, 16 * i, image, (j + 1) * 16, 16);
		}

		System.arraycopy(image, 0, image, image.length / 2, image.length / 2);
		
		final List<Tile> tiles = 
				Tile.build(image, width, height);
		
		assertEquals(4, tiles.size());
		for (int i = 0 ; i < 4 ; i++) {
		
			assertArrayEquals(image16x16, tiles.get(i).raw());
		}
		
		assertEquals(0, tiles.get(0).xPos);
		assertEquals(0, tiles.get(0).yPos);

		assertEquals(16, tiles.get(1).xPos);
		assertEquals( 0, tiles.get(1).yPos);
		
		assertEquals( 0, tiles.get(2).xPos);
		assertEquals(16, tiles.get(2).yPos);
		
		assertEquals(16, tiles.get(3).xPos);
		assertEquals(16, tiles.get(3).yPos);
	}
}
