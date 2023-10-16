package jfxcomponents;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

import awtcomponents.AWTANSI;
import commands.CommandManagement;
import engine.InfoType;
import engine.sys;
import filesystem.VirtualFile;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import libraries.Global;
import main.Main;
import shell.Shell;

public class WindowGUI extends Application {
	
	protected void launch() {
		// Convert args List to Array
		Application.launch(Main.argsMain.stream().toArray(String[]::new));
	}
	
	@Override
	public void init() {
		sys.log("GUI", InfoType.DEBUG, "Window init currently not necessary (empty).");
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		sys.log("JFX", InfoType.INFO, "Loading JavaFX window :)");
		
		try {
			primaryStage.setTitle("S.E.M.I.C.O.L.O.N. Shell " + Global.getVersion());
			
			GUIManager.cmdLine = new PartiallyEditableInlineCSSTextArea("SHELL INIT");
			
			GUIManager.cmdLine.setWrapText(true);
			GUIManager.cmdLine.setBackground(new Background(
					new BackgroundFill(Paint.valueOf("BLACK"), CornerRadii.EMPTY, Insets.EMPTY)));
			
			// The cursor is the mouse symbol when hovering
			GUIManager.cmdLine.setCursor(Cursor.TEXT);
			
	        //Main.cmdLine.setEffect(new GaussianBlur(0));
			// Increases performance in shell for more RAM usage
	        GUIManager.cmdLine.setCache(true);
			
			Image icon = null;
			VirtualFile iconFile = Global.getDataDir().newVirtualFile("/semicolons-icon.png");
			sys.log("JFX", InfoType.DEBUG, "Icon path: " + iconFile.getAbsolutePath());
			try { icon = new Image(
					"file:" + iconFile.getAbsolutePath()); }
			catch (Exception ex) { ex.printStackTrace(); }
			primaryStage.getIcons().add(icon);
			
			GUIManager.cmdLine.relocate(0, 0);
			
			GUIManager.cmdLine.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				
				// Command execute and command repeat
				if (event.getCode().equals(KeyCode.ENTER)) {
					event.consume();
					try {
						CompletionOverlay.commandTypingIsFinished();
						KeyEventHandlers.actionOnEnter();
					} catch (Exception ex) {
						sys.log("JFX", InfoType.ERR, "Exception in command extractor / formatter: "
								+ "Probably the prompt was edited by the user.");
						Shell.print("Whatever you're trying, it's not funny!");
						Shell.showPrompt();
					}
				} else if (event.getCode().equals(KeyCode.UP)) {
					event.consume();
					try {
						KeyEventHandlers.handleCommandRepeat(true);
					} catch (Exception ex) {
						sys.log("JFX", InfoType.ERR, "Command repeat encountered an exception. This is an internal undefined error.");
						Shell.print("You broke something. It's not healthy for your PC.");
						Shell.showPrompt();
						ex.printStackTrace();
					}
				} else if (event.getCode().equals(KeyCode.DOWN)) {
					event.consume();
					try {
						KeyEventHandlers.handleCommandRepeat(false);
					} catch (Exception ex) {
						sys.log("JFX", InfoType.ERR, "Command repeat encountered an exception. This is an internal undefined error.");
						Shell.print("You broke something. It's not healthy for your PC.");
						Shell.showPrompt();
						ex.printStackTrace();
					}
				} else if (event.getCode().equals(KeyCode.TAB)) {
					// Command suggestion is requested
					event.consume();
					CompletionOverlay.disableLockAndShowOverlay(primaryStage);
				}
				
				if (event.getCode().equals(KeyCode.PAGE_UP)) {
					sys.log("JFX", InfoType.DEBUG, "Increasing shell font size.");
					//Main.cmdLine.setFont(new Font("Terminus (TTF)", Main.cmdLine.getFont().getSize() - 1));
				} else if (event.getCode().equals(KeyCode.PAGE_DOWN)) {
					sys.log("JFX", InfoType.DEBUG, "Shrinking shell font size.");
					//Main.cmdLine.setFont(new Font("Terminus (TTF)", Main.cmdLine.getFont().getSize() - 1));
					//TODO show big number on screen for font size change
				}
				
				if (!CompletionOverlay.getOverlay().isShowing())
					CompletionOverlay.getOverlay().fireEvent(event);
			});
			
			GUIManager.cmdLine.setOnKeyPressed((event) -> {
				if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) {
					sys.log("JFX", InfoType.DEBUG, "User pressed CTRL + C");
					Shell.print(ANSI.D_Cyan, "^C");
					CommandManagement.killCurrentIfRunning();
				}
			});
			
			// Possible Completion Overlay
			CompletionOverlay.configureElements(primaryStage);
			
			// Finalization and stage showing
			StackPane root = new StackPane();
			root.getChildren().add(GUIManager.cmdLine);
			root.getChildren().add(new VirtualizedScrollPane<InlineCssTextArea>(GUIManager.cmdLine));
			Scene scene = new Scene(root, GUIManager.WINDOW_WIDTH, GUIManager.WINDOW_HEIGHT);
			GUIManager.configureCssStylesheet(scene);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			CompletionOverlay.showOverlay(primaryStage);
			
			sys.log("JFX", InfoType.DEBUG, "start(primaryStage) method end reached.");
			
		} catch (Exception ex) {
			for(StackTraceElement e: ex.getStackTrace())
		        System.err.println(e);
		}
	}
	
	public void stop() {
		sys.log("JFX", InfoType.INFO, "Stopping JavaFX application...");
		Platform.exit();
		sys.log("JFX", InfoType.INFO, "Stopping JavaFX application done.");
	}
}
