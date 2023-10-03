package jfxcomponents;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

import awtcomponents.AWTANSI;
import commands.CommandManagement;
import engine.InfoType;
import engine.Runphase;
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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import libraries.Err;
import libraries.Global;
import main.Main;
import shell.Shell;
import threads.ThreadAllocation;

public class GUIManager {
	public static final int CURSOR_WIDTH = 13;
	public static final int WINDOW_WIDTH = 900;
	public static final int WINDOW_HEIGHT = 550;
	//private TextArea Main.cmdLine;
	//private String Main.wqtest = "";
	private static WindowGUI mainGUI;
	
	protected static volatile PartiallyEditableInlineCSSTextArea cmdLine;
	protected static Font shellFont;
	public static final Color DEFAULT_SHELL_COLOR = Color.LIME;
	
	public static void loadGUI() {
		mainGUI = new WindowGUI();
		sys.log("JFX", InfoType.INFO, "Starting JavaFX application...");
		mainGUI.launch();
	}
	
	public static void appendText(String text, Color color) {
		sys.log("JFX", InfoType.DEBUG, "Appending new text to cmdLine with " + text.length() + " characters.");
		
		if (Global.getCurrentPhase().equals(Runphase.RUN) && cmdLine != null) {
			// Enqueue cmdLine write in JavaFX thread
			Platform.runLater(() -> {
				try {
					cmdLine.appendText(text);
					cmdLine.setReadOnlyTo(cmdLine.getText().length());
					sys.log("JFX", InfoType.DEBUG, "Text write color hex: (0x)" + color.toString().substring(2, 8));
					// Apply text color on new segment:
					cmdLine.setStyle(cmdLine.getText().length() - text.length(),
										  cmdLine.getText().length(),
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
	
	protected static void configureCssStylesheet(Scene scene) {
		File cssFile = Global.getDataDir().newVirtualFile("/consoleStyle/default-stylesheet.css");
		
		createCssStylesheetFileIfNotExisting(cssFile);
		scene.getStylesheets().clear();
		sys.log("JFX", InfoType.INFO, "Loading external stylesheet...");
		scene.getStylesheets().add("file:///" + cssFile.getAbsolutePath().replace("\\", "/"));
	}
	
	private static void createCssStylesheetFileIfNotExisting(File cssFile) {
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
		
		if (!filesystem.FileCheckUtils.exists(cssFile)) {
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
	
	public static PartiallyEditableInlineCSSTextArea getCmdLine() {
		return cmdLine;
	}
	
	public static Font getShellFont() {
		return shellFont;
	}
}