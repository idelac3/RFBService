package com.scoreunit.rfb.encoding;

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

	public static final byte RAW = 0
			, COPY_RECT = 1
			, RRE = 2
			, HEXTILE = 5
			, ZRLE = 16
			;
}
