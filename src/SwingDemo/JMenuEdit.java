package SwingDemo;


import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.text.DefaultEditorKit;

@SuppressWarnings("serial")
public class JMenuEdit extends JMenu {

	public JMenuEdit() {
		
		setText("Edit");
		
		Action[] textActions = {
				new DefaultEditorKit.CutAction(),
				new DefaultEditorKit.CopyAction(),
				new DefaultEditorKit.PasteAction()
				};

		for (Action action : textActions) {
			add(new JMenuItem(action));
		}
		
		addMouseListener(new MouseListenerJMenu());
		
	}

}
