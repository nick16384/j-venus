package modules;

import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

import engine.ANSI;
import engine.sys;
import libraries.OpenLib;
import libraries.VarLib;

/**
 * Class for writing out request to shell in a separate thread.
 * Support for auto-formatting in ANSI
 * @author theophil
 *
 */
public class ShellWriteThread {
	public static InputStream shellStream = null;
	public static BufferedReader shellReader = null;
	private static boolean noProtectVar = false; //Do not protect text after print
	private static String writeQueue = "";
	private static String prevWrite = ""; //Contains previously written text
	private static int CMDLINE_MAX_LINE_COUNT = 0;
	protected static boolean interrupt = false;
	
	  //=========================================SHELLWRITETHREAD=========================================
	private static final Thread shellWriteThread = new Thread( new Runnable() {
		public final void run() {
			shellStream = new ByteArrayInputStream(main.Main.cmdLine.getText().getBytes());
			shellReader = new BufferedReader(new InputStreamReader(shellStream));
			
			while (!WatchdogThread.shutdownSignal) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
				
				if (interrupt) {
					//TODO make user input only and not writeQueue in shellStream
					//TODO make command not found detection, instead of big stacktrace
					//TODO fix error "badpos setcaretpos" sometimes occurring
					
					//Check if user has inputed anything, and if yes, update shellStream
					//UPDATE SHELL STREAM ==============================================================================
					//updateShellStream();
					//END UPDATE SHELL STREAM ==========================================================================
					
					try {
						CMDLINE_MAX_LINE_COUNT = Integer.parseInt(VarLib.getEnv("$CMDLINE_MAX_LINE_COUNT"));
						sys.log("SHLWRT", 1, "Current cmdLine max. line count: " + CMDLINE_MAX_LINE_COUNT);
					} catch (NumberFormatException nfe) {
						sys.log("WRITE", 2, "Could not parse $CMDLINE_MAX_LINE_COUNT:"
								+ " NumberFormatException. Using default value: 26");
						sys.shellPrint(2, "WRITE", "Could not parse $CMDLINE_MAX_LINE_COUNT.\n"
								+ "Please check, whether $CMDLINE_MAX_LINE_COUNT contains characters and also run\n"
								+ "'chEnv -update $CMDLINE_MAX_LINE_COUNT' if that is the case or the error reoccurs.\n");
						CMDLINE_MAX_LINE_COUNT = 26;
					}
					
					if (writeQueue != "") {
						
						//Autoscroll
			        	autoscroll();
			        	//Autoscroll end
						
				        try {
				        	//================================================================
				        	new engine.ANSI(main.Main.cmdLine)
				        		.appendANSI(writeQueue); //Insert write queue
				        	//================================================================
				        	
				        } catch (BadLocationException ble) {
				        	sys.log("LOWLEVEL", 3, "Cannot write to cmdLine: BadLocationException");
				        	ble.printStackTrace();
				        }
				        try {
				        	main.Main.cmdLine.setCaretPosition(main.Main.cmdLine.getText().length());
				        } catch (IllegalArgumentException iae) {
				        	System.err.println("err shellwritethread badpos setcaretpos");
				        }
				        prevWrite = writeQueue; //Set previously written text to writeQueue
						writeQueue = ""; //Clear write queue
						
						if (!noProtectVar) {
							new ProtectedTextComponent(main.Main.cmdLine)
								.protectText(main.Main.cmdLine.getText().lastIndexOf(VarLib.getPrompt()),
										main.Main.cmdLine.getText().length() - 1);
						} else {
							noProtectVar = false;
						}
						interrupt = false;
					}
				}
				//TODO If cmdLine operations glitch, then try:
				//new ProtectedTextComponent(main.Main.cmdLine).unprotectAllText();
				//System.out.println("loop");
			}
			try {
				if (shellStream != null) { shellStream.close(); }
			} catch (IOException ioe) {
				sys.log("SHLWRITE", 3, "Error closing shellStream. IOException: Details below \\/");
				ioe.printStackTrace();
			}
			
			try {
				Thread.sleep(1000); //Reserve, else Watchdog would throw an error
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Exit thread
		}
	});
	
	//\/ Same as shell write \/
	public static final void appendTextQueue(String message, boolean... noProtect) {
		if (!main.CommandMain.silentExecution) {
			if (noProtect.length >= 1 && noProtect[0] == true) { noProtectVar = true; }
			sys.log("MSG", 0, "SHELLWRITE: " + message.strip());
			writeQueue += message;
		}
		interrupt = true;
		//main.Main.cmdLine.repaint();
		//main.Main.cmdLine.revalidate();
	}
	public static final void appendTextQueue(Color color, String message, boolean... noProtect) {
		if (!main.CommandMain.silentExecution) {
			if (noProtect.length >= 1 && noProtect[0] == true) { noProtectVar = true; }
			message = ANSI.getANSIColorString(color) + message;
			//Reset color at end, so log doesn't get messed up with colors:
			sys.log("MSG", 0, "SHELLWRITE-ANSI: " + message.strip() + "\u001B[0m");
			writeQueue += message;
		}
		interrupt = true;
	}
	
