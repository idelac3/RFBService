package SwingDemo;


import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import RFBDemo.RFBDemo;

public class ComponentListenerForJFrame implements ComponentListener {
	
	private Timer timerDesktopResize;
	
	public ComponentListenerForJFrame() {
		timerDesktopResize = new Timer(1000, new ActionListenerDesktopResize());
		timerDesktopResize.setRepeats(false);
	}
	
    public void componentResized(ComponentEvent e) {

    	int width = 0, height = 0;
    	
    	if (e.getSource() instanceof JFrame) {
    		width   = e.getComponent().getWidth();
    		height  = e.getComponent().getHeight();
    	}

    	if (width < 1 || height < 1) {
        	System.out.println("Invalid size: " + width + ", " + height);
    		return;
    	}

    	if (RFBDemo.rfbClientList.size() > 0) {
    		if ( !timerDesktopResize.isRunning() ) {    		
    			timerDesktopResize.start();
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
