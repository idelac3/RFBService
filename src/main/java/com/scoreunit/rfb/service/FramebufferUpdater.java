package com.scoreunit.rfb.service;

import java.awt.AWTException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.scoreunit.rfb.encoding.EncodingInterface;
import com.scoreunit.rfb.encoding.Encodings;
import com.scoreunit.rfb.encoding.RawEncoder;
import com.scoreunit.rfb.screen.LoadingResource;
import com.scoreunit.rfb.screen.ScreenCapture;

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

	/**
	 * Delay in millisec. which prevents framebuffer update flooding.
	 */
	public static final long DELAY = 100;
	
	private OutputStream out;
	
	private long lastUpdate;
	
	private boolean running;
	
	private FramebufferUpdateRequest updateRequest;
	
	private final CountDownLatch latch;
	
	private final String name;
	
	private boolean loadingState;
	
	public FramebufferUpdater(final String name, final OutputStream out) {

		this.name = name;
		this.out = out;
		this.lastUpdate = 0;
		this.running = false;
		
		this.updateRequest = null;
	
		this.loadingState = true;
		
		this.latch = new CountDownLatch(1);
	}
	
	/**
	 * Tell framebuffer updater thread that there is a new 
	 * framebuffer update request.
	 * 
	 * @param request
	 */
	public void update(final FramebufferUpdateRequest request) {
		
		this.updateRequest = request;
	}

	@Override
	public void run() {

		this.running = true;
		this.latch.countDown();
		
		try {
			
			while (this.running == true) {
				
				// Delay a bit framebuffer updates, in order to prevent flooding.
				long now = System.currentTimeMillis();
				
				if (now - lastUpdate < 100) {
					
					TimeUnit.MILLISECONDS.sleep(DELAY);
				}
	
				final FramebufferUpdateRequest updateRequest = this.updateRequest;
				
				// Another thread has to set framebuffer update request message.
				if (updateRequest != null) {
				
					//
					// Now create new frame buffer update message, and write to socket.
					//
					
					if (this.loadingState == true) {
						
						this.framebufferUpdateLoading();
					}
					else {
					
						this.framebufferUpdate(updateRequest);
					}
					
					this.updateRequest = null;
				}	
				
				lastUpdate = now;
			}
		}
		catch (final Exception excetion) {
			// On any problem, just terminate this thread.
		}
		
		this.running = false;
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
	 * @throws IOException 
	 * @throws AWTException 
	 */
	private void framebufferUpdate(final FramebufferUpdateRequest updateRequest) throws IOException, AWTException {

		// TODO this is simple RAW encoding impl.
		// TODO add more encodings.
		
		final EncodingInterface encoder = new RawEncoder();
		
		final DataOutputStream dataOut = new DataOutputStream(this.out);
		
		dataOut.write(0); // FrameBufferUpdate message type.
		dataOut.write(0); // Padding.
		
		short numberOfRectangles = 1;
		
		dataOut.writeShort(numberOfRectangles);
		
		// Use screen (primary) dimensions.
		short xPos = 0, yPos = 0
				, width = (short) ScreenCapture.getScreenWidth()
				, height = (short) ScreenCapture.getScreenHeight();

		// Send just part of screen as requested by VNC client.
		xPos = updateRequest.xPosition;
		yPos = updateRequest.yPosition;
		width = updateRequest.width;
		height = updateRequest.height;

		dataOut.writeShort(xPos);
		dataOut.writeShort(yPos);
		dataOut.writeShort(width);
		dataOut.writeShort(height);
		dataOut.writeInt(Encodings.RAW);
		
		final int[] imageSource = ScreenCapture.getScreenshot(xPos, yPos, width, height);
		
		final byte[] encodedImage = encoder.encode(imageSource, width, height);
		dataOut.write(encodedImage);
		
		dataOut.flush();
	}

	/**
	 * Helper method to write and send 'loading.raw' image.
	 * <p>
	 * This will present VNC client a splash screen.
	 * 
	 * @throws IOException 
	 */
	private void framebufferUpdateLoading() throws IOException {
		
		final DataOutputStream dataOut = new DataOutputStream(this.out);
		
		dataOut.write(0); // FrameBufferUpdate message type.
		dataOut.write(0); // Padding.
		
		short numberOfRectangles = 1;
		
		dataOut.writeShort(numberOfRectangles);
			
		short xPos = 0, yPos = 0
			, width = LoadingResource.getWidth()
			, height = LoadingResource.getHeight(); // For 'loading.raw' image.
		
		dataOut.writeShort(xPos);
		dataOut.writeShort(yPos);
		dataOut.writeShort(width);
		dataOut.writeShort(height);
		dataOut.writeInt(Encodings.RAW);
			
		final byte[] raw = LoadingResource.raw();
						
		dataOut.write(raw);
		
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
						FramebufferUpdater.class.getSimpleName(), this.name));
		frameBufferUpdateThread.start();
		
		this.latch.await(10, TimeUnit.SECONDS);
	}
	
	/**
	 * Stop this updater instance.
	 */
	public void terminate() {
		
		this.running = false;		
	}

	public boolean isRunning() {
		
		return this.running;
	}
}
