package SwingDemo;


import java.awt.Graphics;

import javax.swing.JMenuBar;

@SuppressWarnings("serial")
public class JMenuBarMain extends JMenuBar {

	public JMenuBarMain() {
		add(new JMenuFile());
		add(new JMenuEdit());
		add(new JMenuHelp());
	}
	
    public void paint(Graphics g) {
        super.paint(g);
        JFrameMainWindow.jFrameMainWindow.doIncrementalFrameBufferUpdate();
    }
}
