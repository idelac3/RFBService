package com.scoreunit.rfb.screen;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.scoreunit.rfb.image.TrueColorImage;

/**
 * Few methods that will help to load 'Loading ...' image,
 * stored in raw format (32-bit, ARGB pixel format).
 * 
 * @author igor.delac@gmail.com
 *
 */
public class LoadingResource {
	
	/**
	 * Load image from resource file.
	 * 
	 * @return	image raw data, ARGB pixel format
	 * 
	 * @throws IOException	if resource file 'loading.png' is not on class path
	 */
	public static TrueColorImage get() throws IOException {
		
		final InputStream inputStream = 
				LoadingResource.class.getClassLoader().getResourceAsStream("loading.png");
		
		if (inputStream == null) {
			
			throw new IOException("Resource 'loading.png' not found on class path.");
		}
		
		final BufferedImage pngImage = ImageIO.read(inputStream);
		final BufferedImage newImage = new BufferedImage(pngImage.getWidth(), pngImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

		final Graphics2D graphics = newImage.createGraphics();
		graphics.drawImage(pngImage, 0, 0, pngImage.getWidth(), pngImage.getHeight(), null);
		graphics.dispose();
		
	    final int[] colorImageBuffer = ((DataBufferInt) newImage.getRaster().getDataBuffer()).getData();

	    return new TrueColorImage(colorImageBuffer, newImage.getWidth(), newImage.getHeight());
	}
}
