package com.scoreunit.rfb.tight;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.util.zip.Inflater;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.scoreunit.rfb.service.SetPixelFormat;

public class TightEncoderTest {

	@Test
	public void test_01_32bit_image() throws Exception {
		
		final SetPixelFormat pixelFormat = SetPixelFormat.default32bit();
		
		final int width = 640, height = 480;
		
		final int[] image = new int[width * height];
		
		final TightEncoder encoder = new TightEncoder();
		
		final byte[] encodedData = encoder.encode(image, width, height, pixelFormat);
		
		int[] decodedImage = decode(encodedData, width, height, 4); // since we use SetPixelFormat with 32-bit image.
		assertEquals(image.length, decodedImage.length);
	}

	@Test
	public void test_02_8bit_bgr233_image() throws Exception {
		
		final byte bitsPerPixel = 8, depth = 8;
		final byte bigEndianFlag = 0, trueColorFlag = 1;
		final short redMax = 7, greenMax = 7, blueMax = 3;
		final byte redShift = 4, greenShift = 2, blueShift = 0;
		
		// Pixel format where only 1 byte is pixel. First two bits are for blue, next two for green color, ... etc.
		final SetPixelFormat pixelFormat = new SetPixelFormat(bitsPerPixel, depth
				, bigEndianFlag, trueColorFlag
				, redMax, greenMax, blueMax
				, redShift, greenShift, blueShift);
		
		final int width = 640, height = 480;
		
		final int[] image = new int[width * height];
		
		final TightEncoder encoder = new TightEncoder();
		
		final byte[] encodedData = encoder.encode(image, width, height, pixelFormat);
		
		int[] decodedImage = decode(encodedData, width, height, 1); // since we use SetPixelFormat with 8-bit image.
		assertEquals(image.length, decodedImage.length);
	}
	
	/**
	 * Code is taken from <b>VncCanvas</b> class, method <i>void handleTightRect(int x, int y, int w, int h)</i>
	 * to test if encoded rectangles are possible to decode.
	 * <p>
	 * Ref.
	 *  <a href="https://www.tightvnc.com/download/1.3.10/tightvnc-1.3.10_javasrc.zip">Java VNC Viewer source code</a>
	 *  
	 * @param encodedData		-	provide here Tight encoded rectangle, or full image
	 * 
	 * @param width				-	width in pixel
	 * @param height			-	height in pixel
	 * 
	 * @param bytesPixel		-	either 1 (for 8-bit image) or 4 for true color 32-bit image
	 * 
	 * @return		true color 32-bit decoded image, if encoding was proper
	 * 
	 * @throws Exception
	 */
	private int[] decode(byte[] encodedData, int width, int height, int bytesPixel) throws Exception {
		
		final BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		final int w = width, h = height, x = 0, y = 0;
		
		final Graphics memGraphics = newImage.createGraphics();
		Color[] colors = new Color[256];;
		
		final RfbProto rfb = new RfbProto(new ByteArrayInputStream(encodedData));
		rfb.framebufferWidth = w;
		rfb.framebufferHeight = h;

	    byte[] pixels8 = null;
	    int[] pixels24 = null;
	    
	    if (bytesPixel == 4) {
	    	
	    	pixels24 = new int[width * height];
	    }
	    else if (bytesPixel == 1) {
	    	
	    	pixels8 = new byte[width * height];
	    }
	    else {
		
			throw new IllegalArgumentException("Please set proper bytesPixel value: 1 or 4. Currently set to: '" + bytesPixel + "'");
		}
		
		//
		// Handle a Tight-encoded rectangle.
		//

		int comp_ctl = rfb.readU8();

		final Inflater[] tightInflaters = new Inflater[4];

		// Flush zlib streams if we are told by the server to do so.
		for (int stream_id = 0; stream_id < 4; stream_id++) {
			if ((comp_ctl & 1) != 0 && tightInflaters[stream_id] != null) {
				tightInflaters[stream_id] = null;
			}
			comp_ctl >>= 1;
		}

		// Check correctness of subencoding value.
		if (comp_ctl > RfbProto.TightMaxSubencoding) {
			throw new Exception("Incorrect tight subencoding: " + comp_ctl);
		}

		// Handle solid-color rectangles.
		if (comp_ctl == RfbProto.TightFill) {

			if (bytesPixel == 1) {
				int idx = rfb.readU8();
				memGraphics.setColor(colors[idx]);
			} else {
				byte[] buf = new byte[3];
				rfb.readFully(buf);

				Color bg = new Color(0xFF000000 | (buf[0] & 0xFF) << 16 | (buf[1] & 0xFF) << 8 | (buf[2] & 0xFF));
				memGraphics.setColor(bg);
			}
			memGraphics.fillRect(x, y, w, h);

			return  ((DataBufferInt) newImage.getRaster().getDataBuffer()).getData();
		}

		if (comp_ctl == RfbProto.TightJpeg) {

			// Read JPEG data.
			byte[] jpegData = new byte[rfb.readCompactLen()];
			rfb.readFully(jpegData);

			// This image is 24-bit per pixel (3 bytes).
			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(jpegData));
			
			byte[] jpegImageData = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
			
			int[] result = new int[jpegImageData.length / 3];
			
			// Convert BGR image into ARGB 32-bit true color image.
			for (int i = 0 ; i < result.length ; i++) {
				
				int pixel = (jpegImageData[i])
						| (jpegImageData[i + 1] << 8)
						| (jpegImageData[i + 2] << 16);
				
				result[i] = pixel;
			}
			
			return result;
		}

