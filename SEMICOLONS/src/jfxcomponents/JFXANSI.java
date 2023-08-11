package jfxcomponents;

import org.fxmisc.richtext.InlineCssTextArea;

import engine.Runphase;
import engine.sys;
import main.Main;

//import java.awt.Color;
import javafx.scene.paint.Color;
import libraries.Global;

/**
 * Code original from:
 * https://stackoverflow.com/questions/6899282/ansi-colors-in-java-swing-text-fields
 * Accessed 08.23.2022
 * 
 * Modified version
 */

public class JFXANSI {
	public static final Color D_Black   = Color.rgb( 000, 000, 000, 0.0d );
	public static final Color D_Red     = Color.rgb( 000, 255, 000, 0.5d );
	public static final Color D_Blue    = Color.rgb( 000, 000, 160, 0.5d );
	public static final Color D_Magenta = Color.rgb( 200, 000, 150, 0.5d );
	public static final Color D_Green   = Color.rgb( 000, 255, 000, 0.5d );
	public static final Color D_Yellow  = Color.rgb( 128, 128, 000, 0.9d ); //last one was 0.502f
	public static final Color D_Cyan    = Color.rgb( 128, 032, 032, 0.5d );
	public static final Color D_White   = Color.rgb( 255, 255, 255, 0.5d ); //last one was 0.753f
	public static final Color B_Black   = Color.rgb( 000, 000, 000, 0.5d );
	public static final Color B_Red     = Color.rgb( 255, 000, 000, 1.0d );
	public static final Color B_Blue    = Color.rgb( 000, 000, 000, 1.0d );
	public static final Color B_Magenta = Color.rgb( 200, 000, 150, 1.0d );
	public static final Color B_Green   = Color.rgb( 000, 255, 000, 1.0d );
	public static final Color B_Yellow  = Color.rgb( 255, 255, 000, 1.0d );
	public static final Color B_Cyan    = Color.rgb( 32, 128, 180, 1.0d );
	public static final Color B_White   = Color.rgb( 255, 255, 255, 1.0d );
	public static final Color cReset    = Color.rgb( 000, 255, 000, 1.0d );
	static Color colorCurrent    = cReset;
	private static String remaining = "";
	private static String lastColorCode = ""; //The last requested color code for logging purposes.

	public static void append(Color c, String s) {
		if (Global.getCurrentPhase().equals(Runphase.PREINIT)
				|| Global.getCurrentPhase().equals(Runphase.INIT))
			return;
		
		if (Main.cmdLine != null)
			insertNewString(s, c); //Private method
	}

	public static void appendANSI(InlineCssTextArea textArea, String s) { // convert ANSI color codes first
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
				append(colorCurrent,addString);
				return;
			}
			// otherwise There is an escape character in the string, so we must process it

			if (aIndex > 0) { // Escape is not first char, so send text up to first escape
				tmpString = addString.substring(0,aIndex);
				append(colorCurrent, tmpString);
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
					append(colorCurrent, tmpString);
					stillSearching = false;
					continue; // jump out of loop early, as the whole string has been sent now
				}

				// there is another escape sequence, so send part of the string and prepare for the next
				tmpString = addString.substring(aPos,aIndex);
				aPos = aIndex;
				append(colorCurrent, tmpString);

			} // while there's text in the input buffer
		}
	}

	public static Color getANSIColor(String ANSIColor) {
		if (!ANSIColor.equals("\u001B[0m") && !lastColorCode.equals(ANSIColor)) {
			sys.log("JFXANSI_DEBUG", 0, "Requesting new ANSI color: " + ANSIColor + "===" + "\u001B[0m");
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
	private static void insertNewString(String s, Color c) {
		try { Main.jfxWinloader.appendText(s, c); } 
		catch (Exception e) {
			sys.log("JFXANSI", 3, "Shell text write failed. Stacktrace below:");
			e.printStackTrace();
		}
	}
}