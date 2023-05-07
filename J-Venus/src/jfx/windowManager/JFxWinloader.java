package jfx.windowManager;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import javax.swing.text.BadLocationException;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CaretNode;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.model.StyledDocument;

import awt.windowManager.WindowMain;
import engine.Keyboard;
import engine.sys;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.css.Style;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.util.Duration;
import libraries.Err;
import libraries.OpenLib;
import libraries.VarLib;
import main.Main;

public class JFxWinloader extends Application {
	//private TextArea Main.cmdLine;
	//private String Main.wqtest = "";
	
	public void loadGUI(String[] args) {
		sys.log("JFX", 1, "Running JavaFX init()...");
		try { init(); } catch (Exception ex) { ex.printStackTrace(); }
		sys.log("JFX", 1, "Starting JavaFX application...");
		Application.launch(args);
	}
	
	public void stop() {
		sys.log("JFX", 1, "Stopping JavaFX application...");
		Platform.exit();
		sys.log("JFX", 1, "Stopping JavaFX application done.");
	}
	
	@Override
	public void start(Stage primaryStage) {
		sys.log("JFX", 1, "Loading JavaFX window :)");
		
		try {
			primaryStage.setTitle("J-Venus " + VarLib.getVersion());
			
			Main.cmdLine = new InlineCssTextArea("SHELL INIT");
			
			Main.cmdLine.setWrapText(true);
			Main.cmdLine.setBackground(new Background(
					new BackgroundFill(Paint.valueOf("BLACK"), CornerRadii.EMPTY, Insets.EMPTY)));
			
			//TODO set cmdLine color style to lime (black is currently invisible) and
			//TODO change cmdLine font to shell font
			
			Main.cmdLine.setStyle(".root {\n"
					+ "-fx-font-family: \"Terminus (TTF)\";\n"
					+ "-fx-font: 12pt \"Terminus (TTF)\";\n"
					+ "-rtfx-caret-shape: block;\n"
					+ "-rtfx-caret-color: red;\n"
					+ "}\n");
			sys.log("\n" + Main.cmdLine.getStyle());
			Main.cmdLine.applyCss();
			Main.cmdLine.requestFocus();
			
			// Caret / cursor configuration
			CaretNode cn = new CaretNode("shellCaret", Main.cmdLine);
			cn.setVisible(true);
			cn.setBlinkRate(Duration.millis(500));
			cn.setScaleX(8);
			cn.setScaleY(0.8);
			cn.translateXProperty().set(5); // Move right so it doesn't overlap with text
			cn.setStroke(Color.LIME); // Set caret fill color
			cn.toFront();
			Main.cmdLine.addCaret(cn);
			Main.cmdLine.displaceCaret(0);
			
			//Main.cmdLine.setFont(new Font("Terminus (TTF)", 18));
			
			//FIXME Fix ANSI colors for JavaFX
			//TODO rename every "JavaDOS", "J-Vexus" and "J-Venus" part to SEMICOLONS
			
	        //Main.cmdLine.setEffect(new GaussianBlur(0));
	        Main.cmdLine.setCache(true);
			
			Image icon = null;
			sys.log("JFX", 1,
					"Icon path: " + VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "semicolons-icon.png");
			try { icon = new Image(
					"file:" + VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "semicolons-icon.png"); }
			catch (Exception ex) { ex.printStackTrace(); }
			primaryStage.getIcons().add(icon);
			
			
			Main.cmdLine.relocate(0, 0);
			
			Main.cmdLine.setOnKeyPressed(event -> {
				if (event.getCode().equals(KeyCode.ENTER)) {
					// ON COMMAND SUBMIT =================================================
		        	actionOnEnter();
		        	// COMMAND SUBMIT END ================================================
				} else if (event.getCode().equals(KeyCode.UP)) {
					handleCommandRepeat();
				}
				
				if (event.getCode().equals(KeyCode.PAGE_UP)) {
					sys.log("JFX", 1, "Increasing shell font size.");
					//Main.cmdLine.setFont(new Font("Terminus (TTF)", Main.cmdLine.getFont().getSize() - 1));
				} else if (event.getCode().equals(KeyCode.PAGE_DOWN)) {
					sys.log("JFX", 1, "Shrinking shell font size.");
					//Main.cmdLine.setFont(new Font("Terminus (TTF)", Main.cmdLine.getFont().getSize() - 1));
					//TODO show big number on screen for font size change
				}
				
				//TODO Add support for command repeat when pressing "up" key
			});
			
			//TODO change style to match a console
			
			StackPane root = new StackPane();
			root.getChildren().add(Main.cmdLine);
			root.getChildren().add(new VirtualizedScrollPane(Main.cmdLine));
			primaryStage.setScene(new Scene(root, 900, 550));
			primaryStage.show();
			
			sys.log("JFX", 1, "start(primaryStage) method end reached.");
			
		} catch (Exception ex) {
			for(StackTraceElement e: ex.getStackTrace())
		        System.err.println(e);
		}
	}
	
