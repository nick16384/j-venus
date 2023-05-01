package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.sys;
import libraries.VarLib;
import main.Main;
import engine.AWTANSI;

/**
 * prints debug information e.g. execThread status, Threads, Variables, etc.
 * @author theophil
 *
 */

public class System_Debug {
	public static String debug(ArrayList<String> params, Map<String, String> paramsWithValues) {
		String rtVal = "OK"; //Return value (overall status)
		
		sys.shellPrint(AWTANSI.D_Cyan, "Debug information:\n" + "Directories:\n");
		sys.shellPrint(AWTANSI.D_Yellow,
		  "\tCurrent        : " + VarLib.getCurrentDir() + "\n"
		+ "\tVexus root     : " + VarLib.getDefaultDir().getAbsolutePath() + "\n"
		+ "\tVexus data     : " + VarLib.getDataDir().getAbsolutePath() + "\n"
		+ "\tVexus binary   : " + VarLib.getBinDir().getAbsolutePath() + "\n"
		+ "\tVexus temporary: " + VarLib.getTempDir().getAbsolutePath() + "\n"
		+ "\tFilesystem root: " + VarLib.getFSRoot() + "\n"
		+ "\tJava Home      : " + VarLib.getJavaHome().getAbsolutePath() + "\n");
		sys.shellPrint(AWTANSI.D_Cyan, "Other files:\n");
		sys.shellPrint(AWTANSI.D_Yellow,
				  "\tJava executable: " + VarLib.getJavaExec().getAbsolutePath() + "\n"
				+ "\tLog file       : " + VarLib.getLogFile().getAbsolutePath() + "\n");
		sys.shellPrint(AWTANSI.D_Cyan, "J-Vexus Status:\n");
		sys.shellPrint(AWTANSI.D_Yellow, "\tRunning phase: ");
		if (sys.getActivePhase().equals("run"))
			sys.shellPrint(AWTANSI.D_Green, "RUN\n");
		else
			sys.shellPrint(AWTANSI.B_Red, sys.getActivePhase().toUpperCase() + "\n");
		
		sys.shellPrint(AWTANSI.D_Yellow, "\tShell mode: ");
		if (sys.getCurrentShellMode().equals("normal"))
			sys.shellPrint(AWTANSI.D_Green, "NORMAL\n");
		else
			sys.shellPrint(AWTANSI.B_Red, sys.getCurrentShellMode().toUpperCase() + "\n");
		
		printThreadStatuses();
		
		return rtVal;
	}
	
	private static void printThreadStatuses() {
		//Watchdog Thread 1
		sys.shellPrint(AWTANSI.D_Yellow, "\tWatchdog 1 [WDT] status: ");
		if (Main.ThreadAllocMain.isWDTActive())
			sys.shellPrint(AWTANSI.D_Green, "ACTIVE\n");
		else
			sys.shellPrint(AWTANSI.B_Red, "INACTIVE\n");
		
		//Watchdog Thread 2
		sys.shellPrint(AWTANSI.D_Yellow, "\tWatchdog 2 [WDT2] status: ");
		if (Main.ThreadAllocMain.isWDT2Active())
			sys.shellPrint(AWTANSI.D_Green, "ACTIVE\n");
		else
			sys.shellPrint(AWTANSI.B_Red, "INACTIVE\n");
		
		//Shell Write Thread
		sys.shellPrint(AWTANSI.D_Yellow, "\tShell Write Thread [SWT] status: ");
		if (Main.ThreadAllocMain.isSWTActive())
			sys.shellPrint(AWTANSI.D_Green, "ACTIVE\n");
		else
			sys.shellPrint(AWTANSI.B_Red, "INACTIVE\n");
		
		//Check User Input Thread
		sys.shellPrint(AWTANSI.D_Yellow, "\tCheck User Input Thread [CUIT] status: ");
		if (Main.ThreadAllocMain.isCUITActive())
			sys.shellPrint(AWTANSI.D_Green, "ACTIVE\n");
		else
			sys.shellPrint(AWTANSI.B_Red, "INACTIVE\n");
		
		//Command Manager Thread
		sys.shellPrint(AWTANSI.D_Yellow, "\tCommand Manager Thread [CMGR] status: ");
		if (Main.ThreadAllocMain.isCMGRActive())
			sys.shellPrint(AWTANSI.D_Green, "ACTIVE\n");
		else
			sys.shellPrint(AWTANSI.B_Red, "INACTIVE\n");
	}
}
