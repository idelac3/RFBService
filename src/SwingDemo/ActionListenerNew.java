package SwingDemo;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionListenerNew implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {
			JFrameMainWindow.jTextAreaPrintout.setText("");
	}

}
