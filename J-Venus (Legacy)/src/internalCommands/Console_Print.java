package internalCommands;
//Add this line to every internalCommands and remove those thingies: /* */

/*    */ import engine.sys;
import libraries.VarLib;

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
				sys.log("PRINT", 1, "Searching for environment variable '" + env + "'");
				if (VarLib.getEnv(env) != null) {
					sys.log("PRINT", 1, "Found! " + env + " -> " + VarLib.getEnv(env));
					word = word.replace(env, VarLib.getEnv(env));
				} else {
					sys.log("PRINT", 1, "Not found. Keeping String.");
				}
			}
			sys.shellPrint(word.replace("\\", "\"") + " ");
		}
     	return null;
	}
}