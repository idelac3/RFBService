package com.scoreunit.rfb.tight;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scoreunit.rfb.image.TrueColorImage;
import com.scoreunit.rfb.service.SetPixelFormat;

/**
 * JPEG compression method, using built-in Java JPEG library.
 * <p>
 * 
 * @author igor.delac@gmail.com
 *
 */
class JpegCompression {
	
	public final static Logger log = LoggerFactory.getLogger(JpegCompression.class);
	
	public JpegCompression() {

	}
	
	public byte[] encode(final TrueColorImage image, final SetPixelFormat pixelFormat) {
		
		final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		
		//
		// First four bits (least significant): stream reset, for the four streams.
		// JPEG compression, sets most significant bits to 0b1001:
		//
		
		byte compressionControl = (byte) 0b10010000;		
		bOut.write(compressionControl);
		
		final ByteArrayOutputStream jpgOut = new ByteArrayOutputStream();

		try {
		
			final byte[] bgr = TrueColorImage.toBGR(image);
			
			final BufferedImage bgrBufferedImage = new BufferedImage(image.width, image.height, BufferedImage.TYPE_3BYTE_BGR);
			final byte[] rasterArray = ((DataBufferByte) bgrBufferedImage.getRaster().getDataBuffer()).getData();
			System.arraycopy(bgr, 0, rasterArray, 0, rasterArray.length);
			
			ImageIO.write(bgrBufferedImage, "jpg", jpgOut);			
			
			bOut.write(CompactLength.calc(jpgOut.size()));
			bOut.write(jpgOut.toByteArray());
			
		} catch (final Exception ex) {
			
			log.error("Unable to write JPEG compressed stream data.", ex);
		}
		
		return bOut.toByteArray();
	}
}
