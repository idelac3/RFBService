package com.scoreunit.rfb.example;

import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.Properties;

import com.scoreunit.rfb.service.RFBService;

/**
 * This is example program to run, as standalone VNC service.
 */
public class RFBServiceExample {

	/**
	 * Start VNC with some arguments
	 *
	 * @param args		list of program arguments
	 *
	 * @throws UnknownHostException	if program files to bind / open the listning TCP port
	 * @throws IOException	if network connection failure occurs
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {

		final CommandArgParser parser = new CommandArgParser(args);
		
		final Properties props = parser.getArguments();
		
		final String listeningPort = props.getProperty("port");
		final String connectTo = props.getProperty("connect");
		final String clip = props.getProperty("clip");
		final String password = props.getProperty("password");
		final String sslKeyFilePath = props.getProperty("sslKeyFile");
		final String sslKeyFilePassword = props.getProperty("sslKeyPass");
		final String help = props.getProperty("help");
		
		final RFBService service = new RFBService();
		
		if (help != null) {
			
			final PrintStream out = System.out;
			
			out.println("\nRFB service");
			out.println("\nUsage:");
			out.println(String.format(" --port [tcp port] \n\t Bind to specific TCP port, and wait for VNC client. Default TCP port is %d.\n", RFBService.DEFAULT_PORT));
			out.println(String.format(" --connect [hostname:port] \n\t Establish connection to VNC client. Default TCP port is 5500.\n"));
			out.println(String.format(" --clip [widthxheight] \n\t Share only part of screen. Eg. 800x600 to share 800x600 pixels of screen, at (0,0) pixel offset.\n"));
			out.println(String.format(" --password [secret] \n\t Set VNC auth. When VNC client connects, it will have to provide correct password.\n"));
			out.println(String.format(" --sslKeyFile [myKey.pfx] \n\t Set SSL secure layer.First generate key and certificate with openssl then bundle them into PKCS12 file (*.pfx).\n"));
			out.println(String.format(" --sslKeyPass [password] \n\t A password used to generate PKCS12 file.\n"));
			out.println("Author: igor.delac@gmail.com");
			out.println();
			
			return;
		}
		
		if (listeningPort != null) {
			
			service.setPort(Integer.parseInt(listeningPort));
		}
		
		if (password != null) {
			
			service.setPassword(password);
		}

		if (clip != null) {
			
			int beginIndex = 0, endIndex = clip.indexOf('x');
			
			int width = Integer.parseInt(clip.substring(beginIndex, endIndex));
			int height = Integer.parseInt(clip.substring(endIndex + 1));
			
			service.setScreenClip(0, 0, width, height);
		}
		
		if (sslKeyFilePath != null && sslKeyFilePassword != null) {
		
			service.enableSSL(sslKeyFilePath, sslKeyFilePassword);
		}
		
		if (connectTo != null) {
			
			int beginIndex = 0, endIndex = connectTo.indexOf(':');
			
			final String host;
			final int port;
			
			if (endIndex == -1) {
				
				host = connectTo;
				port = 5500; // default TCP port when VNC client is waiting for connection.
			}
			else {
			
				host = connectTo.substring(beginIndex, endIndex);
				port = Integer.parseInt(connectTo.substring(endIndex) + 1);
			}
			
			service.connect(host, port);
		}
		else {
			
			service.start();
		}
	}

}
