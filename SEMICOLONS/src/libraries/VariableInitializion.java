/**
 * This class's sole purpose is to initialize all global variables at startup.
 */

package libraries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import awtcomponents.AWTANSI;
import engine.LogLevel;
import engine.sys;
import filesystem.FileCheckUtils;
import filesystem.InternalFiles;
import filesystem.VirtualFile;
import filesystem.VirtualizedLocation;
import main.Main;
import threads.ThreadAllocation;

public class VariableInitializion {
	public static final void initializeAll() {
		// ==================================================================================================
		// ================================= GLOBAL VARIABLE INITIALIZATION
		// =================================

		if (System.getProperty("java.home") == null) {
			System.err.println("INITVARS: The JAVA_HOME variable was not found.");
			System.err.println("INITVARS: SEMICOLONS needs JDK installed in order to work. Exiting...");
			sys.shutdown(1);
		}
		Global.javaHome = new File(System.getProperty("java.home"));
		Global.osName = System.getProperty("os.name");
		Global.username = System.getProperty("user.name");
		try {
			Global.hostname = java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException uhe) {
			sys.log("INITVARS", LogLevel.WARN, "Could not determine hostname for this machine, using default 'LOCAL'");
			Global.hostname = "LOCAL";
		}

		if (Global.osName.equalsIgnoreCase("Linux")) { // If operating system is linux...
			initializeForLinux();

		} else if (Global.osName.contains("Windows")) { // If operating system is any Windows version...
			initializeForWindows();

		}
		// Define log file, and create log stream
		String logfileName = Global.getDateTime(true).replace("|", "_") + ".log";
		if (InternalFiles.getLogFile() == null)
			InternalFiles.setLogFile(Global.getDataDir().newVirtualFile("/logs/" + logfileName));
		try {
			Global.consoleLogStream = new PrintStream(Global.getLogFile());
		} catch (FileNotFoundException fnfe) {
			System.err.println("Log file could not be created under $ROOT/data/logs");
		} catch (NullPointerException npe) {
			System.err.println("NullPointerException while creating log stream.");
		}
		if (Global.consoleLogStream == null) {
			System.err.println("Could not create log file. Logs won't be saved.");
		} else {
			System.out.println("Saving log file to " + Global.getLogFile().getAbsolutePath());
			sys.log("INITVARS", LogLevel.DEBUG, "Log file at " + Global.getLogFile().getAbsolutePath());
		}

		Global.mainThread = Thread.currentThread();
		// try { consoleStream = new PrintStream(new
		// File("/home/theophil/Desktop/log1.txt")); }
		// catch (FileNotFoundException fnfe) { logWrite("INITVARS", 2, "Could not find
		// log path. Cannot save log."); }
		if (Global.osName.equalsIgnoreCase("UnknownOS")) {
			sys.log("LIB", LogLevel.WARN, "INITVARS: Operating system not supported. Exiting...");
			sys.shutdown(1);
		}
		
		prepareEnvironment();

		// Fetch message of the day (replace env aliases with real values.)
		fetchMOTD();
		
		sys.setFileSeparator(Global.fsep);
	}

	private static final void initializeForLinux() {
		Global.FSRoot = new VirtualizedLocation("/");

		// Check if another root folder was specified
		// === SPECIAL ROOT FOLDER SPECIFIED ===
		if (Main.argsMain.contains("--root-folder")
				&& Main.argsMain.get(Main.argsMain.indexOf("--root-folder") + 1) != null)
			try {
				Global.RootDir = new VirtualizedLocation(
						Main.argsMain.get(Main.argsMain.indexOf("--root-folder") + 1));
			} catch (Exception ex) {
				sys.log("");
				ex.printStackTrace();
			}
		if (Main.argsMain.contains("--log-file")
				&& Main.argsMain.get(Main.argsMain.indexOf("--log-file") + 1) != null) {
			sys.log("Specified log file location: " + Main.argsMain.get(Main.argsMain.indexOf("--log-file") + 1));
			InternalFiles.setLogFile(
					Global.FSRoot.newVirtualFile(Main.argsMain.get(Main.argsMain.indexOf("--log-file") + 1)));
		}

		if (Global.RootDir == null || !FileCheckUtils.isDir(Global.RootDir.getActualFile()))
			Global.RootDir = new VirtualizedLocation("/etc/semicolons");
		// === SPECIAL ROOT FOLDER SPECIFIED END ===

		Global.TempDir = new VirtualizedLocation(Global.RootDir.getAbsolutePath() + "/temp");
		Global.BinDir = new VirtualizedLocation(Global.RootDir.getAbsolutePath() + "/bin");
		Global.CmdDir = new VirtualizedLocation(Global.RootDir.getAbsolutePath() + "/commands");
		Global.DataDir = new VirtualizedLocation(Global.RootDir.getAbsolutePath() + "/data");
		Global.HomeDir = new VirtualizedLocation(System.getProperty("user.home"));
		Global.path = "/";
		Global.javaExec = new File(Global.getJavaHome().getAbsoluteFile() + "/bin/java");
	}

