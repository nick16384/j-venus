package jfxcomponents;

import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * An overlay that appears on top of the shell when typing, suggesting command
 * completions.
 */

public class CompletionOverlay {
	private static OverlayPopup overlayPopup;

	public static void configureElements(Stage primaryStage) {
		overlayPopup = new OverlayPopup(primaryStage);
		
		overlayPopup.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (!event.getCode().equals(KeyCode.ENTER)
					&& !event.getCode().equals(KeyCode.BACK_SPACE)
					&& !event.getCode().equals(KeyCode.TAB))
			showOverlay(primaryStage); // Only works, if hiddenLock is false
		});
		
		// TODO Show overlay again, if next command is being typed
		// Maybe use main.Main.commandRepeatInRow
	}

	protected static OverlayPopup getOverlay() {
		return overlayPopup;
	}
	
	// TODO Functionality removed temporarily, for later implementation
	protected static void showOverlay(Stage primaryStage) {
		//overlayPopup.show(primaryStage);
	}
	
	protected static void disableLockAndShowOverlay(Stage primaryStage) {
		//overlayPopup.unlockHidden();
		//overlayPopup.show(primaryStage);
	}
	
	protected static void commandTypingIsFinished() {
		//overlayPopup.unlockHidden();
	}
}
