package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import awtcomponents.AWTANSI;
import engine.InfoType;
import engine.sys;
import libraries.Global;
import main.Main;
import shell.Shell;
import threads.ThreadAllocation;

public class Terminate {
	public static String terminate(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params != null) && (params.size() >= 1) && (params.get(0).equalsIgnoreCase("--force"))) {
			sys.log("TERMINATE", InfoType.WARN, "Forcing shutdown isn't recommended. Please consider using normal shutdown.");
			sys.log("TERMINATE", InfoType.INFO, "Exiting...");
			System.exit(0);
		} else {
			if (ThreadAllocation.isWDTActive()) {
				sys.log("TERMINATE", InfoType.INFO, "Sending shutdown signal to global Thread Allocator...");
				Shell.println(AWTANSI.B_Blue, "Exiting...");
				Shell.setPromptPattern("$-");
				sys.shutdown(0);
			} else {
				sys.log("TERMINATE", InfoType.WARN, "Watchdog Thread not started! If this problem persists, please reinstall!");
				System.exit(1);
			}
		}
		return null;
	}
}