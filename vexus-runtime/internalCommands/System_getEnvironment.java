package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.sys;
import libraries.VarLib;

public class System_getEnvironment {
	public static String getEnv(ArrayList<String> params, Map<String, String> paramsWithValues) {
		String all = ""; //Final listed output
		sys.shellPrint(1, "HIDDEN", "Listing... ");
		int count = 0;
		for (String key : VarLib.getFullEnv().keySet()) {
			all += key + " -> " + VarLib.getEnv(key) + "\n";
			count++;
		}
		sys.shellPrint(1, "HIDDEN", count + " environment variables found.\n");
		return all;
	}
}
