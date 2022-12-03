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

import main.Main;
import main.Lib;

class LowLevel {
	private final byte privLvl = 0; //Privilege Level: Increase means more permissions. OS Dependend, Max: 5
	protected String getPriv() {
		return "nothing here yet";
	}
	
	protected static void shell_write(int priority, String auth, String message) {
		if (priority > 5) {
			Lib.logWrite("LOWLEVEL", 3, ": priority out of range (1 - 5)");
			return;
		}
		StyledDocument doc = Main.cmdLine.getStyledDocument();
		Style style = Main.cmdLine.addStyle("cmdLineStyle", null);
		
		if (auth.equalsIgnoreCase("HIDDEN")) { //If authority wants to be hidden (e.g. during init)
			if (priority == 1) {
				StyleConstants.setForeground(style, new Color(255, 255, 255));
		        try { doc.insertString(doc.getLength(), message, style); }
		        catch (BadLocationException e) { Lib.logWrite("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 2) {
				StyleConstants.setForeground(style, new Color(128, 255, 0));
		        try { doc.insertString(doc.getLength(), message, style); }
		        catch (BadLocationException e) { Lib.logWrite("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 3) {
				StyleConstants.setForeground(style, new Color(255, 255, 0));
		        try { doc.insertString(doc.getLength(), message, style); }
		        catch (BadLocationException e) { Lib.logWrite("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 4) {
				StyleConstants.setForeground(style, new Color(255, 128, 0));
		        try { doc.insertString(doc.getLength(), message, style); }
		        catch (BadLocationException e) { Lib.logWrite("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 5) {
				StyleConstants.setForeground(style, new Color(255, 0, 0));
		        try { doc.insertString(doc.getLength(), message, style); }
		        catch (BadLocationException e) { Lib.logWrite("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			}
		} else {
			if (priority == 1) {
				StyleConstants.setForeground(style, new Color(255, 255, 255));
		        try { doc.insertString(doc.getLength(), "[ " + Lib.getDateTime(false) + ", " + auth + " ]: " + message, style); }
		        catch (BadLocationException e) { Lib.logWrite("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 2) {
				StyleConstants.setForeground(style, new Color(128, 255, 0));
		        try { doc.insertString(doc.getLength(), "[ " + Lib.getDateTime(false) + ", " + auth + " ]: " + message, style); }
		        catch (BadLocationException e) { Lib.logWrite("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 3) {
				StyleConstants.setForeground(style, new Color(255, 255, 0));
		        try { doc.insertString(doc.getLength(), "[ " + Lib.getDateTime(false) + ", " + auth + " ]: " + message, style); }
		        catch (BadLocationException e) { Lib.logWrite("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 4) {
				StyleConstants.setForeground(style, new Color(255, 128, 0));
		        try { doc.insertString(doc.getLength(), "[ " + Lib.getDateTime(false) + ", " + auth + " ]: " + message, style); }
		        catch (BadLocationException e) { Lib.logWrite("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			} else if (priority == 5) {
				StyleConstants.setForeground(style, new Color(255, 0, 0));
		        try { doc.insertString(doc.getLength(), "[ " + Lib.getDateTime(false) + ", " + auth + " ]: " + message, style); }
		        catch (BadLocationException e) { Lib.logWrite("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException"); }
			}
		}
	}
}