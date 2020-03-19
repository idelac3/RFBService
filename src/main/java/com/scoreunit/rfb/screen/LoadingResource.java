package com.scoreunit.rfb.screen;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
	 * Generate image from resource text file.
	 * 
	 * @param width		-	desired width of image, should be either {@link ScreenCapture#getScreenWidth()} or {@link ScreenClip#width}
	 * @param height	-	desired height of image, should be either {@link ScreenCapture#getScreenHeight()} or {@link ScreenClip#height}
	 * 
	 * @return	image raw data, ARGB pixel format
	 * 
	 * @throws IOException	if resource file 'loading.png' is not on class path
	 */
	public static TrueColorImage get(final int width, final int height) throws IOException {
		
		final String resourceFilename = "git.properties";
		
		final InputStream inputStream = 
				LoadingResource.class.getClassLoader().getResourceAsStream(resourceFilename);
		
		if (inputStream == null) {
			
			throw new IOException("Resource '" + resourceFilename + "' not found on class path.");
		}
		
		final BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		final Graphics2D graphics = newImage.createGraphics();
		graphics.setFont(new Font(Font.MONOSPACED, Font.PLAIN, Double.valueOf(1.35f * graphics.getFont().getSize()).intValue()));
		
		final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		int offset = 10, increment = graphics.getFontMetrics().getHeight() + 3;
		while ( (line = reader.readLine()) != null) {
			
			graphics.drawString(line, 10, offset);
			offset = offset + increment;
		}
		
		graphics.dispose();
		
	    final int[] colorImageBuffer = ((DataBufferInt) newImage.getRaster().getDataBuffer()).getData();

	    return new TrueColorImage(colorImageBuffer, newImage.getWidth(), newImage.getHeight());
	}
}
