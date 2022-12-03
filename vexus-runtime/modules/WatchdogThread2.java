package modules;

import engine.ANSI;
import engine.sys;
import libraries.OpenLib;
import libraries.VarLib;

import java.util.Random;

import javax.swing.text.BadLocationException;

public class WatchdogThread2 {
	private static final Thread watchdogThread2 = new Thread( new Runnable() {
		public void run() {
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			try { Thread.sleep(2000); } catch (InterruptedException ie) { ie.printStackTrace(); }
			while (true) {
				if (sys.getActivePhase().equals("run")) {
					if (WatchdogThread.isThreadAlive() == true) {
						try {
							new ProtectedTextComponent(main.Main.cmdLine).unprotectAllText();
							main.Main.cmdLine.getStyledDocument().remove(
									VarLib.getMOTD().length(),
									main.Main.cmdLine.getText().length() - VarLib.getMOTD().length());
							sys.shellPrintln(ANSI.B_Green, "Init success!");
							OpenLib.cmdLinePrepare();
							//main.Main.cmdLine.getStyledDocument().remove(main.Main.cmdLine.getText().lastIndexOf("\n"), 1);
						} catch (BadLocationException ble) {
							// TODO Auto-generated catch block
							ble.printStackTrace();
						}
						main.Main.cmdLine.setCaretPosition(main.Main.cmdLine.getText().length());
					} else {
						sys.shellPrint(ANSI.B_Yellow, "Init fail.\n");
						sys.setActivePhase("stop");
					}
				}
				break;
			}
			while (true) {
				try { Thread.sleep(2000); } catch (InterruptedException ie) { ie.printStackTrace(); }
			}
		}
	});
	
	public static final boolean isWatchdogThread2Alive() {
		if (watchdogThread2.isAlive()) {
			return true;
		} else {
			return false;
		}
	}
	protected static final void runWatchdogThread2() {
		watchdogThread2.start();
	}
}
