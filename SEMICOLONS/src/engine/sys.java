package engine;

/**
 * Used for tasks required very periodically (Short name "sys" is easy to use)
 */

public class sys {
	public static String fsep; // The file separator on the specific OS: "/" on linux, "\" on windows
	private static boolean fsepIsSet = false;
	
	//========================================= LOGGING =========================================
	public static void log(String auth, InfoType status, String message) {
		if (message != null)
			Logging.logWrite(auth, status, message + "\u001B[0m");
	}
	public static void log(String message) {
		if (message != null)
			Logging.logWrite("HIDDEN", InfoType.STATUS, message + "\u001B[0m");
	}
	//========================================= LOGGING END =========================================
	
	public static void setFileSeparator(String newFSep) {
		if (fsepIsSet) {
			sys.log("SYS", InfoType.ERR, "New file separator not accepted, already set.");
			return;
		} else if (!newFSep.equals("/") && !newFSep.equals("\\")) {
			sys.log("SYS", InfoType.ERR, "New file separator \"" + newFSep + "\" not accepted: Invalid character.");
			return;
		}
		fsep = newFSep;
	}
}