		// Read filter id and parameters.
		int numColors = 0, rowSize = w;
		byte[] palette8 = new byte[2];
		int[] palette24 = new int[256];
		boolean useGradient = false;
		if ((comp_ctl & RfbProto.TightExplicitFilter) != 0) {
			int filter_id = rfb.readU8();

			if (filter_id == RfbProto.TightFilterPalette) {
				numColors = rfb.readU8() + 1;
				if (bytesPixel == 1) {
					if (numColors != 2) {
						throw new Exception("Incorrect tight palette size: " + numColors);
					}
					rfb.readFully(palette8);
				} else {
					byte[] buf = new byte[numColors * 3];
					rfb.readFully(buf);

					for (int i = 0; i < numColors; i++) {
						palette24[i] = ((buf[i * 3] & 0xFF) << 16 | (buf[i * 3 + 1] & 0xFF) << 8
								| (buf[i * 3 + 2] & 0xFF));
					}
				}
				if (numColors == 2)
					rowSize = (w + 7) / 8;
			} else if (filter_id == RfbProto.TightFilterGradient) {
				useGradient = true;
			} else if (filter_id != RfbProto.TightFilterCopy) {
				throw new Exception("Incorrect tight filter id: " + filter_id);
			}
		}
		if (numColors == 0 && bytesPixel == 4)
			rowSize *= 3;

		// Read, optionally uncompress and decode data.
		int dataSize = h * rowSize;
		if (dataSize < RfbProto.TightMinToCompress) {
			// Data size is small - not compressed with zlib.
			if (numColors != 0) {
				// Indexed colors.
				byte[] indexedData = new byte[dataSize];
				rfb.readFully(indexedData);

				if (numColors == 2) {
					// Two colors.
					if (bytesPixel == 1) {
						decodeMonoData(rfb, pixels8, x, y, w, h, indexedData, palette8);
					} else {
						decodeMonoData(rfb, pixels24, x, y, w, h, indexedData, palette24);
					}
				} else {
					// 3..255 colors (assuming bytesPixel == 4).
					int i = 0;
					for (int dy = y; dy < y + h; dy++) {
						for (int dx = x; dx < x + w; dx++) {
							pixels24[dy * rfb.framebufferWidth + dx] = palette24[indexedData[i++] & 0xFF];
						}
					}
				}
			} else if (useGradient) {
				// "Gradient"-processed data
				byte[] buf = new byte[w * h * 3];
				rfb.readFully(buf);

				decodeGradientData(rfb, pixels24, x, y, w, h, buf);
			} else {
				// Raw truecolor data.
				if (bytesPixel == 1) {
					for (int dy = y; dy < y + h; dy++) {
						rfb.readFully(pixels8, dy * rfb.framebufferWidth + x, w);

					}
				} else {
					byte[] buf = new byte[w * 3];
					int i, offset;
					for (int dy = y; dy < y + h; dy++) {
						rfb.readFully(buf);

						offset = dy * rfb.framebufferWidth + x;
						for (i = 0; i < w; i++) {
							pixels24[offset + i] = (buf[i * 3] & 0xFF) << 16 | (buf[i * 3 + 1] & 0xFF) << 8
									| (buf[i * 3 + 2] & 0xFF);
						}
					}
				}
			}
		} else {
			// Data was compressed with zlib.
			int zlibDataLen = rfb.readCompactLen();
			byte[] zlibData = new byte[zlibDataLen];
			rfb.readFully(zlibData);

			int stream_id = comp_ctl & 0x03;
			if (tightInflaters[stream_id] == null) {
				tightInflaters[stream_id] = new Inflater();
			}
			Inflater myInflater = tightInflaters[stream_id];
			myInflater.setInput(zlibData);
			byte[] buf = new byte[dataSize];
			myInflater.inflate(buf);


			if (numColors != 0) {
				// Indexed colors.
				if (numColors == 2) {
					// Two colors.
					if (bytesPixel == 1) {
						decodeMonoData(rfb, pixels8, x, y, w, h, buf, palette8);
					} else {
						decodeMonoData(rfb, pixels24, x, y, w, h, buf, palette24);
					}
				} else {
					// More than two colors (assuming bytesPixel == 4).
					int i = 0;
					for (int dy = y; dy < y + h; dy++) {
						for (int dx = x; dx < x + w; dx++) {
							pixels24[dy * rfb.framebufferWidth + dx] = palette24[buf[i++] & 0xFF];
						}
					}
				}
			} else if (useGradient) {
				// Compressed "Gradient"-filtered data (assuming bytesPixel ==
				// 4).
				decodeGradientData(rfb, pixels24, x, y, w, h, buf);
			} else {
				// Compressed truecolor data.
				if (bytesPixel == 1) {
					int destOffset = y * rfb.framebufferWidth + x;
					for (int dy = 0; dy < h; dy++) {
						System.arraycopy(buf, dy * w, pixels8, destOffset, w);
						destOffset += rfb.framebufferWidth;
					}
				} else {
					int srcOffset = 0;
					int destOffset, i;
					for (int dy = 0; dy < h; dy++) {
						myInflater.inflate(buf);
						destOffset = (y + dy) * rfb.framebufferWidth + x;
						for (i = 0; i < w; i++) {
														
							pixels24[destOffset + i] = (buf[srcOffset] & 0xFF) << 16 | (buf[srcOffset + 1] & 0xFF) << 8
									| (buf[srcOffset + 2] & 0xFF);
							srcOffset += 3;
						}
					}
				}
			}
		}
		
