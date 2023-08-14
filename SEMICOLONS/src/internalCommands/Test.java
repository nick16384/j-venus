package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.sys;
import main.Main;
import shell.Shell;
import threads.ThreadAllocation;

public class Test {
	public static String test(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params != null) && (params.size() >= 1) && (params.get(0).equalsIgnoreCase("all"))) {
			Shell.print(1, "TEST", "Performing full test... \n");
			Shell.print(1, "TEST", "Text normal, priority 1 (white) \n");
			Shell.print(2, "TEST", "Text priority 2 (green + yellow) \n");
			Shell.print(3, "TEST", "Text priority 3 or above (yellow) \n");
			Shell.print(4, "TEST", "Text priority 4 (orange) \n");
			Shell.print(5, "TEST", "Text priority 5 (red) \n");
			if (ThreadAllocation.isWDTActive()) {
				Shell.print(2, "TEST", "Watchdog Thread 1 active \n");
			} else {
				Shell.print(4, "TEST", "Warning: Watchdog Thread 1 not running! \n");
				Shell.print(4, "TEST", "This could be an internal problem with JavaDOS. \n");
				Shell.print(4, "TEST", "Restarting JavaDOS is highly recommended! ('reset -full') \n");
				Shell.print(4, "TEST", "If the problem persists after restart, consider reinstalling JavaDOS. \n");
				return "Watchdog1Inactive";
			}
			if (ThreadAllocation.isWDT2Active()) {
				Shell.print(2, "TEST", "Watchdog Thread 2 active \n");
			} else {
				Shell.print(4, "TEST", "Warning: Watchdog Thread 2 not running! \n");
				Shell.print(4, "TEST", "This could be an internal problem with JavaDOS. \n");
				Shell.print(4, "TEST", "Restarting JavaDOS is highly recommended! ('reset -full') \n");
				Shell.print(4, "TEST", "If the problem persists after restart, consider reinstalling JavaDOS. \n");
				return "Watchdog2Inactive";
			}
		} else {
			Shell.print(1, "TEST", "Text normal, priority 1 (white) \n");
			Shell.print(2, "TEST", "Text priority 2 (green + yellow) \n");
			Shell.print(3, "TEST", "Text priority 3 or above (yellow) \n");
			Shell.print(4, "TEST", "Text priority 4 (orange) \n");
			Shell.print(5, "TEST", "Text priority 5 (red) \n");
		}
		
		Shell.print(1, "TEST", "Test done. \n");
		Shell.print(2, "TEST", "\\/ Test error sent \\/");
		
		return "TestError";
	}
}
