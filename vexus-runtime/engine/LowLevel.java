/**
 * This class is used for Low Level System purposes
 * It can only be accessed by class HighLevel
 * Here are the main purposes:
 * - Manage and control rights
 * - Access Deep System objects(files, processes, memory etc.)
 * - Security of JavaDOS
 */
package engine;

import java.awt.*;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import libraries.OpenLib;
import libraries.VarLib;
import main.Main;

class LowLevel {
	private final byte privLvl = 0; //Privilege Level: Increase means more permissions. OS Dependend, Max: 5
	private static String jdosActivePhase = "pre-init";
	protected static String shellMode = "normal";
	protected String getPriv() {
		return "nothing here yet";
	}
	
	protected static void shell_write(int priority, String auth, String message) {
		if (priority > 5) {
			sys.log("LOWLEVEL", 3, ": priority out of range (1 - 5)");
			return;
		}
		StyledDocument doc = Main.cmdLine.getStyledDocument();
		Style style = Main.cmdLine.addStyle("cmdLineStyle", null);
		
		if (auth.equalsIgnoreCase("HIDDEN")) { //If authority wants to be hidden (e.g. during init)
			if (priority == 1) {
				StyleConstants.setForeground(style, new Color(255, 255, 255));
		        try { doc.insertString(doc.getLength(), message, style); }
		        catch (BadLocationException e) { sys.log("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 2) {
				StyleConstants.setForeground(style, new Color(128, 255, 0));
		        try { doc.insertString(doc.getLength(), message, style); }
		        catch (BadLocationException e) { sys.log("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 3) {
				StyleConstants.setForeground(style, new Color(255, 255, 0));
		        try { doc.insertString(doc.getLength(), message, style); }
		        catch (BadLocationException e) { sys.log("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 4) {
				StyleConstants.setForeground(style, new Color(255, 128, 0));
		        try { doc.insertString(doc.getLength(), message, style); }
		        catch (BadLocationException e) { sys.log("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 5) {
				StyleConstants.setForeground(style, new Color(255, 0, 0));
		        try { doc.insertString(doc.getLength(), message, style); }
		        catch (BadLocationException e) { sys.log("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			}
		} else {
			if (priority == 1) {
				StyleConstants.setForeground(style, new Color(255, 255, 255));
		        try { doc.insertString(doc.getLength(), "[ " + VarLib.getDateTime(false) + ", " + auth + " ]: " + message, style); }
		        catch (BadLocationException e) { sys.log("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 2) {
				StyleConstants.setForeground(style, new Color(128, 255, 0));
		        try { doc.insertString(doc.getLength(), "[ " + VarLib.getDateTime(false) + ", " + auth + " ]: " + message, style); }
		        catch (BadLocationException e) { sys.log("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 3) {
				StyleConstants.setForeground(style, new Color(255, 255, 0));
		        try { doc.insertString(doc.getLength(), "[ " + VarLib.getDateTime(false) + ", " + auth + " ]: " + message, style); }
		        catch (BadLocationException e) { sys.log("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 4) {
				StyleConstants.setForeground(style, new Color(255, 128, 0));
		        try { doc.insertString(doc.getLength(), "[ " + VarLib.getDateTime(false) + ", " + auth + " ]: " + message, style); }
		        catch (BadLocationException e) { sys.log("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 5) {
				StyleConstants.setForeground(style, new Color(255, 0, 0));
		        try { doc.insertString(doc.getLength(), "[ " + VarLib.getDateTime(false) + ", " + auth + " ]: " + message, style); }
		        catch (BadLocationException e) { sys.log("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			}
		}
	}
	protected static void setShellMode(String shellMode) {
		if (shellMode.equalsIgnoreCase("native")) {
			LowLevel.shellMode = "native";
			//Main.cmdLine.setEditable(false);
			
		} else if (shellMode.equalsIgnoreCase("legacy")) {
			LowLevel.shellMode = "legacy";
			Main.cmdLine.setFont(new Font("Perfect DOS VGA 437", Font.BOLD, 16));
			VarLib.setPromptPattern("legacy>");
			Main.cmdLine.setText("");
			shell_write(2, "LOWLEVEL", "Changed shell mode to legacy mode. \n");
			shell_write(2, "LOWLEVEL", "This mode uses a limited set of available commands and has less \n");
			shell_write(2, "LOWLEVEL", "resource impact. It is more like a debugging \n");
			shell_write(2, "LOWLEVEL", "and developing mode and is not recommended to be used normally. \n");
			shell_write(2, "LOWLEVEL", "to switch back, type 'shellMode -normal'");
			
		} else {
			LowLevel.shellMode = "normal";
			Main.cmdLine.setFont(new Font("Consolas", Font.BOLD, 16));
			VarLib.setPromptPattern("default");
			Main.cmdLine.setText("");
		}
		//Native shell (non writable, like BSOD on Windows)
		//Normal shell (normal shell, writable and normal command set)
		//Legacy shell (msdos font shell, limited commands and limited resources)
		//TODO Implement legacy mode in lowlevel only
	}
	protected static void setJDOSActivePhase(String phase) {
		if (phase != null) {
			if ((phase.equalsIgnoreCase("pre-init")) || (phase.equalsIgnoreCase("init")) || (phase.equalsIgnoreCase("run"))
					|| (phase.equalsIgnoreCase("stopping"))) {
				//Phases:
				//pre-init -> Pre-Initialization with variable preparation (limited execution environment, no command execution done yet)
				//init -> pre-init done and main initialization commands are executed (like startup)
				//run -> Normal running phase (Only phase in which the user can interact)
				//stop -> JDOS is in stopping mode (UI disabled and stepwise shutdown of JavaDOS)
				jdosActivePhase = phase;
			}
		}
	}
	protected static String getJDOSActivePhase() {
		return jdosActivePhase;
	}
}