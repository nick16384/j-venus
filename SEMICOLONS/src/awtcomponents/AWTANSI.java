package awtcomponents;

import javax.swing.*;
import javax.swing.text.*;

import engine.InfoType;
import engine.Runphase;
import engine.sys;
import libraries.Global;

import java.awt.Color;

/**
 * Code original from:
 * https://stackoverflow.com/questions/6899282/ansi-colors-in-java-swing-text-fields
 * Accessed 08.23.2022
 * 
 * Modified version
 */

public class AWTANSI extends JTextPane {
	public static final Color D_Black   = Color.getHSBColor( 0.000f, 0.000f, 0.000f );
	public static final Color D_Red     = Color.getHSBColor( 0.000f, 1.000f, 0.502f );
	public static final Color D_Blue    = Color.getHSBColor( 0.667f, 1.000f, 0.502f );
	public static final Color D_Magenta = Color.getHSBColor( 0.833f, 1.000f, 0.502f );
	public static final Color D_Green   = Color.getHSBColor( 0.333f, 1.000f, 0.502f );
	public static final Color D_Yellow  = Color.getHSBColor( 0.167f, 1.000f, 0.900f ); //last one was 0.502f
	public static final Color D_Cyan    = Color.getHSBColor( 0.500f, 1.000f, 0.502f );
	public static final Color D_White   = Color.getHSBColor( 0.000f, 0.000f, 1.000f ); //last one was 0.753f
	public static final Color B_Black   = Color.getHSBColor( 0.000f, 0.000f, 0.502f );
	public static final Color B_Red     = Color.getHSBColor( 0.000f, 1.000f, 1.000f );
	public static final Color B_Blue    = Color.getHSBColor( 0.667f, 1.000f, 1.000f );
	public static final Color B_Magenta = Color.getHSBColor( 0.833f, 1.000f, 1.000f );
	public static final Color B_Green   = Color.getHSBColor( 0.333f, 1.000f, 1.000f );
	public static final Color B_Yellow  = Color.getHSBColor( 0.167f, 1.000f, 1.000f );
	public static final Color B_Cyan    = Color.getHSBColor( 0.500f, 1.000f, 1.000f );
	public static final Color B_White   = Color.getHSBColor( 0.000f, 0.000f, 1.000f );
	public static final Color cReset    = Color.getHSBColor( 0.000f, 0.000f, 1.000f );

	public static final Color D_Orange  = Color.getHSBColor( 0.500f, 0.500f, 0.500f ); //Custom colors
	public static final Color B_Orange  = Color.getHSBColor( 0.500f, 0.500f, 0.800f );
	static Color colorCurrent    = cReset;
	private static String remaining = "";
	private static String lastColorCode = ""; //The last requested color code for logging purposes.

	public static void append(JTextPane pane, Color c, String s) throws BadLocationException {
		if (Global.getCurrentPhase().equals(Runphase.PREINIT)
				|| Global.getCurrentPhase().equals(Runphase.INIT))
			return;
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		insertNewString(pane, s, c, aset); //Private method
	}

	public static void appendANSI(JTextPane pane, String s) throws BadLocationException {
		int aPos = 0;   // current char position in addString
		int aIndex = 0; // index of next Escape sequence
		int mIndex = 0; // index of "m" terminating Escape sequence
		String tmpString = "";
		boolean stillSearching = true; // true until no more Escape sequences
		String addString = remaining + s;
		remaining = "";

		if (addString.length() > 0) {
			aIndex = addString.indexOf("\u001B"); // find first escape
			if (aIndex == -1) { // no escape/color change in this string, so just send it with current color
				append(pane, colorCurrent,addString);
				return;
			}
			// otherwise There is an escape character in the string, so we must process it

			if (aIndex > 0) { // Escape is not first char, so send text up to first escape
				tmpString = addString.substring(0,aIndex);
				append(pane, colorCurrent, tmpString);
				aPos = aIndex;
			}
			// aPos is now at the beginning of the first escape sequence

			stillSearching = true;
			while (stillSearching) {
				mIndex = addString.indexOf("m",aPos); // find the end of the escape sequence
				if (mIndex < 0) { // the buffer ends halfway through the ansi string!
					remaining = addString.substring(aPos,addString.length());
					stillSearching = false;
					continue;
				}
				else {
					tmpString = addString.substring(aPos,mIndex+1);
					colorCurrent = getANSIColor(tmpString);
				}
				aPos = mIndex + 1;
				// now we have the color, send text that is in that color (up to next escape)

				aIndex = addString.indexOf("\u001B", aPos);

				if (aIndex == -1) { // if that was the last sequence of the input, send remaining text
					tmpString = addString.substring(aPos,addString.length());
					append(pane, colorCurrent, tmpString);
					stillSearching = false;
					continue; // jump out of loop early, as the whole string has been sent now
				}

				// there is another escape sequence, so send part of the string and prepare for the next
				tmpString = addString.substring(aPos,aIndex);
				aPos = aIndex;
				append(pane, colorCurrent, tmpString);

			} // while there's text in the input buffer
		}
	}

