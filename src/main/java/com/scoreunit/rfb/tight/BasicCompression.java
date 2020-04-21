package com.scoreunit.rfb.tight;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scoreunit.rfb.encoding.RawEncoder;
import com.scoreunit.rfb.image.TrueColorImage;
import com.scoreunit.rfb.service.SetPixelFormat;

/**
 * Basic compression method, with only copy filter.
 * <p>
 * Support for gradient and palette filter are not supported yet.
 * 
 * @author igor.delac@gmail.com
 *
 */
class BasicCompression {
	
	public final static Logger log = LoggerFactory.getLogger(BasicCompression.class);

	final private Deflater deflater;
	
	final private RawEncoder rawEncoder;
	
	public BasicCompression() {

		this.deflater = new Deflater();
		
		this.rawEncoder = new RawEncoder();
	}
	
	public byte[] encode(final TrueColorImage image, final SetPixelFormat pixelFormat) {
		
		final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		
		// First four bits: stream reset (four streams).

		//
		// Basic compression, last four bits are according to table:
		// +--Bits---+--Binary value--+--Description--+
		// | 5-4 	 |  00 	          | Use stream 0  |
		// |         |  01 	          | Use stream 1  |
		// |         |  10            | Use stream 2  |
		// |         |  11            | Use stream 3  |
		// | 6 	     |   0            |               |
		// |         |   1            |read-filter-id |
		// | 7       |   0            |BasicCompressio|
		// +---------+----------------+---------------+
		//
		
		byte compressionControl = 0b00000000;	// Use copy filter (no filter).
		
		bOut.write(compressionControl);
		
		final byte[] raw;
		if (pixelFormat.bitsPerPixel == 32 && pixelFormat.depth == 24) {
		
			// As per description ( https://github.com/rfbproto/rfbproto/blob/master/rfbproto.rst#tight-encoding ),
			// convert PIXEL into TPIXEL (ARGB --> BGR). Reduce 1 byte for each pixel.
			raw = TrueColorImage.toBGR(image);			
		}
		else {
			
			// Let RAW encoder convert 32-bit ARGB image of screen into appropriate
			// raw bytes according to pixel format information.
			raw = rawEncoder.encode(image, pixelFormat);
		}
		
		deflater.setInput(raw);
		
		final byte[] buff = new byte[raw.length];

		// Compress using Java built-in Z-lib compressor.
		int count = deflater.deflate(buff, 0, buff.length, Deflater.FULL_FLUSH);

		try {
			
			bOut.write(CompactLength.calc(count));
			bOut.write(buff, 0, count);
		}
		catch (final IOException ex) {
			
			log.error("Unable to write stream data using basic compression.", ex);
		}
		
		return bOut.toByteArray();
	}
}
