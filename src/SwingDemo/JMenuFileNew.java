package SwingDemo;


import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class JMenuFileNew extends JMenuItem {

	public JMenuFileNew() {
		
		setText("New");		
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		
		addActionListener(new ActionListenerNew());
		
	}
	
    public void paint(Graphics g) {
        super.paint(g);
        JFrameMainWindow.jFrameMainWindow.doIncrementalFrameBufferUpdate();
    }
}
