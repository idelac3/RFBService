package SwingDemo;


import javax.swing.JTextField;

@SuppressWarnings("serial")
public class JTextFieldInput extends JTextField {

	public JTextFieldInput() {
		setText("Put some text here ...");
		addActionListener(new ActionListenerSubmit());
		setColumns(20);
		
		addCaretListener(new CaretListenerTextArea());
	}
}
