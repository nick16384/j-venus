package jfxcomponents;

import javafx.stage.Popup;
import javafx.stage.PopupWindow.AnchorLocation;

import java.util.Optional;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;
import org.reactfx.value.Var;
import static org.reactfx.EventStreams.nonNullValuesOf;

import awtcomponents.AWTANSI;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import main.Main;

/**
 * An overlay that appears on top of the shell when typing, suggesting command
 * completions.
 */

public class CompletionOverlay {
	private static Popup overlayPopup;
	private static TextArea overlayArea;

	public static void configureElements() {
		overlayPopup = new Popup();
		overlayPopup.sizeToScene();
		overlayPopup.setAnchorLocation(AnchorLocation.CONTENT_BOTTOM_LEFT);
		overlayPopup.setAutoFix(true);

		// The overlay steals focus of primaryStage, so text needs to be transferred.
		overlayPopup.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			// Applying text the direct way to save some time. (Instead of using SWT)
			// Note that ENTER needs no extra text to be appended,
			// since the KeyEvent is handled in JFxWinloader
			if (!event.getCode().equals(KeyCode.ENTER) && !event.getCode().equals(KeyCode.BACK_SPACE))
				Main.jfxWinloader.appendText(event.getText(), JFXANSI.B_White);
			
			if (event.getCode().equals(KeyCode.BACK_SPACE))
				Main.cmdLine.deletePreviousChar();
		});
		
		// This mess of code is to follow the caret during command suggestions.
		// I have no clue what it does whatsoever.
		// It is strongly shrinked and modified, so here is the link of the original demo if you're interested:
		// https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/PopupDemo.java
		
		EventStream<Optional<Bounds>> caretBounds = nonNullValuesOf(Main.cmdLine.caretBoundsProperty());
		
		EventStreams.combine(caretBounds, Var.newSimpleVar(true).values())
				.subscribe(tuple3 -> {
					Optional<Bounds> opt = tuple3._1;
					if (opt.isPresent()) {
						Bounds b = opt.get();
						// Only change X if X and Y have changed
						
						// FIXME Only change X if xandy have changed. Y gets updated before X problem
						if (overlayPopup.getY() != b.getMaxY())
							overlayPopup.setX(b.getMaxX() - 0.9);
						overlayPopup.setY(b.getMaxY() + 0);
					}
				});
		
		// Mystery caret follow code ends here.

		overlayArea = new TextArea();
		overlayArea.setText("OVERLAY TEST");
		overlayArea.setOpacity(1.0);
		overlayArea.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		overlayArea.setMinSize(200, 100);
		overlayArea.setMaxSize(200, 100);
		overlayArea.setEditable(false);
		// TODO Place overlay below cursor (or prompt)
		// overlayArea.setTranslateX(-400);

		overlayPopup.getContent().add(overlayArea);
	}

	protected static Popup getOverlay() {
		return overlayPopup;
	}

	private static Subscription feedVisibilityToLabelText(EventStream<Optional<Bounds>> boundsStream, Popup popup,
			String item) {
		return boundsStream.map(o -> o.isPresent() ? " is " : " is not ")
				.subscribe(visibilityStatus -> System.out.println(item + visibilityStatus + "within the viewport"));
	}
}
