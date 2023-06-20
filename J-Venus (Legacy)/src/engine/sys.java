package engine;

import libraries.OpenLib;
import main.Main;

import java.awt.Color;

/**
 * Contains relevant methods for logging and internal phases and modes
 * Easier access by short name "sys.somefunction()",
 * than old "HighLevel.somefunction()"
 *
 */

public class sys {
	
		
		//OLD CODE
		/**/
	
	
	//================================================================================================
	//===========================================SHELLWRITE===========================================
	/**
	 * Old shell_write() method (deprecated). Keeping for legacy support only.
	 * 
	 * @param priority
	 * @param auth
	 * @param message
	 * @param noProtect
	 */
	public static void shellPrint(int priority, String auth, String message, boolean... noProtect) {
		if (main.Main.singleThreaded) {
			Main.mainFrameAWT.getCmdLine().setText(Main.mainFrameAWT.getCmdLine().getText() + message);
		} else {
			if (priority == 0) { //Priority 0 / Just print, nothing important
				Main.ThreadAllocMain.getSWT().appendTextQueue(AWTANSI.D_White, message, noProtect);
			} else if (priority == 1) { //Priority 1 / Info, Progress, etc.
				Main.ThreadAllocMain.getSWT().appendTextQueue(AWTANSI.D_White, message, noProtect);
			} else if (priority == 2) { //Priority 2 / Warnings
				Main.ThreadAllocMain.getSWT().appendTextQueue(AWTANSI.D_Yellow, message, noProtect);
			} else if (priority == 3) { //Priority 3 / Non-Critical errors
				Main.ThreadAllocMain.getSWT().appendTextQueue(AWTANSI.D_Red, message, noProtect);
			} else if (priority == 4) { //Priority 4 / Critical errors
				Main.ThreadAllocMain.getSWT().appendTextQueue(AWTANSI.B_Red, message, noProtect);
			} else if (priority == 5) { //Priority 5 / Fatal or Non-recoverable errors
				Main.ThreadAllocMain.getSWT().appendTextQueue(AWTANSI.B_Red, message, noProtect);
			} else { //If priority out of range, choose default white
				Main.ThreadAllocMain.getSWT().appendTextQueue(AWTANSI.D_White, message, noProtect);
			}
		}
	}
	
	/**
	 * 
	 * @param color
	 * @param message
	 * @param noProtect
	 */
	public static void shellPrint(Color color, String message, boolean... noProtect) {
		if (main.Main.singleThreaded) {
			Main.mainFrameAWT.getCmdLine().setText(Main.mainFrameAWT.getCmdLine().getText() + message);
		} else {
			Main.ThreadAllocMain.getSWT().appendTextQueue(color, message, noProtect);
		}
	}
	public static void shellPrintln(Color color, String message, boolean... noProtect) {
		if (main.Main.singleThreaded) {
			Main.mainFrameAWT.getCmdLine().setText(Main.mainFrameAWT.getCmdLine().getText() + message);
		} else {
			Main.ThreadAllocMain.getSWT().appendTextQueue(color, message + "\n", noProtect);
		}
	}
	public static void shellPrint(String message) {
		Main.ThreadAllocMain.getSWT().appendTextQueue(AWTANSI.cReset, message);
	}
	public static void shellPrintln(String message) {
		Main.ThreadAllocMain.getSWT().appendTextQueue(AWTANSI.cReset, message + "\n");
	}
	/**
	 * Direct shellWrite when in single-threaded mode. More efficient than going through shellWriteThread.
	 * @param message
	 */
	public static void direct_shell_write(String message) {
		Main.mainFrameAWT.getCmdLine().setText(Main.mainFrameAWT.getCmdLine().getText() + message);
	}
	//==========================================SHELLWRITE END========================================
	//================================================================================================
	
	//========================================= LOGGING =========================================
	public static void log(String auth, int status, String message) {
		if (message != null)
			OpenLib.logWrite(auth, status, message + "\u001B[0m");
	}
	public static void log(String message) {
		if (message != null)
			OpenLib.logWrite("HIDDEN", 0, message + "\u001B[0m");
	}
	//========================================= LOGGING END =========================================
	
	public static void setShellMode(String shellMode) {
		if (shellMode == null) {
			System.err.println("Unable to change shellMode. new value equals null.");
			return;
		}
		if (shellMode.equals("NORMAL")
			|| shellMode.equals("LEGACY")
			|| shellMode.equals("NATIVE")) {
			LowLevel.setShellMode(shellMode);
		} else {
			System.err.println("Unable to change shellMode. new value is other than allowed ones.");
			System.out.println("Allowed shellModes: NORMAL, LEGACY, NATIVE");
		}
	}
	public static void setActivePhase(String phase) {
		if (phase == null) {
			System.err.println("Unable to change phase. new value equals null.");
			return;
		}
		if (phase.equals("pre-init")
			|| phase.equals("init")
			|| phase.equals("run")
			|| phase.equals("stop")
			|| phase.equals("err")) {
			System.out.println("--- CHANGING SHELL MODE TO '" + phase + "' ---");
			try { sys.log("SYS", 1, "--- CHANGING SHELL MODE TO '" + phase + "' ---"); }
			catch (Exception e) { System.err.println("Cannot print phase change to shell with logWrite()"); }
			LowLevel.setJDOSActivePhase(phase);
		} else {
			System.err.println("Unable to change phase. new value is other than allowed ones.");
			System.out.println("Allowed phases: pre-init, init, run, stop, err");
		}
		//Wait for state to fully change. Some unintentional issues will occur otherwise.
		try { Thread.sleep(500); } catch (InterruptedException ie) { ie.printStackTrace(); }
	}
	public static String getCurrentShellMode() {
		return LowLevel.shellMode;
	}
	public static String getActivePhase() {
		return LowLevel.getJDOSActivePhase();
	}
}
