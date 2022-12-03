package modules;

import java.util.ArrayList;
import java.util.Set;

import main.Lib;

public final class WatchdogThread {
	private static boolean shutdownSignal = false;
	private static int exitCode = 0;
	private static long timeStart = System.currentTimeMillis();
	private static final Thread watchdogThread = new Thread( new Runnable() {
		public final void run() {
			System.out.println("JDOS security checker thread started.");
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			
			boolean mandatoryThreadsActive;
			int activeMandatoryThreads = 0;
			String[] mandatoryThreadNames = new String[] {"Thread-0", "Thread-1"};
			Set<Thread> activeThreads = Thread.getAllStackTraces().keySet();
			
			while (!shutdownSignal) {
				//TODO check if main threads hung up (and then shutdown)
				
				try { Thread.sleep(2000); } catch (InterruptedException ie) {}
				activeMandatoryThreads = 0;
				mandatoryThreadsActive = false;
				activeThreads = Thread.getAllStackTraces().keySet();
				
				for (Thread thread : activeThreads) {
					for (String mandatoryThreadName : mandatoryThreadNames) {
						if (thread.getName().equalsIgnoreCase(mandatoryThreadName)) {
							activeMandatoryThreads++;
						}
					}
				}
				if (!(activeMandatoryThreads == mandatoryThreadNames.length)) {
					shutdownSignal = true;
				}
			}
			
			long activeTime = System.currentTimeMillis() - timeStart;
			System.out.println("JDOS active time: " + activeTime + "ms");
			System.out.println("Watchdog Threads stopping...");
			System.exit(exitCode);
		}
	});
	
	public final static void runThread() {
		if (watchdogThread.isAlive()) { System.err.println("WatchdogThread already running."); }
		else { watchdogThread.start(); }
		if (WatchdogThread2.isWatchdogThread2Alive()) { System.err.println("WatchdogThread2 already running."); }
		else { WatchdogThread2.runWatchdogThread2(); }
	}
	public final static boolean isThreadAlive() {
		return watchdogThread.isAlive();
	}
	public final void shutdownJavaDOS(int exitCode) {
		this.exitCode = exitCode;
		shutdownSignal = true;
	}
	public final static String[] getStats() {
		final String[] currentStats = new String[] {
			Long.toString(System.currentTimeMillis() - timeStart)
		};
		
		return currentStats;
	}
}
