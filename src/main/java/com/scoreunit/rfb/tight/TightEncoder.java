package com.scoreunit.rfb.tight;

import com.scoreunit.rfb.encoding.EncodingInterface;
import com.scoreunit.rfb.encoding.Encodings;
import com.scoreunit.rfb.image.TrueColorImage;
import com.scoreunit.rfb.service.SetPixelFormat;

/**
 * Implementation as described in 
 * <a href="https://github.com/rfbproto/rfbproto/blob/master/rfbproto.rst#tight-encoding">Tight encoding</a>.
 * <p>
 * Note that this is early implementation, not efficient as it could be.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class TightEncoder implements EncodingInterface {

	private final BasicCompression basicCompression;
	
	private final JpegCompression jpegCompression;

	/**
	 * New Tight encoder.
	 */
	public TightEncoder() {
	
		this.basicCompression = new BasicCompression();
		this.jpegCompression = new JpegCompression();
	}
	
	@Override
	public byte[] encode(final TrueColorImage image, final SetPixelFormat pixelFormat) {
		
		// If pixel format allows JPEG compression, then use it.
		if (pixelFormat.bitsPerPixel == 32 
				&& pixelFormat.depth == 24) {
		
			return this.jpegCompression.encode(image, pixelFormat);
		}
		
		// Otherwise, fall-back to basic compression.
		return this.basicCompression.encode(image, pixelFormat);		
	}

	@Override
	public int getType() {

		return Encodings.TIGHT;
	}
}
