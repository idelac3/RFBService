package SwingDemo;


import java.awt.Font;

import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class JTextAreaPrintout extends JTextArea {

	public JTextAreaPrintout() {
		setFont(new Font("Monospaced", Font.PLAIN, 16));
		addCaretListener(new CaretListenerTextArea());
		
		getDocument().addDocumentListener(new DocumentListenerChange());
	}

}