	public static Color getANSIColor(String ANSIColor) {
		if (!ANSIColor.equals("\u001B[0m") && !lastColorCode.equals(ANSIColor)) {
			sys.log("ANSI_DEBUG", InfoType.STATUS, "Requesting new ANSI color: " + ANSIColor + "===" + "\u001B[0m");
			lastColorCode = ANSIColor;
		}
		if (ANSIColor.equals("\u001B[30m"))        { return D_Black; }
		else if (ANSIColor.equals("\u001B[31m"))   { return D_Red; }
		else if (ANSIColor.equals("\u001B[32m"))   { return D_Green; }
		else if (ANSIColor.equals("\u001B[33m"))   { return D_Yellow; }
		else if (ANSIColor.equals("\u001B[34m"))   { return D_Blue; }
		else if (ANSIColor.equals("\u001B[35m"))   { return D_Magenta; }
		else if (ANSIColor.equals("\u001B[36m"))   { return D_Cyan; }
		else if (ANSIColor.equals("\u001B[37m"))   { return D_White; }
		else if (ANSIColor.equals("\u001B[0;30m")) { return D_Black; }
		else if (ANSIColor.equals("\u001B[0;31m")) { return D_Red; }
		else if (ANSIColor.equals("\u001B[0;32m")) { return D_Green; }
		else if (ANSIColor.equals("\u001B[0;33m")) { return D_Yellow; }
		else if (ANSIColor.equals("\u001B[0;34m")) { return D_Blue; }
		else if (ANSIColor.equals("\u001B[0;35m")) { return D_Magenta; }
		else if (ANSIColor.equals("\u001B[0;36m")) { return D_Cyan; }
		else if (ANSIColor.equals("\u001B[0;37m")) { return D_White; }
		else if (ANSIColor.equals("\u001B[1;30m")) { return B_Black; }
		else if (ANSIColor.equals("\u001B[1;31m")) { return B_Red; }
		else if (ANSIColor.equals("\u001B[1;32m")) { return B_Green; }
		else if (ANSIColor.equals("\u001B[1;33m")) { return B_Yellow; }
		else if (ANSIColor.equals("\u001B[1;34m")) { return B_Blue; }
		else if (ANSIColor.equals("\u001B[1;35m")) { return B_Magenta; }
		else if (ANSIColor.equals("\u001B[1;36m")) { return B_Cyan; }
		else if (ANSIColor.equals("\u001B[1;37m")) { return B_White; }
		else if (ANSIColor.equals("\u001B[0m"))    { return cReset; }
		//ADD \e... codes for unix
		else { System.err.println("No result for: " + ANSIColor); return B_White; }
	}

	public static String getANSIColorString (Color c) {
		if (c.equals(D_Black)) { return "\u001B[30m"; }
		else if (c.equals(D_Red)) { return "\u001B[31m"; }
		else if (c.equals(D_Green)) { return "\u001B[32m"; }
		else if (c.equals(D_Yellow)) { return "\u001B[33m"; }
		else if (c.equals(D_Blue)) { return "\u001B[34m"; }
		else if (c.equals(D_Magenta)) { return "\u001B[35m"; }
		else if (c.equals(D_Cyan)) { return "\u001B[36m"; }
		else if (c.equals(D_White)) { return "\u001B[37m"; }
		else if (c.equals(D_Green)) { return "\u001B[32m"; }
		else if (c.equals(B_Black)) { return "\u001B[1;30m"; }
		else if (c.equals(B_Red)) { return "\u001B[1;31m"; }
		else if (c.equals(B_Green)) { return "\u001B[1;32m"; }
		else if (c.equals(B_Yellow)) { return "\u001B[1;33m"; }
		else if (c.equals(B_Blue)) { return "\u001B[1;34m"; }
		else if (c.equals(B_Magenta)) { return "\u001B[1;35m"; }
		else if (c.equals(B_Cyan)) { return "\u001B[1;36m"; }
		else if (c.equals(B_White)) { return "\u001B[1;37m"; }
		else if (c.equals(cReset)) { return "\u001B[0m"; }
		else { return "\u001B[37m"; }
	}
	
	/**
	 * This method is used to make code more readable
	 *  (insertNewString() instead of a bunch of code)
	 * @param s
	 * @param c
	 * @param aset
	 */
	private static void insertNewString(JTextPane pane, String s, Color c, AttributeSet aset) {
		Runnable appendTextRun = new Runnable() {
			public void run() {
				int len = pane.getDocument().getLength(); 
				try {pane.getDocument().insertString(len, s, aset);} 
				catch (Exception e) {
					sys.log("ANSI_APPEND", InfoType.ERR, "BadLocationException while appending text.");
					try {
						for (int i = 0; i < 100; i++) {
							append(pane, c, s);
							try { Thread.sleep(50); } catch (InterruptedException ie) { ie.printStackTrace(); }
							if (pane.getText().endsWith(s))
								continue;
							sys.log("ANSI_SHLWRT", InfoType.WARN, "Shell write verification failed on attempt No. " + i);
							if (i >= 99)
								sys.log("ANSI_SHLWRT", InfoType.ERR, "Tried to append text 100 times unsuccessfully.");
						}
					} catch (BadLocationException ble) {
						sys.log("ANSI_APPEND", InfoType.ERR, "BadLocationException while appending text inside catch block.");
					}
				}
			}
		};
		SwingUtilities.invokeLater(appendTextRun);
	}
}