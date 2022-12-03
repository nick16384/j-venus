package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.HighLevel;
import main.Lib;

public class Terminate {
	public static String terminate(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params != null) && (params.get(0).equalsIgnoreCase("force"))) {
			Lib.logWrite("TERMINATE", 2, "Forcing shutdown isn't recommended. Please consider using normal shutdown.");
			Lib.logWrite("TERMINATE", 0, "Exiting...");
			System.exit(0);
		} else {
			if (modules.WatchdogThread.isThreadAlive()) {
				Lib.logWrite("TERMINATE", 1, "Sending shutdown signal to WatchdogThread...");
				HighLevel.shell_write(2, "HIDDEN", "Exiting...");
				Lib.setPrompt("");
				new modules.WatchdogThread().shutdownJavaDOS(0);
			} else {
				Lib.logWrite("TERMINATE", 3, "Watchdog Thread not started! If this problem persists, please reinstall!");
				System.exit(1);
			}
		}
		return null;
	}
}