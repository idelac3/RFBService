package com.scoreunit.rfb.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.scoreunit.rfb.encoding.Encodings;
import com.scoreunit.rfb.screen.ScreenClip;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClientHandlerTest {

	private ExecutorService executor = Executors.newCachedThreadPool();

	@Test
	public void test_01_sessionWithVNCauth() throws IOException, InterruptedException, ExecutionException, TimeoutException {
	
		buildSession("blabla123", (byte) 0, null); 
	}
	
	@Test
	public void test_02_sessionWithoutPassword() throws IOException, InterruptedException, ExecutionException, TimeoutException {
	
		buildSession(null, (byte) 1, null); 
	}

	@Test
	public void test_03_sessionWithScreenClip() throws IOException, InterruptedException, ExecutionException, TimeoutException {
	
		short xPos = 0, yPos = 0, width = 640, height = 480;
		
		buildSession("top secret!", (byte) 1, new ScreenClip(xPos, yPos, width, height)); 
	}
	
	public void buildSession(final String password
			, final byte shareDesktopFlag
			, final ScreenClip clip) throws IOException, InterruptedException, ExecutionException, TimeoutException {
	
		final boolean withVNCauth = password != null;
		
		final int tcpPort = RFBServiceTest.randomPort();

		final CountDownLatch latch = new CountDownLatch(1);
		
		executor.submit( () -> {
			
			try {
				
				//
				// Simulate VNC client side.
				//
				
				final ServerSocket server = new ServerSocket(tcpPort);
				
				latch.countDown();
				
				final Socket client = server.accept();
				
				final DataInputStream in   = new DataInputStream(client.getInputStream());
				final DataOutputStream out = new DataOutputStream(client.getOutputStream());
				
				byte[] buff = new byte[12]; // protocol version.
				in.read(buff);				
				assertEquals("RFB 003.008\n", new String(buff));
				
				out.write(buff); // replay with RFB protocol version string.
				out.flush();
				
				// Possible security types: 1 - None, 2 - VNC auth.
				buff = new byte[2];
				in.read(buff); // VNC auth. in this example, since password is set
				
				if (withVNCauth == true) {
					
					assertArrayEquals(new byte[]{0x01, 0x02}, buff);
					
					out.write(0x02); // VNC client selects value '2 - VNC auth.'
				}
				else {
					
					assertArrayEquals(new byte[]{0x01, 0x01}, buff); // VNC client should select auth. value '1 - None'.
					
					out.write(0x01);	// VNC client selects value '1 - None' (no auth.) 
				}
				out.flush();
				
				if (withVNCauth == true) {
				
					buff = new byte[16];
					in.read(buff); // wait for challenge from server.
					
					out.write(DESCipher.enc(password, buff)); // respond with DES encrypted challenge using secret value as key.
					out.flush();
				}
				
				buff = new byte[4];
				in.read(buff);
				assertArrayEquals(new byte[]{0x00, 0x00, 0x00, 0x00}, buff); // This means OK, VNC client is authenticated.

				out.write(shareDesktopFlag); // ClientInit message: Share desktop flag.
				out.flush();
				
				buff = new byte[20]; // for ServerInit message.
				in.read(buff);
				int desktopNameLen = in.readInt(); // Desktop name length.
				buff = new byte[desktopNameLen];
				in.read(buff);
				
				//
				// Send some messages like SetPixelFormat, SetEncodings, ... etc.
				//
				
				out.write(0); // SetPixelFormat message code.
				out.write(new byte[]{0, 0, 0}); // padding.
				SetPixelFormat.write(out, SetPixelFormat.default32bit());				
				out.flush();
				
				out.write(2); // SetEncodings message code.
				out.write(0); // Padding.
				out.writeShort(1); // This 'client' supports only RAW encoding.
				out.writeInt(Encodings.RAW);
				out.flush();
				
				out.write(3); // Framebuffer update request message code.
				out.write(0); // Incremental = false.
				out.writeShort(0); // x-pos
				out.writeShort(0); // y-pos
				out.writeShort(100); // width
				out.writeShort(100); // height
				out.flush();
				
				out.write(4); // Key event message.
				out.write(new byte[]{
						0,	// down flag
						0, 0, // padding
						0x00, 0x00, (byte) 0xff, 0x0d // enter key code.
				});
				out.flush();
				
				out.write(5); // Pointer event.
				out.write(new byte[]{
						1,	// button mask
						0, 0, // x position
						0, 0, // y position
				});
				out.flush();
				
				out.write(6); // Client cut text.
				out.write( new byte[] {
				0, 0, 0 // padding
				, 0, 0, 0, 5 // length of value
				, 'a', 'b', 'c', 'd', 'e', // value 
				}); 
				out.flush();
				
                try {
                    
                    final Clipboard clipboard = 
                            Toolkit.getDefaultToolkit().getSystemClipboard();
                    final Transferable t = clipboard.getContents(null);
                    // assertTrue( t.isDataFlavorSupported(DataFlavor.stringFlavor) == true);
                                        
                    String result = String.valueOf(t.getTransferData(DataFlavor.stringFlavor));
                    assertEquals("abcde", result);
                }
                catch (final Exception ex) {
                    
                }
                
				client.close();
				server.close();
			} catch (final IOException ex) {
				
				ex.printStackTrace();
				
				fail();
			}
		});
		
		latch.await(1, TimeUnit.SECONDS);
		
		final Socket socket = new Socket(InetAddress.getLoopbackAddress(), tcpPort);

		final RFBConfig config = new RFBConfig();
		config.setPassword(password);
		config.setScreenClip(clip);
		
		final ClientHandler handler = new ClientHandler(socket, config);		
		Future<?> future = executor.submit( handler );
		
		try {
		
			future.get(2, TimeUnit.SECONDS);
		} catch (final TimeoutException ex) {
		
			ClientHandlerTest.printThreadStackTrace();
		}
		
		assertTrue(handler.isRunning() == false);
	}

	public static void printThreadStackTrace() {
		
		final Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
		
		final PrintStream out = System.out;
		
		for (final Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
			
			out.println(entry.getKey().getName());
			
			for (final StackTraceElement el : entry.getValue()) {
				
				out.println(String.format("\t%s", el));
			}
			
			out.println();
		}		
	}

}
