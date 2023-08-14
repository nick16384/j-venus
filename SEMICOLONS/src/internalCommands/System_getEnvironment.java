package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.sys;
import libraries.Env;
import libraries.Global;
import shell.Shell;

public class System_getEnvironment {
	public static String getEnv(ArrayList<String> params, Map<String, String> paramsWithValues) {
		String all = ""; //Final listed output
		Shell.print(1, "HIDDEN", "Listing... ");
		int count = 0;
		for (String key : Env.getFullEnv().keySet()) {
			all += key + " -> " + Env.getEnv(key) + "\n";
			count++;
		}
		Shell.print(1, "HIDDEN", count + " environment variables found.\n"
				+ all);
		return null;
	}
}
