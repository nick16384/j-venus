package jfxcomponents;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

import awtcomponents.AWTANSI;
import engine.InfoType;
import engine.Runphase;
import engine.sys;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import libraries.Global;
import main.Main;
import shell.Shell;
import threads.ThreadAllocation;

public class JFxWinloader extends Application {
	//private TextArea Main.cmdLine;
	//private String Main.wqtest = "";
	
	public void loadGUI(String[] args) {
		sys.log("JFX", InfoType.DEBUG, "Running JavaFX init()...");
		try { init(); } catch (Exception ex) { ex.printStackTrace(); }
		sys.log("JFX", InfoType.INFO, "Starting JavaFX application...");
		Application.launch(args);
	}
	
	public void stop() {
		sys.log("JFX", InfoType.INFO, "Stopping JavaFX application...");
		Platform.exit();
		sys.log("JFX", InfoType.INFO, "Stopping JavaFX application done.");
	}
	
	@Override
	public void start(Stage primaryStage) {
		sys.log("JFX", InfoType.INFO, "Loading JavaFX window :)");
		
		try {
			primaryStage.setTitle("S.E.M.I.C.O.L.O.N. Shell " + Global.getVersion());
			
			Main.cmdLine = new PartiallyEditableInlineCSSTextArea("SHELL INIT");
			
			Main.cmdLine.setWrapText(true);
			Main.cmdLine.setBackground(new Background(
					new BackgroundFill(Paint.valueOf("BLACK"), CornerRadii.EMPTY, Insets.EMPTY)));
			
			// The cursor is the mouse symbol when hovering
			Main.cmdLine.setCursor(Cursor.TEXT);
			
	        //Main.cmdLine.setEffect(new GaussianBlur(0));
	        Main.cmdLine.setCache(true);
			
			Image icon = null;
			sys.log("JFX", InfoType.INFO,
					"Icon path: " + Global.getDataDir().getAbsolutePath() + Global.fsep + "semicolons-icon.png");
			try { icon = new Image(
					"file:" + Global.getDataDir().getAbsolutePath() + Global.fsep + "semicolons-icon.png"); }
			catch (Exception ex) { ex.printStackTrace(); }
			primaryStage.getIcons().add(icon);
			
			
			Main.cmdLine.relocate(0, 0);
			
			Main.cmdLine.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				
				// Command execute and command repeat
				if (event.getCode().equals(KeyCode.ENTER)) {
					event.consume();
					try {
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
						KeyEventHandlers.handleCommandRepeat();
					} catch (Exception ex) {
						sys.log("JFX", InfoType.ERR, "Command repeat encountered an exception. This is an internal undefined error.");
						Shell.print("You broke something. It's not healthy for your PC.");
						Shell.showPrompt();
					}
				}
				
				if (event.getCode().equals(KeyCode.PAGE_UP)) {
					sys.log("JFX", InfoType.DEBUG, "Increasing shell font size.");
					//Main.cmdLine.setFont(new Font("Terminus (TTF)", Main.cmdLine.getFont().getSize() - 1));
				} else if (event.getCode().equals(KeyCode.PAGE_DOWN)) {
					sys.log("JFX", InfoType.DEBUG, "Shrinking shell font size.");
					//Main.cmdLine.setFont(new Font("Terminus (TTF)", Main.cmdLine.getFont().getSize() - 1));
					//TODO show big number on screen for font size change
				}
				
				// Ignore text removal, if it would affect read-only text.
				if (event.getCode().equals(KeyCode.BACK_SPACE)) {
					if (Main.cmdLine.getCaretPosition() <= Main.cmdLine.getReadOnlyToIndex()
							|| !Main.cmdLine.getSelectedText().equals(""))
					event.consume();
				}
			});
			