	public InlineCssTextArea getCmdLine() {
		return Main.cmdLine;
	}
	
	public void triggerScrollUpdate() {
		Platform.runLater(() -> {
			Main.cmdLine.requestFollowCaret();
			Main.cmdLine.displaceCaret(Main.cmdLine.getText().length());
			Platform.requestNextPulse();
		});
	}
	
	public void appendText(String text, Color color) {
		sys.log("JFX", 1, "Appending new text to cmdLine with " + text.length() + " characters.");
		
		if (sys.getActivePhase().equals("run") && Main.cmdLine != null) {
			Main.cmdLine.setEditable(false);
			Platform.requestNextPulse();
			// Enqueue cmdLine write in JavaFX thread
			Platform.runLater(() -> {
				try {
					Main.cmdLine.appendText(text);
					sys.log("JFX", 1, "Text write color hex: " + color.toString().substring(2, 8));
					// Apply text color on new segment:
					Main.cmdLine.setStyle(Main.cmdLine.getText().length() - text.length(),
							Main.cmdLine.getText().length(), "-fx-fill: #"
									+ color.toString().substring(2, 8) + ";");
				} catch (Exception ex) {
					sys.log("JFX", 2, "Writing text to cmdLine failed, probably because duplicate entries.");
					//ex.printStackTrace();
				}
			});
			Platform.requestNextPulse();
			Main.cmdLine.setEditable(true);
		} else {
			sys.log("JFX", 3, "Appending text not possible, because Main.cmdLine is null.");
		}
	}
	
	/**
	 * Clears Main.cmdLine (set text to "")
	 */
	public void clearCmdLine() {
		Platform.runLater(() -> {
			Main.cmdLine.clear();
		});
	}
	
	/**
	 * handleCommandRepeat() is responsible for adding the last-executed command
	 * into the shell.
	 */
	
	private void handleCommandRepeat() {
		//========================================COMMAND REPEAT============================================
		main.Main.tabCountInRow++;
		OpenLib.cmdLinePrepare();
		
		main.Main.commandHistory.clear();
		try {
			//Add all entries of cmd_history to LinkedList main.Main.commandHistory
			main.Main.commandHistory.addAll(Arrays.asList(Files.readString(Paths.get(
					VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history")).split("\n")));
		} catch (IOException ioe) {
			//TODO edit command history and TAB repeating further
			sys.log("MAIN", 3, "CMD History read fail. main.Main.commandHistory<LinkedList> is empty now.");
		}
		
		if (main.Main.tabCountInRow > main.Main.commandHistory.size()) {
			Toolkit.getDefaultToolkit().beep();
			sys.log("MAIN", 1, "Command history end reached");
		} else if (main.Main.tabCountInRow == 1) {
			//Write out last command without it getting protected (..., true)
			sys.log("REPEAT", 0, "Command repeat: "
					+ main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow));
			sys.shellPrint(1, "HIDDEN", main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow), true);
		} else {
			//TODO Find some sort of replaceLast() \/ -------------------
			/*try {
				WindowMain.cmdLine.getStyledDocument().remove(
						WindowMain.cmdLine.getText().indexOf(main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow + 1)),
						main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow + 1).length());
			} catch (BadLocationException ble) {
				OpenLib.logWrite("MAIN", 3, "Command repeat error: Could not remove old command");
			}*/
			sys.log("REPEAT", 0, "Command repeat(" + main.Main.tabCountInRow + "): "
					+ main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow));
			sys.shellPrint(1, "HIDDEN", main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow), true);
		}
		//========================================COMMAND REPEAT END============================================
	}
	
