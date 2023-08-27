package jfxcomponents;

import javafx.stage.Popup;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.stage.Stage;

import java.util.Optional;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;
import org.reactfx.value.Var;
import static org.reactfx.EventStreams.nonNullValuesOf;

import awtcomponents.AWTANSI;
import engine.InfoType;
import engine.sys;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import main.Main;
import shell.Shell;
import threads.ThreadAllocation;

/**
 * An overlay that appears on top of the shell when typing, suggesting command
 * completions.
 */

public class CompletionOverlay {
	private static OverlayPopup overlayPopup;
	
	private static boolean currentCommandTypingIsFinished;

	public static void configureElements(Stage primaryStage) {
		overlayPopup = new OverlayPopup(primaryStage);
		
		currentCommandTypingIsFinished = false;
		
		// TODO Show overlay again, if next command is being typed
		// Maybe use main.Main.commandRepeatInRow
	}

	protected static Popup getOverlay() {
		return overlayPopup;
	}
	
	protected static void showOverlay(Stage primaryStage) {
		overlayPopup.unlockHidden();
		overlayPopup.show(primaryStage);
	}
}
