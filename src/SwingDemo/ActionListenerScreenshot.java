package SwingDemo;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class ActionListenerScreenshot implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		Component gui = JFrameMainWindow.jFrameMainWindow;
		
		/*
		 * Screenshot of JFrame or any Swing component.
		 * 
		 * http://stackoverflow.com/questions/5853879/swing-obtain-image-of-jframe
		 */		
		final BufferedImage image = new BufferedImage(
			      gui.getWidth(),
			      gui.getHeight(),
			      //BufferedImage.TYPE_INT_RGB
			      BufferedImage.TYPE_BYTE_GRAY
			      );
		
		gui.paint(image.getGraphics());

		final JFrame preview = new JFrame("Preview");
		preview.setLocation(gui.getX() + 25, gui.getY() + 25);
		
		JLabel picLabel = new JLabel(new ImageIcon(image));
		
		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(picLabel);
		preview.setContentPane(jScrollPane);
		
		preview.pack();
		preview.setVisible(true);		
		
	}

}
