package jfxcomponents;

import static org.reactfx.EventStreams.nonNullValuesOf;

import java.util.Optional;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.value.Var;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.stage.Stage;
import javafx.stage.Window;
import main.Main;

public class OverlayPopup extends Popup {
	private Stage primaryStage;
	private TextArea overlayArea;
	private int selectionIndex;
	private boolean hiddenLock;
	
	public OverlayPopup(Stage primaryStage) {
		super();
		this.sizeToScene();
		this.setAnchorLocation(AnchorLocation.CONTENT_BOTTOM_LEFT);
		this.setAutoFix(true);
		
		this.hiddenLock = true;
		this.primaryStage = primaryStage;
		this.selectionIndex = 0;
		
		// The overlay steals focus of primaryStage, so text needs to be transferred.
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			// Applying text the direct way to save some time. (Instead of using SWT)
			// Note that ENTER needs no extra text to be appended,
			// since the KeyEvent is handled in JFxWinloader
			if (!event.getCode().equals(KeyCode.ENTER)
					&& !event.getCode().equals(KeyCode.BACK_SPACE)
					&& !event.getCode().equals(KeyCode.TAB))
				Main.cmdLine.appendText(event.getText());
			
			if (event.getCode().equals(KeyCode.BACK_SPACE)) {
				Main.cmdLine.deletePreviousChar();
				this.hide();
			}
			
			if (event.getCode().equals(KeyCode.TAB)) {
				event.consume(); // Consume to not trigger JFxWinloader's command repeat
				addOption("se4a8nt9zaw t9a");
				select(selectionIndex + 1);
				addSelectionToCmdline();
			}
		});
		
		// Hide suggestions on BACKSPACE and SHIFT + TAB
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (new KeyCodeCombination(KeyCode.TAB, KeyCombination.SHIFT_DOWN).match(event)) {
				this.hide();
			}
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
						this.setX(b.getMaxX() - JFxWinloader.CURSOR_WIDTH);
						this.setY(b.getMaxY() + 0);
					}
				});
		
		// Mystery caret follow code ends here.

		overlayArea = new TextArea();
		overlayArea.setText("OVERLAY TEST");
		overlayArea.setOpacity(1.0);
		overlayArea.setBackground(
				new Background(
						new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		overlayArea.setMinSize(200, 100);
		overlayArea.setMaxSize(200, 100);
		overlayArea.setEditable(false);
		// TODO Place overlay below cursor (or prompt)
		// overlayArea.setTranslateX(-400);

		this.getContent().add(overlayArea);
	}
	
	@Override
	public void hide() {
		hideUntilReactivated();
	}
	
	public void hideUntilReactivated() {
		hiddenLock = true;
		super.hide();
	}
	
	public void unlockHidden() {
		hiddenLock = false;
	}
	
	@Override
	public void show(Window owner) {
		if (hiddenLock)
			return;
		Platform.runLater(() -> {
			super.show(owner);
		});
	}
	
	public void addOption(String suggestion) {
		if (suggestion.trim().contains("\n"))
			return;
		overlayArea.appendText("\n" + suggestion.trim());
	}
	
	public void select(int fromUpToDown) {
		selectionIndex = fromUpToDown;
		
		overlayArea.deselect();
		String[] lines = overlayArea.getText().split("\n");
		
		int selectionStart = 0;
		int selectionEnd = 0;
		
		for (int i = 0; i < fromUpToDown - 1; i++) {
			selectionStart += lines[i].length() + 1;
		}
		
		for (int i = 0; i < fromUpToDown; i++) {
			selectionEnd += lines[i].length() + 1;
		}
		
		overlayArea.selectRange(selectionStart, selectionEnd);
	}
	
	private String getCurrentSelection() {
		return overlayArea.getSelectedText().trim();
	}
	
	private void addSelectionToCmdline() {
		Main.cmdLine.deleteText(
				Main.cmdLine.getReadOnlyToIndex(),
				Main.cmdLine.getText().length());
		Main.cmdLine.appendText(this.getCurrentSelection());
	}
}
