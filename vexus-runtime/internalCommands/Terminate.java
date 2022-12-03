package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.ANSI;
import engine.sys;
import libraries.OpenLib;
import libraries.VarLib;

public class Terminate {
	public static String terminate(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params != null) && (params.size() >= 1) && (params.get(0).equalsIgnoreCase("force"))) {
			sys.log("TERMINATE", 2, "Forcing shutdown isn't recommended. Please consider using normal shutdown.");
			sys.log("TERMINATE", 0, "Exiting...");
			System.exit(0);
		} else {
			if (modules.WatchdogThread.isThreadAlive()) {
				sys.log("TERMINATE", 1, "Sending shutdown signal to WatchdogThread...");
				sys.shellPrintln(ANSI.B_Blue, "Exiting...");
				VarLib.setPromptPattern("$-");
				new modules.WatchdogThread().shutdownVexus(0);
			} else {
				sys.log("TERMINATE", 3, "Watchdog Thread not started! If this problem persists, please reinstall!");
				System.exit(1);
			}
		}
		return null;
	}
}