	private static final void initializeForWindows() {
		Global.FSRoot = new VirtualizedLocation(System.getenv("SYSTEMROOT") + "\\");

		// Check if another root folder was specified
		// === SPECIAL ROOT FOLDER SPECIFIED ===
		if (Main.argsMain.contains("--root-folder")
				&& Main.argsMain.get(Main.argsMain.indexOf("--root-folder") + 1) != null)
			try {
				Global.RootDir = new VirtualizedLocation(
						Main.argsMain.get(Main.argsMain.indexOf("--root-folder") + 1));
			} catch (Exception ex) {
				sys.log("");
				ex.printStackTrace();
			}
		if (Main.argsMain.contains("--log-file")
				&& Main.argsMain.get(Main.argsMain.indexOf("--log-file") + 1) != null) {
			sys.log("Specified log file location: " + Main.argsMain.get(Main.argsMain.indexOf("--log-file") + 1));
			InternalFiles.setLogFile(
					Global.FSRoot.newVirtualFile(Main.argsMain.get(Main.argsMain.indexOf("--log-file") + 1)));
		}

		if (!FileCheckUtils.isDir(Global.RootDir.getActualFile()))
			Global.RootDir = new VirtualizedLocation(
					"C:\\Program Files\\SEMICOLONS");
		// === SPECIAL ROOT FOLDER SPECIFIED END ===

		Global.RootDir = new VirtualizedLocation("C:\\Program Files\\SEMICOLONS");
		Global.TempDir = new VirtualizedLocation(Global.RootDir.getAbsolutePath() + "\\temp");
		Global.BinDir = new VirtualizedLocation(Global.RootDir.getAbsolutePath() + "\\bin");
		Global.CmdDir = new VirtualizedLocation(Global.RootDir.getAbsolutePath() + "\\commands");
		Global.DataDir = new VirtualizedLocation(Global.RootDir.getAbsolutePath() + "\\data");
		Global.HomeDir = new VirtualizedLocation(System.getProperty("user.home"));
		Global.path = System.getenv("SYSTEMROOT") + "\\";
		Global.javaExec = new File(
				Global.getJavaHome().getAbsoluteFile() + "\\bin\\java.exe");
	}
	
	/**
	 * Read "message of the day"-file, take out lines starting with // or #, and replace envVars
	 */
	private static final void fetchMOTD() {
		sys.log("INITVARS", LogLevel.INFO, "Fetching MOTD (message of the day)...");
		String motdRaw = Global.getDataDir().newVirtualFile("/motd").readContents();
		if (motdRaw != "") {
			Global.motd = "";
			Global.motd = motdRaw;
			/*String[] motdRawLines = motdRaw.split("\n");
			for (String line : motdRawLines) {
				if (!line.startsWith("#") && (!line.startsWith("//"))) {
					motd += line + "\n";
				}
			}*/
			int evcount = 0;
			while (Global.motd.contains("$")) {
				evcount++;
				try {
					String nextEnvName = Global.motd.substring(Global.motd.indexOf("$")).split(" ")[0];
					sys.log("The next envV to be replaced is: " + nextEnvName);
					Global.motd = Global.motd.replaceFirst(
							"\\" + nextEnvName,
							Env.getEnv(nextEnvName));
				} catch (NullPointerException npe) {
					sys.log("VLIB", LogLevel.ERR, "Replacement of $VAR with actual value failed in MOTD fetching.");
					Global.motd = Global.motd.replaceFirst("\\$", "");
				}
				System.out.println("EVCount: " + evcount);
			}
		}
	}
	
	private static final void prepareEnvironment() {
		Env.addEnv("$$NULL", null);
		Env.addEnv("$$ALL", "REF-ALL-VAR");
		Env.addEnv("$VERSION", Global.getVersion());
		Env.addEnv("$PATH", Global.path);
		Env.addEnv("$FSROOT", Global.getFSRoot());
		Env.addEnv("$USERNAME", Global.username);
		Env.addEnv("$HOSTNAME", Global.hostname);
		Env.addEnv("$VENUS_ROOT", Global.getRootDir().getAbsolutePath());
		Env.addEnv("$VENUS_TMP", Global.getTempDir().getAbsolutePath());
		Env.addEnv("$VENUS_BIN", Global.getBinDir().getAbsolutePath());
		Env.addEnv("$VENUS_CMD", Global.getCmdDir().getAbsolutePath());
		Env.addEnv("$VENUS_DATA", Global.getDataDir().getAbsolutePath());
		Env.addEnv("$JAVA_HOME", Global.getJavaHome().getAbsolutePath());
		Env.addEnv("$MAX_ENV_SIZE", "4096");
		Env.addEnv("$SCROLL_AT_LINES", Integer.toString(Global.CMDLINE_MAX_LINE_COUNT));
		Env.addEnv("$CMDLINE_MAX_LINE_COUNT", "26");
		Env.addEnv("$MAX_CMD_QUEUE_LENGTH", "16");
		Env.addEnv("$$ARG_MT", Boolean.toString(!libraries.Global.singleThreaded)); // Multithreading enabled
		Env.addEnv("$$ARG_FULLSCREEN", Boolean.toString(libraries.Global.fullscreen)); // Fullscreen mode on
		Env.addEnv("$$ARG_JFX", Boolean.toString(libraries.Global.javafxEnabled)); // JavaFX is enabled
	}
}
