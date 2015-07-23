package SwingDemo;


import java.awt.Graphics;

import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class JMenuHelpAbout extends JMenuItem {

	public JMenuHelpAbout() {
		setText("About");
		addActionListener(new ActionListenerAbout());
		
	}
	
    public void paint(Graphics g) {
        super.paint(g);
        JFrameMainWindow.jFrameMainWindow.doIncrementalFrameBufferUpdate();
    }
}
