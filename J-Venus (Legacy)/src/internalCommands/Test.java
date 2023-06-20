package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.sys;
import main.Main;

public class Test {
	public static String test(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params != null) && (params.size() >= 1) && (params.get(0).equalsIgnoreCase("all"))) {
			sys.shellPrint(1, "TEST", "Performing full test... \n");
			sys.shellPrint(1, "TEST", "Text normal, priority 1 (white) \n");
			sys.shellPrint(2, "TEST", "Text priority 2 (green + yellow) \n");
			sys.shellPrint(3, "TEST", "Text priority 3 or above (yellow) \n");
			sys.shellPrint(4, "TEST", "Text priority 4 (orange) \n");
			sys.shellPrint(5, "TEST", "Text priority 5 (red) \n");
			if (Main.ThreadAllocMain.isWDTActive()) {
				sys.shellPrint(2, "TEST", "Watchdog Thread 1 active \n");
			} else {
				sys.shellPrint(4, "TEST", "Warning: Watchdog Thread 1 not running! \n");
				sys.shellPrint(4, "TEST", "This could be an internal problem with JavaDOS. \n");
				sys.shellPrint(4, "TEST", "Restarting JavaDOS is highly recommended! ('reset -full') \n");
				sys.shellPrint(4, "TEST", "If the problem persists after restart, consider reinstalling JavaDOS. \n");
				return "Watchdog1Inactive";
			}
			if (Main.ThreadAllocMain.isWDT2Active()) {
				sys.shellPrint(2, "TEST", "Watchdog Thread 2 active \n");
			} else {
				sys.shellPrint(4, "TEST", "Warning: Watchdog Thread 2 not running! \n");
				sys.shellPrint(4, "TEST", "This could be an internal problem with JavaDOS. \n");
				sys.shellPrint(4, "TEST", "Restarting JavaDOS is highly recommended! ('reset -full') \n");
				sys.shellPrint(4, "TEST", "If the problem persists after restart, consider reinstalling JavaDOS. \n");
				return "Watchdog2Inactive";
			}
		} else {
			sys.shellPrint(1, "TEST", "Text normal, priority 1 (white) \n");
			sys.shellPrint(2, "TEST", "Text priority 2 (green + yellow) \n");
			sys.shellPrint(3, "TEST", "Text priority 3 or above (yellow) \n");
			sys.shellPrint(4, "TEST", "Text priority 4 (orange) \n");
			sys.shellPrint(5, "TEST", "Text priority 5 (red) \n");
		}
		
		sys.shellPrint(1, "TEST", "Test done. \n");
		sys.shellPrint(2, "TEST", "\\/ Test error sent \\/");
		
		return "TestError";
	}
}
