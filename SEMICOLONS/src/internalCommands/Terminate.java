package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import awtcomponents.AWTANSI;
import engine.sys;
import libraries.Global;
import main.Main;

public class Terminate {
	public static String terminate(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params != null) && (params.size() >= 1) && (params.get(0).equalsIgnoreCase("--force"))) {
			sys.log("TERMINATE", 2, "Forcing shutdown isn't recommended. Please consider using normal shutdown.");
			sys.log("TERMINATE", 0, "Exiting...");
			System.exit(0);
		} else {
			if (Main.ThreadAllocMain.isWDTActive()) {
				sys.log("TERMINATE", 1, "Sending shutdown signal to global Thread Allocator...");
				sys.shellPrintln(AWTANSI.B_Blue, "Exiting...");
				Global.setPromptPattern("$-");
				Main.ThreadAllocMain.shutdownVexus(0);
			} else {
				sys.log("TERMINATE", 3, "Watchdog Thread not started! If this problem persists, please reinstall!");
				System.exit(1);
			}
		}
		return null;
	}
}