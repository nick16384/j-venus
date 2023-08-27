package jfxcomponents;

import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import engine.InfoType;
import engine.sys;
import filesystem.InternalFiles;
import filesystem.VirtualFile;
import javafx.scene.input.KeyCode;
import libraries.Err;
import libraries.VariableInitializion;
import libraries.Global;
import main.Main;
import shell.Shell;
import threads.ThreadAllocation;

/**
 * Separate class, specific to running actions that happen on key events ENTER and UP in JFxWinloader.
 * @author nick16384
 *
 */

public class KeyEventHandlers {
	/**
	 * This piece of code is responsible for doing the work,
	 * when enter has been pressed (meaning a new command has been submitted).
	 */
	
	protected static synchronized void actionOnEnter() {
		Shell.getCommandHistory().resetRowStats();
		//UPDATE SHELL STREAM ==============================================================================
		ThreadAllocation.getSWT().updateShellStream();
		//END UPDATE SHELL STREAM ==========================================================================
		//Splitting WindowMain.cmdLine text into command
		String[] lines = Main.cmdLine.getText().split("\n");
		
		String lastLine = lines[lines.length - 1];
		
		System.err.println("VarLib prompt: " + Shell.getPrompt());
		System.err.println("Last line length: " + lastLine.length());
		System.err.println("Last line content: " + lastLine);
		System.err.println("Prompt length: " + Shell.getPrompt().length());
		
		//Extract full command from last line (Remove prompt)
		//Dev. note: VarLib.getPrompt() contains ANSI excapes, but cmdLine.getText() doesn't, so
		//all ANSI escape chars had to be cleared out by the regex shown.
		String fullCommand = lastLine.substring(
				Shell.getPrompt().replaceAll("\u001B\\[[\\d;]*[^\\d;]","").length(), lastLine.length());
		
		//if (fullCommand.contains(VarLib.getPrompt())) { fullCommand = fullCommand.split("\\$ ")[1]; }
		if (!fullCommand.isBlank()) {
			if (fullCommand.contains(" && ")) {
				sys.log("MAIN", InfoType.WARN, "Info: Found multiple commands connected with '&&'.");
				sys.log("MAIN", InfoType.WARN, "This is still experimental: Expect errors.");
				Shell.println("Using experimental command interconnect: '&&'");
				for (String subCommand : fullCommand.split(" && ")) {
					sys.log("MAIN", InfoType.DEBUG, "Running '" + fullCommand + "'");
					sys.log("Subcommand: " + subCommand);
					try {
						commandProcessing.Command cmd = new commandProcessing.Command(subCommand);
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
				sys.log("MAIN", InfoType.DEBUG, "Sending '" + fullCommand + "' to Command Parser");
				try {
					new commandProcessing.Command(fullCommand).start();
					//For returnVal, try:
					//CommandMain.executeCommand(new components.Command(fullCommand));
				} catch (Exception ex) {
					Err.shellPrintErr(ex, "FATAL ERROR", "Non-caught JVM exception in class CmdMain");
				}
			}
			//=========================ADD FULLCMD TO HISTORY===============================
			Shell.getCommandHistory().add(fullCommand);
			try {
				String history = InternalFiles.getCmdHistory().readContents();
				int max_history_size = Integer.parseInt(
						Global.getDataDir().newVirtualFile("/cmd_history_max_length").readContents().trim());
				//Remove first entry of history until size of entries is below count in cmd_history_max_length
				while (history.split("\n").length > max_history_size) {
					InternalFiles.getCmdHistory().writeString(
							history.replaceFirst(history.split("\n")[0], ""), StandardOpenOption.WRITE);
				}
				InternalFiles.getCmdHistory().writeString(fullCommand + "\n", StandardOpenOption.APPEND);
			} catch (NumberFormatException nfe) {
				sys.log("MAIN", InfoType.ERR, "Parsing cmd_history_max_length failed. Check file exists" +
						" and contains a number below 2.147.483.647");
			}
			//============================END ADD FULLCMD TO HISTORY==============================
		} else {
			Shell.showPrompt();
		}
	}
	
	/**
	 * handleCommandRepeat() is responsible for adding the last-executed command
	 * into the shell.
	 */
	protected static synchronized void handleCommandRepeat(boolean forwardRepeat) {
		//========================================COMMAND REPEAT============================================
		Shell.getCommandHistory().commandRepeatRequested(forwardRepeat);
		
		String insertion = Shell.getCommandHistory().getCurrent();
		System.out.println("Insertion : " + insertion);
		
		// A command was repeated already, so it needs to be removed first
		if (Shell.getCommandHistory().getRepeatInRow() >= 1) {
			Main.cmdLine.deleteText(
					Main.cmdLine.getText().length() - Main.cmdLine.getLastWrittenText().length(),
					Main.cmdLine.getText().length());
		}
		
		// Text needs to be applied directly, so user can edit it.
		Main.jfxWinloader.getCmdLine().appendText(insertion);
		//========================================COMMAND REPEAT END============================================
	}
}
