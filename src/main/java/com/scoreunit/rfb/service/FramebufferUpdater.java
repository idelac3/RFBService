package com.scoreunit.rfb.service;

import java.awt.AWTException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

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
import com.scoreunit.rfb.screen.ScreenCaptureInterface;
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
	 * Instance of {@link ScreenCaptureInterface}, to capture image of screen or part of screen.
	 */
	private final ScreenCaptureInterface screenCapture;

	/**
	 * This can speed up the encoding of the changed part of the screen,
	 * by forcing encoding using more CPU cores.
	 */
	private final ExecutorService executor;

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
		
		this.screenCapture = new ScreenCapture();
		
		this.latch = new CountDownLatch(1);

		this.executor = Executors.newFixedThreadPool(4);
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

		long lastDelta = DELAY;

		try {
			
			while (this.running == true) {				
	
				//
				// Wait for frame buffer update request message.
				//
				
				final List<FramebufferUpdateRequest> incomingRequests = new ArrayList<>();

				// Take one from the queue, and then drain the queue into the array list.
				// This will ensure that the queue is empty.
				// If the client starts flooding
				// with the framebuffer update requests, ignore all but the last request.
				incomingRequests.add(this.updateRequests.take());

				this.updateRequests.drainTo(incomingRequests);

				// Remove all null elements (if any).
				incomingRequests.removeIf(Objects::isNull);

				// Go back, and wait again for update request(s).
				if (incomingRequests.size() == 0) {

					TimeUnit.MILLISECONDS.sleep(DELAY);

					continue;
				}

				// Get that last update request. Previous requests are ignored.
				// This prevents the flooding from the client side.
				final FramebufferUpdateRequest updateRequest = incomingRequests.get(incomingRequests.size() - 1);

				if (this.loadingState == true) {

					//
					// On first message, welcome VNC client with 'Loading ...' splash screen.
					//
										
					this.framebufferUpdateLoading();

					this.loadingState = false;
					
					// TimeUnit.SECONDS.sleep(4); // Give user few seconds to read welcome message.
				}
				else if (this.richCursorSent == false &&
						SelectEncoder.containsEncoding(Encodings.RICH_CURSOR, this.clientEncodings) == true) {
					
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

					long startedAt = System.currentTimeMillis();
					boolean updated = this.framebufferUpdate(updateRequest);
					long endedAt = System.currentTimeMillis();

					long delta = endedAt - startedAt;

					long delay = DELAY - delta;

					// It might happen that method returns false, if screen image did not change.
					if (updated == false) {
					
						// Put back frame buffer update request in queue.
						// Take it again after DELAY time period.
						this.updateRequests.put(updateRequest);

						if (delay > 0) {

							TimeUnit.MILLISECONDS.sleep(delay);
						}
						else {

							if (Math.abs(delta - lastDelta) > DELAY) {

								log.warn(String.format("Took %d msec. to process framebuffer update request.", delta));
							}

							lastDelta = delta;
						}
					}
				}
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
				return this.screenCapture.getScreenshot(x, y, w, h);
			}

			// Return full screen image.
			return this.screenCapture.getScreenshot();
		} catch (final Exception exception) {

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
		
		this.lastEncoder = SelectEncoder.selectEncoder(this.lastEncoder, this.clientEncodings, this.preferredEncodings);
		
		return this.lastEncoder;
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
	 * @throws IOException if the network connection breaks
	 */
	private boolean framebufferUpdate(final FramebufferUpdateRequest updateRequest) throws IOException {

		// Find suitable encoder for frame buffer update response.
		final EncodingInterface encoder = selectEncoder();
		
		// Indicator if VNC client needs full frame buffer data (screen image).
		final boolean fullUpdate = (updateRequest.incremental == 0);
		
		if (fullUpdate == true) {

			// This will enforce method getChangedTiles() to return list with complete screen image.
			this.lastImage = null;
		}
		
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

		// Store here the Future instances for each encoded tile.
		final List<Future<ByteArrayOutputStream>> tasks = new ArrayList<>();
		final CountDownLatch latch = new CountDownLatch(tiles.size());
		for (final Tile tile : tiles) {

			// Encode in another thread, each tile.
			final Future<ByteArrayOutputStream> future = this.executor.submit( () -> {

				final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				final DataOutputStream dataOut0 = new DataOutputStream(bOut);

				dataOut0.writeShort(tile.xPos);
				dataOut0.writeShort(tile.yPos);
				dataOut0.writeShort(tile.width);
				dataOut0.writeShort(tile.height);
				dataOut0.writeInt(encoder.getType());

				final byte[] encodedImage = encoder.encode(new TrueColorImage(tile.raw(), tile.width, tile.height), this.pixelFormat);
				dataOut0.write(encodedImage);

				latch.countDown();

				return bOut;
			});

			tasks.add(future);
		}

		try {

			// Wait for the all tasks / encoders to finish encoding.
			latch.await();
			for (final Future<ByteArrayOutputStream> future : tasks) {

				// Write out to the RFB client the result.
				final ByteArrayOutputStream bOut = future.get();
				dataOut.write(bOut.toByteArray());
			}
		}
		catch (final InterruptedException | ExecutionException ex) {
			log.error(String.format("Interrupted while encoding." ), ex);
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
				
		final int width, height;
		
		if (this.screenClip != null) {
		
			width  = this.screenClip.width;
			height = this.screenClip.height;
		}
		else {
			
			width = this.screenCapture.getScreenWidth();
			height = this.screenCapture.getScreenHeight();
		}
		
		if (width == -1 || height == -1) {
		
			// Terminate here if screen dimension is not available.
			return;
		}
		
		final TrueColorImage loadingImage = LoadingResource.get(width, height);
		
		final DataOutputStream dataOut = new DataOutputStream(this.out);
		
		dataOut.write(0); // FrameBufferUpdate message type.
		dataOut.write(0); // Padding.
		
		final List<Tile> tiles = Tile.build(loadingImage);
		
		short numberOfRectangles = (short) tiles.size();
		
		dataOut.writeShort(numberOfRectangles);
			
		for (final Tile tile : tiles) {
			
			final short xPos = tile.xPos, yPos = tile.yPos
					, tileWidth = tile.width, tileHeight = tile.height;
			
			dataOut.writeShort(xPos);
			dataOut.writeShort(yPos);
			dataOut.writeShort(tileWidth);
			dataOut.writeShort(tileHeight);		
				
			this.lastEncoder = SelectEncoder.selectEncoder(this.lastEncoder, this.clientEncodings, this.preferredEncodings);
			dataOut.writeInt(this.lastEncoder.getType());
			
			dataOut.write(this.lastEncoder.encode(new TrueColorImage(tile.raw(), tileWidth, tileHeight), this.pixelFormat));
		}
		
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
		
		final byte[] cursorData = encoder.encode(null, pixelFormat);
		
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
