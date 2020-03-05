package com.scoreunit.rfb.example;

import com.scoreunit.rfb.service.RFBService;

public class RFBServiceExample {

	public static void main(String[] args) {

		/*
		 * Example of service with default TCP port 5900,
		 * and no authentication.
		 */
		final RFBService service = new RFBService();		
		service.start();
	}

}
