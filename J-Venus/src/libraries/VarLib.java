package libraries;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.Element;

import engine.AWTANSI;
import engine.sys;
import main.Main;

public class VarLib {
	protected static final String VERSION = "23.05 IOTA";
	//Message of the day -> start msg before any commands e.g. Welcome to ...
	protected static String motd = "J-Venus Version " + VERSION + " Warning: Beta state, problems may occur.\n"
			+ "Copyleft () 2021 - 2023 The J-Venus Project. All rights reserved.\n"
			+ "GNU General Public License v3. Created with Eclipse Oracle.\n"
			+ "Warning: Log is currently very verbose due to debugging reasons.\n"
			+ "Will be reduced within alpha versions.\n";
	protected static String path = "";
	protected static String dateTime = "NODATETIME";
	protected static String username = "";
	protected static String hostname = "";
	protected static final String DEFAULT_PROMPT_PATTERN =
			"\u001B[1;32m$USERNAME\u001B[1;37m@\u001B[1;32m$HOSTNAME\u001B[1;36m:$PATH\u001B[1;37m$# "; //Lots of color
	protected static String promptPattern = DEFAULT_PROMPT_PATTERN;
	public static final int CMDLINE_MAX_LINE_COUNT = getCmdLineLineNumber();
	protected static String prompt = ">>";
	protected static String fsRoot = "";
	public static PrintStream consoleLogStream = null;
	public static String consoleString = "";
	protected static Thread mainThread;
	public final static String fsep = FileSystems.getDefault().getSeparator();
	protected static String osName = "UnknownOS";
	protected static Map<String, File> extCommands = new HashMap<>();
	protected static File DfltDir;
	protected static File TempDir;
	protected static File BinDir;
	protected static File CmdDir;
	protected static File DataDir;
	protected static File javaHome;
	protected static File javaExec;
	//Log file to save consoleLogStream to
	protected static File logfile;
	//Contains environment variables visible by the user and to other commands
	protected static Map<String, String> env = new HashMap<>();
	
	public static String getVersion() {
		return VERSION;
	}
	public static String getCurrentDir() {
		return path;
	}
	public static File getDefaultDir() {
		return DfltDir;
	}
	public static File getTempDir() {
		return TempDir;
	}
	public static File getBinDir() {
		return BinDir;
	}
	public static File getCmdDir() {
		return CmdDir;
	}
	public static File getDataDir() {
		return DataDir;
	}
	public static File getLogFile() {
		return logfile;
	}
	public static Map<String, File> getExtCommands() {
		return extCommands;
	}
	public static File getJavaHome() {
		return javaHome;
	}
	public static File getJavaExec() {
		return javaExec;
	}
	public static String getPrompt() {
		return prompt;
	}
	public static String getOSName() {
		return osName;
	}
	public static Thread getMainThreadReference() {
		return mainThread;
	}
	public static String getFSRoot() {
		return fsRoot;
	}
	public static void setCurrentDir(String newDir) {
		path = newDir;
	}
	public static void setExtCommands(Map<String, File> newExtCommands) {
		extCommands = newExtCommands;
	}
	public static void setPromptPattern(String newPromptPattern) {
		if (newPromptPattern != null) {
			if (newPromptPattern.equalsIgnoreCase("default"))
				promptPattern = DEFAULT_PROMPT_PATTERN;
			else
				promptPattern = newPromptPattern;
		} else {
			sys.log("OPENLIB", 2, "Attempt to set prompt to null, changing nothing.");
		}
	}
	public static String getPromptWithPattern(String pattern) {
		String newPrompt = "";
		if (pattern.contains("$")) {
			for (String patternPart : pattern.split("\\$")) {
				if (patternPart.startsWith("#")) {
					newPrompt = newPrompt.concat("$" + patternPart.replaceFirst("\\#", ""));
				} else if (patternPart.startsWith("-")) {
					newPrompt = "";
				} else if (patternPart.startsWith("USERNAME")) {
					newPrompt = newPrompt.concat(VarLib.username + patternPart.replaceFirst("(\\$)?USERNAME", ""));
				} else if (patternPart.startsWith("HOSTNAME")) {
					newPrompt = newPrompt.concat(VarLib.hostname + patternPart.replaceFirst("(\\$)?HOSTNAME", ""));
				} else if (patternPart.startsWith("PATH")) {
					newPrompt = newPrompt.concat(VarLib.path + patternPart.replaceFirst("(\\$)?PATH", ""));
				} else if (patternPart.startsWith("TIME")) {
					newPrompt = newPrompt.concat(getDateTime(false) + patternPart.replaceFirst("(\\$)?TIME", ""));
				} else if (patternPart.startsWith("DATETIME")) {
					newPrompt = newPrompt.concat(getDateTime(true) + patternPart.replaceFirst("(\\$)?DATETIME", ""));
				} else {
					newPrompt = newPrompt.concat(patternPart);
				}
			}
		} else if (!pattern.isBlank()) {
			newPrompt = pattern;
		} else if (!VarLib.prompt.isBlank()) {
			newPrompt = VarLib.prompt;
		} else {
			newPrompt = "DEFAULT>";
		}
		return newPrompt;
	}
	public static String getDefaultPromptPattern() {
		return DEFAULT_PROMPT_PATTERN;
	}
	
