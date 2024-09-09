package com.scoreunit.rfb.encoding;

import com.scoreunit.rfb.tight.TightEncoder;

/**
 * 'The RFB Protocol' documentation, page 32,
 * by Tristan Richardson, RealVNC Ltd.
 * <p>
 * Version 3.8, Last updated 26 November 2010
 * 
 * @author igor.delac@gmail.com
 *
 */
public class Encodings {

	/**
	 * List of encoding types.
	 */
	public static final int RAW = 0
			, COPY_RECT = 1
			, RRE = 2
			, HEXTILE = 5
			, ZRLE = 16
			, ZLIB = 6
			, TIGHT = 7
			, RICH_CURSOR = -239;
			;

	private Encodings() {
	
		// No need for instance of this class.
	}
	
	/**
	 * Create new encoder for frame buffer.
	 * 
	 * @param encodingType	-	one of {@link Encodings#RAW}, {@link Encodings#ZLIB} ...
	 * 
	 * @return	encoder instance, or null if wrong or non-existing type was provided
	 */
	public static EncodingInterface newInstance(final int encodingType) {
		
		if (encodingType == RAW) {
			
			return new RawEncoder();
		}
		
		if (encodingType == HEXTILE) {
			
			return new HextileEncoder();
		}
		
		if (encodingType == ZLIB) {
		
			return new ZlibEncoder();
		}

		if (encodingType == RICH_CURSOR) {
			
			return new RichCursorEncoder();
		}
		
		if (encodingType == TIGHT) {
			
			return new TightEncoder();
		}
		
		return null;
	}

}
