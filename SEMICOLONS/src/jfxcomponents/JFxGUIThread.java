package jfxcomponents;

import engine.LogLevel;
import engine.sys;
import main.Main;
import threads.ThreadAllocation;

public class JFxGUIThread {
	private static Thread jfxGUIThread;
	private static volatile boolean isGUIActive;
	
	public static void initialize() {
		jfxGUIThread = new Thread(null, () -> {
			sys.log("JFXT", LogLevel.INFO, "Starting JFx GUI thread.");
			/*while (GUIManager == null)
				try { Thread.sleep(50); } catch (InterruptedException ie) { ie.printStackTrace(); }*/
			
			sys.log("JFXT", LogLevel.INFO, "Launching JavaFX GUI...");
			isGUIActive = true;
			GUIManager.loadGUI();
			//loadGUI() will not return until window is closed or Platform.exit() is called.
			isGUIActive = false;
			sys.log("JFXT", LogLevel.INFO, "JavaFX window was closed. Stopping SEMICOLONS.");
			sys.shutdown(0);
		}, "JFXT");
		
		jfxGUIThread.setDaemon(true);
		jfxGUIThread.start();
	}
	
	public static boolean isRunning() {
		return jfxGUIThread != null && jfxGUIThread.isAlive();
	}
	
	public static boolean isGUIActive() {
		return isGUIActive;
	}
}