		if (pixels24 == null && pixels8 != null) {
			
			int len = pixels8.length;
			pixels24 = new int[len];
			
			for (int i = 0 ; i < len ; i++) {
				
				pixels24[i] = (pixels8[i] << 24) | (pixels8[i] << 16) | (pixels8[i] << 8);
			}
		}	
		
		return pixels24;		
	}

	/**
	 * Decode 1bpp-encoded bi-color rectangle (8-bit and 24-bit versions).
	 */
	void decodeMonoData(RfbProto rfb, byte[] pixels8, int x, int y, int w, int h, byte[] src, byte[] palette) {

		int dx, dy, n;
		int i = y * rfb.framebufferWidth + x;
		int rowBytes = (w + 7) / 8;
		byte b;

		for (dy = 0; dy < h; dy++) {
			for (dx = 0; dx < w / 8; dx++) {
				b = src[dy * rowBytes + dx];
				for (n = 7; n >= 0; n--)
					pixels8[i++] = palette[b >> n & 1];
			}
			for (n = 7; n >= 8 - w % 8; n--) {
				pixels8[i++] = palette[src[dy * rowBytes + dx] >> n & 1];
			}
			i += (rfb.framebufferWidth - w);
		}
	}

	void decodeMonoData(RfbProto rfb, int[] pixels24, int x, int y, int w, int h, byte[] src, int[] palette) {

		int dx, dy, n;
		int i = y * rfb.framebufferWidth + x;
		int rowBytes = (w + 7) / 8;
		byte b;

		for (dy = 0; dy < h; dy++) {
			for (dx = 0; dx < w / 8; dx++) {
				b = src[dy * rowBytes + dx];
				for (n = 7; n >= 0; n--)
					pixels24[i++] = palette[b >> n & 1];
			}
			for (n = 7; n >= 8 - w % 8; n--) {
				pixels24[i++] = palette[src[dy * rowBytes + dx] >> n & 1];
			}
			i += (rfb.framebufferWidth - w);
		}
	}
	
	void decodeGradientData(RfbProto rfb, int[] pixels24, int x, int y, int w, int h, byte[] buf) {

		int dx, dy, c;
		byte[] prevRow = new byte[w * 3];
		byte[] thisRow = new byte[w * 3];
		byte[] pix = new byte[3];
		int[] est = new int[3];

		int offset = y * rfb.framebufferWidth + x;

		for (dy = 0; dy < h; dy++) {

			/* First pixel in a row */
			for (c = 0; c < 3; c++) {
				pix[c] = (byte) (prevRow[c] + buf[dy * w * 3 + c]);
				thisRow[c] = pix[c];
			}
			pixels24[offset++] = (pix[0] & 0xFF) << 16 | (pix[1] & 0xFF) << 8 | (pix[2] & 0xFF);

			/* Remaining pixels of a row */
			for (dx = 1; dx < w; dx++) {
				for (c = 0; c < 3; c++) {
					est[c] = ((prevRow[dx * 3 + c] & 0xFF) + (pix[c] & 0xFF) - (prevRow[(dx - 1) * 3 + c] & 0xFF));
					if (est[c] > 0xFF) {
						est[c] = 0xFF;
					} else if (est[c] < 0x00) {
						est[c] = 0x00;
					}
					pix[c] = (byte) (est[c] + buf[(dy * w + dx) * 3 + c]);
					thisRow[dx * 3 + c] = pix[c];
				}
				pixels24[offset++] = (pix[0] & 0xFF) << 16 | (pix[1] & 0xFF) << 8 | (pix[2] & 0xFF);
			}

			System.arraycopy(thisRow, 0, prevRow, 0, w * 3);
			offset += (rfb.framebufferWidth - w);
		}
	}
}
