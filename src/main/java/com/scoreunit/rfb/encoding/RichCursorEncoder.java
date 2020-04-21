package com.scoreunit.rfb.encoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scoreunit.rfb.image.TrueColorImage;
import com.scoreunit.rfb.screen.LoadingResource;
import com.scoreunit.rfb.service.SetPixelFormat;

/**
 * This encoder will prepare information for VNC client
 * about cursor shape.
 * <p>
 * It will work only if pixel format is set to 32-bit true color.
 * <p>
 * VNC client should receive rich cursor encoded data only once per session.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class RichCursorEncoder implements EncodingInterface {
	
	public final static Logger log = LoggerFactory.getLogger(RichCursorEncoder.class);
	
	/**
	 * Return cursor pixels and bitmask, or null value if resource files are not on class path.
	 */
	@Override
	public byte[] encode(final TrueColorImage image, SetPixelFormat pixelFormat) {
		
		try {
			
			final String bitmaskName = "cursorEncodingBitmask.raw"
					, pixelName = "cursorEncodingPixels.raw";
			
			final InputStream inputStream1 = 
					LoadingResource.class.getClassLoader().getResourceAsStream(pixelName);
			
			if (inputStream1 == null) {
				
				log.error("Resource '" + pixelName + "' not found on class path.");
				
				return null;
			}
	
			final InputStream inputStream2 = 
					LoadingResource.class.getClassLoader().getResourceAsStream(bitmaskName);
			
			if (inputStream2 == null) {
				
				log.error("Resource '" + bitmaskName + "' not found on class path.");
				
				return null;
			}
			
			final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			
			int encodedPixelsLength = 1296, bitmaskLength = 54;
			final byte[] encodedPixels = new byte[encodedPixelsLength];
			final byte[] bitmask       = new byte[bitmaskLength];
			
			int len = inputStream1.read(encodedPixels);
			if (len != encodedPixelsLength) {
				
				log.error("Unable to read completely cursor pixels.");
				
				return null;
			}
			
			len = inputStream2.read(bitmask);
			if (len != bitmaskLength) {
				
				log.error("Unable to read completely bitmask value.");
				
				return null;
			}		
			
			bOut.write(encodedPixels);
			bOut.write(bitmask);
			
			if (bOut.size() != encodedPixelsLength + bitmaskLength) {
				
				log.error("Resulting cursor buffer length is unexpected: " + bOut.size() + ". Expected is: " + String.valueOf(encodedPixelsLength + bitmaskLength));
				
				return null;
			}
			
			return bOut.toByteArray();
		} catch (final IOException ex) {
			
			log.error("Cursor data reading failed.", ex);
			return null;
		}
	}

	@Override
	public int getType() {

		return Encodings.RICH_CURSOR;
	}

}
