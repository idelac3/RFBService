package SwingDemo;


import java.awt.event.KeyEvent;

import javax.swing.JMenu;

@SuppressWarnings("serial")
public class JMenuFile extends JMenu {

	public JMenuFile() {
		
		setText("File");
		setMnemonic(KeyEvent.VK_F);
		
		add(new JMenuFileNew());
		addSeparator();
		add(new JMenuFileExit());
		
		addMouseListener(new MouseListenerJMenu());
		
	}

}
