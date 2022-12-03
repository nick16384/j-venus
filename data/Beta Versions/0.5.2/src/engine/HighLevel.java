package engine;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class HighLevel {
	public static Object[] commandSplitArray(String fullCommand) {
		String command = null;
		ArrayList<String> params = new ArrayList<String>();
		Map<String, String> paramsWithValues = new HashMap<String, String>();
		
		command = fullCommand.split(" ")[0];
		fullCommand = fullCommand.replaceFirst(command + " ", "");
		
		for (String cmdPart : fullCommand.split(" ")) {
			if (cmdPart.startsWith("--")) {
				paramsWithValues.put(cmdPart.trim().replaceFirst("--", "").split(":")[0], cmdPart.split(":")[1]);
			} else if (cmdPart.startsWith("-")) {
				params.add(cmdPart.trim().replaceFirst("-", ""));
			} else if (cmdPart.startsWith("/")) {
				params.add(cmdPart.trim().replaceFirst("/", ""));
			} else {
				params.add(cmdPart.trim());
			}
		}
		
		System.out.println("Command: " + command);
		for (String param : params) { System.out.println("Parameter: " + param); }
		for (String paramKey : paramsWithValues.keySet()) {
			System.out.println("Parameter key : " + paramKey + ", value: " + paramsWithValues.get(paramKey));
		}
		
		if (command.isBlank()) {
			command = null;
		}
		if (params.isEmpty()) {
			params = null;
		}
		if (paramsWithValues.isEmpty()) {
			paramsWithValues = null;
		}
		
		final Object[] cmdContent = new Object[] {command, params, paramsWithValues};
		return cmdContent;
	}
	
	//TODO public boolean requestLLmethod (String methodName, ...) {}
	public static void shell_write(int priority, String auth, String message) {
		LowLevel.shell_write(priority, auth, message);
	}
}
