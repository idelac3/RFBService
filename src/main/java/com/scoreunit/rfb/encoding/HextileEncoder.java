package com.scoreunit.rfb.encoding;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scoreunit.rfb.image.TrueColorImage;
import com.scoreunit.rfb.service.SetPixelFormat;

/**
 * Hextile encoding divides image into 16x16 pixel tiles.
 * <p>
 * This implementation is very simple:<br>
 * It will turn image into tiles, check if tile is single-coloured
 * and if it is, then it will encode tile with 1 pixel, otherwise
 * it will encode as raw tile using {@link RawEncoder}.
 * <p>
 *  
 * Ref.<br>
 * <a href="https://github.com/rfbproto/rfbproto/blob/master/rfbproto.rst#hextile-encoding">Hextile encoding</a>
 * 
 * @author igor.delac@gmail.com
 *
 */
public class HextileEncoder implements EncodingInterface {

	/**
	 * Default {@link Logger} instance.
	 */
	public final static Logger log = LoggerFactory.getLogger(HextileEncoder.class);
	
	/**
	 * Each tile begins with byte named <i>subencoding mask</i>.
	 * <p>
	 * It contains bits, that define how to decode tile data.
	 * <p>
	 * Mask bits are defined by 
	 * <p>
	 * <a href="https://github.com/rfbproto/rfbproto/blob/master/rfbproto.rst#id77">hextile encoding</a> document.
	 */
	public final static byte MASK_RAW = 0b00000001
			, MASK_BACKGROUND_SPECIFIED = 0b00000010
			, MASK_FOREGROUND_SPECIFIED = 0b00000100
			, MASK_ANY_SUBRECT = 0b00001000
			, MASK_SUBRECTS_COLOURED = 0b00010000
			;
	
	private final RawEncoder rawEncoder;

	/**
	 * New Hextile encoder.
	 */
	public HextileEncoder() {
		
		this.rawEncoder = new RawEncoder();
	}
	
	/**
	 * This is simple hextile encoding implementation, that will
	 * simply divide image into hex. tiles (16x16 pixel),
	 * and write them continuously from upper left to bottom right,
	 * in order.
	 * 
	 */
	@Override
	public byte[] encode(final TrueColorImage image, final SetPixelFormat pixelFormat) {

		final List<Tile> tiles = Tile.build(image);

		final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(bOut);
		
		for (final Tile tile : tiles) {
			
			try {
				
				final Integer singlePixelValue = singlePixelTile(tile);
				
				if (singlePixelValue != null) {
					
					// When mask byte has background bit set,
					// tile consists of single colour. All pixels have same value. 
					byte subencodingMask = MASK_BACKGROUND_SPECIFIED;
					
					out.write(subencodingMask);
					
					// Use pixel transform routing with pixel format provided. This covers case when
					// VNC client requests 8-bit color mode, while source image is 32-bit color image.					
					final byte bitsPerPixel = pixelFormat.bitsPerPixel;
					
					if (bitsPerPixel == 8) {
					
						out.writeByte(PixelTransform.transform(singlePixelValue, pixelFormat));
					}
					else if (bitsPerPixel == 16) {
						
						out.writeShort(PixelTransform.transform(singlePixelValue, pixelFormat));
					}
					else if (bitsPerPixel == 32) {
						
						out.writeInt(PixelTransform.transform(singlePixelValue, pixelFormat));
					}
					else {
						
						log.error("Unsupported bits per pixel value: " + bitsPerPixel);
						
						break;
					}
				}
				else {
					
					byte subencodingMask = MASK_RAW;
					
					// If tile contains more colours, then use raw encoding of tile. 
					out.write(subencodingMask);
					out.write(rawEncoder.encode(new TrueColorImage(tile.raw(), tile.width, tile.height), pixelFormat));
				}
			}
			catch (final IOException ex) {
				
				log.error("Hextile encoding problem.", ex);
			}
		}
		
		return bOut.toByteArray();
	}

	@Override
	public int getType() {
		
		return Encodings.HEXTILE;
	}

	/**
	 * Check if tile contains only 1 color.
	 * 
	 * @param tile	-	{@link Tile} object
	 * 
	 * @return	pixel value, or <i>null</i> if tile is not <i>single-colored</i>
	 */
	private Integer singlePixelTile(final Tile tile) {
		
		if (tile == null || tile.raw() == null) {
		
			return null;
		}
		
		final int[] raw = tile.raw();
		
		int firstPixel = raw[0];
		
		for (int i = 1 ; i < raw.length ; i++) {
			
			if (raw[i] != firstPixel) {
				
				return null;
			}
		}
		
		return Integer.valueOf(firstPixel);
	}
}
