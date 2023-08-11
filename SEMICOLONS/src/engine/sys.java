package engine;

/**
 * Used for tasks required very periodically (Short name "sys" is easy to use)
 */

public class sys {
	public static String fsep; // The file separator on the specific OS: "/" on linux, "\" on windows
	private static boolean fsepIsSet = false;
	
	//========================================= LOGGING =========================================
	public static void log(String auth, int status, String message) {
		if (message != null)
			Logging.logWrite(auth, status, message + "\u001B[0m");
	}
	public static void log(String message) {
		if (message != null)
			Logging.logWrite("HIDDEN", 0, message + "\u001B[0m");
	}
	//========================================= LOGGING END =========================================
	
	public static void setFileSeparator(String newFSep) {
		if (fsepIsSet)
			sys.log("SYS", 3, "New file separator not accepted, already set.");
		else if (!newFSep.equals("/") || !newFSep.equals("\\"))
			sys.log("SYS", 3, "New file separator \"" + newFSep + "\" not accepted: Invalid character.");
		fsep = newFSep;
	}
}
