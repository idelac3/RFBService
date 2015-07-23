package SwingDemo;


import java.awt.Graphics;

import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class JMenuFileScreenshot extends JMenuItem {

	public JMenuFileScreenshot() {
		setText("Screenshot");
		addActionListener(new ActionListenerScreenshot());
		
		//addMouseListener(new MouseListenerJMenu());
	}
	
    public void paint(Graphics g) {
        super.paint(g);
        JFrameMainWindow.jFrameMainWindow.doIncrementalFrameBufferUpdate();
    }
}
