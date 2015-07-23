package RFBDemo;


import java.awt.AWTException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import RFBService.RFBService;
import RobotService.RobotKeyboard;
import RobotService.RobotMouse;
import RobotService.RobotScreen;
import SwingDemo.JFrameMainWindow;

/**
 * Demo application that show how to implement RFB service and protocol to
 * allow Swing / GUI components to be displayed remotely, usually on VNC viewers.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class RFBDemo {

	/**
	 * List of open VNC sessions. 
	 */
	public static List<RFBService> rfbClientList = new ArrayList<RFBService>();
	
	/**
	 * Main application entry.
	 * 
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException
	 * @throws IOException
	 * @throws AWTException
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException, AWTException {

		/*
		 * Look for Nimbus look. Better than default Metal look.
		 */
    	LookAndFeelInfo[] installedInfos = UIManager.getInstalledLookAndFeels();
    	for (LookAndFeelInfo info : installedInfos) {
    		if (info.getName().equalsIgnoreCase("Nimbus")) {
    			UIManager.setLookAndFeel(info.getClassName());
    			break;
    		}
    	}
    	
    	/*
    	 * Start Swing GUI in separate thread.
    	 */
    	SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				new JFrameMainWindow().setVisible(true);

			}
		});

    	/*
    	 * Initialize static Robot objects for screen, keyboard and mouse. 
    	 */
		RobotScreen.robo = new RobotScreen();
		RobotKeyboard.robo = new RobotKeyboard();
		RobotMouse.robo = new RobotMouse();
		
		/*
		 * Use TCP port 5902 (display :2) as an example to listen.
		 */
		int port = 5902;
		ServerSocket serverSocket;
		serverSocket = new ServerSocket(port);
		
		/*
		 * Limit sessions to 100. This is lazy way, if 
		 * somebody really open 100 sessions, server socket
		 * will stop listening and no new VNC viewers will be 
		 * able to connect.
		 */
		while (rfbClientList.size() < 100) {
			
			/*
			 * Wait and accept new client.
			 */
			Socket client = serverSocket.accept();
			
			/*
			 * Create new object for each client.
			 */
			RFBService rfbService = new RFBService(client);
			
			/*
			 * Add it to list.
			 */
			rfbClientList.add(rfbService);
			
			/*
			 * Handle new client session in separate thread.
			 */
			(new Thread(rfbService, "RFBService" + rfbClientList.size())).start();
			
		}
		
		/*
		 * Close server socket. No more client connections.
		 */
		serverSocket.close();

	}

}
