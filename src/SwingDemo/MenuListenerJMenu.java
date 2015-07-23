package SwingDemo;


import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class MenuListenerJMenu implements MenuListener {

	@Override
	public void menuCanceled(MenuEvent e) {
		JFrameMainWindow jFrameMainWindow = JFrameMainWindow.jFrameMainWindow;
		jFrameMainWindow.doIncrementalFrameBufferUpdate();
	}

	@Override
	public void menuDeselected(MenuEvent e) {
		JFrameMainWindow jFrameMainWindow = JFrameMainWindow.jFrameMainWindow;
		jFrameMainWindow.doIncrementalFrameBufferUpdate();		
	}

	@Override
	public void menuSelected(MenuEvent e) {
		// TODO Auto-generated method stub
		
	}

}
