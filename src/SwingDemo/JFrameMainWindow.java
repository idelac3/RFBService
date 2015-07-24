package SwingDemo;


import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import RFBDemo.RFBDemo;

/**
 * Main JFrame window. This window define screen size for VNC clients.
 * This class also holds static references for some JComponents like
 * JTextArea which need to be accessible by some action listeners.
 * 
 * @author igor.delac@gmail.com
 *
 */
@SuppressWarnings("serial")
public class JFrameMainWindow extends JFrame {
	
	/**
	 * Static access to instance of {@link JFrameMainWindow}.
	 */
	public static JFrameMainWindow jFrameMainWindow;
	
	/**
	 * Static access to instance of {@link JTextAreaPrintout}.
	 */
	public static JTextArea jTextAreaPrintout;
	
	/**
	 * Static access to instance of {@link JTextFieldInput}.
	 */
	public static JTextField jTextFieldInput;
	
	/**
	 * A timer that will do frame buffer update on every 
	 * VNC viewer connected to this application.
	 */
	private Timer timerUpdateFrameBuffer;
	
	/**
	 * A demo window showing Swing GUI. This window together with
	 * components inside should display on VNC viewer.
	 */
	public JFrameMainWindow() {

		jFrameMainWindow = this;
		jTextAreaPrintout = new JTextAreaPrintout();
		jTextFieldInput = new JTextFieldInput();

		/*
		 * Setup main window location, size, title, etc.
		 */
		setTitle("Main window");
		setLocation(150, 200);
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		/*
		 * Add listener(s).
		 */
		addComponentListener(new ComponentListenerForJFrame());
		addWindowStateListener(new WindowStateListenerForJFrame());
		
		/*
		 * Add components.
		 */
		setJMenuBar(new JMenuBarMain());
		add(new JScrollPaneTextArea(), BorderLayout.CENTER);
		add(new JPanelBottomLine(), BorderLayout.SOUTH);
	
		/*
		 * Define timer for frame buffer update with 400 ms delay and no repeat.
		 */
		timerUpdateFrameBuffer = new Timer(400, new ActionListenerFrameBufferUpdate());
		timerUpdateFrameBuffer.setRepeats(false);
		
	}
	
	/**
	 * Starts frame buffer update. Procedure actually starts timer
	 * so that action is delayed, which cause that only most recent
	 * screen capture will be transfered over TCP connection.
	 */
	public void doIncrementalFrameBufferUpdate() {

		if (RFBDemo.rfbClientList.size() == 0) {
			return;
		}

		if (!timerUpdateFrameBuffer.isRunning()) {		
			timerUpdateFrameBuffer.start();
		} 
	
	}

}