	public static final void startThread() {
		shellWriteThread.start();
	}
	
	public static final boolean isThreadAlive() {
		return shellWriteThread.isAlive();
	}
	
	//=========================================== PRIVATE METHODS ===========================================
	//Now (22.08) also working with ANSI colors (hopefully)
	public static final void scroll(int lineCount) {
		new ProtectedTextComponent(main.Main.cmdLine).unprotectAllText(); //Unprotect text, removal otherwise won't work
		sys.log("SCRL", 1, "Scrolling by " + lineCount + " line(s):");
		if (lineCount > CMDLINE_MAX_LINE_COUNT) {
			sys.log("SCRL", 1, "Warning: scroll value higher than max. line count. Expect errors.");
		}
		
		//Total characters to remove at the top
		int removeChars = ordinalIndexOf(main.Main.cmdLine.getText(), "\n", lineCount);
		
		//Remove certain number of characters (removeChars)
		try {
			main.Main.cmdLine.getDocument().remove(0, removeChars);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Finds the n'th index of the specified substring(substr) in the total string(str)
	 * @param str
	 * @param substr
	 * @param n
	 * @return The index of n'th occurrence of substr in str
	 */
	private static int ordinalIndexOf(String str, String substr, int n) {
	    int pos = str.indexOf(substr);
	    while (--n > 0 && pos != -1)
	        pos = str.indexOf(substr, pos + 1);
	    return pos;
	}
	
	/**
	 * Counts the number of lines in main.Main.cmdLine and returns it as an integer.
	 * Note: Currently not working (default max. should be 26). Fix later.
	 * This function is only kept here for working later with it.
	 * @return Number of line breaks in cmdLine
	 */
	@SuppressWarnings("unused")
	private static int countLines(JTextPane textArea) {
	    AttributedString text = new AttributedString(textArea.getText());
	    FontRenderContext frc = textArea.getFontMetrics(textArea.getFont())
	        .getFontRenderContext();
	    AttributedCharacterIterator charIt = text.getIterator();
	    LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(charIt, frc);
	    float formatWidth = (float) textArea.getSize().width;
	    lineMeasurer.setPosition(charIt.getBeginIndex());

	    int noLines = 0;
	    while (lineMeasurer.getPosition() < charIt.getEndIndex()) {
	      lineMeasurer.nextLayout(formatWidth);
	      noLines++;
	    }

	    return noLines;
	  }
	
	private static void autoscroll() {
		int writeQueueLineBreakCount = writeQueue.split("\n").length + 1;
    	int cmdLineLineBreakCount = main.Main.cmdLine.getText().split("\n").length;
    	int totalLineBreakCount = writeQueueLineBreakCount + cmdLineLineBreakCount;
    	int scrollByLines = totalLineBreakCount - CMDLINE_MAX_LINE_COUNT;
    	if (scrollByLines > 0) {
    		//Write to log
    		sys.log("SHLWRT", 1, "Autoscroll: " + totalLineBreakCount
    				+ " > " + CMDLINE_MAX_LINE_COUNT);
    		sys.log("SHLWRT", 1, "Line breaks in cmdLine: " + cmdLineLineBreakCount);
    		sys.log("SHLWRT", 1, "Line breaks in writeQueue: " + writeQueueLineBreakCount);
    		sys.log("SHLWRT", 1, "Max. Line breaks in cmdLine: " + CMDLINE_MAX_LINE_COUNT);
    		//Scroll by calculated amount of lines (method scroll(int lines) defined in class)
			scroll(scrollByLines);
		} else {
			sys.log("SHLWRT", 1, "No Autoscroll: "
					+ totalLineBreakCount + " <= " + CMDLINE_MAX_LINE_COUNT);
		}
	}
	
	/**
	 * Updates modules.ShellWriteThread.shellStream with content, the user entered
	 * Should only be used, WHEN the user entered something. Otherwise, bugs and exceptions may occur.
	 */
	public static void updateShellStream() {
		String appendStr = "";
		try {
			String cmdLineText = main.Main.cmdLine.getText();
			String lastLineText = main.Main.cmdLine.getText().split("\n")[cmdLineText.split("\n").length - 1];
			String prevWriteLastLine = prevWrite.split("\n")[prevWrite.split("\n").length - 1];
			appendStr = lastLineText.substring(prevWriteLastLine.length());
			appendStr = appendStr.trim();
		} catch (IndexOutOfBoundsException ioobe) {
			appendStr = "";
			sys.log("SHLWRT", 2, "User hasn't inputed nothing.");
			ioobe.printStackTrace();
		}
		shellStream = new ByteArrayInputStream(appendStr.getBytes());
		sys.log("SHLWRT:DEBUG", 0, "Previously written text: " + "\u001B[32m" + prevWrite + "\u001B[0m");
		sys.log("SHLWRT:DEBUG", 0, "New user input: " + "\u001B[32m" + appendStr + "\u001B[0m");
	}
}
