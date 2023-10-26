package threads;

import commands.CommandManagement;
import engine.LogLevel;
import engine.sys;
import jfxcomponents.JFxGUIThread;

/**
 * This class is responsible for managing threads. (not AWT ones!)
 */
public class ThreadAllocation {
	
	public static void launchAll() {
		sys.log("THREAD-ALLOC", LogLevel.INFO, "Launching all internal threads...");
		if (WatchdogThread.isRunning())
		{ sys.log("THREAD-ALLOC", LogLevel.WARN, "WatchdogThread already running."); }
		else { WatchdogThread.initialize(); }
		
		if (WatchdogThread2.isRunning())
		{ sys.log("THREAD-ALLOC", LogLevel.WARN, "WatchdogThread2 already running."); }
		else { WatchdogThread2.initialize(); }
		
		if (ShellWriteThread.isRunning())
		{ sys.log("THREAD-ALLOC", LogLevel.WARN, "ShellWriteThread already running."); }
		else { ShellWriteThread.initialize(); }
		
		if (CommandManagement.isRunning())
		{ sys.log("THREAD-ALLOC", LogLevel.WARN, "Command Manager Thread already running."); }
		else { CommandManagement.initialize(); }
		
		if (JFxGUIThread.isRunning())
		{ sys.log("THREAD-ALLOC", LogLevel.WARN, "JavaFX GUI Thread already running."); }
		else { JFxGUIThread.initialize(); }
	}
	
	public static boolean isWDTActive()  { return WatchdogThread.isRunning(); }
	public static boolean isWDT2Active() { return WatchdogThread2.isRunning(); }
	public static boolean isSWTActive()  { return ShellWriteThread.isRunning(); }
	public static boolean isCMGRActive() { return CommandManagement.isRunning(); }
	public static boolean isJFXTActive() { return JFxGUIThread.isRunning(); }
}
