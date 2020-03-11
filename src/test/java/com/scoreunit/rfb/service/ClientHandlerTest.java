package com.scoreunit.rfb.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class ClientHandlerTest {

	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@Test
	public void test_01_sessionWithVNCauth() throws IOException, InterruptedException {
	
		final String secret = "password1";
		
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
				assertArrayEquals(new byte[]{0x01, 0x02}, buff);
				
				out.write(0x02); // VNC client selects value 2 - VNC auth.
				out.flush();
				
				buff = new byte[16];
				in.read(buff); // wait for challenge from server.
				
				out.write(DESCipher.enc(secret, buff)); // respond with DES encrypted challenge using secret value as key.
				out.flush();
				
				buff = new byte[4];
				in.read(buff);
				assertArrayEquals(new byte[]{0x00, 0x00, 0x00, 0x00}, buff); // This means OK, VNC client is authenticated.

				out.write(0x00); // ClientInit message: Share desktop flag = false.
				out.flush();
				
				buff = new byte[20]; // for ServerInit message.
				in.read(buff);
				int desktopNameLen = in.readInt(); // Desktop name length.
				buff = new byte[desktopNameLen];
				in.read(buff);
				
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
		config.setPassword(secret);
		
		final ClientHandler handler = new ClientHandler(socket, config);		
		handler.run();
	}

}
