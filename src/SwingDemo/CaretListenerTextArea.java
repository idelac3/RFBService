package SwingDemo;


import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class CaretListenerTextArea implements CaretListener {

	@Override
	public void caretUpdate(CaretEvent arg0) {
		JFrameMainWindow jFrameMainWindow = JFrameMainWindow.jFrameMainWindow;
		jFrameMainWindow.doIncrementalFrameBufferUpdate();		
	}
	
}
