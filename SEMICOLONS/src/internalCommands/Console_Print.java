package internalCommands;
//Add this line to every internalCommands and remove those thingies: /* */

import engine.LogLevel;
/*    */ import engine.sys;
import libraries.Env;
import libraries.Global;
import shell.Shell;

/*    */ import java.util.ArrayList;
/*    */ import java.util.Map;


public class Console_Print {
	public static String print(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if (params == null || params.size() == 0) {
			return "reqParamParseError";
		}
		
		for (String word : params) {
			if (word.contains("$")) {
				String env = "$" + word.split("\\$")[1].split(" ")[0];
				sys.log("PRINT", LogLevel.DEBUG, "Searching for environment variable '" + env + "'");
				if (Env.getEnv(env) != null) {
					sys.log("PRINT", LogLevel.DEBUG, "Found! " + env + " -> " + Env.getEnv(env));
					word = word.replace(env, Env.getEnv(env));
				} else {
					sys.log("PRINT", LogLevel.DEBUG, "Not found. Keeping String.");
				}
			}
			Shell.print(word.replace("\\", "\"") + " ");
		}
     	return null;
	}
}