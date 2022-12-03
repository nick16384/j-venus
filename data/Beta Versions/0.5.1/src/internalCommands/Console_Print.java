package internalCommands;

import engine.HighLevel;

public class Console_Print {
	public static String print(String[] reqParams, String[] optParams) {
		if (reqParams == null || reqParams.length == 0) {
			System.err.println("INTERN.CONSOLE.PRINT: reqParam Parse Error");
			return "reqParamParseError";
		}
		
		engine.HighLevel.shell_write(1, "HIDDEN", reqParams[0]);
		
		return null;
	}
}
