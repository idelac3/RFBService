package com.scoreunit.rfb.encoding;

import java.nio.ByteBuffer;
import java.util.zip.Deflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scoreunit.rfb.image.TrueColorImage;
import com.scoreunit.rfb.service.SetPixelFormat;

/**
 * Zlib is a very simple encoding that uses zlib library to compress raw pixel
 * data. This encoding achieves good compression, but consumes a lot of CPU
 * time. Support for this encoding is provided for compatibility with VNC
 * servers that might not understand Tight encoding which is more efficient than
 * Zlib in nearly all real-life situations.
 * <p>
 * This encoder is stateful, ensure that each RFB connection with VNC client,
 * has its own instance of this encoder.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class ZlibEncoder implements EncodingInterface {

	public final static Logger log = LoggerFactory.getLogger(ZlibEncoder.class);

	final private Deflater deflater;

	final private RawEncoder rawEncoder;

	/**
	 * Create new zlib encoder.
	 * 
	 */
	public ZlibEncoder() {

		this.deflater = new Deflater();

		this.rawEncoder = new RawEncoder();
	}

	/**
	 * Zlib will just compress raw encoded image.
	 */
	@Override
	public byte[] encode(final TrueColorImage image, final SetPixelFormat pixelFormat) {

		final byte[] raw = rawEncoder.encode(image, pixelFormat);
		deflater.setInput(raw);
		
		final byte[] buff = new byte[2 * raw.length];

		// Seems that we need to invoke deflate() method with FULL_FLUSH arg.
		int count = deflater.deflate(buff, 0, buff.length, Deflater.FULL_FLUSH);

		final int length = 4 + count;
		final byte[] result = new byte[length];

		final ByteBuffer buffer = ByteBuffer.wrap(result);
		buffer.putInt(count);
		buffer.put(buff, 0, count);

		return result;
	}

	@Override
	public int getType() {

		return Encodings.ZLIB;
	}

}
