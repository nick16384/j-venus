package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.HighLevel;

public class Console_Print {
	public static String print(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if (params == null || params.size() == 0) {
			System.err.println("INTERN.CONSOLE.PRINT: reqParam Parse Error");
			return "reqParamParseError";
		}
		
		engine.HighLevel.shell_write(1, "HIDDEN", params.get(0));
		
		return null;
	}
}
