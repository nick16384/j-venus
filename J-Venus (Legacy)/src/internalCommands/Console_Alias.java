package internalCommands;

import java.util.ArrayList;

import engine.sys;

public class Console_Alias {
	public static String alias(ArrayList<String> params) {
		if (params == null || params.size() < 1) {
			sys.shellPrintln("No Parameters specified. Try using --help or -h for help.");
			return "ParseErr_TooFewParams";
		}
		
		if (params.size() < 2) {
			if (params.get(0).equals("--help") || params.get(0).equals("-h")) {
				String helpStr = 
						"Usage: alias aliasName originalCommand [OPTION]...\n"
						+ "       alias --delete aliasName\n"
						+ "Sets and alias for the specified \"originalCommand\", that can be used the same way.\n\n"
						+ "  -p, --permanent     Save aliases permanently in external file.\n"
						+ "  -d, --delete        Delete alias (permanent or temporary).\n"
						+ "  -h, --help          Display this help text.";
				sys.shellPrintln(helpStr);
				return null;
			}
			//Only return error, if help is not specified.
			return "ParseErr_TooFewParams";
		}
		
		sys.shellPrintln("alias command is currently not functional. (Developer's TODO)");
		//Two syntax types may follow: "alias --delete aliasname" OR "alias something somethingelse --someparams"
		
		//Check for alias deletion
		if (params.get(0).equals("--delete") || params.get(0).equals("-d")) {
			
		}
		return null;
	}
}
