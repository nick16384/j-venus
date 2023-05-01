package threads;

import commandProcessing.CommandManager;
import engine.sys;
import jfx.windowManager.JFxGUIThread;

/**
 * This class is responsible for managing threads. (not AWT ones!)
 */
public class ThreadAllocator {
	private final WatchdogThread WDT;
	private final WatchdogThread2 WDT2;
	private final ShellWriteThread SWT;
	private final CheckUserInputThread CUIT;
	private final CommandManager CMGR;
	private final JFxGUIThread JFXT;
	//TODO Add CMGR
	
	public ThreadAllocator() {
		sys.log("THREAD-ALLOC", 1, "Initializing thread objects.");
		WDT = new WatchdogThread();
		WDT2 = new WatchdogThread2();
		SWT = new ShellWriteThread();
		CUIT = new CheckUserInputThread();
		CMGR = new CommandManager();
		JFXT = new JFxGUIThread();
	}
	
	public void launchAll() {
		sys.log("THREAD-ALLOC", 1, "Launching all vexus-internal threads...");
		if (WDT.isRunning())  { sys.log("THREAD-ALLOC", 2, "WatchdogThread already running."); }
		else { WDT.start(); }
		if (WDT2.isRunning()) { sys.log("THREAD-ALLOC", 2, "WatchdogThread2 already running."); }
		else { WDT2.start(); }
		if (SWT.isRunning())  { sys.log("THREAD-ALLOC", 2, "ShellWriteThread already running."); }
		else { SWT.start(); }
		if (CUIT.isRunning()) { sys.log("THREAD-ALLOC", 2, "CheckUserInputThread already running."); }
		else { CUIT.start(); }
		if (CMGR.isRunning()) { sys.log("THREAD-ALLOC", 2, "Command Manager Thread already running."); }
		else { CMGR.start(); }
		if (JFXT.isRunning()) { sys.log("THREAD-ALLOC", 2, "JavaFX GUI Thread already running."); }
		else { JFXT.start(); }
	}
	
	public WatchdogThread getWDT()        { return this.WDT; }
	public WatchdogThread2 getWDT2()      { return this.WDT2; }
	public ShellWriteThread getSWT()      { return this.SWT; }
	public CheckUserInputThread getCUIT() { return this.CUIT; }
	public CommandManager getCMGR()       { return this.CMGR; }
	public JFxGUIThread getJFXT()         { return this.JFXT; }
	
	public boolean isWDTActive()  { return WDT.isRunning(); }
	public boolean isWDT2Active() { return WDT2.isRunning(); }
	public boolean isSWTActive()  { return SWT.isRunning(); }
	public boolean isCUITActive() { return CUIT.isRunning(); }
	public boolean isCMGRActive() { return CMGR.isRunning(); }
	public boolean isJFXTActive() { return JFXT.isRunning(); }
	
	public void shutdownVexus(int exitCode) {
		sys.log("THREAD-ALLOC", 1, "Calling WDT thread(s) shutdown.");
		WDT.shutdownVexus(exitCode);
	}
	
	public boolean isShutdownSignalActive() {
		return WDT.isShutdownSignalActive();
	}
}
