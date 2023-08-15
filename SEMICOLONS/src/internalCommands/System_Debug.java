package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import awtcomponents.AWTANSI;
import engine.Runphase;
import engine.sys;
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
		
		Shell.print(AWTANSI.D_Cyan, "Debug information:\n");
		Shell.print(AWTANSI.D_Cyan, "JVM Process PID: " + ProcessHandle.current().pid() + "\n");
		Shell.print(AWTANSI.D_Cyan, "Directories:\n");
		Shell.print(AWTANSI.D_Yellow,
		  "\tCurrent        : " + Global.getCurrentDir() + "\n"
		+ "\tVexus root     : " + Global.getDefaultDir().getAbsolutePath() + "\n"
		+ "\tVexus data     : " + Global.getDataDir().getAbsolutePath() + "\n"
		+ "\tVexus binary   : " + Global.getBinDir().getAbsolutePath() + "\n"
		+ "\tVexus temporary: " + Global.getTempDir().getAbsolutePath() + "\n"
		+ "\tFilesystem root: " + Global.getFSRoot() + "\n"
		+ "\tJava Home      : " + Global.getJavaHome().getAbsolutePath() + "\n");
		Shell.print(AWTANSI.D_Cyan, "Other files:\n");
		Shell.print(AWTANSI.D_Yellow,
				  "\tJava executable: " + Global.getJavaExec().getAbsolutePath() + "\n"
				+ "\tLog file       : " + Global.getLogFile().getAbsolutePath() + "\n");
		Shell.print(AWTANSI.D_Cyan, "J-Vexus Status:\n");
		Shell.print(AWTANSI.D_Yellow, "\tRunning phase: ");
		if (Global.getCurrentPhase().equals(Runphase.RUN))
			Shell.print(AWTANSI.D_Green, "RUN\n");
		else
			Shell.print(AWTANSI.B_Red, Global.getCurrentPhase().toString() + "\n");
		
		printThreadStatuses();
		
		return rtVal;
	}
	
	private static void printThreadStatuses() {
		//Watchdog Thread 1
		Shell.print(AWTANSI.D_Yellow, "\tWatchdog 1 [WDT] status: ");
		if (ThreadAllocation.isWDTActive())
			Shell.print(AWTANSI.D_Green, "ACTIVE\n");
		else
			Shell.print(AWTANSI.B_Red, "INACTIVE\n");
		
		//Watchdog Thread 2
		Shell.print(AWTANSI.D_Yellow, "\tWatchdog 2 [WDT2] status: ");
		if (ThreadAllocation.isWDT2Active())
			Shell.print(AWTANSI.D_Green, "ACTIVE\n");
		else
			Shell.print(AWTANSI.B_Red, "INACTIVE\n");
		
		//Shell Write Thread
		Shell.print(AWTANSI.D_Yellow, "\tShell Write Thread [SWT] status: ");
		if (ThreadAllocation.isSWTActive())
			Shell.print(AWTANSI.D_Green, "ACTIVE\n");
		else
			Shell.print(AWTANSI.B_Red, "INACTIVE\n");
		
		//Command Manager Thread
		Shell.print(AWTANSI.D_Yellow, "\tCommand Manager Thread [CMGR] status: ");
		if (ThreadAllocation.isCMGRActive())
			Shell.print(AWTANSI.D_Green, "ACTIVE\n");
		else
			Shell.print(AWTANSI.B_Red, "INACTIVE\n");
	}
}