			Main.cmdLine.setOnKeyPressed((event) -> {
				if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(event)) {
					sys.log("JFX", InfoType.DEBUG, "User pressed CTRL + C");
					Shell.print(AWTANSI.D_Cyan, "^C");
					ThreadAllocation.getCMGR().killCurrentIfRunning();
				}
			});
			
			// Possible Completion Overlay
			CompletionOverlay.configureElements();
			
			
			// Finalization and stage showing
			StackPane root = new StackPane();
			root.getChildren().add(Main.cmdLine);
			root.getChildren().add(new VirtualizedScrollPane<InlineCssTextArea>(Main.cmdLine));
			//root.getChildren().add(CompletionOverlay.getOverlayArea());
			Scene scene = new Scene(root, 900, 550);
			configureCssStylesheet(scene);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			sys.log("JFX", InfoType.DEBUG, "start(primaryStage) method end reached.");
			
		} catch (Exception ex) {
			for(StackTraceElement e: ex.getStackTrace())
		        System.err.println(e);
		}
	}
	
	public InlineCssTextArea getCmdLine() {
		return Main.cmdLine;
	}
	
	public void appendText(String text, Color color) {
		sys.log("JFX", InfoType.DEBUG, "Appending new text to cmdLine with " + text.length() + " characters.");
		
		if (Global.getCurrentPhase().equals(Runphase.RUN) && Main.cmdLine != null) {
			// Enqueue cmdLine write in JavaFX thread
			Platform.runLater(() -> {
				try {
					Main.cmdLine.appendText(text);
					Main.cmdLine.setReadOnlyTo(Main.cmdLine.getText().length());
					sys.log("JFX", InfoType.DEBUG, "Text write color hex: (0x)" + color.toString().substring(2, 8));
					// Apply text color on new segment:
					Main.cmdLine.setStyle(Main.cmdLine.getText().length() - text.length(),
										  Main.cmdLine.getText().length(),
										  "-fx-fill: #" + color.toString().substring(2, 8) + ";");
				} catch (Exception ex) {
					sys.log("JFX", InfoType.WARN, "Writing text to cmdLine failed.");
					ex.printStackTrace();
				}
			});
			Platform.requestNextPulse();
		} else {
			sys.log("JFX", InfoType.WARN, "Appending text not possible, because Main.cmdLine is null.");
		}
	}
	
	private void configureCssStylesheet(Scene scene) {
		File cssFile = new File(Global.getDataDir() + Global.fsep
				+ "consoleStyle" + Global.fsep + "default-stylesheet.css");
		
		createCssStylesheetFileIfNotExisting(cssFile);
		scene.getStylesheets().clear();
		sys.log("JFX", InfoType.INFO, "Loading external stylesheet...");
		scene.getStylesheets().add("file:///" + cssFile.getAbsolutePath().replace("\\", "/"));
	}
	
	private void createCssStylesheetFileIfNotExisting(File cssFile) {
		String cssData =
				".root {\n"
				+ "	-fx-font-family: \"Terminus (TTF)\";\n"
				+ "	-fx-font: 12pt \"Terminus (TTF)\";\n"
				+ "}\n"
				+ "\n"
				+ ".caret {\n"
				+ "	-fx-fill: #00ff00;\n"
				+ "	-fx-stroke: #00ff00;\n"
				+ "	-fx-scale-x: 8;\n"
				+ "	-fx-scale-y: 0.9;\n"
				+ "	\n"
				+ "	/*Move caret to right so it does not overlap with text*/\n"
				+ "	-fx-translate-x: 4;\n"
				+ "}";
		
		if (!libraries.FileCheckUtils.exists(cssFile)) {
			try {
				sys.log("JFX:CSS", InfoType.INFO, "External CSS stylesheet does not exist. Creating default file.");
				cssFile.createNewFile();
				Files.writeString(cssFile.toPath(), cssData, StandardOpenOption.WRITE);
			} catch (IOException ioe) {
				sys.log("JFX:CSS", InfoType.ERR, "Error creating new default CSS stylesheet.");
				sys.log("JFX:CSS", InfoType.ERR, "Running in fallback color mode.");
			}
		}
	}
}