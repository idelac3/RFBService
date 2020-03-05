package com.scoreunit.rfb.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 'The RFB Protocol' documentation, page 22,
 * by Tristan Richardson, RealVNC Ltd.
 * <p>
 * Version 3.8, Last updated 26 November 2010
 *
 * @author igor.delac@gmail.com
 *
 */
class FramebufferUpdateRequest {

	/**
	 * The server assumes that the client keeps a copy of all parts of the framebuffer in which
	 * it is interested. This means that normally the server only needs to send incremental
	 * updates to the client.
	 * <p>
	 * However, if for some reason the client has lost the contents of a particular area which it
	 * needs, then the client sends a FramebufferUpdateRequest with incremental set to zero
	 * (false). This requests that the server send the entire contents of the specified area as
	 * soon as possible.
	 */
	public byte incremental;
	
	/**
	 * Notifies the server that the client is interested in the area of the framebuffer specified
	 * by x-position, y-position, width and height. The server usually responds to a Frame-
	 * bufferUpdateRequest by sending a FramebufferUpdate. Note however that a single
	 * FramebufferUpdate may be sent in reply to several FramebufferUpdateRequests.
	 */
	public short xPosition, yPosition, width, height;
	
	/**
	 * Create new FramebufferUpdateRequest object.
	 * 
	 * @param 
	 */
	public FramebufferUpdateRequest(final byte incremental
			, final short xPosition, final short yPosition
			, final short width, final short height
			) {

		this.incremental = incremental;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.width = width;
		this.height = height;		
	}
	
	/**
	 * Notifies the server that the client is interested in the area of the framebuffer specified
	 * by x-position, y-position, width and height. The server usually responds to a Frame-
	 * bufferUpdateRequest by sending a FramebufferUpdate. Note however that a single
	 * FramebufferUpdate may be sent in reply to several FramebufferUpdateRequests.
	 * <p>
	 * The server assumes that the client keeps a copy of all parts of the framebuffer in which
	 * it is interested. This means that normally the server only needs to send incremental
	 * updates to the client.
	 * <p>
	 * However, if for some reason the client has lost the contents of a particular area which it
	 * needs, then the client sends a FramebufferUpdateRequest with incremental set to zero
	 * (false). This requests that the server send the entire contents of the specified area as
	 * soon as possible. The area will not be updated using the CopyRect encoding.
	 * <p>
	 * 
	 * @param inputStream		-	{@link InputStream} to read raw data from
	 * 
	 * @return	instance of {@link FramebufferUpdateRequest} message
	 * 
	 * @throws IOException	if connections breaks
	 */
	public static FramebufferUpdateRequest read(final InputStream inputStream) throws IOException {
		
		final DataInputStream in = new DataInputStream(inputStream);
		
		byte incremental = in.readByte();
		short xPosition = in.readShort();
		short yPosition = in.readShort();
		short width = in.readShort();
		short height = in.readShort();
		
		return new FramebufferUpdateRequest(incremental, xPosition, yPosition, width, height);
	}
}
