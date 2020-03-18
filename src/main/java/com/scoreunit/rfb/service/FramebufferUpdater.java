package com.scoreunit.rfb.service;

import java.awt.AWTException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scoreunit.rfb.encoding.EncodingInterface;
import com.scoreunit.rfb.encoding.Encodings;
import com.scoreunit.rfb.encoding.HextileEncoder;
import com.scoreunit.rfb.encoding.RawEncoder;
import com.scoreunit.rfb.encoding.RichCursorEncoder;
import com.scoreunit.rfb.encoding.Tile;
import com.scoreunit.rfb.image.TrueColorImage;
import com.scoreunit.rfb.screen.LoadingResource;
import com.scoreunit.rfb.screen.ScreenCapture;
import com.scoreunit.rfb.screen.ScreenClip;

/**
 * 'The RFB Protocol' documentation, page 28,
 * by Tristan Richardson, RealVNC Ltd.
 * <p>
 * Version 3.8, Last updated 26 November 2010
 * 
 * @author igor.delac@gmail.com
 *
 */
class FramebufferUpdater implements Runnable {
	
	public final static Logger log = LoggerFactory.getLogger(FramebufferUpdater.class);
	
	/**
	 * Delay in millisec. which prevents framebuffer update flooding.
	 */
	public static final long DELAY = 100;
	
	/**
	 * Output stream where to write frame buffer updates.
	 */
	private OutputStream out;

	/**
	 * Flag to control this thread.
	 */
	private boolean running;
	
	/**
	 * A queue for request message from VNC client.
	 * <p>
	 * Here {@link ClientHandler} thread will put {@link FramebufferUpdateRequest}
	 * messages, and {@link FramebufferUpdater} will consume them.
	 */
	private BlockingQueue<FramebufferUpdateRequest> updateRequests;
	
	/**
	 * CountDown latch is used to properly start this thread.
	 * <p>
	 * Another thread will wait for the latch to count down,
	 * meaning that this thread has successfully spawned.
	 */
	private final CountDownLatch latch;
	
	/**
	 * Reference to parent client handler which created this updater instance.
	 */
	private final ClientHandler clientHandler;
	
	/**
	 * Flag which is used to send initial screen, from 'loading.raw' file
	 * to VNC client, before real screen is captured.
	 */
	private boolean loadingState;
	
	/**
	 * Flag will tell if this updater annonced to VNC client
	 * data about rich cursor.
	 */
	private boolean richCursorSent;
	
	/**
	 * A list of supported VNC client encodings. Most VNC clients supports {@link Encodings#RAW},
	 * {@link Encodings#HEXTILE} and {@link Encodings#ZRLE}.
	 * <p>
	 * Note that this list might contain also pseudo-encodings for cursor rendering, desktop resize, etc.
	 */
	private int[] clientEncodings;

	/**
	 * A list of preferred RFB service encodings. All VNC clients must supports {@link Encodings#RAW} at least.
	 * <p>
	 * Preferred encodings, if set,
	 *  are used to override client supported encoding list, {@link #clientEncodings}.
	 * <p>
	 * However client supportedf encoding list must contain also encoder that is chosen.  
	 */
	private int[] preferredEncodings;

	/**
	 * Save reference to last used encoder, in case that
	 * client does not change very of encoding scheme.
	 */
	private EncodingInterface lastEncoder;
	
	/**
	 * Divide screen image into tiles, 
	 * and keep last frame buffer image.
	 * <p>
	 * This is used to update tiles (parts of screen image) 
	 * that are changed.
	 */
	private List<Tile> lastImage;
	
	/**
	 * If set, this object will define which area of screen should be 
	 * presented to VNC client.
	 * <p>
	 * This is useful if only primary screen should be shared, in multi-monitor setups, etc. 
	 */
	private ScreenClip screenClip;
	
	/**
	 * Keep here desired VNC client pixel format information.
	 * <p>
	 * This will be used by frame buffer encoders, to properly 
	 * translate pixels, eg. from 32-bit source image, into 8-bit image, etc.
	 */
	private SetPixelFormat pixelFormat;
	
	/**
	 * Create new instance of updater.
	 * 
	 * @param clientHandler	-	reference to client handler
	 * @param out			-	{@link OutputStream} object obtained from client handler's socket object
	 */
	public FramebufferUpdater(final ClientHandler clientHandler, final OutputStream out) {

		this.clientHandler = clientHandler;
		this.out = out;

		this.running = false;
		
		this.updateRequests = new LinkedBlockingQueue<>();
	
		this.loadingState = true;
		this.richCursorSent = true;

		this.lastEncoder = null;
				
		// Initially support only RAW encoding.
		this.clientEncodings = new int[] {Encodings.RAW};
		
		this.lastImage = null;
		
		this.screenClip = null;
		
		// Default pixel format, 32-bit true image.
		this.pixelFormat = SetPixelFormat.default32bit();
		
		this.latch = new CountDownLatch(1);
	}
	