	/**
	 * This piece of code is responsible for doing the work,
	 * when enter has been pressed (meaning a new command has been submitted).
	 */
	
	public void actionOnEnter() {
		main.Main.tabCountInRow = 0;
		//UPDATE SHELL STREAM ==============================================================================
		Main.ThreadAllocMain.getSWT().updateShellStream();
		//END UPDATE SHELL STREAM ==========================================================================
		//Splitting WindowMain.cmdLine text into command
		String[] lines = Main.cmdLine.getText().split("\n");
		
		String lastLine = lines[lines.length - 1];
		
		System.err.println("VarLib prompt: " + VarLib.getPrompt());
		System.err.println("Last line length: " + lastLine.length());
		System.err.println("Last line content: " + lastLine);
		System.err.println("Prompt length: " + VarLib.getPrompt().length());
		
		//Extract full command from last line (Remove prompt)
		//Dev. note: VarLib.getPrompt() contains ANSI excapes, but cmdLine.getText() doesn't, so
		//all ANSI escape chars had to be cleared out by the regex shown.
		String fullCommand = lastLine.substring(
				VarLib.getPrompt().replaceAll("\u001B\\[[\\d;]*[^\\d;]","").length(), lastLine.length());
		
		//if (fullCommand.contains(VarLib.getPrompt())) { fullCommand = fullCommand.split("\\$ ")[1]; }
		if (!fullCommand.isBlank()) {
			if (fullCommand.contains(" && ")) {
				sys.log("MAIN", 2, "Info: Found multiple commands connected with '&&'.");
				sys.log("MAIN", 2, "This is still experimental: Expect errors.");
				sys.shellPrintln("Using experimental command interconnect: '&&'");
				for (String subCommand : fullCommand.split(" && ")) {
					sys.log("MAIN", 0, "Running '" + fullCommand + "'");
					sys.log("Subcommand: " + subCommand);
					try {
						components.Command cmd = new components.Command(subCommand);
						cmd.start();
						sys.log("New thread started (subCommand placed into cmdQueue)");
						//For returnVal, try:
						//CommandMain.executeCommand(new components.Command(fullCommand));
					} catch (Exception ex) {
						//Error information is printed to stdout and shell
						Err.shellPrintErr(ex, "FATAL ERROR", "Non-caught JVM exception in class CmdMain");
					}
				}
			} else {
				sys.log("MAIN", 0, "Sending '" + fullCommand + "' to Command Parser");
				try {
					new components.Command(fullCommand).start();
					//For returnVal, try:
					//CommandMain.executeCommand(new components.Command(fullCommand));
				} catch (Exception ex) {
					Err.shellPrintErr(ex, "FATAL ERROR", "Non-caught JVM exception in class CmdMain");
				}
			}
			//=========================ADD FULLCMD TO HISTORY===============================
			main.Main.commandHistory.add(fullCommand);
			try {
				String history = Files.readString(Paths.get(
						VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history"));
				int max_history_size = Integer.parseInt(Files.readString(Paths.get(
						VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history_max_length")).trim());
				//Remove first entry of history until size of entries is below count in cmd_history_max_length
				while (history.split("\n").length > max_history_size) {
					Files.writeString(Paths.get(
							VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history"),
							history.replaceFirst(history.split("\n")[0], ""),
							StandardOpenOption.WRITE);
				}
				Files.writeString(Paths.get(
						VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history"),
						fullCommand + "\n", StandardOpenOption.APPEND);
			} catch (IOException ioe) {
				sys.log("MAIN", 2, "IOException while writing to cmd history.");
			} catch (NumberFormatException nfe) {
				sys.log("MAIN", 2, "Parsing cmd_history_max_length failed. Check file exists" +
						" and contains a number below 2.147.483.647");
			}
			//============================END ADD FULLCMD TO HISTORY==============================
		} else {
			OpenLib.cmdLinePrepare();
		}
	}
}
