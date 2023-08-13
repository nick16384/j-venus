package threads;

import javax.swing.text.BadLocationException;

import engine.InfoType;
import engine.Runphase;
import engine.sys;
import libraries.Global;
import main.Main;

public class ShellWriteThread implements InternalThread {
	// Double buffer principle: One part is written to while the other one is handled by shellWriteThread.
	private static String[] writeQueue;
	private static int activeQueueBuffer;
	private static int inactiveQueueBuffer;
	
	private Thread shellWriteThread = new Thread(() -> {
		while (!ThreadAllocation.isShutdownSignalActive()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				sys.log("SWT", InfoType.DEBUG, "SWT interrupted, writing to shell");
				// TODO Fix double buffer system
				if (writeQueue[activeQueueBuffer].isEmpty())
					continue;
				
				if (Main.javafxEnabled) {
					jfxcomponents.JFXANSI.appendANSI(
							Main.jfxWinloader.getCmdLine(), writeQueue[activeQueueBuffer]);
				} else {
					try {
						awtcomponents.AWTANSI.appendANSI(
								Main.mainFrameAWT.getCmdLine(), writeQueue[activeQueueBuffer]);
					} catch (BadLocationException ble) {
						sys.log("SWT", InfoType.WARN, "Write fail on AWT element. BadLocationException");
					}
				}
				Main.jfxWinloader.triggerScrollUpdate();
				writeQueue[activeQueueBuffer] = "";
				inactiveQueueBuffer = activeQueueBuffer;
				activeQueueBuffer = activeQueueBuffer == 0 ? 1 : 0;
			}
		}
	});
	
	public void writeToShell(String text) {
		if (text != null)
			writeQueue[activeQueueBuffer] += text;
		shellWriteThread.interrupt();
	}
	
	public void writeToShell(javafx.scene.paint.Color color, String text) {
		writeQueue[inactiveQueueBuffer] += text == null ? "" : text;
		if (Global.getCurrentPhase().equals(Runphase.RUN)
				&& Main.jfxWinloader != null
				&& Main.jfxWinloader.getCmdLine() != null) {
			shellWriteThread.interrupt();
		}
	}
	
	public void writeToShellAWT(java.awt.Color color, String text) {
		sys.log("SWT", InfoType.DEBUG, "Received AWT color, converting to JavaFX format.");
		javafx.scene.paint.Color jfxColor;
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
		writeToShellAWT(color, text);
	}
	
	@Override
	public void start() {
		if (!shellWriteThread.isAlive()) {
			writeQueue = new String[] { "", "" };
			activeQueueBuffer = 0;
			inactiveQueueBuffer = 1;
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
