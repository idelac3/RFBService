package SwingDemo;


import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import RFBDemo.RFBDemo;

public class ComponentListenerForJFrame implements ComponentListener {
	
	private Timer timerDesktopResize;
	
	public ComponentListenerForJFrame() {
		/*
		 * Here is Timer used, since JFrame resize event is pretty
		 * aggressive and triggers frequently while mouse pointer
		 * is resizing JFrame window.
		 * 
		 * With timer and delay of 1 second, actual number of desktop resize
		 * messages sent to VNC viewers is reduced.
		 */
		timerDesktopResize = new Timer(1000, new ActionListenerDesktopResize());
		timerDesktopResize.setRepeats(false);
	}
	
    public void componentResized(ComponentEvent e) {

    	int width = 0, height = 0;
    	
    	/*
    	 * Check that source of event is JFrame.
    	 */
    	if (e.getSource() instanceof JFrame) {
    		width   = e.getComponent().getWidth();
    		height  = e.getComponent().getHeight();
    	}

    	/*
    	 * Check that dimensions are proper.
    	 */
    	if (width > 0 && height > 0) {
    		/*
    		 * Check that we have some VNC viewers connected.
    		 */
        	if (RFBDemo.rfbClientList.size() > 0) {
        		if ( !timerDesktopResize.isRunning() ) {    	
        			/*
        			 * Start timer.
        			 */
        			timerDesktopResize.start();
        		}
        	}
    	}


 
		
    }

	@Override
	public void componentHidden(ComponentEvent arg0) {

	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentShown(ComponentEvent arg0) {

	}
	
}
