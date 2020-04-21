package com.scoreunit.rfb.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.scoreunit.rfb.encoding.Encodings;
import com.scoreunit.rfb.screen.ScreenClip;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RFBServiceTest {

	public final static long TIMEOUT = 1000; // 1 sec. timeout.
	
	private int tcpPort = -1;
	
	@Before
	public void setup() {
	
		if (this.tcpPort == -1) {
		
			this.tcpPort = randomPort();
		}
	}

	@Test
	public void test_00_getterSetters() {
		
		final RFBService service = new RFBService();
		assertEquals(RFBService.DEFAULT_PORT, service.getPort());
		
		service.setPort(6900);
		assertEquals(6900, service.getPort());
		
		service.setScreenClip( (short) 1, (short) 2, (short) 3, (short) 4);
		service.setScreenClip(new ScreenClip((short) 1, (short) 2, (short) 3, (short) 4));
		
		service.setPassword("blabla123");
		
		service.setPreferredEncodings(new int[] {Encodings.RAW, Encodings.ZLIB});
	}
	
	@Test
	public void test_01_startStopService() throws TimeoutException {
				
		final RFBService service = new RFBService(this.tcpPort);
		
		assertFalse(service.isRunning());
		
		service.start();
		
		waitFor(TIMEOUT, (x) -> service.isRunning() == true );
		
		assertTrue(service.isRunning());
		
		service.terminate();
		
		waitFor(TIMEOUT, (x) -> service.isRunning() == false );
		
		assertFalse(service.isRunning());
	}

	
	@Test
	public void test_02_singleClientConnected() throws TimeoutException, IOException {
				
		final RFBService service = new RFBService(this.tcpPort);
		
		assertFalse(service.isRunning());
		
		service.start();
		
		waitFor(TIMEOUT, (x) -> service.isRunning() );

		assertTrue(service.isRunning());
		
		final Socket client = new Socket(InetAddress.getLoopbackAddress(), this.tcpPort);
		
		waitFor(TIMEOUT, (x) -> service.getClientHandlers().size() > 0 );
		
		assertEquals(1, service.getClientHandlers().size());
		
		client.close();
		
		service.terminate();
		
		waitFor(TIMEOUT, (x) -> service.isRunning() == false );
		
		assertEquals(0, service.getClientHandlers().size());		
	}
	
	@Test
	public void test_03_connectToClient() throws TimeoutException, IOException, InterruptedException, ExecutionException {

		final ExecutorService executor = Executors.newSingleThreadExecutor();
		
		final int clientPort = randomPort();
		
		final ServerSocket client = new ServerSocket(clientPort);
		final Future<String> future = executor.submit( () -> {

			Socket socket = client.accept();

			String retVal = null;
			
			try {
				
				byte[] buf = new byte[12]; // RFB proto.version string. 
				socket.getInputStream().read(buf);
				
				retVal = new String(buf);
			} catch (IOException e) {

				e.printStackTrace();				
			}
			finally {
				socket.close();
			}
			
			return retVal;
		});
		
		final RFBService service = new RFBService();
		service.connect(InetAddress.getLoopbackAddress().getHostAddress(), clientPort);
		
		final String result = future.get(1, TimeUnit.SECONDS);
		assertEquals(ProtocolVersion.ver, result);
		
		client.close();
	}
	
	@Test
	public void test_04_sslConfiguration() throws IOException, TimeoutException {
	
		final RFBService service = new RFBService();
		
		// By default, SSL is not configured.
		assertFalse(service.isSSLEnabled());
		
		// Bad config. with non-existing key file.
		service.enableSSL("blabla.pfx", "123456");
		assertFalse(service.isSSLEnabled());
				
		final String[] resourceFileNames = {
				"store-example1.jks", "key-example1.pfx"
		};
		
		final String password = "blabla123";
	
		// Test with both *.pfx and *.jks files.
		for (final String resourceFileName : resourceFileNames) {
			
			//
			// Copy resource file data into temp. folder.
			//
			
			final InputStream in = RFBServiceTest.class.getClassLoader().getResourceAsStream(resourceFileName);
			assertNotNull(in);
			
			final String tmpKeyFile = Paths.get(System.getProperty("java.io.tmpdir"), resourceFileName).toString(); 
			final FileOutputStream fOut = new FileOutputStream(tmpKeyFile);
			
			final byte[] buffer = new byte[1024];
			int len;
						
			while ( (len = in.read(buffer)) > 0) {
			
				fOut.write(buffer, 0, len);
			}
			
			fOut.close();
			
			service.enableSSL(tmpKeyFile, password);
			assertTrue(service.isSSLEnabled());
			
			service.start();
			waitFor(TIMEOUT, (x) -> service.isRunning() );
			assertTrue(service.isRunning());
			
			service.terminate();
			waitFor(TIMEOUT, (x) -> service.isRunning() == false);
			assertFalse(service.isRunning());
			
			service.disableSSL();
			assertFalse(service.isSSLEnabled());
			
			Files.delete(Paths.get(tmpKeyFile));
		}
	}
	
	/**
	 * Method will wait (block) until give function or lambda returns value <i>true</i>.
	 * 
	 * @param timeout			-		timeout value in millisec. 
	 * @param func				-		function to use to determine when to stop waiting
	 * 
	 * @throws TimeoutException	-		if timeout value is reached
	 */
	public static void waitFor(final long timeout, final Function<Void, Boolean> func) throws TimeoutException {
		
		long startedAt = System.currentTimeMillis();		
		
		while (func.apply(null) == false) {
			
			Thread.yield();
					
			long delta = System.currentTimeMillis() - startedAt;
			
			if (delta > timeout) {
				
				throw new TimeoutException("Timeout value reached.");
			}
		}		
	}
	
	public static int randomPort() {
		
		int value = 10000 + (int) (System.currentTimeMillis() % 10000);
		
		return value;
	}
}
