package com.scoreunit.rfb.service;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.scoreunit.rfb.encoding.Encodings;
import com.scoreunit.rfb.screen.ScreenClip;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FramebufferUpdaterTest {

	@Test
	public void test_01_basic() throws InterruptedException {
		
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		
		final Socket socket = new Socket();
		final RFBConfig config = new RFBConfig();
		final ClientHandler clientHandler = new ClientHandler(socket, config);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		final FramebufferUpdater updater = new FramebufferUpdater(clientHandler, out);

		executor.submit(updater);
		
		long startedAt = System.currentTimeMillis();
		
		while (updater.isRunning() == false) {
			
			Thread.yield();
			TimeUnit.MILLISECONDS.sleep(FramebufferUpdater.DELAY);
			
			long now = System.currentTimeMillis();
			long delta = now - startedAt;
			
			// Break loop if updater boot time is too long.
			assertTrue(delta < 10 * FramebufferUpdater.DELAY);
		}
		
		assertTrue(updater.isRunning());
		
		updater.setClientEncodings(new SetEncodings(new int[]{Encodings.RAW}));
		updater.setPixelFormat(SetPixelFormat.default32bit());
		updater.setPreferredEncodings(new int[]{Encodings.HEXTILE, Encodings.ZLIB, Encodings.RAW});
		updater.setScreenClip(new ScreenClip((short) 0, (short) 0, (short) 640, (short) 480));
		
		// Now fill updater with framebuffer requests ...
		for (int i = 0; i < 10 ; i++) {
			
			// Some bad value ...
			short xPosition = 0, yPosition = -5, width = 10000, height = 20000;
			
			updater.update(new FramebufferUpdateRequest((byte) (i % 2), xPosition, yPosition, width, height));
			
			TimeUnit.MILLISECONDS.sleep(FramebufferUpdater.DELAY / 2);
		}
		
		assertTrue(updater.isRunning());
		
		updater.terminate();
		
		startedAt = System.currentTimeMillis();
		
		while (updater.isRunning() == true) {
			
			Thread.yield();
			TimeUnit.MILLISECONDS.sleep(FramebufferUpdater.DELAY);
			
			long now = System.currentTimeMillis();
			long delta = now - startedAt;
			
			// Break loop if updater boot time is too long.
			assertTrue(delta < 10 * FramebufferUpdater.DELAY);
		}
		
		assertTrue(updater.isRunning() == false);
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.SECONDS);
	}

	@Test
	public void test_02_unsupportedClientEncodings() throws InterruptedException {
		
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		
		final Socket socket = new Socket();
		final RFBConfig config = new RFBConfig();
		final ClientHandler clientHandler = new ClientHandler(socket, config);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		final FramebufferUpdater updater = new FramebufferUpdater(clientHandler, out);

		executor.submit(updater);
		
		long startedAt = System.currentTimeMillis();
		
		while (updater.isRunning() == false) {
			
			Thread.yield();
			TimeUnit.MILLISECONDS.sleep(FramebufferUpdater.DELAY);
			
			long now = System.currentTimeMillis();
			long delta = now - startedAt;
			
			// Break loop if updater boot time is too long.
			assertTrue(delta < 10 * FramebufferUpdater.DELAY);
		}
		
		assertTrue(updater.isRunning());
		
		updater.setClientEncodings(new SetEncodings(new int[]{Encodings.RRE}));
		updater.setPixelFormat(SetPixelFormat.default32bit());
		updater.setScreenClip(new ScreenClip((short) 0, (short) 0, (short) 640, (short) 480));
		
		// Now fill updater with framebuffer requests ...
		for (int i = 0; i < 10 ; i++) {
			
			// Some bad value ...
			short xPosition = 0, yPosition = -5, width = 10000, height = 20000;
			
			updater.update(new FramebufferUpdateRequest((byte) (i % 2), xPosition, yPosition, width, height));
			
			TimeUnit.MILLISECONDS.sleep(FramebufferUpdater.DELAY / 2);
		}
		
		assertTrue(updater.isRunning());
		
		updater.terminate();
		
		startedAt = System.currentTimeMillis();
		
		while (updater.isRunning() == true) {
			
			Thread.yield();
			TimeUnit.MILLISECONDS.sleep(FramebufferUpdater.DELAY);
			
			long now = System.currentTimeMillis();
			long delta = now - startedAt;
			
			// Break loop if updater boot time is too long.
			assertTrue(delta < 10 * FramebufferUpdater.DELAY);
		}
		
		assertTrue(updater.isRunning() == false);
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.SECONDS);
	}
	

	@Test
	public void test_03_badScreenClip() throws InterruptedException {
		
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		
		final ScreenClip[] clips = new ScreenClip[] {
				new ScreenClip((short) -1, (short) 0, (short) 10000, (short) 10000),
				new ScreenClip((short) 0, (short) -1, (short) 10000, (short) 10000),
				new ScreenClip((short) 0, (short) 0, (short) 10000, (short) -10000),
				new ScreenClip((short) 0, (short) 0, (short) -10000, (short) 10000),
				new ScreenClip((short) -4, (short) -4, (short) -10000, (short) -10000),
		};
		
		final Socket socket = new Socket();
		final RFBConfig config = new RFBConfig();
		final ClientHandler clientHandler = new ClientHandler(socket, config);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		final FramebufferUpdater updater = new FramebufferUpdater(clientHandler, out);

		executor.submit(updater);
		
		long startedAt = System.currentTimeMillis();
		
		while (updater.isRunning() == false) {
			
			Thread.yield();
			TimeUnit.MILLISECONDS.sleep(FramebufferUpdater.DELAY);
			
			long now = System.currentTimeMillis();
			long delta = now - startedAt;
			
			// Break loop if updater boot time is too long.
			assertTrue(delta < 10 * FramebufferUpdater.DELAY);
		}
		
		assertTrue(updater.isRunning());
		
		updater.setClientEncodings(new SetEncodings(new int[]{Encodings.RRE}));
		updater.setPixelFormat(SetPixelFormat.default32bit());
		updater.setScreenClip(new ScreenClip((short) 0, (short) 0, (short) 10000, (short) 10000));
		
		// Now fill updater with framebuffer requests ...
		for (int i = 0; i < 10 ; i++) {
			
			// Some bad value ...
			short xPosition = 0, yPosition = -5, width = 10000, height = 20000;
			
			updater.update(new FramebufferUpdateRequest((byte) (i % 2), xPosition, yPosition, width, height));
			
			TimeUnit.MILLISECONDS.sleep(FramebufferUpdater.DELAY / 2);
			
			final ScreenClip badClip = clips[i % clips.length];			
			updater.setScreenClip(badClip);
			
			if (i > clips.length) {
				
				updater.setScreenClip(null);
			}
		}
		
		assertTrue(updater.isRunning());
		
		updater.terminate();
		
		startedAt = System.currentTimeMillis();
		
		while (updater.isRunning() == true) {
			
			Thread.yield();
			TimeUnit.MILLISECONDS.sleep(FramebufferUpdater.DELAY);
			
			long now = System.currentTimeMillis();
			long delta = now - startedAt;
			
			// Break loop if updater boot time is too long.
			assertTrue(delta < 10 * FramebufferUpdater.DELAY);
		}
		
		assertTrue(updater.isRunning() == false);
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.SECONDS);
	}
}