	public static String getDateTime(boolean includeDate) {
		if (includeDate) {
			VarLib.dateTime = new SimpleDateFormat("yyyy.MM.dd|HH:mm:ss").format(Calendar.getInstance().getTime());
		} else {
			VarLib.dateTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		}
		return VarLib.dateTime;
	}
	
	/**
	 * Environment variables
	 * Normaly with dollar sign and uppercase
	 * example: $PATH, $JDOS_ROOT
	 * Special ones with double-dollar-sign (constants, cannot change):
	 * $$ALL, $$NULL
	 */
	
	public static void addEnv(String key, String val) {
		try {
			if (Integer.getInteger(getEnv("$MAX_ENV_SIZE")) > env.size()) { //If max env count exceeded
				sys.shellPrint(2, "HIDDEN", "Maximum environment variable amount exceeded.\n"
						+ "Modify $MAX_ENV_SIZE to change. Warning: values above default (4096)\n"
						+ "Can lead to increased memory usage. Normally, this limit is not reached,\n"
						+ "so please check for loops that add Envs each time. Also you may try\n"
						+ "restarting JavaDOS.");
				return;
			}
		} catch (NullPointerException npe) {
			if (sys.getActivePhase().equalsIgnoreCase("run")) {
				sys.shellPrint(2, "VARLIB", "Environment variable '$MAX_ENV_SIZE' does not exist.\n"
						+ "Please add it manually or run 'chEnv -update $MAX_ENV_SIZE'.\n");
				sys.log("VARLIB", 2, "Environment variable '$MAX_ENV_SIZE' does not exist.\n"
						+ "Please add it manually or run 'chEnv -update $MAX_ENV_SIZE'.");
				npe.printStackTrace();
			}
		}
		if (key == null) {
			sys.log("VARLIB", 3, "Tried to add envV with key null. Exited with error.");
			engine.sys.shellPrint(3, "VARLIB", "Cannot add envV with key null.\n");
			return;
		} else if (val == null && engine.sys.getActivePhase().equalsIgnoreCase("run")) {
			sys.log("VARLIB", 3, "Tried to set an envV with value 'null', when in RUN phase. Exited with error.");
			engine.sys.shellPrint(3, "VARLIB", "Cannot set an envV with value 'null', when in RUN phase.\n");
		}
		
		sys.log("VARLIB", 1, "Adding new envV '" + key + "' -> '" + val + "'");
		env.put(key, val);
		
		sys.log("VARLIB", 1, "Finished adding envV, validating...");
		if (getEnv(key) != null && getEnv(key).equals(val)) {
			sys.log("VARLIB", 1, "Validation succeeded: " + key + ", " + getEnv(key));
			if (!sys.getActivePhase().equals("init"))
				sys.shellPrint(AWTANSI.B_Green, "Success: " + key + " -> " + val + "\n");
		} else {
			sys.log("VARLIB", 3, "Validation failed; " + val + " != " + getEnv(key));
			if (!sys.getActivePhase().equals("init")) {
				sys.shellPrint(AWTANSI.B_Yellow, "Could not create envV. Information below:\n");
				sys.shellPrint("Created variable, but validation failed \\/\n"
						+ "Method call key: " + key + "\n"
						+ "Method call value: " + val + "\n"
						+ "envV Value with getEnv() call: " + getEnv(key) + "\n");
				sys.shellPrint(AWTANSI.B_Magenta, "Try 'env' to see, if your envV exists or try again.\n");
			}
		}
	}
	
