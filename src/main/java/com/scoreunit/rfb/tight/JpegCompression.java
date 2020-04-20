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
	
	public byte[] encode(final int[] image, final int width, final int height
			, final SetPixelFormat pixelFormat) {
		
		final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		
		//
		// First four bits (least significant): stream reset, for the four streams.
		// JPEG compression, sets most significant bits to 0b1001:
		//
		
		byte compressionControl = (byte) 0b10010000;		
		bOut.write(compressionControl);
		
		final ByteArrayOutputStream jpgOut = new ByteArrayOutputStream();
/*		
		final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final int[] array = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
		System.arraycopy(image, 0, array, 0, array.length);
		
		final BufferedImage bufferedImage1 = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		bufferedImage1.createGraphics().drawImage(bufferedImage, 0, 0, new ImageObserver() {

			@Override
			public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
				// TODO Auto-generated method stub
				return false;
			}
		});
*/		
		try {
		
			final byte[] bgr = TrueColorImage.toBGR(new TrueColorImage(image, width, height));
			
			final BufferedImage bgrBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
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
