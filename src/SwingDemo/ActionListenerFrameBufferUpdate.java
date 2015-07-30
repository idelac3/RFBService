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

	/**
	 * Row matcher function. It compares two rows and returns positive result if they are equal.
	 * This function does not make sense if buffers are not the same dimension.<BR>
	 *  <B>NOTE:</B> This function does not check for index bounds in arrays!
	 *  
	 * @param buf1 first image buffer
	 * @param row1 row in first image buffer
	 * @param buf2 second image buffer
	 * @param row2 row in second image buffer
	 * @param width width of row
	 * @return <I>true</I> if equal, otherwise <I>false</I>
	 */
    private boolean rowMatch(int[] buf1, int row1, int[] buf2, int row2, int width) {
        boolean match = true;
        
        for (int i = row1 * width, j = row2 * width;
                i < (row1 + 1) * width && j < (row2 + 1) * width;
                i++, j++) {
            if (buf1[i] != buf2[j]) {
                match = false;
                break;
            }
        }
        
        return match;
    }

    private boolean colMatch(int[] buf1, int col1, int[] buf2, int col2, int width) {
        boolean match = true;
        
        for (int i = col1, j = col2;
                i < buf1.length && j < buf2.length;
                i = i + width, j = j + width) {
            if (buf1[i] != buf2[j]) {
                match = false;
                break;
            }
        }
        
        return match;
    }
    
    private int[] subBuffer(int[] buffer, int width, int x1, int y1, int x2, int y2) {
    	
		int w1 = x2 - x1 + 1;
		int h1 = y2 - y1 + 1;
		
		int[] newBuffer = new int[w1 * h1];
		
		for (int y = y1; y <= h1; y++) {
			int srcPos = y * width + x1;
			int destPos = (y - y1) * w1;
			int length = w1;
			System.arraycopy(buffer, srcPos, newBuffer, destPos, length);
		}
		return newBuffer;
	}
}
