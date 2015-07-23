package SwingDemo;


import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JPanelBottomLine extends JPanel {

	public JPanelBottomLine() {
		
		JLabel jLabel1 = new JLabel(" O ");
		jLabel1.setOpaque(true);
		jLabel1.setBackground(Color.RED);

		JLabel jLabel2 = new JLabel(" O ");
		jLabel2.setOpaque(true);
		jLabel2.setBackground(Color.ORANGE);

		JLabel jLabel3 = new JLabel(" O ");
		jLabel3.setOpaque(true);
		jLabel3.setBackground(Color.GREEN);

		add(jLabel1);
		add(jLabel2);
		add(jLabel3);
		
		add(JFrameMainWindow.jTextFieldInput);
		add(new JButtonSubmit());

		
	}
}
