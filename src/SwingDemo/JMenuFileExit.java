package SwingDemo;


import java.awt.Graphics;

import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class JMenuFileExit extends JMenuItem {

	public JMenuFileExit() {
		setText("Exit");
		addActionListener(new ActionListenerExit());
		
	}
	
    public void paint(Graphics g) {
        super.paint(g);
        JFrameMainWindow.jFrameMainWindow.doIncrementalFrameBufferUpdate();
    }
}
