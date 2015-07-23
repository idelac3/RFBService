package SwingDemo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.Iterator;

import RFBService.RFBService;
import RFBDemo.RFBDemo;

public class ActionListenerDesktopResize implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {

		Iterator<RFBService> it = RFBDemo.rfbClientList.iterator();
		while (it.hasNext()) {

			RFBService rfbClient = it.next();

			if (rfbClient.incrementalFrameBufferUpdate) {

				try {

					rfbClient.sendDesktopSize();

				} catch (SocketException ex) {
					it.remove();
				} catch (IOException ex) {
					ex.printStackTrace();

					it.remove();
				}

			}
		}

	}

}
