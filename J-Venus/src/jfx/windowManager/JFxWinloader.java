package jfx.windowManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import engine.sys;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
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
			Main.cmdLine = new TextArea();
			Main.cmdLine.setWrapText(true);
			Main.cmdLine.setText("SHELL INIT");
			Main.cmdLine.setStyle(""
					+ "-fx-control-inner-background:#000000;"
					//+ " -fx-font-family: Terminus(TTF);"
					//+ " -fx-font: 12pt Terminus(TTF);" //Font is set below
					+ " -fx-highlight-fill: #00ff00;"
					+ " -fx-highlight-text-fill: #000000;"
					+ " -fx-text-fill: #00ff00; ");
			sys.log("\n" + Main.cmdLine.getStyle());
			Main.cmdLine.setFont(new Font("Terminus (TTF)", 18));
			
			Main.cmdLine.relocate(0, 0);
			
			Main.cmdLine.setOnKeyPressed(event -> {
				if (event.getCode().equals(KeyCode.ENTER)) {
					// ON COMMAND SUBMIT =================================================
		        	actionOnEnter();
		        	// COMMAND SUBMIT END ================================================
				} else if (event.getCode().equals(KeyCode.PLUS)) {
					sys.log("JFX", 1, "Increasing shell font size.");
					Main.cmdLine.setFont(new Font("Terminus (TTF)", Main.cmdLine.getFont().getSize() + 1));
					// Remove last character
					Main.cmdLine.setText(Main.cmdLine.getText().substring(0, Main.cmdLine.getText().length() - 1));
					Main.cmdLine.positionCaret(Main.cmdLine.getText().length());
				} else if (event.getCode().equals(KeyCode.MINUS)) {
					sys.log("JFX", 1, "Shrinking shell font size.");
					Main.cmdLine.setFont(new Font("Terminus (TTF)", Main.cmdLine.getFont().getSize() - 1));
					// Remove last character
					Main.cmdLine.setText(Main.cmdLine.getText().substring(0, Main.cmdLine.getText().length() - 1));
					Main.cmdLine.positionCaret(Main.cmdLine.getText().length());
				}
				
				//TODO Add support for command repeat when pressing "up" key
			});
			
			//TODO change style to match a console

			StackPane root = new StackPane();
			root.getChildren().add(Main.cmdLine);
			primaryStage.setScene(new Scene(root, 900, 550));
			primaryStage.show();
			
			sys.log("JFX", 1, "start(primaryStage) method end reached.");
			
		} catch (Exception ex) {
			for(StackTraceElement e: ex.getStackTrace())
		        System.err.println(e);
		}
	}
	
	public TextArea getCmdLine() {
		return Main.cmdLine;
	}
	
	public void appendText(String text) {
		sys.log("JFX", 1, "Appending new text to cmdLine with " + text.length() + " characters.");
		
		if (Main.cmdLine != null) {
			Main.cmdLine.setEditable(false);
			Platform.requestNextPulse();
			// Enqueue cmdLine write in JavaFX thread
			Platform.runLater(() -> {
				 Main.cmdLine.appendText(text);
			});
			Platform.requestNextPulse();
			Main.cmdLine.setEditable(true);
		} else {
			sys.log("JFX", 3, "Appending text not possible, because Main.cmdLine is null.");
		}
	}
	
	public void clearCmdLine() {
		Main.cmdLine.clear();
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
		String[] lines = Main.cmdLine.textProperty().get().split("\n");
		
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
