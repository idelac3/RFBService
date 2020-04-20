package com.scoreunit.rfb.tight;

import com.scoreunit.rfb.encoding.EncodingInterface;
import com.scoreunit.rfb.encoding.Encodings;
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

	/**
	 * Flag to control which compression to use: <i>Basic</i>
	 * compression will use <i>Zlib</i> losless method to compress
	 * raw screen image, while default <i>JpegCompression</i> will
	 * use built-in Java JPEG method to compress screen image.
	 * <p>
	 * Note that if pixel format is not 32-bit with 24 color depth,
	 * it will use basic compression regardless of this flag value.
	 */
	public static boolean USE_BASIC_COMPRESSION = false;
	
	private final BasicCompression basicCompression;
	
	private final JpegCompression jpegCompression;
	
	public TightEncoder() {
	
		this.basicCompression = new BasicCompression();
		this.jpegCompression = new JpegCompression();
	}
	
	@Override
	public byte[] encode(final int[] image, final int width, final int height
			, final SetPixelFormat pixelFormat) {

		// If basic compression is enforced by flag value, then use it
		// regardless of input pixel format information.
		if (TightEncoder.USE_BASIC_COMPRESSION == true) {
			
			return this.basicCompression.encode(image, width, height, pixelFormat);
		}
		
		// If pixel format allows JPEG compression, then use it.
		if (pixelFormat.bitsPerPixel == 32 
				&& pixelFormat.depth == 24) {
		
			return this.jpegCompression.encode(image, width, height, pixelFormat);
		}
		
		// Otherwise, fall-back to basic compression.
		return this.basicCompression.encode(image, width, height, pixelFormat);		
	}

	@Override
	public int getType() {

		return Encodings.TIGHT;
	}
}
