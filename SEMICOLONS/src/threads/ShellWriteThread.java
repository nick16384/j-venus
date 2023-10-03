package threads;

import javax.swing.text.BadLocationException;

import engine.InfoType;
import engine.Runphase;
import engine.sys;
import internalCommands.System_Cause_Error_Termination;
import jfxcomponents.GUIManager;
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
					sys.log("SWT", InfoType.DEBUG, "Swapped active buffer, writing to shell");
					
					// =========================== WRITE TO SHELL ===========================
					if (Global.javafxEnabled) {
						jfxcomponents.JFXANSI.appendANSI(
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
	
	public static void writeToShell(javafx.scene.paint.Color color, String text) {
		if (text == null)
			return;
		synchronized (swtMonitor) {
			writeBuffer.appendToInactive(text);
			if (Global.getCurrentPhase().equals(Runphase.RUN)
					&& GUIManager.getCmdLine() != null) {
				swtMonitor.notify();
			}
		}
	}
	
	public static void writeToShellAWT(java.awt.Color color, String text) {
		sys.log("SWT", InfoType.DEBUG, "Received AWT color, converting to JavaFX format.");
		javafx.scene.paint.Color jfxColor;
		// Get a JavaFX compatible color from an AWT color
		jfxColor = new javafx.scene.paint.Color(
				color.getRed() / 255,
				color.getBlue() / 255,
				color.getGreen() / 255,
				color.getTransparency() / 255);
		writeToShell(jfxColor, text);
	}
	
	@Deprecated
	public static void appendTextQueue(java.awt.Color color, String text, boolean... noProtect) {
		// Ignore noProtect
		sys.log("SWT", InfoType.WARN, "Using old AWT-compliant appendTextQueue method with noProtect.");
		writeToShellAWT(color, text);
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
