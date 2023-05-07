package threads;

import engine.AWTANSI;
import engine.sys;
import libraries.OpenLib;
import libraries.VarLib;
import main.Main;

import java.util.Random;

import javax.swing.text.BadLocationException;

import components.ProtectedTextComponent;

public class WatchdogThread2 implements VexusThread {
	private Thread watchdogThread2;

	protected WatchdogThread2() {
		watchdogThread2 = new Thread(null, new Runnable() {
			public void run() {
				Thread.currentThread().setPriority(4); //1 is MIN_PRIORITY, 10 is MAX_PRIORITY
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
				while (true) {
					if (sys.getActivePhase().equals("run")) {
						if (Main.ThreadAllocMain.isWDTActive()) {
							/*sys.shellPrintln(AWTANSI.B_Green, "Init success!");
							OpenLib.cmdLinePrepare();*/
							
							try {
								if (Main.javafxEnabled && Main.jfxWinloader.getCmdLine() != null)
									sys.log("WDT2", 1, "Not setting caret to last position");
									// set jfx caret to last pos
								else
									Main.mainFrame.getCmdLine()
											.setCaretPosition(Main.mainFrame.getCmdLine().getText().length());
							} catch (NullPointerException npe) {
								sys.log("WDT2", 3, "Setting cursor to last text position threw an error. Main.mainFrame probably is null.");
							} catch (IllegalArgumentException iae) {
								sys.log("WDT2", 3, "Setting cursor to last text position threw an error, because the set position was out of range.");
							}
						} else {
							sys.shellPrint(AWTANSI.B_Yellow, "Init fail.\n");
							sys.setActivePhase("err");
						}
						
						try {
							if (!Main.javafxEnabled) {
								Main.mainFrame.getCmdLine().setEditable(true);
								new ProtectedTextComponent(Main.mainFrame.getCmdLine()).unprotectAllText();
							}
						} catch (NullPointerException npe) {
							npe.printStackTrace();
						}
						
						break;
					}
				}
				while (!Main.ThreadAllocMain.isShutdownSignalActive()) {
					try { Thread.sleep(2000); } catch (InterruptedException ie) { ie.printStackTrace(); }
					if (!Main.ThreadAllocMain.isWDTActive()) {
						WatchdogThread.stopWithError(2, 15000, "[WDT2] Watchdog Thread 1 (WDT) is found inactive.\n"
								+ "Because it cannot detect further errors, Vexus will be terminated.\n"
								+ "This is probably an internal error or bug. If this issue continues to persist\n"
								+ "and is reproducible, try restarting or reinstalling, and if that still doesn't help,\n"
								+ "contact me and I'll try to fix the problem.");
					}
					
					try {
						if (!Main.javafxEnabled) {
							Main.mainFrame.getCmdLine().setEditable(true);
							new ProtectedTextComponent(Main.mainFrame.getCmdLine()).unprotectAllText();
						}
					} catch (NullPointerException npe) {
						npe.printStackTrace();
					}
				}
			}
		}, "WDT2");
	}
	
	@Override
	public void start() {
		watchdogThread2.start();
	}

	@Override
	public void suspend() {
		sys.log("WDT2", 2, "WatchdogThread2 cannot be suspended.");
	}

	@Override
	public boolean isRunning() {
		return watchdogThread2.isAlive();
	}
}
