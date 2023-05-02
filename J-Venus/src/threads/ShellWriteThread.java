package threads;

import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Scanner;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

import awt.windowManager.WindowMain;
import components.ProtectedTextComponent;
import engine.AWTANSI;
import engine.sys;
import libraries.OpenLib;
import libraries.VarLib;
import main.Main;

/**
 * Class for writing out request to shell in a separate thread. Support for
 * auto-formatting in ANSI
 * 
 * @author nick16384
 *
 */
public class ShellWriteThread implements VexusThread {
	public PipedOutputStream shellStream = null;
	public BufferedReader shellReader = null;
	public Scanner shellScanner = null;
	private boolean noProtectVar = false; // Do not protect text after print
	private String writeQueue = "";
	private String prevWrite = ""; // Contains previously written text
	private int CMDLINE_MAX_LINE_COUNT = 0;
	protected boolean interrupt = false;
	private boolean suspend = false;
	private Thread shellWriteThread;

	// =========================================SHELLWRITETHREAD=========================================
	protected ShellWriteThread() {
		shellWriteThread = new Thread(null, new Runnable() {
			public final void run() {
				// TODO fix NullPointerException with mainFrame and NoClassDefFoundError in
				// VarLib
				while (sys.getActivePhase().equals("pre-init") || sys.getActivePhase().equals("init")) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
					// Do nothing, until active phase is "run".
				}
				sys.log("ShellWriteThread", 1, "Active phase run detected.");
				
				while (main.Main.mainFrame == null && !main.Main.javafxEnabled)
					try { Thread.sleep(500); } catch (InterruptedException ie) { ie.printStackTrace(); }
				sys.log("ShellWriteThread", 1, "SWT is now ready.");
				
				// Check for JavaFX
				if (main.Main.javafxEnabled)
					sys.log("SWT", 2, "JavaFX is currently very unstable on ShellWriteThread.");
				
				//One-time-initialization for shell streams
				try {
					shellStream = new PipedOutputStream();
					shellReader = new BufferedReader(new InputStreamReader(new PipedInputStream(shellStream)));
					shellScanner = new Scanner(shellReader);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				updateShellStream();
				
				//Run if neither WDT's shutdown signal nor local suspend signal is active
				while (!Main.ThreadAllocMain.isShutdownSignalActive() && !suspend) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}

					if (interrupt) {
						// TODO make user input only and not writeQueue in shellStream
						// TODO make command not found detection, instead of big stacktrace
						// TODO fix error "badpos setcaretpos" sometimes occurring

						// Check if user has inputed anything, and if yes, update shellStream
						// UPDATE SHELL STREAM
						// ==============================================================================
						// updateShellStream();
						// END UPDATE SHELL STREAM
						// ==========================================================================

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

						if (writeQueue != "" && !sys.getActivePhase().equals("error")) {

							// Autoscroll
							if (!Main.javafxEnabled)
								autoscroll();
							else
								sys.log("SWT", 1, "Autoscroll unnecessary. JavaFX supports it by itself.");
							// Autoscroll end

							try {
								// ================================================================
								// Insert write queue (either JavaFX or AWT)
								if (Main.javafxEnabled)
									new engine.JFXANSI(Main.jfxWinloader.getCmdLine()).appendANSI(writeQueue);
								else
									new engine.AWTANSI(Main.mainFrame.getCmdLine()).appendANSI(writeQueue);
								
								// ================================================================

							} catch (BadLocationException ble) {
								sys.log("SWT", 3, "Cannot write to cmdLine: BadLocationException");
							} catch (NullPointerException npe) {
								sys.log("SWT", 3, "Cannot write to cmdLine: NullPointerException (main.mainFrame probably is null)");
							}
							try {
								if (Main.javafxEnabled && Main.jfxWinloader.getCmdLine() != null)
									Main.jfxWinloader.getCmdLine().selectPositionCaret(
											Main.jfxWinloader.getCmdLine().lengthProperty().get());
								else
									Main.mainFrame.getCmdLine()
											.setCaretPosition(Main.mainFrame.getCmdLine().getText().length());
							} catch (IllegalArgumentException iae) {
								sys.log("SWT", 2, "Setting cursor to last position failed, because the value was out of range.");
							} catch (NullPointerException npe) {
								sys.log("SWT", 2, "Setting cursor to last position failed, because main.mainFrame is null.");
							}

							prevWrite = writeQueue; // Set previously written text to writeQueue
							writeQueue = ""; // Clear write queue

							if (!noProtectVar) {
								try {
									if (!Main.javafxEnabled)
										new ProtectedTextComponent(Main.mainFrame.getCmdLine()).protectText(
												Main.mainFrame.getCmdLine().getText().lastIndexOf(VarLib.getPrompt()),
												Main.mainFrame.getCmdLine().getText().length() - 1);
								} catch (NullPointerException npe) {
									sys.log("SWT", 3, "Text could not be protected from user deletion, probably because main.mainFrame is null.");
								}
							} else {
								noProtectVar = false;
							}
							interrupt = false;
						}
					}
					// TODO If cmdLine operations glitch, then try:
					// new ProtectedTextComponent(main.Main.cmdLine).unprotectAllText();
					// System.out.println("loop");
				}
				sys.log("SWT", 1, "Closing streams...");
				try { shellStream.close(); } catch (IOException ioe) { sys.log("SWT", 3, "Fail on shellStream."); }
				try { shellReader.close(); } catch (IOException ioe) { sys.log("SWT", 3, "Fail on shellReader."); }
				try { shellScanner.close(); } catch (Exception ex) { sys.log("SWT", 3, "Fail on shellScanner."); }
				sys.log("SWT", 1, "Closing streams done.");

				try {
					Thread.sleep(1000); // Reserve, else Watchdog would throw an error
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// Exit thread
			}
		}, "SWT");
	}

	// \/ Same as shell write \/
	public final void appendTextQueue(String message, boolean... noProtect) {
		if (noProtect.length >= 1 && noProtect[0] == true) {
			noProtectVar = true;
		}
		sys.log("MSG", 0, "SHELLWRITE: " + message.strip());
		writeQueue += message;
		interrupt = true;
		// main.Main.cmdLine.repaint();
		// main.Main.cmdLine.revalidate();
	}

	public final void appendTextQueue(Color color, String message, boolean... noProtect) {
		if (noProtect.length >= 1 && noProtect[0] == true) {
			noProtectVar = true;
		}
		message = AWTANSI.getANSIColorString(color) + message;
		// Reset color at end, so log doesn't get messed up with colors:
		sys.log("MSG", 0, "SHELLWRITE-ANSI: " + message.strip() + "\u001B[0m");
		writeQueue += message;
		interrupt = true;
	}
	
	@Override
	public final void start() {
		shellWriteThread.start();
	}
	
	@Override
	public final boolean isRunning() {
		return shellWriteThread.isAlive();
	}
	
	@Override
	public final void suspend() {
		sys.shellPrintln(AWTANSI.B_Yellow, "Suspending ShellWriteThread!");
		sys.log("SWT", 2, "Suspending ShellWriteThread!");
		try { Thread.sleep(150); } catch (InterruptedException ie) { ie.printStackTrace(); }
		this.suspend = true;
	}

	// =========================================== PRIVATE METHODS
	// ===========================================
	// Now (22.08) also working with ANSI colors (hopefully)
	public final void scroll(int lineCount) {
		// Unprotect text, removal otherwise won't work
		new ProtectedTextComponent(Main.mainFrame.getCmdLine()).unprotectAllText();
		sys.log("SCRL", 1, "Scrolling by " + lineCount + " line(s):");
		if (lineCount > CMDLINE_MAX_LINE_COUNT) {
			sys.log("SCRL", 1, "Warning: scroll value higher than max. line count. Expect errors.");
		}

		// Total characters to remove at the top
		int removeChars = ordinalIndexOf(Main.mainFrame.getCmdLine().getText(), "\n", lineCount);

		// Remove certain number of characters (removeChars)
		try {
			Main.mainFrame.getCmdLine().getDocument().remove(0, removeChars + 1);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Finds the n'th index of the specified substring(substr) in the total
	 * string(str)
	 * 
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
	 * Note: Currently not working (default max. should be 26). Fix later. This
	 * function is only kept here for working later with it.
	 * 
	 * @return Number of line breaks in cmdLine
	 */
	@SuppressWarnings("unused")
	private static int countLines(JTextPane textArea) {
		AttributedString text = new AttributedString(textArea.getText());
		FontRenderContext frc = textArea.getFontMetrics(textArea.getFont()).getFontRenderContext();
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

	private void autoscroll() {
		int writeQueueLineBreakCount = writeQueue.split("\n").length + 1;
		int cmdLineLineBreakCount = 0;
		if (Main.mainFrame != null) {
			cmdLineLineBreakCount = Main.mainFrame.getCmdLine().getText().split("\n").length;
		}
		int totalLineBreakCount = writeQueueLineBreakCount + cmdLineLineBreakCount;
		int scrollByLines = totalLineBreakCount - CMDLINE_MAX_LINE_COUNT;
		if (scrollByLines > 0) {
			// Write to log
			sys.log("SHLWRT", 1, "Autoscroll: " + totalLineBreakCount + " > " + CMDLINE_MAX_LINE_COUNT);
			sys.log("SHLWRT", 1, "Line breaks in cmdLine: " + cmdLineLineBreakCount);
			sys.log("SHLWRT", 1, "Line breaks in writeQueue: " + writeQueueLineBreakCount);
			sys.log("SHLWRT", 1, "Max. Line breaks in cmdLine: " + CMDLINE_MAX_LINE_COUNT);
			// Scroll by calculated amount of lines (method scroll(int lines) defined in
			// class)
			scroll(scrollByLines);
		} else {
			sys.log("SHLWRT", 1, "No Autoscroll: " + totalLineBreakCount + " <= " + CMDLINE_MAX_LINE_COUNT);
		}
	}

	/**
	 * Updates modules.ShellWriteThread.shellStream with content, the user entered. 
	 * Should only be used, WHEN the user entered something. Otherwise, bugs and
	 * exceptions may occur.
	 */
	public void updateShellStream() {
		if (Main.javafxEnabled)
			return;
		String appendStr = ""; // Newly added text from user if any
		try {
			String cmdLineText = Main.mainFrame.getCmdLine().getText();
			String lastLineText = cmdLineText.split("\n")[cmdLineText.split("\n").length - 1];
			String prevWriteLastLine = prevWrite.split("\n")[prevWrite.split("\n").length - 1];
			
			// Remove any ANSI color code patterns
			// Alternative REGEX if the one doesn't work: "\\d{1,2}(;\\d{1,2})?"
			prevWriteLastLine = prevWriteLastLine.replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "");
			
			appendStr = lastLineText.substring(prevWriteLastLine.length());
			appendStr = appendStr.trim();
		} catch (IndexOutOfBoundsException ioobe) {
			appendStr = "";
			sys.log("SHLWRT", 2, "No new user input.");
			// ioobe.printStackTrace();
		}
		try {
			sys.log("SWT:DEBUG", 1, "Appending new bytes to shellStream...");
			shellStream.write(appendStr.getBytes());
			shellStream.write("\n".getBytes());
		} catch (IOException ioe) { sys.log("SWT", 3, "Writing data to shellStream failed."); ioe.printStackTrace(); }
		sys.log("SHLWRT:DEBUG", 0, "Last line of previously written text: " + "\u001B[32m" + prevWrite + "\u001B[0m");
		sys.log("SHLWRT:DEBUG", 0, "New user input: " + "\u001B[32m" + appendStr + "\u001B[0m");
		try {
			// Validate if stream data matches appendStr
			if (shellReader.ready() && shellReader.readLine().equals(appendStr)) {
				sys.log("SHLWRT:DEBUG", 0, "\u001B[32m stream data validated. \u001B[0m");
			} else {
				sys.log("SWT:DEBUG", 2, "\\u001B[1;33m stream data validation mismatch. \u001B[0m");
			}
		} catch (IOException ioe) {
			sys.log("SWT:DEBUG", 2, "\\u001B[1;33m stream data validation exception. \u001B[0m");
		}
	}
}
