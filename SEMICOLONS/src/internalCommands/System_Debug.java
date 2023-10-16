package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.Runphase;
import engine.sys;
import jfxcomponents.ANSI;
import libraries.Global;
import main.Main;
import shell.Shell;
import threads.ThreadAllocation;

/**
 * prints debug information e.g. execThread status, Threads, Variables, etc.
 * @author theophil
 *
 */

public class System_Debug {
	public static String debug(ArrayList<String> params, Map<String, String> paramsWithValues) {
		String rtVal = "OK"; //Return value (overall status)
		
		Shell.print(ANSI.D_Cyan, "Debug information:\n");
		Shell.print(ANSI.D_Cyan, "JVM Process PID: " + ProcessHandle.current().pid() + "\n");
		Shell.print(ANSI.D_Cyan, "Directories:\n");
		Shell.print(ANSI.D_Yellow,
		  "\tCurrent         : " + Global.getCurrentDir() + "\n"
		+ "\troot directory  : " + Global.getRootDir().getAbsolutePath() + "\n"
		+ "\tdata directory  : " + Global.getDataDir().getAbsolutePath() + "\n"
		+ "\tbinary directory: " + Global.getBinDir().getAbsolutePath() + "\n"
		+ "\ttemp. directory : " + Global.getTempDir().getAbsolutePath() + "\n"
		+ "\tFilesystem root : " + Global.getFSRoot() + "\n"
		+ "\tJava Home       : " + Global.getJavaHome().getAbsolutePath() + "\n");
		Shell.print(ANSI.D_Cyan, "Other files:\n");
		Shell.print(ANSI.D_Yellow,
				  "\tJava executable: " + Global.getJavaExec().getAbsolutePath() + "\n"
				+ "\tLog file       : " + Global.getLogFile().getAbsolutePath() + "\n");
		Shell.print(ANSI.D_Cyan, "J-Vexus Status:\n");
		Shell.print(ANSI.D_Yellow, "\tRunning phase: ");
		if (Global.getCurrentPhase().equals(Runphase.RUN))
			Shell.print(ANSI.D_Green, "RUN\n");
		else
			Shell.print(ANSI.B_Red, Global.getCurrentPhase().toString() + "\n");
		
		printThreadStatuses();
		
		return rtVal;
	}
	
	private static void printThreadStatuses() {
		//Watchdog Thread 1
		Shell.print(ANSI.D_Yellow, "\tWatchdog 1 [WDT] status: ");
		if (ThreadAllocation.isWDTActive())
			Shell.print(ANSI.D_Green, "ACTIVE\n");
		else
			Shell.print(ANSI.B_Red, "INACTIVE\n");
		
		//Watchdog Thread 2
		Shell.print(ANSI.D_Yellow, "\tWatchdog 2 [WDT2] status: ");
		if (ThreadAllocation.isWDT2Active())
			Shell.print(ANSI.D_Green, "ACTIVE\n");
		else
			Shell.print(ANSI.B_Red, "INACTIVE\n");
		
		//Shell Write Thread
		Shell.print(ANSI.D_Yellow, "\tShell Write Thread [SWT] status: ");
		if (ThreadAllocation.isSWTActive())
			Shell.print(ANSI.D_Green, "ACTIVE\n");
		else
			Shell.print(ANSI.B_Red, "INACTIVE\n");
		
		//Command Manager Thread
		Shell.print(ANSI.D_Yellow, "\tCommand Manager Thread [CMGR] status: ");
		if (ThreadAllocation.isCMGRActive())
			Shell.print(ANSI.D_Green, "ACTIVE\n");
		else
			Shell.print(ANSI.B_Red, "INACTIVE\n");
	}
}
