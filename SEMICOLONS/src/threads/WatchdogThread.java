package threads;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import awt.windowManager.WindowMain;
import engine.AWTANSI;
import engine.sys;
import libraries.VarLib;
import main.Main;

public final class WatchdogThread implements VexusThread {
	protected boolean shutdownSignal;
	private int exitCode;
	private long timeStart;
	//Indicated whether non-critical error message have already been displayed:
	private boolean nonCriticalAlreadyDisplayed;
	private Thread watchdogThread;

	protected WatchdogThread() {
		shutdownSignal = false;
		exitCode = 0;
		timeStart = System.currentTimeMillis();
		nonCriticalAlreadyDisplayed = false;
		
		//TODO implement check for required threads as this probably now works.
		String[] requiredThreads = new String[] { "WDT", "WDT2", "Thread-0", "SWT", "CMGR", "CUIT" };
		System.err.println("Dev TODO: implement check for required threads as this probably now works.");

		watchdogThread = new Thread(null, new Runnable() {
			public final void run() {
				sys.log("WATCHDOG", 0, "Vexus security checker thread started.");
				Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

				try {
					Thread.sleep(5000);
				} catch (InterruptedException ie) {
					sys.log("WATCHDOG", 3, "Watchdog startup sleep has been interrupted.");
				}
				sys.log("WATCHDOG", 0, "Watchdog idle now.");

				while (sys.getActivePhase().equals("init")) {
					// wait for phase to change to "run"
				}

				while (!shutdownSignal) {
					// TODO check if main threads hung up (and then shutdown)
					
					//====================================== CHECKING ======================================
					
					try {
						if (sys.getActivePhase().equals("stop")) {
							break;
						}
						
						//Threads status
						if (!Main.ThreadAllocMain.getWDT2().isRunning()) {
							stopWithError(1, 15000, "Watchdog thread 2 is not running, and in the event of this thread\n"
									+ "(\"Watchdog Thread 1\") malfunctioning, there won't be any detection of internal\n"
									+ "errors anymore, so vexus has to shutdown. If this error reoccurs, some file might\n"
									+ "be missing and try to reinstall.");
							break;
						} else if (!Main.ThreadAllocMain.getSWT().isRunning()) {
							WindowMain.cmdLine.setText("Fatal error: ShellWriteThread not running, causing no more shell\n"
									+ "output to be displayed. If this issue persists, try reinstalling.");
							//This error message might not display due to SWT inactivity
							stopWithError(1, 15000, "The Shell Write Thread is not running. This will most probably cause no\n"
									+ "shell output anymore, so it would be strange, if you saw this message.\n"
									+ "Obviously, the shell can't be used when no feedback is coming from the system,\n"
									+ "and because of this, we have to shutdown. Try reinstalling, if this issue persists.");
							break;
						} else if (!Main.ThreadAllocMain.getCUIT().isRunning() && !nonCriticalAlreadyDisplayed) {
							sys.shellPrintln(AWTANSI.B_Yellow, "Warning: the internal \"User Input Detection Thread\" has stopped,\n"
									+ "and therefore, some control signals like CTRL + C may not work anymore.\n"
									+ "This is not a critical error, but might indicate a problem and you should try to restart.\n"
									+ "If that does not get rid of the problem, consider reinstalling.");
							nonCriticalAlreadyDisplayed = true;
						} else if (!Main.ThreadAllocMain.getCMGR().isRunning()) {
							stopWithError(1, 15000, "The Command Management Thread is inactive. This will cause no commands to\n"
									+ "be executed anymore unless you have enabled deprecated methods and disabled multithreading.\n"
									+ "This shell can't be used when no commands are getting processed, so this process will\n"
									+ "have to shutdown.");
							break;
						}
						
						//Current directory (path) validity
						if (VarLib.getCurrentDir() == null) {
							stopWithError(1, 15000, "The internal path variable was set to \"null\".\n"
									+ "This means, that the program cannot determine your current path anymore\n"
									+ "and might do some strange stuff breaking functionality completely.\n"
									+ "To prevent that from happening, this shutdown was induced to smoothly suspend\n"
									+ "the running Java instance and not hard kill it.");
							break;
						} else if (VarLib.getCurrentDir().isBlank()) {
							stopWithError(1, 15000, "The internal path variable is blank, which should not be,\n"
									+ "since every OS has some kind of filesystem root (e.g. \"C:\\\" on Windows and"
									+ "\"/\" for Unix-like systems.\n"
									+ "This means, that the program cannot determine your current path anymore\n"
									+ "and might do some strange stuff breaking functionality completely.\n"
									+ "To prevent that from happening, this shutdown was induced to smoothly suspend\n"
									+ "the running Java instance and not hard kill it.");
							break;
						} else if (!(Files.exists(Paths.get(VarLib.getCurrentDir()), LinkOption.NOFOLLOW_LINKS))
								|| !(Files.isDirectory(Paths.get(VarLib.getCurrentDir()), LinkOption.NOFOLLOW_LINKS))) {
							stopWithError(1, 15000, "The internal path variable does not refer to a valid\n"
									+ "folder location. This could mean, that you changed your directory(cd) to\n"
									+ "a file or nonexistent path and the system didn't catch the error,\n"
									+ "some internal function tried to change your path in the background, or\n"
									+ "you changed your directory to a symlink (a link to another file or folder):\n"
									+ "Even if that particular link may have depicted a folder, they'll all be defaulted\n"
									+ "to files, because symlinks aren't supported yet.");
							break;
						}
						
						//Internal state and phase checking
						if (!(sys.getActivePhase().equalsIgnoreCase("run"))) {
							stopWithError(1, 15000, "After 5 seconds of waiting, the system still doesn't seem to have fully\n"
									+ "finished initializing. That could be, either because an internal state caused\n"
									+ "some kind of change, that disallowed setting the current phase to required mode 'run',\n"
									+ "or this Java process could not allocate enough resources to finish startup early enough.\n"
									+ "Try restarting (both Vexus and your Computer) or disable programs that may interfere\n"
									+ "or drastically reduce computer performance.");
							break;
						}
						
						// Check periodically every 500ms.
						try {
							Thread.sleep(500);
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						}
					} catch (Exception ex) {
						stopWithError(1, 15000, "The internal Watchdog Thread, which checks internal functions are running\n"
								+ "without errors, has thrown an exception, which means, some check failed miserably. This\n"
								+ "problem probably arose from the program and has nothing to do with you, but because\n"
								+ "stable operation is not guaranteed from now, this program will now terminate.");
						sys.log("WDT", 4, "Exception thrown inside WDT. Stopping with error. Stacktrace below:");
						ex.printStackTrace();
					}
				}
				
				//====================================== CHECKING END ======================================

				long activeTime = System.currentTimeMillis() - timeStart;
				sys.log("STOPPING", 0, "Vexus active time: " + activeTime + "ms");
				sys.log("STOPPING", 0, "Saving log file to: /var/J-Vexus_logs/ ");

				// String logFilePath = "/var/J-Vexus_logs/" + "logfile1.txt";

				sys.log("WATCHDOG", 0, "Threads stopping...");
				if (Main.javafxEnabled && Main.ThreadAllocMain.getJFXT().isGUIActive()) Main.jfxWinloader.stop();
				System.exit(exitCode);
			}
		}, "WDT");
	}

