package modules;

import main.Lib;

public class WatchdogThread2 {
	private static final Thread watchdogThread2 = new Thread( new Runnable() {
		public void run() {
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			while (true) {
				try { Thread.sleep(5000); } catch (InterruptedException ie) {}
				Lib.logWrite("WATCHDOG2", 1, "WatchdogThread2 active");
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