	/**
	 * Set desired VNC client pixel format information.
	 * <p>
	 * This will be used by frame buffer encoders, to properly 
	 * translate pixels, eg. from 32-bit source image, into 8-bit image, etc.
	 * 
	 * @param	newPixelFormat		-	instance of {@link SetPixelFormat}, as received from VNC client
	 */
	public void setPixelFormat(final SetPixelFormat newPixelFormat) {
	
		this.pixelFormat = newPixelFormat;
		
		this.richCursorSent = false; // This will cause main loop to (re)send cursor information.
	}
	
	/**
	 * Set {@link ScreenClip}. Screen clip object defines which area of screen should be 
	 * presented to VNC client.
	 * <p>
	 * This is useful if only primary screen should be shared, in multi-monitor setups, etc. 
	 */
	public void setScreenClip(final ScreenClip clip) {
		
		this.screenClip = clip;
	}
	
	/**
	 * Tell frame buffer updater thread that there is a new 
	 * frame buffer update request.
	 * 
	 * @param request	-	an {@link FramebufferUpdateRequest} message
	 * 
	 * @throws InterruptedException 
	 */
	public void update(final FramebufferUpdateRequest request) throws InterruptedException {
		
		this.updateRequests.put(request);
	}

	/**
	 * Update list of supported encoding schema(s) by VNC client.
	 * <p>
	 * Note that in this list are also <i>pseudo-encodings</i> for cursor rendering, desktop resize, etc.
	 *  
	 * @param setEncodingsRequest	-	list of encodings from {@link SetEncodings} message from VNC client
	 */
	public void setClientEncodings(final SetEncodings setEncodingsRequest) {
	
		this.clientEncodings = setEncodingsRequest.encodingType;		
	}
	
	/**
	 * Set a list of preferred encoding schema(s) by RFB service.
	 * <p>
	 * This list is used to override order of client supported encodings, {@link #clientEncodings}.
	 * 
	 * @param preferredEncodings 	-	list of encodings from {@link RFBConfig} instance
	 */
	public void setPreferredEncodings(final int[] preferredEncodings) {
	
		this.preferredEncodings = preferredEncodings;		
	}
	
	@Override
	public void run() {

		this.running = true;
		this.latch.countDown();
		
		try {
			
			while (this.running == true) {				
	
				//
				// Wait for frame buffer update request message.
				//
				
				final FramebufferUpdateRequest updateRequest = this.updateRequests.poll(DELAY, TimeUnit.MILLISECONDS);

				// Here be careful to check updateRequest object against null value,
				//  since frame buffer updater thread is started in parallel with client handler thread.
				if (updateRequest == null) {
				
					continue; // go back, and wait again for update request.
				}
				
				if (this.loadingState == true) {

					//
					// On first message, welcome VNC client with 'Loading ...' splash screen.
					//
										
					this.framebufferUpdateLoading();

					this.loadingState = false;
				}
				else if (this.richCursorSent == false &&
						this.pixelFormat.bitsPerPixel == 32) {
					
					//
					// Here is routine to send rich cursor data, which is available if pixel format is set to 32-bit.
					//
					
					this.framebufferUpdateRichCursor();
					
					this.richCursorSent = true;
				}
				else {

					//
					// Now create new frame buffer update message, and write to socket.
					//
					
					boolean updated = this.framebufferUpdate(updateRequest);
				
					// It might happen that method returns false, if screen image did not change.
					if (updated == false) {
					
						// Put back frame buffer update request in queue.
						// Take it again after DELAY time period.
						this.updateRequests.put(updateRequest);
					}
				}				
						
				TimeUnit.MILLISECONDS.sleep(DELAY);
			}
		}
		catch (final Exception exception) {
			
			// On any problem, just terminate this thread.
		}
		
		this.running = false;
		this.clientHandler.terminate();
	}

	/**
	 * Take image of screen (clip).
	 * 
	 * @return	an 32-bit ARGB image.
	 */
	private TrueColorImage getScreenImage() {
		
		try {
			
			if (this.screenClip != null) {
				
				int x = this.screenClip.xPos
						, y = this.screenClip.yPos
						, w = this.screenClip.width
						, h = this.screenClip.height;

				// Return region (clip) of screen image.
				return ScreenCapture.getScreenshot(x, y, w, h);
			}

			return ScreenCapture.getScreenshot();
		} catch (final AWTException exception) {

			log.error("Unable to capture screen image.", exception);			
		}
		
		return null;
	}
	
