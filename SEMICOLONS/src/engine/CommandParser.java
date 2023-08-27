package engine;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import commandProcessing.Command;
import commandProcessing.EnvVarParser;

public class CommandParser {
	//Add second command (after |) to execution queue while executing first one
	private static Command[] executionQueue = new Command[0];
	
	public static synchronized Object[] parseCmd(String fullCmd) {
		String command = "";
		ArrayList<String> params = new ArrayList<>();
		
		//Strings in fullCmd like "some parameter" which are one, but separated by spaces
		ArrayList<String> quotedSubStrs = new ArrayList<>();
		
		if (fullCmd.contains(" ")) {
			command = fullCmd.split(" ")[0];
			fullCmd = fullCmd.substring(command.length() + 1);
		} else {
			command = fullCmd;
			fullCmd = "";
		}
		
		//Counts number of quotes in fullCommand
		Pattern pattern = Pattern.compile("[^\"]*\"");
		Matcher matcher = pattern.matcher(fullCmd);
		int quoteCount = 0;
		while (matcher.find()) {
		    quoteCount++;
		}
		sys.log("[DEBUG by CommandParser] Found " + quoteCount + " quotes in command");
		
		if (fullCmd.contains("\"") && quoteCount % 2 == 0) {
			//Checked if fullCmd contains quotes, and number of quotes is even
			int index = 0;
			for (String substr : fullCmd.split("\"")) {
				if (index % 2 > 0) {
					quotedSubStrs.add(substr);
					fullCmd = fullCmd.replace("\"" + substr + "\"", "");
					sys.log("Found quoted substring: " + substr);
				}
				index++;
			}
		} else {
			sys.log("[DEBUG by CommandParser] no quotes found or number is uneven");
		}
		
		if (!fullCmd.isBlank()) {
			// Note that envVars are only replaced, if they're unquoted!
			fullCmd = EnvVarParser.parseEnvVars(fullCmd);
			if (fullCmd.contains(" ")) {
				for (String substr : fullCmd.split(" ")) {
					params.add(substr);
				}
			} else {
				params.add(fullCmd);
			}
		}
		
		params.addAll(quotedSubStrs);
		
		for (String param : params) {
			sys.log("Parameter: " + param);
		}
		
		return new Object[] {command, params};
	}
	
	public static Object[] getExecutionQueue() {
		return executionQueue;
	}
	
	//TODO work further with executionQueue
	//TODO work on startupscripts
	public static String getExecutionQueueStr() {
		String out = "";
		for (Command cmd : executionQueue) {
			out += cmd.getFullCommand() + "\n";
		}
		out = out + "\n";
		return out;
	}
}