package SwingDemo;


import javax.swing.JButton;
import javax.swing.JDialog;

@SuppressWarnings("serial")
public class JButtonHelpAboutOK extends JButton {

	public JButtonHelpAboutOK(JDialog jDialogAbout) {
		setText("OK");
		addActionListener(new ActionListenerHelpAboutOK(jDialogAbout));
	}
}
