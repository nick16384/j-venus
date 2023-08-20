package threads;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import awtcomponents.AWTANSI;
import awtcomponents.WindowMain;
import engine.InfoType;
import engine.Runphase;
import engine.sys;
import javafx.application.Platform;
import libraries.Global;
import main.Main;
import shell.Shell;

public final class WatchdogThread implements InternalThread {
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
				sys.log("WDT", InfoType.STATUS, "Watchdog enabled.");
				Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

				try {
					Thread.sleep(5000);
				} catch (InterruptedException ie) {
					sys.log("WATCHDOG", InfoType.ERR, "Watchdog startup sleep has been interrupted.");
				}
				sys.log("WATCHDOG", InfoType.STATUS, "Watchdog idle now.");

				while (Global.getCurrentPhase().equals(Runphase.INIT)) {
					// wait for phase to change to "run"
				}

				while (!shutdownSignal) {
					// TODO check if main threads hung up (and then shutdown)
					
					//====================================== CHECKING ======================================
					
					try {
						if (Global.getCurrentPhase().equals(Runphase.STOP)) {
							break;
						}
						
						//Threads status
						if (!ThreadAllocation.getWDT2().isRunning()) {
							stopWithError(1, 15000, "Watchdog thread 2 is not running, and in the event of this thread\n"
									+ "(\"Watchdog Thread 1\") malfunctioning, there won't be any detection of internal\n"
									+ "errors anymore, so vexus has to shutdown. If this error reoccurs, some file might\n"
									+ "be missing and try to reinstall.");
							break;
						} else if (!ThreadAllocation.getSWT().isRunning()) {
							WindowMain.cmdLine.setText("Fatal error: ShellWriteThread not running, causing no more shell\n"
									+ "output to be displayed. If this issue persists, try reinstalling.");
							//This error message might not display due to SWT inactivity
							stopWithError(1, 15000, "The Shell Write Thread is not running. This will most probably cause no\n"
									+ "shell output anymore, so it would be strange, if you saw this message.\n"
									+ "Obviously, the shell can't be used when no feedback is coming from the system,\n"
									+ "and because of this, we have to shutdown. Try reinstalling, if this issue persists.");
							break;
						} else if (!ThreadAllocation.getCMGR().isRunning()) {
							stopWithError(1, 15000, "The Command Management Thread is inactive. This will cause no commands to\n"
									+ "be executed anymore unless you have enabled deprecated methods and disabled multithreading.\n"
									+ "This shell can't be used when no commands are getting processed, so this process will\n"
									+ "have to shutdown.");
							break;
						}
						
						//Current directory (path) validity
						if (Global.getCurrentDir() == null) {
							stopWithError(1, 15000, "The internal path variable was set to \"null\".\n"
									+ "This means, that the program cannot determine your current path anymore\n"
									+ "and might do some strange stuff breaking functionality completely.\n"
									+ "To prevent that from happening, this shutdown was induced to smoothly suspend\n"
									+ "the running Java instance and not hard kill it.");
							break;
						} else if (Global.getCurrentDir().isBlank()) {
							stopWithError(1, 15000, "The internal path variable is blank, which should not be,\n"
									+ "since every OS has some kind of filesystem root (e.g. \"C:\\\" on Windows and"
									+ "\"/\" for Unix-like systems.\n"
									+ "This means, that the program cannot determine your current path anymore\n"
									+ "and might do some strange stuff breaking functionality completely.\n"
									+ "To prevent that from happening, this shutdown was induced to smoothly suspend\n"
									+ "the running Java instance and not hard kill it.");
							break;
						} else if (!(Files.exists(Paths.get(Global.getCurrentDir()), LinkOption.NOFOLLOW_LINKS))
								|| !(Files.isDirectory(Paths.get(Global.getCurrentDir()), LinkOption.NOFOLLOW_LINKS))) {
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
						if (!(Global.getCurrentPhase().equals(Runphase.RUN))) {
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
						sys.log("WDT", InfoType.CRIT, "Exception thrown inside WDT. Stopping with error. Stacktrace below:");
						ex.printStackTrace();
					}
				}
				
				//====================================== CHECKING END ======================================

				long activeTime = System.currentTimeMillis() - timeStart;
				sys.log("STOPPING", InfoType.STATUS, "Active time: " + activeTime + "ms");
				sys.log("STOPPING", InfoType.STATUS, "Saving log file to: /var/J-Vexus_logs/ ");

				// String logFilePath = "/var/J-Vexus_logs/" + "logfile1.txt";

				sys.log("WATCHDOG", InfoType.STATUS, "Threads stopping...");
				if (Global.javafxEnabled && ThreadAllocation.getJFXT().isGUIActive()) Main.jfxWinloader.stop();
				System.exit(exitCode);
			}
		}, "WDT");
	}

	@Override
	public final void start() {
		if (watchdogThread.isAlive()) {
			sys.log("WATCHDOG", InfoType.WARN, "WatchdogThread already running.");
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
		sys.log("WTT", InfoType.NONCRIT, "Watchdog thread cannot be suspended.");
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
		try { Thread.sleep(1000); } catch (InterruptedException ie) { ie.printStackTrace(); }
		if (Global.javafxEnabled) {
			Platform.runLater(() -> { Main.cmdLine.clear(); });
		} else {
			new components.ProtectedTextComponent(Main.mainFrameAWT.getCmdLine()).unprotectAllText();
			Main.mainFrameAWT.getCmdLine().setText("");
		}
		Global.setErrorRunphase();
		try { Thread.sleep(200); } catch (InterruptedException ie) { ie.printStackTrace(); }
		sys.log("[WDT]", InfoType.CRIT, errMsg);
		Platform.runLater(() -> {
			Shell.println(AWTANSI.B_Yellow,
					"\n\n===============================================\n"
							+ "There was an operation-critical error and execution cannot proceed.\n\n"
							+ errMsg + "\n\n"
							+ "I'm very sorry for that, but Vexus is in beta and things like this happen all the time.\n"
							+ "Please contact me, if this error is reproducible and bugs you around (a lot):\n"
							+ "https://theophil.pudelkern.com/\n"
							+ "===============================================");
		});
		if (!Global.javafxEnabled) { Main.mainFrameAWT.getCmdLine().setEditable(false); }
		Shell.println(AWTANSI.B_Cyan, "Log file is at: " + Global.getLogFile().getAbsolutePath());
		if (waitBeforeStop > 100 && waitBeforeStop < 60000) {
			Platform.runLater(() -> {
			Shell.println(AWTANSI.B_Green,
					"This JVM will be suspended in " + Double.toString(waitBeforeStop / 1000) + " seconds.");
			});
			try {
				Thread.sleep(waitBeforeStop);
			} catch (InterruptedException ie) {
				sys.log("Error stop wait was interrupted.");
			}
		} else {
			sys.log("WTT", InfoType.ERR, "Can't wait less than 100 or more than 60,000 milliseconds until VM suspension.");
			sys.log("WTT", InfoType.ERR, "Defaulting to 10 seconds.");
			Platform.runLater(() -> {
				Shell.println(AWTANSI.B_Green, "This JVM will be suspended in 10 seconds.");
			});
			try {
				Thread.sleep(waitBeforeStop);
			} catch (InterruptedException ie) {
				sys.log("Error stop wait was interrupted.");
			}
		}
		ThreadAllocation.getWDT().shutdownVexus(exitCode);
	}
}