	/**
	 * Get part of screen image that has changed.
	 * <p>
	 * Also update {@link FramebufferUpdater#lastImage} member reference.
	 * 
	 * @return	{@link List} of {@link Tile} objects that are changed comparing to last invocation
	 */
	private List<Tile> getChangedTiles() {
		
		final TrueColorImage image = getScreenImage();
		
		if (this.lastImage == null) {
					
			this.lastImage = Tile.build(image.raw, image.width, image.height);
			
			return this.lastImage;
		}
		
		final List<Tile> newImage = Tile.build(image.raw, image.width, image.height);
		
		if (newImage.size() != this.lastImage.size()) {
		
			this.lastImage = newImage;
			
			return this.lastImage;
		}
		
		final List<Tile> result = new ArrayList<>();
		
		for (int index = 0 ; index < newImage.size() ; index++) {
		
			if (this.lastImage.get(index).equals(newImage.get(index)) == false) {
				
				// Found tile that's changed.
				result.add(newImage.get(index));
			}
		}
		
		// Update reference with current screen image.
		this.lastImage = newImage;
		
		return result;
	}
	
	/**
	 * Method will select appropriate {@link EncodingInterface} instance,
	 * based on {@link #clientEncodings} and {@link #preferredEncodings}.
	 * <p>
	 * It will also consider {@link #lastEncoder} reference.
	 * 
	 * @return	on of {@link RawEncoder}, {@link HextileEncoder}, etc.
	 * 
	 */
	private EncodingInterface selectEncoder() {
		
		// Reuse previously used encoder, if already set,
		// and VNC client supports it.
		if (this.lastEncoder != null && 
				containsEncoding(this.lastEncoder.getType(), this.clientEncodings) == true) {
			
			return this.lastEncoder;
		}
		
		// Use RFB service preferred encoding type list.
		if (this.preferredEncodings != null) {
						
			// Look if some of preferred encodings is present in VNC client supported list.
			for (int encoding : this.preferredEncodings) {
				
				if (containsEncoding(encoding, this.clientEncodings) == true) {
					
					this.lastEncoder = Encodings.newInstance((byte) encoding);
					
					if (this.lastEncoder == null) {
						
						this.lastEncoder = new RawEncoder();
					}
					
					log.info(String.format("Selected preferred encoder: '%s'.", this.lastEncoder.getClass().getSimpleName()));
					
					return this.lastEncoder;
				}
			}
		}
		
		// Finally, use client list of supported encoding types.
		for (int encoding : this.clientEncodings) {

			this.lastEncoder = Encodings.newInstance((byte) encoding);
			
			if (this.lastEncoder != null) {
				
				log.info(String.format("Selected encoder by client: '%s'.", this.lastEncoder.getClass().getSimpleName()));
				
				return this.lastEncoder;
			}
		}
		
		log.info(String.format("Selected fall-back encoder: '%s'.", RawEncoder.class.getSimpleName()));
		
		// Fall-back, raw encoder return. If all of above fails to return result.
		return new RawEncoder();
	}
	
