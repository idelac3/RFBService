package SwingDemo;


import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DocumentListenerChange implements DocumentListener {

	@Override
	public void changedUpdate(DocumentEvent e) {
		JFrameMainWindow jFrameMainWindow = JFrameMainWindow.jFrameMainWindow;
		jFrameMainWindow.doIncrementalFrameBufferUpdate();		
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		JFrameMainWindow jFrameMainWindow = JFrameMainWindow.jFrameMainWindow;
		jFrameMainWindow.doIncrementalFrameBufferUpdate();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		JFrameMainWindow jFrameMainWindow = JFrameMainWindow.jFrameMainWindow;
		jFrameMainWindow.doIncrementalFrameBufferUpdate();
	}

}
