package jfxcomponents;

import engine.InfoType;
import engine.sys;
import main.Main;
import threads.InternalThread;
import threads.ThreadAllocation;

public class JFxGUIThread {
	private static Thread jfxGUIThread;
	private static volatile boolean isGUIActive;
	
	public static void initialize() {
		jfxGUIThread = new Thread(null, () -> {
			sys.log("JFXT", InfoType.INFO, "Starting JFx GUI thread.");
			while (Main.jfxWinloader == null)
				try { Thread.sleep(50); } catch (InterruptedException ie) { ie.printStackTrace(); }
			
			sys.log("JFXT", InfoType.INFO, "Launching JavaFX GUI...");
			isGUIActive = true;
			Main.jfxWinloader.loadGUI(Main.argsMain);
			//loadGUI() will not return until window is closed or Platform.exit() is called.
			isGUIActive = false;
			sys.log("JFXT", InfoType.INFO, "JavaFX window was closed. Stopping SEMICOLONS.");
			sys.shutdown(0);
		}, "JFXT");
		
		jfxGUIThread.setDaemon(true);
		jfxGUIThread.start();
	}
	
	public static void suspend() {
		Main.jfxWinloader.stop();
	}
	
	public static boolean isRunning() {
		return jfxGUIThread != null && jfxGUIThread.isAlive();
	}
	
	public static boolean isGUIActive() {
		return isGUIActive;
	}
}
