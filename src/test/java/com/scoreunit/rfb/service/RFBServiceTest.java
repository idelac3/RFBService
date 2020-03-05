package com.scoreunit.rfb.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

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
