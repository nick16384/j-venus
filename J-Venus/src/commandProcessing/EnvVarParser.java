package commandProcessing;

import engine.sys;
import libraries.VarLib;

public class EnvVarParser {
	
	/**
	 * Returns a string, where environment variable names in input string have
	 * been replaced with their representing values (at libraries.VarLib)
	 * @param in Input text containing $[something]
	 * @return
	 */
	public static String parseEnvVars(String in) {
		String currentToken = "";
		String replacement = "";
		
		if (in.contains("\""))
			return in;
		
		while (in.contains("$")) {
			currentToken = in.split("\\$")[1].split(" ")[0];
			
			replacement = VarLib.getEnv("$" + currentToken);
			if (replacement == null)
				replacement = VarLib.getEnv("$$" + currentToken);
			if (replacement == null)
				replacement = currentToken;
			
			sys.log("EVP", 0, "Current token: \"" + currentToken + "\" -> \"" + replacement + "\"");
			in = in.replaceFirst("[\\$, \\$\\$]?" + currentToken, replacement);
		}
		
		return in;
	}
}
