package SwingDemo;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;

public class ActionListenerAbout implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JDialog jDialogAbout = new JDialog(JFrameMainWindow.jFrameMainWindow);
		jDialogAbout.setTitle("About");

		jDialogAbout.setLayout(new GridLayout(0, 1));
		
		jDialogAbout.add(new JLabel("A demo for RFB protocol."));
		jDialogAbout.add(new JLabel("Author: igor.delac@gmail.com"));
		
		jDialogAbout.setLocation(
				JFrameMainWindow.jFrameMainWindow.getX() + (JFrameMainWindow.jFrameMainWindow.getWidth() - jDialogAbout.getWidth()) / 2, 
				JFrameMainWindow.jFrameMainWindow.getY() + (JFrameMainWindow.jFrameMainWindow.getHeight() - jDialogAbout.getHeight()) / 2);
		
		jDialogAbout.setVisible(true);
		jDialogAbout.pack();

	}

}
