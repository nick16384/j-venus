package shell;

import java.awt.Color;

import awtcomponents.AWTANSI;
import components.CommandHistory;
import engine.InfoType;
import engine.Runphase;
import engine.sys;
import javafx.application.Platform;
import libraries.Global;
import main.Main;
import threads.ThreadAllocation;

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
			sys.log("OPENLIB", InfoType.WARN, "Attempt to set prompt to null, changing nothing.");
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
			Shell.print(AWTANSI.B_Green, "\n" + prompt);

		} else {
			sys.log("LIB", InfoType.CRIT, "Shell prepare was called during pre-init. Doing nothing, but this");
			sys.log("LIB", InfoType.CRIT, "is unusual and should not be seen multiple times.");
			sys.log("LIB", InfoType.CRIT, "Although, it's just a beta version by now, so it's just like that :)");
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
				ThreadAllocation.getSWT().appendTextQueue(AWTANSI.D_White, message, noProtect);
			} else if (priority == 1) { //Priority 1 / Info, Progress, etc.
				ThreadAllocation.getSWT().appendTextQueue(AWTANSI.D_White, message, noProtect);
			} else if (priority == 2) { //Priority 2 / Warnings
				ThreadAllocation.getSWT().appendTextQueue(AWTANSI.D_Yellow, message, noProtect);
			} else if (priority == 3) { //Priority 3 / Non-Critical errors
				ThreadAllocation.getSWT().appendTextQueue(AWTANSI.D_Red, message, noProtect);
			} else if (priority == 4) { //Priority 4 / Critical errors
				ThreadAllocation.getSWT().appendTextQueue(AWTANSI.B_Red, message, noProtect);
			} else if (priority == 5) { //Priority 5 / Fatal or Non-recoverable errors
				ThreadAllocation.getSWT().appendTextQueue(AWTANSI.B_Red, message, noProtect);
			} else { //If priority out of range, choose default white
				ThreadAllocation.getSWT().appendTextQueue(AWTANSI.D_White, message, noProtect);
			}
		}
	}
	
	/**
	 * 
	 * @param color
	 * @param message
	 * @param noProtect
	 */
	public static synchronized void print(Color color, String message, boolean... noProtect) {
		if (libraries.Global.singleThreaded) {
			Main.mainFrameAWT.getCmdLine().setText(Main.mainFrameAWT.getCmdLine().getText() + message);
		} else {
			ThreadAllocation.getSWT().appendTextQueue(color, message, noProtect);
		}
	}
	public static synchronized void println(Color color, String message, boolean... noProtect) {
		if (libraries.Global.singleThreaded) {
			Main.mainFrameAWT.getCmdLine().setText(Main.mainFrameAWT.getCmdLine().getText() + message);
		} else {
			ThreadAllocation.getSWT().appendTextQueue(color, message + "\n", noProtect);
		}
	}
	public static synchronized void print(String message) {
		ThreadAllocation.getSWT().appendTextQueue(AWTANSI.cReset, message);
	}
	public static synchronized void println(String message) {
		ThreadAllocation.getSWT().appendTextQueue(AWTANSI.cReset, message + "\n");
	}
	/**
	 * Direct shellWrite when in single-threaded mode. More efficient than going through shellWriteThread.
	 * @param message
	 */
	public static void direct_shell_write(String message) {
		Main.mainFrameAWT.getCmdLine().setText(Main.mainFrameAWT.getCmdLine().getText() + message);
	}
	
	// ======================================== SHELL PRINTING END ========================================
	
	/**
	 * Clears Main.cmdLine (set text to "")
	 */
	public static void clearCmdLine() {
		if (Main.cmdLine != null) {
			Platform.runLater(() -> {
				Main.cmdLine.clear();
			});
		}
	}
	
	public static void triggerScrollUpdate() {
		if (Main.cmdLine != null) {
			Platform.runLater(() -> {
				Main.cmdLine.requestFollowCaret();
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
