package jfxcomponents;

import static org.reactfx.EventStreams.nonNullValuesOf;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.value.Var;

import engine.LogLevel;
import engine.sys;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
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
	private ListView<String> selectionListView;
	private volatile int selectionIndex;
	private boolean hiddenLock;
	
	public OverlayPopup(Stage primaryStage) {
		super();
		this.sizeToScene();
		this.setAnchorLocation(AnchorLocation.CONTENT_BOTTOM_LEFT);
		this.setAutoFix(true);
		
		this.hiddenLock = false;
		selectionIndex = 0;
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (!this.isShowing())
				return;
			
			
			
			if (new KeyCodeCombination(KeyCode.TAB, KeyCombination.SHIFT_DOWN).match(event)) {
				event.consume();
				this.hideUntilReactivated();
			}
			
			System.err.println("Key Event: " + event.getCode());
			
			if (event.getCode().equals(KeyCode.BACK_SPACE)) {
				event.consume();
				this.hideUntilReactivated();
			}
			
			if (event.getCode().equals(KeyCode.TAB)) {
				event.consume();
				addOption("SEL" + (new Random().nextInt(100000)));
				select(selectionIndex + 1);
				addSelectionToCmdline();
			}
			
			if (event.getCode().equals(KeyCode.UP)) {
				event.consume();
				select(selectionIndex - 1);
				addSelectionToCmdline();
			} else if (event.getCode().equals(KeyCode.DOWN)) {
				event.consume();
				select(selectionIndex + 1);
				addSelectionToCmdline();
			}
		});
		
		// This mess of code is to follow the caret during command suggestions.
		// I have no clue what it does whatsoever.
		// It is strongly shrinked and modified, so here is the link of the original demo if you're interested:
		// https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/PopupDemo.java
		
		EventStream<Optional<Bounds>> caretBounds = nonNullValuesOf(GUIManager.getCmdLine().caretBoundsProperty());
		
		EventStreams.combine(caretBounds, Var.newSimpleVar(true).values())
				.subscribe(tuple3 -> {
					Optional<Bounds> opt = tuple3._1;
					if (opt.isPresent()) {
						Bounds b = opt.get();
						this.setX(b.getMaxX() - GUIManager.CURSOR_WIDTH);
						this.setY(b.getMaxY() + 0);
					}
				});
		
		// Mystery caret follow code ends here.

		selectionListView = new ListView<>();
		selectionListView.setBackground(
				new Background(
						new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		selectionListView.setMinSize(200, 100);
		selectionListView.setMaxSize(200, 100);
		selectionListView.setEditable(false);

		this.getContent().add(selectionListView);
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
		select(0);
		Platform.runLater(() -> {
			super.show(owner);
		});
	}
	
	public void changeOption(int index, String newSuggestion) {
		index = getValidIndexFrom(index);
		selectionListView.getItems().set(index, newSuggestion);
	}
	
	public void addOption(String suggestion) {
		if (suggestion == null || suggestion.isBlank() || suggestion.trim().contains("\n"))
			return;
		selectionListView.getItems().add(suggestion.trim());
	}
	
	/**
	 * Returns a valid index inside the selectionListView.
	 * @apiNote A selection index of -1 causes the index to wrap around to the selectionListView end.
	 *  If the index is one above the selectionListView max index, then 0 will be returned.
	 */
	private int getValidIndexFrom(int index) {
		// Check for wrap around
		index = index == -1 ? selectionListView.getItems().size() - 1 : index;
		index = index == selectionListView.getItems().size() ? 0 : index;
		
		// Filter any invalid numbers (out of range)
		index = index <= 0 ? 0 : index;
		index = index > selectionListView.getItems().size() - 1 ? selectionListView.getItems().size() - 1 : index;
		
		if (index < 0 || index > selectionListView.getItems().size() - 1) {
			// If index is out of range, there are currently no options to select from.
			return -1;
		}
		
		return index;
	}
	
	private String getCurrentSelection() {
		return selectionListView.getSelectionModel().getSelectedItem();
	}
	
	private void select(int index) {
		System.err.println("Index before fetch: " + index);
		index = getValidIndexFrom(index);
		System.err.println("Index after fetch: " + index);
		selectionIndex = index;
		if (index <= -1) { sys.log("POPUP", LogLevel.DEBUG, "Nothing to select, returning."); return; }
		selectionListView.getSelectionModel().select(index);
	}
	
	private void addSelectionToCmdline() {
		GUIManager.getCmdLine().deleteText(
				GUIManager.getCmdLine().getReadOnlyToIndex(),
				GUIManager.getCmdLine().getText().length());
		GUIManager.getCmdLine().appendText(this.getCurrentSelection());
	}
}
