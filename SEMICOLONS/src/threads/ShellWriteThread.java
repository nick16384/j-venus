package threads;

import javax.swing.text.BadLocationException;

import engine.InfoType;
import engine.Runphase;
import engine.sys;
import internalCommands.System_Cause_Error_Termination;
import libraries.Global;
import main.Main;
import shell.DoubleTextBuffer;
import shell.Shell;

public class ShellWriteThread implements InternalThread {
	// Double buffer principle: One part is written to while the other one is handled by shellWriteThread.
	private static DoubleTextBuffer writeBuffer;
	
	private Thread shellWriteThread = new Thread(() -> {
		while (!Global.getCurrentPhase().equals(Runphase.RUN)
				|| Main.jfxWinloader == null
				|| Main.jfxWinloader.getCmdLine() == null) {
			try { Thread.sleep(50); } catch (InterruptedException ie) {}
		}
		writeToShell(""); // Interrupt itself
		
		while (!ThreadAllocation.isShutdownSignalActive()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				writeBuffer.swapActive();
				sys.log("SWT", InfoType.DEBUG, "Swapped active buffer, writing to shell");
				if (writeBuffer.readFromActive().isEmpty())
					continue;
				
				// =========================== WRITE TO SHELL ===========================
				if (Global.javafxEnabled) {
					jfxcomponents.JFXANSI.appendANSI(
							Main.jfxWinloader.getCmdLine(), writeBuffer.readFromActive());
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
	});
	
	public void writeToShell(String text) {
		if (text == null)
			return;
		writeBuffer.appendToInactive(text);
		shellWriteThread.interrupt();
	}
	
	public void writeToShell(javafx.scene.paint.Color color, String text) {
		if (text == null)
			return;
		writeBuffer.appendToInactive(text);
		if (Global.getCurrentPhase().equals(Runphase.RUN)
				&& Main.jfxWinloader != null
				&& Main.jfxWinloader.getCmdLine() != null) {
			shellWriteThread.interrupt();
		}
	}
	
	public void writeToShellAWT(java.awt.Color color, String text) {
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
	public void appendTextQueue(java.awt.Color color, String text, boolean... noProtect) {
		// Ignore noProtect
		sys.log("SWT", InfoType.WARN, "Using old AWT-compliant appendTextQueue method with noProtect.");
		writeToShellAWT(color, text);
	}
	
	@Override
	public void start() {
		if (!shellWriteThread.isAlive()) {
			writeBuffer = new DoubleTextBuffer();
			shellWriteThread.setPriority(8);
			shellWriteThread.start();
		}
	}
	@Override
	public void suspend() {
		sys.log("SWT", InfoType.ERR, "Suspending SWT is not possible.");
	}
	@Override
	public boolean isRunning() {
		return shellWriteThread.isAlive();
	}
	
	public void updateShellStream() {
		sys.log("pls implement updateShellStream in SWT");
	}
}
