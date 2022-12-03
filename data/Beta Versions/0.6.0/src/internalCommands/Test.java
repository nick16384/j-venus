package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.HighLevel;

public class Test {
	public static String test(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params != null) && (params.size() >= 1) && (params.get(0).equalsIgnoreCase("all"))) {
			HighLevel.shell_write(1, "TEST", "Performing full test... \n");
			HighLevel.shell_write(1, "TEST", "Text normal, priority 1 (white) \n");
			HighLevel.shell_write(2, "TEST", "Text priority 2 (green + yellow) \n");
			HighLevel.shell_write(3, "TEST", "Text priority 3 or above (yellow) \n");
			HighLevel.shell_write(4, "TEST", "Text priority 4 (orange) \n");
			HighLevel.shell_write(5, "TEST", "Text priority 5 (red) \n");
			if (modules.WatchdogThread.isThreadAlive()) {
				HighLevel.shell_write(2, "TEST", "Watchdog Thread 1 active \n");
			} else {
				HighLevel.shell_write(4, "TEST", "Warning: Watchdog Thread 1 not running! \n");
				HighLevel.shell_write(4, "TEST", "This could be an internal problem with JavaDOS. \n");
				HighLevel.shell_write(4, "TEST", "Restarting JavaDOS is highly recommended! ('reset -full') \n");
				HighLevel.shell_write(4, "TEST", "If the problem persists after restart, consider reinstalling JavaDOS. \n");
				return "Watchdog1Inactive";
			}
			if (modules.WatchdogThread2.isWatchdogThread2Alive()) {
				HighLevel.shell_write(2, "TEST", "Watchdog Thread 2 active \n");
			} else {
				HighLevel.shell_write(4, "TEST", "Warning: Watchdog Thread 2 not running! \n");
				HighLevel.shell_write(4, "TEST", "This could be an internal problem with JavaDOS. \n");
				HighLevel.shell_write(4, "TEST", "Restarting JavaDOS is highly recommended! ('reset -full') \n");
				HighLevel.shell_write(4, "TEST", "If the problem persists after restart, consider reinstalling JavaDOS. \n");
				return "Watchdog2Inactive";
			}
		} else {
			HighLevel.shell_write(1, "TEST", "Text normal, priority 1 (white) \n");
			HighLevel.shell_write(2, "TEST", "Text priority 2 (green + yellow) \n");
			HighLevel.shell_write(3, "TEST", "Text priority 3 or above (yellow) \n");
			HighLevel.shell_write(4, "TEST", "Text priority 4 (orange) \n");
			HighLevel.shell_write(5, "TEST", "Text priority 5 (red) \n");
		}
		
		HighLevel.shell_write(1, "TEST", "Test done. \n");
		HighLevel.shell_write(2, "TEST", "\\/ Test error sent \\/");
		
		return "TestError";
	}
}
