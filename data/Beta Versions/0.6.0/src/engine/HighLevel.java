package engine;

import java.util.Map;

import javax.swing.text.StyledDocument;

import java.util.ArrayList;
import java.util.HashMap;

import main.Lib;

public class HighLevel {
	
		
		//OLD CODE
		/**/
	
	//TODO public boolean requestLLmethod (String methodName, ...) {}
	public static void shell_write(int priority, String auth, String message, boolean... noProtect) {
		//LowLevel.shell_write(priority, auth, message);
		modules.ShellWriteThread.shell_write(message, noProtect);
	}
	public static void setShellMode(String shellMode) {
		LowLevel.setShellMode(shellMode);
	}
	public static void setActiveJDOSPhase(String phase) {
		LowLevel.setJDOSActivePhase(phase);
	}
	public static String getCurrentShellMode() {
		return LowLevel.shellMode;
	}
	public static String getActiveJDOSPhase() {
		return LowLevel.getJDOSActivePhase();
	}
}
