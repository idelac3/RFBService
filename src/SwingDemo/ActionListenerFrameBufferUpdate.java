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
		 * First criteria for differentiated update is that screen buffer is not empty.
		 */
		boolean diffUpdateOfScreen = (RobotScreen.robo.getColorImageBuffer() != null);
		
		/*
		 * Get dimensions and location of main JFrame window.
		 */
		int offsetX = JFrameMainWindow.jFrameMainWindow.getX();
		int offsetY = JFrameMainWindow.jFrameMainWindow.getY();

		int width  = JFrameMainWindow.jFrameMainWindow.getWidth();
		int height = JFrameMainWindow.jFrameMainWindow.getHeight();

		/*
		 * Here current screen buffer will be saved.
		 */
		int[] oldScreenBuffer = null;
		
		if (diffUpdateOfScreen) {
			/*
			 * Save reference to current screen buffer.
			 */
			oldScreenBuffer = RobotScreen.robo.getColorImageBuffer();
		}

		/*
		 * Capture new screenshot into image buffer.
		 */
		RobotScreen.robo.getScreenshot(offsetX, offsetY, width, height);

		if (diffUpdateOfScreen) {
			/*
			 * Differentiated update is not possible if old and new buffers are not same length.
			 */
			if (RobotScreen.robo.getColorImageBuffer().length != oldScreenBuffer.length) {
				diffUpdateOfScreen = false;
			}
		}
		
		/*
		 * For RFB protocol, put (X, Y) coordinates at (0, 0).
		 */
		offsetX = 0;
		offsetY = 0;
		
		Iterator<RFBService> it = RFBDemo.rfbClientList.iterator();
		while (it.hasNext()) {

			RFBService rfbClient = it.next();

			if (rfbClient.incrementalFrameBufferUpdate) {

				try {
			
					/*
					 * Check if only different rows should be send to client.
					 */
					if (diffUpdateOfScreen) {
						
						for (int row = 0; row < height; row++) {
							/*
							 * Look for different rows ...
							 */
							if (! rowMatch(oldScreenBuffer, row, RobotScreen.robo.getColorImageBuffer(), row, width)) {								
								
								int[] singleRow = new int[width];
								System.arraycopy(RobotScreen.robo.getColorImageBuffer(), row * width, singleRow, 0, width);
								
								/*
								 * ... and just send difference. Equal rows skip.
								 */
								rfbClient.sendFrameBufferUpdate(
										offsetX, offsetY + row, 
										width, 1, 
										0, 
										singleRow);
								
							}
						}
						
					}
					else {

						/*
						 * Send complete window.
						 */
						rfbClient.sendFrameBufferUpdate(
							offsetX, offsetY, 
							width, height, 
							0, 
							RobotScreen.robo.getColorImageBuffer());
					}
					
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

	/**
	 * Row matcher function. It compares two rows and returns positive result if they are equal.
	 * This function does not make sense if buffers are not the same dimension.<BR>
	 *  <B>NOTE:</B> This function does not check for index bounds in arrays!¨
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
}
