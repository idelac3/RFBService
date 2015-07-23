package SwingDemo;


import javax.swing.JMenu;

@SuppressWarnings("serial")
public class JMenuHelp extends JMenu {

	public JMenuHelp() {
		
		setText("Help");
		
		add(new JMenuHelpAbout());
		
		addMouseListener(new MouseListenerJMenu());
		
	}

}
