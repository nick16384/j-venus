package modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import engine.sys;
import libraries.OpenLib;
import libraries.VarLib;

public final class WatchdogThread {
	protected static boolean shutdownSignal = false;
	private static int exitCode = 0;
	private static long timeStart = System.currentTimeMillis();
	private static final Thread watchdogThread = new Thread( new Runnable() {
		public final void run() {
			sys.log("WATCHDOG", 0, "Vexus security checker thread started.");
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			
			boolean mandatoryThreadsActive;
			int activeMandatoryThreads = 0;
			String[] mandatoryThreadNames = new String[] {"Thread-0", "Thread-1", "Thread-2"};
			Set<Thread> activeThreads = Thread.getAllStackTraces().keySet();
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException ie) {
				sys.log("WATCHDOG", 3, "Watchdog startup sleep has been interrupted.");
			}
			sys.log("WATCHDOG", 0, "Watchdog idle now.");
			
			while (sys.getActivePhase().equals("init")) {
				//wait for phase to change to "run"
			}
			
			while (!shutdownSignal) {
				//TODO check if main threads hung up (and then shutdown)
				
				try { Thread.sleep(500); } catch (InterruptedException ie) {}
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
					sys.setShellMode("native");
					new ProtectedTextComponent(main.Main.cmdLine).unprotectAllText();
					main.Main.cmdLine.setText("");
					sys.shellPrint(4, "WATCHDOG", "J-Vexus has encountered a critical error and has to be shutdown. \n");
					sys.shellPrint(4, "WATCHDOG", "See log file for further information. \n");
					sys.shellPrint(4, "WATCHDOG", "Automatic shutdown in 15 seconds... \n");
					main.Main.cmdLine.setCaretPosition(main.Main.cmdLine.getText().length());
					main.Main.cmdLine.setEditable(false);
					try { Thread.sleep(15000); } catch (InterruptedException ie) {}
					shutdownSignal = true;
				}
				if (VarLib.getCurrentDir() == null) {
					sys.setShellMode("native");
					new ProtectedTextComponent(main.Main.cmdLine).unprotectAllText();
					main.Main.cmdLine.setText("");
					sys.shellPrint(4, "WATCHDOG", "J-Vexus has encountered a critical error and has to be shutdown. \n");
					sys.shellPrint(4, "WATCHDOG", "See log file for further information. \n");
					sys.shellPrint(4, "WATCHDOG", "Automatic shutdown in 15 seconds... \n");
					main.Main.cmdLine.setCaretPosition(main.Main.cmdLine.getText().length());
					main.Main.cmdLine.setEditable(false);
					sys.log("WATCHDOG", 4, "Usual watchdog check has found the following error:");
					sys.log("WATCHDOG", 4, "Current working directory is null. Vexus was shutdown to prevent further damage.");
					sys.log("WATCHDOG", 4, "IF this error persists, please reinstall J-Vexus and report the problem.");
					try { Thread.sleep(15000); } catch (InterruptedException ie) {}
					shutdownSignal = true;
				} else if (VarLib.getCurrentDir().isBlank()) {
					sys.setShellMode("native");
					new ProtectedTextComponent(main.Main.cmdLine).unprotectAllText();
					main.Main.cmdLine.setText("");
					sys.shellPrint(4, "WATCHDOG", "J-Vexus has encountered a critical error and has to be shutdown. \n");
					sys.shellPrint(4, "WATCHDOG", "See log file for further information. \n");
					sys.shellPrint(4, "WATCHDOG", "Automatic shutdown in 15 seconds... \n");
					main.Main.cmdLine.setCaretPosition(main.Main.cmdLine.getText().length());
					main.Main.cmdLine.setEditable(false);
					sys.log("WATCHDOG", 4, "Usual watchdog check has found the following error:");
					sys.log("WATCHDOG", 4, "Current working directory is blank. Vexus was shutdown to prevent further damage.");
					sys.log("WATCHDOG", 4, "IF this error persists, please reinstall J-Vexus and report the problem.");
					try { Thread.sleep(15000); } catch (InterruptedException ie) {}
					shutdownSignal = true;
				} else if (!(Files.exists(Paths.get(VarLib.getCurrentDir()), LinkOption.NOFOLLOW_LINKS)) 
					|| !(Files.isDirectory(Paths.get(VarLib.getCurrentDir()), LinkOption.NOFOLLOW_LINKS))) {
					//HighLevel.setShellMode("native");
					main.Main.cmdLine.setEditable(false);
					sys.shellPrint(4, "WATCHDOG", "\nYou changed your directory to a non-existent\n");
					sys.shellPrint(4, "WATCHDOG", "path. Your current(invalid) directory was:\n");
					sys.shellPrint(4, "WATCHDOG", VarLib.getCurrentDir() + "\n");
					sys.shellPrint(4, "WATCHDOG", "Please note that by now, symbolic and hard links will\n");
					sys.shellPrint(4, "WATCHDOG", "be detected as file and not a directory and so will\n");
					sys.shellPrint(4, "WATCHDOG", "eventually show a \"not valid\"-error\n");
					sys.shellPrint(4, "WATCHDOG", "Changing back to valid filesystem root:\n");
					sys.shellPrint(4, "WATCHDOG", VarLib.getFSRoot() + "\n");
					sys.shellPrint(4, "WATCHDOG", "See log file for further information. \n");
					main.Main.cmdLine.setCaretPosition(main.Main.cmdLine.getText().length());
					sys.log("WATCHDOG", 4, "Usual watchdog check has found the following error:");
					sys.log("WATCHDOG", 4, "Current working directory is not valid in the current file system.");
					sys.log("WATCHDOG", 4, "Setting the working directory to default root.");
					sys.log("WATCHDOG", 4, "IF this error persists, please reinstall J-Vexus and report the problem.");
					VarLib.setCurrentDir(VarLib.getFSRoot());
					//HighLevel.setShellMode("normal");
					main.Main.cmdLine.setEditable(true);
					OpenLib.cmdLinePrepare();
					//TODO make cmdLinePrepare() work at this point after folder not found exception
				}
				
				if (!(sys.getActivePhase().equalsIgnoreCase("run"))) {
					sys.setShellMode("native");
					main.Main.cmdLine.setText("");
					sys.shellPrint(3, "WATCHDOG", "Hang during init detection\n");
					sys.shellPrint(4, "WATCHDOG", "J-Vexus has encountered a critical error and has to be shutdown. \n");
					sys.shellPrint(4, "WATCHDOG", "See log file for further information. \n");
					sys.shellPrint(4, "WATCHDOG", "Automatic shutdown in 15 seconds... \n");
					main.Main.cmdLine.setCaretPosition(main.Main.cmdLine.getText().length());
					main.Main.cmdLine.setEditable(false);
					sys.log("WATCHDOG", 5, "Usual watchdog check has found the following error:");
					sys.log("WATCHDOG", 5, "The internal J-Vexus phase variable was not set to required mode 'run'");
					sys.log("WATCHDOG", 5, "This must not be a problem but can be an issue.");
					sys.log("WATCHDOG", 5, "Vexus will be shutdown in 15 seconds.");
					sys.log("WATCHDOG", 5, "IF this error persists, please reinstall J-Vexus and report the problem.");
					try { Thread.sleep(15000); } catch (InterruptedException ie) {}
					shutdownSignal = true;
				}
			}
			
			long activeTime = System.currentTimeMillis() - timeStart;
			sys.log("STOPPING", 0, "Vexus active time: " + activeTime + "ms");
			sys.log("STOPPING", 0, "Saving log file to: /var/J-Vexus_logs/ ");
			
			//String logFilePath = "/var/J-Vexus_logs/" + "logfile1.txt";
			
			sys.log("WATCHDOG", 0, "Watchdog Threads stopping...");
			System.exit(exitCode);
		}
	});
	
	public final static void runThreads() {
		if (watchdogThread.isAlive()) { sys.log("WATCHDOG", 2, "WatchdogThread already running."); }
		else { watchdogThread.start(); }
		if (WatchdogThread2.isWatchdogThread2Alive()) { sys.log("WATCHDOG", 2, "WatchdogThread2 already running."); }
		else { WatchdogThread2.runWatchdogThread2(); }
		if (ShellWriteThread.isThreadAlive()) { sys.log("WATCHDOG", 2, "ShellWriteThread already running."); }
		else { ShellWriteThread.startThread(); }
		if (CheckUserInputThread.isThreadAlive()) { sys.log("WATCHDOG", 2, "CheckUserInputThread already running."); }
		else { CheckUserInputThread.startThread(); }
	}
	public final static boolean isThreadAlive() {
		return watchdogThread.isAlive();
	}
	public final void shutdownVexus(int exitCode) {
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
