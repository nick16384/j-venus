package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import awtcomponents.AWTANSI;
import engine.LogLevel;
import engine.sys;
import jfxcomponents.ANSI;
import libraries.Global;
import main.Main;
import shell.Shell;
import threads.ThreadAllocation;

public class Terminate {
	public static String terminate(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params != null) && (params.size() >= 1) && (params.get(0).equalsIgnoreCase("--force"))) {
			sys.log("TERMINATE", LogLevel.WARN, "Forcing shutdown isn't recommended. Please consider using normal shutdown.");
			sys.log("TERMINATE", LogLevel.INFO, "Exiting...");
			System.exit(0);
		} else {
			if (ThreadAllocation.isWDTActive()) {
				sys.log("TERMINATE", LogLevel.INFO, "Sending shutdown signal to global Thread Allocator...");
				Shell.println(ANSI.B_Blue, "Exiting...");
				Shell.setPromptPattern("$-");
				sys.shutdown(0);
			} else {
				sys.log("TERMINATE", LogLevel.WARN, "Watchdog Thread not started! If this problem persists, please reinstall!");
				System.exit(1);
			}
		}
		return null;
	}
}