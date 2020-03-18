package com.scoreunit.rfb.service;

import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 'The RFB Protocol' documentation, page 20,
 * by Tristan Richardson, RealVNC Ltd.
 * <p>
 * Version 3.8, Last updated 26 November 2010
 *
 * @author igor.delac@gmail.com
 *
 */
public class SetPixelFormat {

	public byte bitsPerPixel, depth, bigEndianFlag, trueColorFlag;
	
	public short redMax, greenMax, blueMax;
	
	public byte redShift, greenShift, blueShift;

	/**
	 * Create new SetPixelFormat message.
	 * 
	 * @param bitsPerPixel		-	bits-per-pixel is the number of bits used for each pixel value on the wire.
	 * 								<p>
	 * 								This must be greater than or equal to the depth which is the number of useful bits in the pixel value.
	 * 								<p> 
	 * 								Currently bits-per-pixel must be 8, 16 or 32.
	 * @param depth				-	depth is the number of useful bits in the pixel value
	 * @param bigEndianFlag		-	Big-endian-flag is non-zero (true) if multi-byte pixels are interpreted as big endian.
	 * 								<p> 
	 * 								Of course this is meaningless for 8 bits-per-pixel.
	 * @param trueColorFlag		-	If true-colour-flag is non-zero (true) then the last six items specify how to extract the red, green and blue intensities from the pixel value
	 * @param redMax			-	Red-max is the maximum red value
	 * @param greenMax			-	Green-max is the maximum green value
	 * @param blueMax			-	Blue-max is the maximum blue value
	 * @param redShift			-	Red-shift is the number of shifts needed to get the red value in a pixel to the least significant bit
	 * @param greenShift		-	Green-shift is the number of shifts needed to get the green value in a pixel to the least significant bit
	 * @param blueShift			-	Blue-shift is the number of shifts needed to get the blue value in a pixel to the least significant bit
	 */
	public SetPixelFormat(byte bitsPerPixel, byte depth, byte bigEndianFlag, byte trueColorFlag, short redMax,
			short greenMax, short blueMax, byte redShift, byte greenShift, byte blueShift) {

		this.bitsPerPixel = bitsPerPixel;
		this.depth = depth;
		this.bigEndianFlag = bigEndianFlag;
		this.trueColorFlag = trueColorFlag;
		this.redMax = redMax;
		this.greenMax = greenMax;
		this.blueMax = blueMax;
		this.redShift = redShift;
		this.greenShift = greenShift;
		this.blueShift = blueShift;
	}
	
	/**
	 * A default pixel format for RGB display, where red value takes most significant byte, and blue value is least significant byte.
	 */
	public SetPixelFormat() {
		
		this.bitsPerPixel = (byte) Toolkit.getDefaultToolkit().getColorModel().getPixelSize();
		this.depth = bitsPerPixel;
		this.bigEndianFlag = 0;
		this.trueColorFlag = 1;
		
		this.redMax   = (short) 255;
		this.greenMax = (short) 255;
		this.blueMax  = (short) 255;
		
		this.redShift   = 16;
		this.greenShift = 8;
		this.blueShift  = 0;
		
		if (this.bitsPerPixel == 24) {
			
			/*
			 * VNC viewers do not support color mode of 24-bits.
			 */
			
			this.bitsPerPixel = 32;
			this.depth = this.bitsPerPixel;
		}
		
		if (this.bitsPerPixel == 16) {
			
			/*
			 * Just in case that display is a 16-bit color mode.
			 * Use appropriate maximum color values and bit positions at which 
			 * each color value begins.
			 */
			
			this.redMax   = (byte) 0x1F;
			this.greenMax = (byte) 0x3F;
			this.blueMax  = (byte) 0x1F;
			
			this.redShift   = 11;
			this.greenShift = 5;
			this.blueShift  = 0;
		}
	}
	
	public static void write(final OutputStream outputStream, final SetPixelFormat message) throws IOException {
		
		final DataOutputStream out = new DataOutputStream(outputStream);
		
		out.write(message.bitsPerPixel);
		out.write(message.depth);
		out.write(message.bigEndianFlag);
		out.write(message.trueColorFlag);
		out.writeShort(message.redMax);
		out.writeShort(message.greenMax);
		out.writeShort(message.blueMax);
		out.write(message.redShift);
		out.write(message.greenShift);
		out.write(message.blueShift);
		out.write(new byte[]{0, 0, 0}); // Padding.		
	}
	
	public static SetPixelFormat read(final InputStream inputStream) throws IOException {
		
		final DataInputStream in = new DataInputStream(inputStream);
		
		final SetPixelFormat setPixelFormat = new SetPixelFormat();
		
		setPixelFormat.bitsPerPixel = in.readByte();
		setPixelFormat.depth = in.readByte();
		setPixelFormat.bigEndianFlag = in.readByte();
		setPixelFormat.trueColorFlag = in.readByte();
		
		setPixelFormat.redMax = in.readShort();
		setPixelFormat.greenMax = in.readShort();
		setPixelFormat.blueMax = in.readShort();
		
		setPixelFormat.redShift = in.readByte();
		setPixelFormat.greenShift = in.readByte();
		setPixelFormat.blueShift = in.readByte();
		
		in.read(new byte[3]); // Padding.
		
		return setPixelFormat;
	}

	public static SetPixelFormat default32bit() {

		return new SetPixelFormat(
				(byte) 32			// bits per pixel
				, (byte) 24			// depth
				, (byte) 0			// big endian
				, (byte) 1			// true color
				, (short) 255, (short) 255, (short) 255	// red, green, blue max.
				, (byte) 16, (byte) 8, (byte) 0	// red, green, blue shift.
				);
	}
}
