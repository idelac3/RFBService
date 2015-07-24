package SwingDemo;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

public class ActionListenerHelpAboutOK implements ActionListener {

	private JDialog jDialogAbout;
	
	public ActionListenerHelpAboutOK(JDialog jDialogAbout) {
		this.jDialogAbout = jDialogAbout;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		jDialogAbout.dispose();
		JFrameMainWindow.jFrameMainWindow.doIncrementalFrameBufferUpdate();
	}

}
