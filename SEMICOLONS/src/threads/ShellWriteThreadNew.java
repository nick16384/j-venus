package threads;

import javax.swing.text.BadLocationException;

import engine.sys;
import main.Main;

public class ShellWriteThreadNew implements InternalThread {
	private static String writeQueue = "";
	
	private Thread shellWriteThread = new Thread(() -> {
		while (!ThreadAllocation.isShutdownSignalActive()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				sys.log("SWT", 1, "SWT interrupted, writing to shell");
				if (writeQueue.isEmpty())
					continue;
				
				if (Main.javafxEnabled) {
					jfxcomponents.JFXANSI.appendANSI(Main.jfxWinloader.getCmdLine(), writeQueue);
				} else {
					try {
						awtcomponents.AWTANSI.appendANSI(Main.mainFrameAWT.getCmdLine(), writeQueue);
					} catch (BadLocationException ble) {
						sys.log("SWT", 2, "Write fail on AWT element. BadLocationException");
					}
				}
				
				writeQueue = "";
			}
		}
	});
	
	public void writeToShell(String text) {
		if (text != null)
			writeQueue += text;
		shellWriteThread.interrupt();
	}
	
	@Override
	public void start() {
		if (!shellWriteThread.isAlive())
			shellWriteThread.start();
	}
	@Override
	public void suspend() {
		sys.log("SWT", 3, "Suspending SWT is not possible.");
	}
	@Override
	public boolean isRunning() {
		return shellWriteThread.isAlive();
	}
	
	
}
