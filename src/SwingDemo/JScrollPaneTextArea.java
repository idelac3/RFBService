package SwingDemo;

import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class JScrollPaneTextArea extends JScrollPane {

	public JScrollPaneTextArea() {
		setViewportView(JFrameMainWindow.jTextAreaPrintout);
		getVerticalScrollBar().addAdjustmentListener(new AdjustmentListenerScrollBar());
		getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListenerScrollBar());
	}
}
