package threads;

import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Scanner;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import awtcomponents.AWTANSI;
import components.Command;
import components.ProtectedTextComponent;
import components.Shell;
import engine.InfoType;
import engine.Runphase;
import engine.sys;
import libraries.Env;
import libraries.Global;
import main.Main;

/**
 * Class for writing out request to shell in a separate thread. Support for
 * auto-formatting in ANSI
 * 
 * @author nick16384
 *
 */
public class ShellWriteThreadOld implements InternalThread {
	public PipedOutputStream shellStream = null;
	public BufferedReader shellReader = null;
	public Scanner shellScanner = null;
	private boolean noProtectVar = false; // Do not protect text after print
	private String writeQueue = "";
	private String prevWrite = ""; // Contains previously written text
	private int CMDLINE_MAX_LINE_COUNT = 0;
	// protected boolean interrupt = false;
	private boolean suspend = false;
	private Thread shellWriteThread;

	// ========================================= SHELL WRITE THREAD =========================================
	protected ShellWriteThreadOld() {
		shellWriteThread = new Thread(null, new Runnable() {
			public final void run() {
				// TODO fix NullPointerException with mainFrame and NoClassDefFoundError in
				// VarLib
				while (Global.getCurrentPhase().equals(Runphase.PREINIT)
						|| Global.getCurrentPhase().equals(Runphase.INIT)) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
					// Do nothing, until active phase is "run".
				}
				sys.log("ShellWriteThread", InfoType.DEBUG, "Active phase run detected.");

				while (main.Main.mainFrameAWT == null && !main.Main.javafxEnabled)
					try {
						Thread.sleep(500);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				sys.log("ShellWriteThread", InfoType.DEBUG, "SWT is now ready.");

				// Check for JavaFX
				if (main.Main.javafxEnabled)
					sys.log("SWT", InfoType.WARN, "JavaFX is currently very unstable on ShellWriteThread.");

				// One-time-initialization for shell streams
				try {
					shellStream = new PipedOutputStream();
					shellReader = new BufferedReader(new InputStreamReader(new PipedInputStream(shellStream)));
					shellScanner = new Scanner(shellReader);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				updateShellStream();

				// Run if neither WDT's shutdown signal nor local suspend signal is active
				while (!ThreadAllocation.isShutdownSignalActive() && !suspend) {
					try {
						// Wait until thread gets InterruptedException
						Thread.sleep(200);
					} catch (InterruptedException ie) {
						sys.log("SWT", InfoType.DEBUG, "Got interrupt, writing to shell.");
						if (!writeQueue.isBlank()) {
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
								CMDLINE_MAX_LINE_COUNT = Integer.parseInt(Env.getEnv("$CMDLINE_MAX_LINE_COUNT"));
								sys.log("SHLWRT", InfoType.DEBUG, "Current cmdLine max. line count: " + CMDLINE_MAX_LINE_COUNT);
							} catch (NumberFormatException nfe) {
								sys.log("WRITE", InfoType.WARN, "Could not parse $CMDLINE_MAX_LINE_COUNT:"
										+ " NumberFormatException. Using default value: 26");
								Shell.print(2, "WRITE", "Could not parse $CMDLINE_MAX_LINE_COUNT.\n"
										+ "Please check, whether $CMDLINE_MAX_LINE_COUNT contains characters and also run\n"
										+ "'chEnv -update $CMDLINE_MAX_LINE_COUNT'"
										+ "if that is the case or the error reoccurs.\n");
								CMDLINE_MAX_LINE_COUNT = 26;
							}
							// Autoscroll
							if (!Main.javafxEnabled)
								autoscroll();
							else
								sys.log("SWT", InfoType.DEBUG, "Autoscroll unnecessary. JavaFX supports it by itself.");
							// Autoscroll end

							try {
								// ================================================================
								// Insert write queue (either JavaFX or AWT)
								if (writeQueue.contains("\033[2J")) {
									sys.log("SWT", InfoType.DEBUG, "ClearScreen ANSI character received.");
									if (Main.javafxEnabled)
										Main.jfxWinloader.clearCmdLine();
									else
										try {
											new Command("clear").start();
										} catch (IOException ioe) {
											ioe.printStackTrace();
										}
								} else {
									if (Main.javafxEnabled)
										jfxcomponents.JFXANSI.appendANSI(
												Main.jfxWinloader.getCmdLine(),
												writeQueue);
									else
										awtcomponents.AWTANSI.appendANSI(Main.mainFrameAWT.getCmdLine(),
												writeQueue);
								}
								
								// ================================================================

							} catch (BadLocationException ble) {
								sys.log("SWT", InfoType.ERR, "Cannot write to cmdLine: BadLocationException");
							} catch (NullPointerException npe) {
								sys.log("SWT", InfoType.ERR, "Cannot write to cmdLine:"
										+ " NullPointerException (main.mainFrame probably is null)");
							}
							try {
								if (Main.javafxEnabled && Main.jfxWinloader.getCmdLine() != null)
									Main.jfxWinloader.triggerScrollUpdate();
								else
									Main.mainFrameAWT.getCmdLine()
											.setCaretPosition(Main.mainFrameAWT.getCmdLine().getText().length());
							} catch (IllegalArgumentException iae) {
								sys.log("SWT", InfoType.WARN, "Setting cursor to last position failed,"
										+ "because the value was out of range.");
							} catch (NullPointerException npe) {
								sys.log("SWT", InfoType.WARN,
										"Setting cursor to last position failed, because main.mainFrame is null.");
							}
							
							writeQueue = ""; // Clear write queue

							if (!noProtectVar) {
								try {
									if (!Main.javafxEnabled)
										new ProtectedTextComponent(Main.mainFrameAWT.getCmdLine()).protectText(
												Main.mainFrameAWT.getCmdLine().getText().lastIndexOf(
														Shell.getPrompt()),
												Main.mainFrameAWT.getCmdLine().getText().length() - 1);
								} catch (NullPointerException npe) {
									sys.log("SWT", InfoType.ERR, "Text could not be protected from user deletion,"
											+ " probably because main.mainFrame is null.");
								}
							} else {
								noProtectVar = false;
							}
						}
					}
					// TODO If cmdLine operations glitch, then try:
					// new ProtectedTextComponent(main.Main.cmdLine).unprotectAllText();
					// System.out.println("loop");
				}
				sys.log("SWT", InfoType.DEBUG, "Closing streams...");
				try { shellStream.close();}
				catch (IOException ioe) { sys.log("SWT", InfoType.ERR, "Fail on shellStream."); }
				try { shellReader.close(); }
				catch (IOException ioe) { sys.log("SWT", InfoType.ERR, "Fail on shellReader."); }
				try { shellScanner.close(); }
				catch (Exception ex) { sys.log("SWT", InfoType.ERR, "Fail on shellScanner."); }
				sys.log("SWT", InfoType.INFO, "Closing streams done.");

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
		sys.log("MSG", InfoType.DEBUG, "SHELLWRITE: " + message.strip());
		while (shellWriteThread.isInterrupted()) { try { Thread.sleep(200); } catch (InterruptedException e) {} }
		writeQueue += message;
		
		if (Global.getCurrentPhase().equals(Runphase.RUN)
				|| Global.getCurrentPhase().equals(Runphase.ERR))
			shellWriteThread.interrupt();
		
		// main.Main.cmdLine.repaint();
		// main.Main.cmdLine.revalidate();
	}

	@SuppressWarnings("javadoc")
	public final void appendTextQueue(Color color, String message, boolean... noProtect) {
		if (noProtect.length >= 1 && noProtect[0] == true) {
			noProtectVar = true;
		}
		message = AWTANSI.getANSIColorString(color) + message;
		// Reset color at end, so log doesn't get messed up with colors:
		sys.log("MSG", InfoType.DEBUG, "SHELLWRITE-ANSI: " + message.strip() + "\u001B[0m");
		while (shellWriteThread.isInterrupted()) { try { Thread.sleep(50); } catch (InterruptedException e) {} }
		writeQueue += message;
		if (Global.getCurrentPhase().equals(Runphase.RUN)
				|| Global.getCurrentPhase().equals(Runphase.ERR))
			shellWriteThread.interrupt();
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
		Shell.println(AWTANSI.B_Yellow, "Suspending ShellWriteThread!");
		sys.log("SWT", InfoType.WARN, "Suspending ShellWriteThread!");
		try {
			Thread.sleep(150);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		this.suspend = true;
	}

	// =========================================== PRIVATE METHODS
	// ===========================================
	// Now (22.08) also working with ANSI colors (hopefully)
	public final void scroll(int lineCount) {
		// Unprotect text, removal otherwise won't work
		new ProtectedTextComponent(Main.mainFrameAWT.getCmdLine()).unprotectAllText();
		sys.log("SCRL", InfoType.DEBUG, "Scrolling by " + lineCount + " line(s):");
		if (lineCount > CMDLINE_MAX_LINE_COUNT) {
			sys.log("SCRL", InfoType.WARN, "Warning: scroll value higher than max. line count. Expect errors.");
		}

		// Total characters to remove at the top
		int removeChars = ordinalIndexOf(Main.mainFrameAWT.getCmdLine().getText(), "\n", lineCount);

		// Remove certain number of characters (removeChars)
		try {
			Main.mainFrameAWT.getCmdLine().getDocument().remove(0, removeChars + 1);
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
		if (Main.mainFrameAWT != null) {
			cmdLineLineBreakCount = Main.mainFrameAWT.getCmdLine().getText().split("\n").length;
		}
		int totalLineBreakCount = writeQueueLineBreakCount + cmdLineLineBreakCount;
		int scrollByLines = totalLineBreakCount - CMDLINE_MAX_LINE_COUNT;
		if (scrollByLines > 0) {
			// Write to log
			sys.log("SHLWRT", InfoType.DEBUG, "Autoscroll: " + totalLineBreakCount + " > " + CMDLINE_MAX_LINE_COUNT);
			sys.log("SHLWRT", InfoType.DEBUG, "Line breaks in cmdLine: " + cmdLineLineBreakCount);
			sys.log("SHLWRT", InfoType.DEBUG, "Line breaks in writeQueue: " + writeQueueLineBreakCount);
			sys.log("SHLWRT", InfoType.DEBUG, "Max. Line breaks in cmdLine: " + CMDLINE_MAX_LINE_COUNT);
			// Scroll by calculated amount of lines (method scroll(int lines) defined in
			// class)
			scroll(scrollByLines);
		} else {
			sys.log("SHLWRT", InfoType.DEBUG, "No Autoscroll: " + totalLineBreakCount + " <= " + CMDLINE_MAX_LINE_COUNT);
		}
	}

	/**
	 * Updates modules.ShellWriteThread.shellStream with content, the user entered.
	 * Should only be used, WHEN the user entered something. Otherwise, bugs and
	 * exceptions may occur.
	 */
	public void updateShellStream() {
		if (Main.javafxEnabled) {
			// TODO add support for shell stream update with javafx
			sys.log("SWT", InfoType.WARN, "Updating shell stream with JavaFX is currently unsupported.");
			return;
		} else {
			String appendStr = ""; // Newly added text from user if any
			try {
				String cmdLineText = Main.mainFrameAWT.getCmdLine().getText();
				String lastLineText = cmdLineText.split("\n")[cmdLineText.split("\n").length - 1];
				String prevWriteLastLine = prevWrite.split("\n")[prevWrite.split("\n").length - 1];
				
				// Remove any ANSI color code patterns
				// Alternative REGEX if the one doesn't work: "\\d{1,2}(;\\d{1,2})?"
				prevWriteLastLine = prevWriteLastLine.replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "");
			
				appendStr = lastLineText.substring(prevWriteLastLine.length());
				appendStr = appendStr.trim();
			} catch (IndexOutOfBoundsException ioobe) {
				appendStr = "";
				sys.log("SHLWRT", InfoType.DEBUG, "No new user input.");
				// ioobe.printStackTrace();
			}
			try {
				sys.log("SWT:DEBUG", InfoType.DEBUG, "Appending new bytes to shellStream...");
				shellStream.write(appendStr.getBytes());
				shellStream.write("\n".getBytes());
			} catch (IOException ioe) {
				sys.log("SWT", InfoType.DEBUG, "Writing data to shellStream failed.");
				ioe.printStackTrace();
			}
			sys.log("SHLWRT:DEBUG", InfoType.DEBUG, "Last line of previously written text: " + "\u001B[32m" + prevWrite + "\u001B[0m");
			sys.log("SHLWRT:DEBUG", InfoType.DEBUG, "New user input: " + "\u001B[32m" + appendStr + "\u001B[0m");
			try {
				// Validate if stream data matches appendStr
				if (shellReader.ready() && shellReader.readLine().equals(appendStr)) {
					sys.log("SHLWRT:DEBUG", InfoType.DEBUG, "\u001B[32m stream data validated. \u001B[0m");
				} else {
					sys.log("SWT:DEBUG", InfoType.DEBUG, "\\u001B[1;33m stream data validation mismatch. \u001B[0m");
				}
			} catch (IOException ioe) {
				sys.log("SWT:DEBUG", InfoType.DEBUG, "\\u001B[1;33m stream data validation exception. \u001B[0m");
			}
		}
	}
}