	@Override
	public final void start() {
		if (watchdogThread.isAlive()) {
			sys.log("WATCHDOG", 2, "WatchdogThread already running.");
		} else {
			watchdogThread.start();
		}
	}

	@Override
	public final boolean isRunning() {
		return watchdogThread.isAlive();
	}

	@Override
	public final void suspend() {
		sys.log("WTT", 3, "Watchdog thread cannot be suspended.");
	}

	protected final void shutdownVexus(int exitCode) {
		sys.log("[WDT] Got shutdown command. Exit code: " + exitCode);
		this.exitCode = exitCode;
		shutdownSignal = true;
	}

	public final String[] getStats() {
		final String[] currentStats = new String[] { Long.toString(System.currentTimeMillis() - this.timeStart) };
		return currentStats;
	}
	
	public long getTimeStart() {
		return timeStart;
	}

	public final boolean isShutdownSignalActive() {
		return shutdownSignal;
	}
	
	/**
	 * Is called, when WTT or WTT2 detect some kind of error.
	 * 
	 * @param exitCode       Exit code to suspend JVM with
	 * @param waitBeforeStop How many milliseconds to wait from error display to VM
	 *                       shutdown
	 * @param errMsg         Error message to display to the user
	 */
	protected static final void stopWithError(int exitCode, int waitBeforeStop, String errMsg) {
		new components.ProtectedTextComponent(Main.mainFrameAWT.getCmdLine()).unprotectAllText();
		Main.mainFrameAWT.getCmdLine().setText("");
		sys.setActivePhase("error");
		sys.setShellMode("native");
		try { Thread.sleep(200); } catch (InterruptedException ie) { ie.printStackTrace(); }
		sys.log("[WDT]", 4, errMsg);
		sys.shellPrintln(AWTANSI.B_Yellow,
				"\n\n===============================================\n"
				+ "There was an operation-critical error and execution cannot proceed.\n\n"
				+ errMsg + "\n\n"
				+ "I'm very sorry for that, but Vexus is in beta and things like this happen all the time.\n"
				+ "Please contact me, if this error is reproducible and bugs you around (a lot):\n"
				+ "https://theophil.pudelkern.com/\n"
				+ "===============================================");
		Main.mainFrameAWT.getCmdLine().setEditable(false);
		sys.shellPrintln(AWTANSI.B_Cyan, "Log file is at: " + VarLib.getLogFile().getAbsolutePath());
		if (waitBeforeStop > 100 && waitBeforeStop < 60000) {
			sys.shellPrintln(AWTANSI.B_Green,
					"This JVM will be suspended in " + Double.toString(waitBeforeStop / 1000) + " seconds.");
			try {
				Thread.sleep(waitBeforeStop);
			} catch (InterruptedException ie) {
				sys.log("Error stop wait was interrupted.");
			}
		} else {
			sys.log("WTT", 3, "Can't wait less than 100 or more than 60,000 milliseconds until VM suspension.");
			sys.log("WTT", 3, "Defaulting to 10 seconds.");
			sys.shellPrintln(AWTANSI.B_Green, "This JVM will be suspended in 10 seconds.");
			try {
				Thread.sleep(waitBeforeStop);
			} catch (InterruptedException ie) {
				sys.log("Error stop wait was interrupted.");
			}
		}
		Main.ThreadAllocMain.getWDT().shutdownVexus(exitCode);
	}
}
