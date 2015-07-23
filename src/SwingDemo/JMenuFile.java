package SwingDemo;


import javax.swing.JMenu;

@SuppressWarnings("serial")
public class JMenuFile extends JMenu {

	public JMenuFile() {
		
		setText("File");
		
		add(new JMenuFileScreenshot());
		addSeparator();
		add(new JMenuFileExit());
		
		addMouseListener(new MouseListenerJMenu());
		
	}

}