	/**
	 * Method will examine if list of encodings contain given encoding type.
	 * 
	 * @param type			-	desired encoding type to check if its in encoding list
	 * @param encodings		-	encoding list
	 * 
	 * @return	true if encoding type is found in list
	 */
	private boolean containsEncoding(final int type, final int[] encodings) {
	
		if (encodings == null) {
			
			return false;
		}
		
		for (int encodingType : encodings) {
			
			if (encodingType == type) {
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * A framebuffer update consists of a sequence of rectangles of pixel data which the client
	 * should put into its framebuffer. It is sent in response to a FramebufferUpdateRequest
	 * from the client. Note that there may be an indefinite period between the Framebuffer-
	 * UpdateRequest and the FramebufferUpdate.
	 * <p>
	 * If the client has not lost any contents of the area in which it is interested, then it
	 * sends a FramebufferUpdateRequest with incremental set to non-zero (true). If and
	 * when there are changes to the specified area of the framebuffer, the server will send a
	 * FramebufferUpdate. Note that there may be an indefinite period between the Frame-
	 * bufferUpdateRequest and the FramebufferUpdate.
	 * <p>
	 * @param updateRequest	-	received message {@link FramebufferUpdateRequest} from VNC client
	 * 
	 * @return	true if frame buffer was updated, or false if screen image did not change from last invocation
	 * 
	 * @throws IOException 
	 * @throws AWTException 
	 */
	private boolean framebufferUpdate(final FramebufferUpdateRequest updateRequest) throws IOException, AWTException {

		// Find suitable encoder for frame buffer update response.
		final EncodingInterface encoder = selectEncoder();
		
		//
		// Take current image of screen,
		// as list of tiles.
		// Update only tiles that are different comparing to last invocation.
		//
		
		final List<Tile> tiles = getChangedTiles();
		
		if (tiles.isEmpty() == true) {
			
			return false;
		}
		
		final DataOutputStream dataOut = new DataOutputStream(this.out);
		
		dataOut.write(0); // FrameBufferUpdate message type.
		dataOut.write(0); // Padding.
		
		short numberOfRectangles = (short) tiles.size();
		
		dataOut.writeShort(numberOfRectangles);

		for (final Tile tile : tiles) {
			
			dataOut.writeShort(tile.xPos);
			dataOut.writeShort(tile.yPos);
			dataOut.writeShort(tile.width);
			dataOut.writeShort(tile.height);
			dataOut.writeInt(encoder.getType());
			
			final byte[] encodedImage = encoder.encode(tile.raw(), tile.width, tile.height, this.pixelFormat);
			dataOut.write(encodedImage);
		}
		
		dataOut.flush();
		
		return true;
	}

	/**
	 * Helper method to write and send 'loading.png' image.
	 * <p>
	 * This will present VNC client a splash screen.
	 * 
	 * @throws IOException 
	 */
	private void framebufferUpdateLoading() throws IOException {
				
		final TrueColorImage loadingImage = LoadingResource.get();
		
		final DataOutputStream dataOut = new DataOutputStream(this.out);
		
		dataOut.write(0); // FrameBufferUpdate message type.
		dataOut.write(0); // Padding.
		
		short numberOfRectangles = 1;
		
		dataOut.writeShort(numberOfRectangles);
			
		short xPos = 0, yPos = 0
			, width = (short) loadingImage.width
			, height = (short) loadingImage.height; // For 'loading.png' image.
		
		dataOut.writeShort(xPos);
		dataOut.writeShort(yPos);
		dataOut.writeShort(width);
		dataOut.writeShort(height);
		dataOut.writeInt(Encodings.RAW);
			
		final RawEncoder encoder = new RawEncoder();
		
		dataOut.write(encoder.encode(loadingImage.raw, loadingImage.width, loadingImage.height, this.pixelFormat));
		
		dataOut.flush();
	}
	
	/**
	 * Helper method to write and send rich cursor image.
	 * <p>
	 * This will present VNC client to display mouse pointer to end user.
	 * 
	 * @throws IOException 
	 */
	private void framebufferUpdateRichCursor() throws IOException {
		
		final RichCursorEncoder encoder = new RichCursorEncoder();
		
		final byte[] cursorData = encoder.encode(null, 0, 0, pixelFormat);
		
		if (cursorData == null) {
			
			return;
		}
		
		final DataOutputStream dataOut = new DataOutputStream(this.out);
		
		dataOut.write(0); // FrameBufferUpdate message type.
		dataOut.write(0); // Padding.
		
		short numberOfRectangles = 1;
		
		dataOut.writeShort(numberOfRectangles);
			
		short xPos = 0, yPos = 0
			, width = 18
			, height = 18; // Depends on 'cursor*.raw' captured data.
		
		dataOut.writeShort(xPos);
		dataOut.writeShort(yPos);
		dataOut.writeShort(width);
		dataOut.writeShort(height);
		dataOut.writeInt(Encodings.RICH_CURSOR);
		
		dataOut.write(cursorData);
		
		dataOut.flush();
	}
	
	/**
	 * Start an framebuffer updater instance.
	 * 
	 * @throws InterruptedException
	 */
	public void start() throws InterruptedException {
		
		final Thread frameBufferUpdateThread = new Thread(this,
				String.format("%s-[%s]",
						FramebufferUpdater.class.getSimpleName(), this.clientHandler.toString()));
		frameBufferUpdateThread.start();
		
		this.latch.await(10, TimeUnit.SECONDS);
	}
	
	/**
	 * Stop this updater instance.
	 */
	public void terminate() {
		
		this.running = false;		
	}

	/**
	 * Check if this instance of updater is running.
	 * 
	 * @return	true if thread (main loop) is running
	 */
	public boolean isRunning() {
		
		return this.running;
	}
}
