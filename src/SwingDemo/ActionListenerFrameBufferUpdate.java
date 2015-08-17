package SwingDemo;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.Iterator;

import RFBDemo.RFBDemo;
import RFBService.RFBService;
import RobotService.RobotScreen;

public class ActionListenerFrameBufferUpdate implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {

		/*
		 * Get dimensions and location of main JFrame window.
		 */
		int offsetX = JFrameMainWindow.jFrameMainWindow.getX();
		int offsetY = JFrameMainWindow.jFrameMainWindow.getY();

		int width  = JFrameMainWindow.jFrameMainWindow.getWidth();
		int height = JFrameMainWindow.jFrameMainWindow.getHeight();

		/*
		 * Do not update screen if main window dimension has changed.
		 * Upon main window resize, another action listener will 
		 * take action.
		 */
		int screenWidth = RFBDemo.rfbClientList.get(0).screenWidth;
		int screenHeight = RFBDemo.rfbClientList.get(0).screenHeight;
		if (width != screenWidth || height != screenHeight) {
			return;
		}
			
		/*
		 * Capture new screenshot into image buffer.
		 */
		RobotScreen.robo.getScreenshot(offsetX, offsetY, width, height);
		
		int[] delta = RobotScreen.robo.getDeltaImageBuffer();					

		if (delta == null) {

			offsetX = 0;
			offsetY = 0;
			
			Iterator<RFBService> it = RFBDemo.rfbClientList.iterator();
			while (it.hasNext()) {

				RFBService rfbClient = it.next();

				if (rfbClient.incrementalFrameBufferUpdate) {

					try {

						/*
						 * Send complete window.
						 */
						rfbClient.sendFrameBufferUpdate(
								offsetX, offsetY, 
								width, height, 
								0, 
								RobotScreen.robo.getColorImageBuffer());
					}
					catch (SocketException ex) {
						it.remove();
					}
					catch (IOException ex) {
						ex.printStackTrace();

						it.remove();
					}

					rfbClient.incrementalFrameBufferUpdate = false;

				}
			}
		}
		else {

			offsetX = RobotScreen.robo.getDeltaX();
			offsetY = RobotScreen.robo.getDeltaY();

			width =  RobotScreen.robo.getDeltaWidth();
			height =  RobotScreen.robo.getDeltaHeight();

			Iterator<RFBService> it = RFBDemo.rfbClientList.iterator();
			while (it.hasNext()) {

				RFBService rfbClient = it.next();

				if (rfbClient.incrementalFrameBufferUpdate) {

					try {
						
						/*
						 * Send only delta rectangle.
						 */
						rfbClient.sendFrameBufferUpdate(
								offsetX, offsetY, 
								width, height, 
								0, 
								delta);

					}
					catch (SocketException ex) {
						it.remove();
					}
					catch (IOException ex) {
						ex.printStackTrace();

						it.remove();
					}

					rfbClient.incrementalFrameBufferUpdate = false;

				}
			}
		}
	}


    
}
