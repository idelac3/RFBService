package SwingDemo;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseListenerJMenu implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent arg0) {
		JFrameMainWindow jFrameMainWindow = JFrameMainWindow.jFrameMainWindow;
		jFrameMainWindow.doIncrementalFrameBufferUpdate();				
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}

}