	//TODO fix "max env size not existing" error
	
	public static String getEnv(String key) {
		if (key != null && env.containsKey(key)) {
			return env.get(key);
		} else if (key.equalsIgnoreCase("$$NULL")) {
			return null;
		} else {
			return null;
		}
	}
	public static Map<String, String> getFullEnv() {
		return env;
	}
	public static void changeEnv(String key, String newVal) {
		if (newVal != null && key != null) {
			if (!key.startsWith("$$")) { //Do not modify constants
				env.replace(key, newVal);
			} else {
				sys.log("VARLIB:CHENV", 2, "Tried to modify special variable or constant ($$*). "
						+ "This incident will be reported.");
				sys.shellPrint(2, "HIDDEN", "Tried to modify special variable or constant ($$*).\n"
						+ "This incident will be reported.\n");
			}
		}
	}
	
	//Source http://rememberjava.com/ui/2017/02/19/line_numbers.html
	private static int getCmdLineLineNumber() {
		if (!sys.getActivePhase().equals("run")) {
			return 26; //Return default value to not throw NPE when trying to access mainFrame in init phase.
		}
		// According to the Document.getRootElements() doc, there will "typically"
		// only be one root element.
		Element root = Main.mainFrameAWT.getCmdLine().getDocument().getDefaultRootElement();
		int len = root.getElementCount();
		for (int i = 0; i < len; i++) {
			if (root.getElement(i) == root /*thisElement*/ ) { //thisElement
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Read "message of the day"-file, take out lines starting with // or #, and replace envVars
	 */
	public static void fetchMOTD() {
		System.err.println("Fetching message of the day (MOTD)");
		
		String motdRaw = "";
		try {
			motdRaw = Files.readString(Paths.get(getDataDir().getAbsolutePath() + fsep + "motd"));
		} catch (IOException | NullPointerException e) {
			sys.log("VARLIB", 3, "Cannot read motd file. Using default message.");
			e.printStackTrace();
		}
		if (motdRaw != "") {
			motd = "";
			motd = motdRaw;
			/*String[] motdRawLines = motdRaw.split("\n");
			for (String line : motdRawLines) {
				if (!line.startsWith("#") && (!line.startsWith("//"))) {
					motd += line + "\n";
				}
			}*/
			int evcount = 0;
			while (motd.contains("$")) {
				evcount++;
				try {
					String nextEnvName = motd.substring(motd.indexOf("$")).split(" ")[0];
					sys.log("The next envV to be replaced is: " + nextEnvName);
					motd = motd.replaceFirst(
							"\\" + nextEnvName,
							getEnv(nextEnvName));
				} catch (NullPointerException npe) {
					sys.log("VLIB", 3, "Replacement of $VAR with actual value failed in MOTD fetching.");
					motd = motd.replaceFirst("\\$", "");
				}
				System.out.println("EVCount: " + evcount);
			}
		}
	}
	
	public static String getMOTD () {
		return motd;
	}
}
