package main;

import java.io.IOException;
import java.lang.Exception;

import javax.swing.JFrame;

import java.util.Arrays;
import java.util.List;

import engine.LogLevel;
import engine.sys;
import shell.Shell;
import libraries.Global;
import threads.WatchdogThread;

public class Main {
	public static List<String> argsMain;
	
	public static void main(String[] args) {
		argsMain = Arrays.asList(args);
		sys.log("MAIN", LogLevel.INFO, "Commandline used for this VM (if empty, probably running on Windows):\n"
				+ ProcessHandle.current().info().commandLine().get());
		sys.log("MAIN", LogLevel.INFO, "Parent(s) launch info:");
		int stackedParentsCount = printProcessParentsInfo(ProcessHandle.current(), 0);
		sys.log("MAIN", LogLevel.INFO, "Stacked parents: " + stackedParentsCount);
		if (stackedParentsCount > 20) {
			sys.log("MAIN", LogLevel.CRIT, "This JVM instance has restarted itself too often. Shutting down.");
			System.exit(1);
		}
		
		//==================================== INIT ====================================
		
		System.out.println("[NoLog] Verifying installation files...");
		if (Arrays.asList(args).contains("--no-check-install")) {
			System.out.println("[NoLog] Verification skipped.");
		} else {
			if (engine.CheckInstall.fileCheck() == null) {
				System.out.println("[NoLog] Verification successful.");
			} else {
				System.out.println("[NoLog] Verification not successful.");
				System.out.println("[NoLog] The following file or folder does not exist or has incorrect permission settings:");
				System.out.println("[NoLog] " + engine.CheckInstall.fileCheck());
				System.exit(1);
			}
		}
		
		engine.Init.init(args);
		
		if (Arrays.asList(args).contains("--awt")) {
			Global.javafxEnabled = false;
			sys.log("MAIN", LogLevel.INFO, "Using deprecated AWT window loader.");
			awtcomponents.AWTWinload.awtWinload();
		} else {
			sys.log("MAIN", LogLevel.INFO, "Using default JavaFX window loader.");
			try {
				Global.javafxEnabled = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try { Thread.sleep(1000); } catch (InterruptedException ie) { ie.printStackTrace(); }
		Shell.showPrompt();
		Global.setNextRunphase(); // RUN
		
		//==================================== INIT END ====================================
	}
	
	public static awtcomponents.WindowMain mainFrameAWT;
	
	//========================================MAIN===========================================
	public static final void initAWTWindow() {
		sys.log("MAIN", LogLevel.DEBUG, "Creating new WindowMain object.");
		mainFrameAWT = new awtcomponents.WindowMain("J-Vexus " + Global.getVersion());
		sys.log("MAIN", LogLevel.DEBUG, "Attaching KeyListener to mainFrame.");
		awtcomponents.KeyListenerAttacher.attachKeyListener(mainFrameAWT);
	}
	
	public JFrame getMainWindow() {
		return Main.mainFrameAWT;
	}
	
	/**
	 * Current SEMICOLONS runtime in milliseconds since program start (idle watchdog).
	 * @return
	 */
	public static long getRuntime() {
		return System.currentTimeMillis() - WatchdogThread.getTimeStart();
	}
	
	public static void restartVMIfSupported() {
		sys.log("Trying JVM restart.");
		ProcessHandle.current().info().commandLine().ifPresentOrElse(
				(cmdLineArg) -> { try { Runtime.getRuntime().exec(cmdLineArg.split(" ")); System.exit(0); }
				catch (IOException ioe) { ioe.printStackTrace(); }},
				() -> { sys.log("VM restart not supported. Probably on Windows."); });
	}
	
	/**
	 * Recursive function to print info about all JVM process parents.
	 * @param process
	 * @param stackCount The times the function has called itself (Or how many parents it has.)
	 */
	private static int printProcessParentsInfo(ProcessHandle process, int stackCount) {
		if (process.parent().isPresent()) {
			sys.log(process.parent().get().info().commandLine().get());
		}
		return process.parent().isPresent()
				? printProcessParentsInfo(process.parent().get(), stackCount + 1)
				: stackCount;
	}
}