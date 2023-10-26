package shell;

import components.CommandHistory;
import engine.LogLevel;
import engine.Runphase;
import engine.sys;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import jfxcomponents.ANSI;
import jfxcomponents.GUIManager;
import libraries.Global;
import main.Main;
import threads.ShellWriteThread;

/**
 * Contains all functions required by other methods for the shell.
 */
public class Shell {
	protected static final String DEFAULT_PROMPT_PATTERN = "\u001B[1;32m$USERNAME\u001B[1;37m" + "@"
			+ "\u001B[1;32m$HOSTNAME\u001B[1;36m" + ":$PATH\u001B[1;37m$# "; // Color and shell combined
	protected static String promptPattern = DEFAULT_PROMPT_PATTERN;
	protected static String prompt = ">>";
	
	private static CommandHistory commandHistory;

	public static String getPrompt() {
		return prompt;
	}
	
	// ======================================== PROMPT MODIFICATION ========================================
	public static void setPromptPattern(String newPromptPattern) {
		if (newPromptPattern != null) {
			if (newPromptPattern.equalsIgnoreCase("default"))
				promptPattern = DEFAULT_PROMPT_PATTERN;
			else
				promptPattern = newPromptPattern;
		} else {
			sys.log("OPENLIB", LogLevel.WARN, "Attempt to set prompt to null, changing nothing.");
		}
	}

	public static String getPromptWithPattern(String pattern) {
		String newPrompt = "";
		if (pattern.contains("$")) {
			for (String patternPart : pattern.split("\\$")) {
				if (patternPart.startsWith("#")) {
					newPrompt = newPrompt.concat("$" + patternPart.replaceFirst("\\#", ""));
				} else if (patternPart.startsWith("-")) {
					newPrompt = "";
				} else if (patternPart.startsWith("USERNAME")) {
					newPrompt = newPrompt
							.concat(Global.getUsername() + patternPart.replaceFirst("(\\$)?USERNAME", ""));
				} else if (patternPart.startsWith("HOSTNAME")) {
					newPrompt = newPrompt
							.concat(Global.getHostname() + patternPart.replaceFirst("(\\$)?HOSTNAME", ""));
				} else if (patternPart.startsWith("PATH")) {
					newPrompt = newPrompt
							.concat(Global.getCurrentDir() + patternPart.replaceFirst("(\\$)?PATH", ""));
				} else if (patternPart.startsWith("TIME")) {
					newPrompt = newPrompt
							.concat(Global.getDateTime(false) + patternPart.replaceFirst("(\\$)?TIME", ""));
				} else if (patternPart.startsWith("DATETIME")) {
					newPrompt = newPrompt
							.concat(Global.getDateTime(true) + patternPart.replaceFirst("(\\$)?DATETIME", ""));
				} else {
					newPrompt = newPrompt.concat(patternPart);
				}
			}
		} else if (!pattern.isBlank()) {
			newPrompt = pattern;
		} else if (!prompt.isBlank()) {
			newPrompt = prompt;
		} else {
			newPrompt = "DEFAULT>";
		}
		return newPrompt;
	}

	public static String getDefaultPromptPattern() {
		return DEFAULT_PROMPT_PATTERN;
	}
	
	// TODO Make two-line prompt, if one line is too long
	public static void showPrompt() {
		if (!Global.javafxEnabled)
			Main.mainFrameAWT.getCmdLine().setEditable(false);
		if (Global.getCurrentPhase().equals(Runphase.INIT)) {

			Shell.print(Global.getMOTD()); // Print message of the day, when in init phase
			prompt = getPromptWithPattern(promptPattern);
			clearCmdLine();
			Shell.print(1, "HIDDEN", prompt);

		} else if (Global.getCurrentPhase().equals(Runphase.RUN)) {

			prompt = getPromptWithPattern(promptPattern);
			Shell.print(ANSI.B_Green, "\n" + prompt);

		} else {
			sys.log("LIB", LogLevel.CRIT, "Shell prepare was called during pre-init. Doing nothing, but this");
			sys.log("LIB", LogLevel.CRIT, "is unusual and should not be seen multiple times.");
			sys.log("LIB", LogLevel.CRIT, "Although, it's just a beta version by now, so it's just like that :)");
		}
		if (!Global.javafxEnabled)
			Main.mainFrameAWT.getCmdLine().setEditable(true);
	}
	
