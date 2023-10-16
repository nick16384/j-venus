package threads;

import javax.swing.text.BadLocationException;

import engine.InfoType;
import engine.Runphase;
import engine.sys;
import internalCommands.System_Cause_Error_Termination;
import jfxcomponents.GUIManager;
import jfxcomponents.ANSI;
import libraries.Global;
import main.Main;
import shell.DoubleTextBuffer;
import shell.Shell;

public class ShellWriteThread {
	// Double buffer principle: One part is written to while the other one is handled by shellWriteThread.
	private static DoubleTextBuffer writeBuffer;
	private static final Object swtMonitor = new Object();
	
	private static Thread shellWriteThread;
	
	protected static void initialize() {
		shellWriteThread = new Thread(() -> {
			
			Global.waitUntilReady();
			
			// Interrupt itself to write text from writeBuffer before loop started.
			// Normally, notify() is used, but a notify flag is not kept so by the time,
			// the loop is reached, the notify signal is already gone.
			selfInterrupt();
			
			while (!sys.isShutdownSignalActive()) {
				synchronized (swtMonitor) {
					try {
						swtMonitor.wait(500);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
					
					if (writeBuffer.readFromInactive().isBlank())
						continue;
					
					// jfx.GUIManager is now static so change accordingly in all other classes
					
					writeBuffer.swapActive();
					
					// =========================== WRITE TO SHELL ===========================
					if (Global.javafxEnabled) {
						jfxcomponents.ANSI.appendANSI(
								GUIManager.getCmdLine(), writeBuffer.readFromActive());
					} else {
						try { awtcomponents.AWTANSI.appendANSI(
								Main.mainFrameAWT.getCmdLine(), writeBuffer.readFromActive());
						} catch (BadLocationException ble) {
							sys.log("SWT", InfoType.WARN, "Write fail on AWT element. BadLocationException"); }
					}
					// =========================== WRITE TO SHELL END ===========================
					
					Shell.triggerScrollUpdate();
					writeBuffer.clearActive();
				}
			}
		}, "SWT");
		
		writeBuffer = new DoubleTextBuffer();
		shellWriteThread.setPriority(8);
		shellWriteThread.setDaemon(true);
		shellWriteThread.start();
	}
	
	private static void selfInterrupt() {
		shellWriteThread.interrupt();
	}
	
	public static void writeToShell(String text) {
		if (text == null)
			return;
		synchronized (swtMonitor) {
			writeBuffer.appendToInactive(text);
			swtMonitor.notify();
		}
	}
	
	/**
	 * Appends text to the shell, whilst also removing color information.
	 * If useDefaultColor is true, "JFXANSI.cReset" is used
	 * @param text
	 * @param useDefaultColor
	 */
	public static void writeToShell(String text, boolean useDefaultColor) {
		if (text == null)
			return;
		synchronized (swtMonitor) {
			if (useDefaultColor)
				writeBuffer.appendToInactive(ANSI.getANSIColorString(ANSI.cReset) + text);
			else
				writeBuffer.appendToInactive(text);
			if (Global.getCurrentPhase().equals(Runphase.RUN)
					&& GUIManager.getCmdLine() != null) {
				swtMonitor.notify();
			}
		}
	}
	
	public static void suspend() {
		sys.log("SWT", InfoType.ERR, "Suspending SWT is not possible.");
	}
	
	public static boolean isRunning() {
		return shellWriteThread != null && shellWriteThread.isAlive();
	}
	
	public static void updateShellStream() {
		sys.log("pls implement updateShellStream in SWT");
	}
}
