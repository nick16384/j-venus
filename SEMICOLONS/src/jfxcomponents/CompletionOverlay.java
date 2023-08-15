package jfxcomponents;

import javafx.scene.control.TextArea;
import main.Main;

/**
 * An overlay that appears on top of the shell when typing, suggesting command completions.
 */

public class CompletionOverlay {
	private static TextArea overlayArea;
	
	public static void configureElements() {
		overlayArea = new TextArea();
		overlayArea.setText("OVERLAY TEST");
		overlayArea.setOpacity(0.5);
		overlayArea.setMinSize(200, 100);
		overlayArea.setMaxSize(200, 100);
		overlayArea.setEditable(false);
		// TODO Place overlay below cursor (or prompt)
		overlayArea.setTranslateX(-400);
	}
	
	protected static TextArea getOverlayArea() {
		return overlayArea;
	}
}