	// ======================================== PROMPT MODIFICATION END ========================================
	
	// ======================================== SHELL PRINTING ========================================
	
	public static void print(int priority, String auth, String message, boolean... noProtect) {
		if (libraries.Global.singleThreaded) {
			Main.mainFrameAWT.getCmdLine().setText(Main.mainFrameAWT.getCmdLine().getText() + message);
		} else {
			if (priority == 0) { //Priority 0 / Just print, nothing important
				ShellWriteThread.writeToShell(ANSI.getANSIColorString(ANSI.D_White) + message, false);
			} else if (priority == 1) { //Priority 1 / Info, Progress, etc.
				ShellWriteThread.writeToShell(ANSI.getANSIColorString(ANSI.D_White) + message, false);
			} else if (priority == 2) { //Priority 2 / Warnings
				ShellWriteThread.writeToShell(ANSI.getANSIColorString(ANSI.D_Yellow) + message, false);
			} else if (priority == 3) { //Priority 3 / Non-Critical errors
				ShellWriteThread.writeToShell(ANSI.getANSIColorString(ANSI.D_Red) + message, false);
			} else if (priority == 4) { //Priority 4 / Critical errors
				ShellWriteThread.writeToShell(ANSI.getANSIColorString(ANSI.B_Red) + message, false);
			} else if (priority == 5) { //Priority 5 / Fatal or Non-recoverable errors
				ShellWriteThread.writeToShell(ANSI.getANSIColorString(ANSI.B_Red) + message, false);
			} else { //If priority out of range, choose default white
				ShellWriteThread.writeToShell(ANSI.getANSIColorString(ANSI.D_White) + message, false);
			}
		}
	}
	
	/**
	 * 
	 * @param color
	 * @param message
	 * @param noProtect
	 */
	public static void print(Color color, String message, boolean... noProtect) {
		if (libraries.Global.singleThreaded) {
			Main.mainFrameAWT.getCmdLine().setText(Main.mainFrameAWT.getCmdLine().getText() + message);
		} else {
			ShellWriteThread.writeToShell(ANSI.getANSIColorString(color) + message, false);
		}
	}
	public static void println(Color color, String message, boolean... noProtect) {
		if (libraries.Global.singleThreaded) {
			Main.mainFrameAWT.getCmdLine().setText(Main.mainFrameAWT.getCmdLine().getText() + message);
		} else {
			ShellWriteThread.writeToShell("\n" + ANSI.getANSIColorString(color) + message, false);
		}
	}
	public static void print(String message) {
		ShellWriteThread.writeToShell(message, true);
	}
	public static void println(String message) {
		ShellWriteThread.writeToShell("\n" + message, true);
	}
	/**
	 * Direct shellWrite when in single-threaded mode. More efficient than going through shellWriteThread.
	 * @param message
	 */
	public static synchronized void direct_shell_write(String message) {
		Main.mainFrameAWT.getCmdLine().setText(Main.mainFrameAWT.getCmdLine().getText() + message);
	}
	
	// ======================================== SHELL PRINTING END ========================================
	
	/**
	 * Clears Main.cmdLine (set text to "")
	 */
	public static void clearCmdLine() {
		if (GUIManager.getCmdLine() != null) {
			Platform.runLater(() -> {
				GUIManager.getCmdLine().clear();
			});
		}
	}
	
	public static void triggerScrollUpdate() {
		if (GUIManager.getCmdLine() != null) {
			Platform.runLater(() -> {
				GUIManager.getCmdLine().requestFollowCaret();
				Platform.requestNextPulse();
			});
		}
	}
	
	public static void initializeCommandHistory() {
		if (commandHistory != null)
			return;
		
		commandHistory = new CommandHistory();
	}
	
	public static CommandHistory getCommandHistory() {
		return commandHistory;
	}
}
