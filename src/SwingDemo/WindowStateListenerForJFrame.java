package SwingDemo;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.Iterator;

import RFBDemo.RFBDemo;
import RFBService.RFBService;
import RobotService.RobotScreen;

public class WindowStateListenerForJFrame implements WindowStateListener {

	@Override
	public void windowStateChanged(WindowEvent arg0) {
		
		/*
		 * Minimized condition.
		 */
		boolean minimized = ( (arg0.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED );
		
		if (minimized) {
			
			/*
			 * Take dimension of main window.
			 */
			int width  = JFrameMainWindow.jFrameMainWindow.getWidth();
			int height = JFrameMainWindow.jFrameMainWindow.getHeight();
			
			/*
			 * Fill image buffer with generated pattern of vertical bars.
			 */
			RobotScreen.robo.setMinimizedImageBuffer(width, height);
			
			/*
			 * For RFB protocol, put (X, Y) coordinates at (0, 0).
			 */
			int offsetX = 0;
			int offsetY = 0;
			
			/*
			 * Go over each VNC connection and push generated pattern.
			 */
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

				}
			}			
		}

	}

}
