package SwingDemo;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionListenerSubmit implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFrameMainWindow.jTextAreaPrintout.append(JFrameMainWindow.jTextFieldInput.getText() + "\n");
		JFrameMainWindow.jTextFieldInput.setText("");
		
	}

}
