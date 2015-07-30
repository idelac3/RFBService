package SwingDemo;


import java.awt.event.KeyEvent;

import javax.swing.JMenu;

@SuppressWarnings("serial")
public class JMenuHelp extends JMenu {

	public JMenuHelp() {
		
		setText("Help");
		setMnemonic(KeyEvent.VK_H);
		
		add(new JMenuHelpAbout());
		
		addMouseListener(new MouseListenerJMenu());
		
	}

}
