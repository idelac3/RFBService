package SwingDemo;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class AdjustmentListenerScrollBar implements AdjustmentListener {

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		JFrameMainWindow.jFrameMainWindow.doIncrementalFrameBufferUpdate();		
	}

}
