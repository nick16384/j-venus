package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.sys;
import libraries.VarLib;
import engine.ANSI;

/**
 * prints debug information e.g. execThread status, Threads, Variables, etc.
 * @author theophil
 *
 */

public class System_Debug {
	public static String debug(ArrayList<String> params, Map<String, String> paramsWithValues) {
		String rtVal = "OK"; //Return value (overall status)
		
		sys.shellPrint(ANSI.D_Cyan, "Debug information:\n" + "Directories:\n");
		sys.shellPrint(ANSI.D_Yellow,
		  "\tCurrent        : " + VarLib.getCurrentDir() + "\n"
		+ "\tVexus root     : " + VarLib.getDefaultDir().getAbsolutePath() + "\n"
		+ "\tVexus data     : " + VarLib.getDataDir().getAbsolutePath() + "\n"
		+ "\tVexus binary   : " + VarLib.getBinDir().getAbsolutePath() + "\n"
		+ "\tVexus temporary: " + VarLib.getTempDir().getAbsolutePath() + "\n"
		+ "\tFilesystem root: " + VarLib.getFSRoot() + "\n"
		+ "\tJava Home      : " + VarLib.getJavaHome().getAbsolutePath() + "\n");
		sys.shellPrint(ANSI.D_Cyan, "Other files:\n");
		sys.shellPrint(ANSI.D_Yellow,
				  "\tJava executable: " + VarLib.getJavaExec().getAbsolutePath() + "\n"
				+ "\tLog file       : " + VarLib.getLogFile().getAbsolutePath() + "\n");
		sys.shellPrint(ANSI.D_Cyan, "J-Vexus Status:\n");
		sys.shellPrint(ANSI.D_Yellow, "\tRunning phase: ");
		if (sys.getActivePhase().equals("run"))
			sys.shellPrint(ANSI.D_Green, "RUN\n");
		else
			sys.shellPrint(ANSI.B_Red, sys.getActivePhase().toUpperCase() + "\n");
		
		sys.shellPrint(ANSI.D_Yellow, "\tShell mode: ");
		if (sys.getCurrentShellMode().equals("normal"))
			sys.shellPrint(ANSI.D_Green, "NORMAL\n");
		else
			sys.shellPrint(ANSI.B_Red, sys.getCurrentShellMode().toUpperCase() + "\n");
		
		sys.shellPrint(ANSI.D_Yellow, "\tWatchdog 1 status: ");
		if (modules.WatchdogThread.isThreadAlive() == true)
			sys.shellPrint(ANSI.D_Green, "ACTIVE\n");
		else
			sys.shellPrint(ANSI.B_Red, "INACTIVE\n");
		
		sys.shellPrint(ANSI.D_Yellow, "\tWatchdog 2 status: ");
		if (modules.WatchdogThread2.isWatchdogThread2Alive() == true)
			sys.shellPrint(ANSI.D_Green, "ACTIVE\n");
		else
			sys.shellPrint(ANSI.B_Red, "INACTIVE\n");
		
		return rtVal;
	}
}
