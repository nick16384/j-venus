/**
 * This class contains useful utilities and stores a variety of variables
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
import engine.sys;
import main.Main;
import threads.ThreadAllocation;

public class VariableInitializion {
	public static final void initializeAll() {
		// ==================================================================================================
		// ================================= GLOBAL VARIABLE INITIALIZATION
		// =================================

		if (System.getProperty("java.home") == null) {
			System.err.println("INITVARS: The JAVA_HOME variable was not found.");
			System.err.println("INITVARS: J-Vexus needs JDK installed in order to work. Exiting...");
			ThreadAllocation.shutdownVexus(1);
		}
		Global.javaHome = new File(System.getProperty("java.home"));
		Global.osName = System.getProperty("os.name");
		Global.username = System.getProperty("user.name");
		try {
			Global.hostname = java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException uhe) {
			sys.log("INITVARS", 2, "Could not determine hostname for this machine, using default 'LOCAL'");
			Global.hostname = "LOCAL";
		}

		if (Global.osName.equalsIgnoreCase("Linux")) { // If operating system is linux...
			initializeForLinux();

		} else if (Global.osName.contains("Windows")) { // If operating system is any Windows version...
			initializeForWindows();

		}
		// Define log file, and create log stream
		String logfileName = Global.getDateTime(true).replace("|", "_") + ".log";
		Global.logfile = new File(
				Global.getDataDir().getAbsolutePath() + Global.fsep + "logs" + Global.fsep + logfileName);
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
			sys.log("INITVARS", 0, "Log file at " + Global.getLogFile().getAbsolutePath());
		}

		Global.mainThread = Thread.currentThread();
		// try { consoleStream = new PrintStream(new
		// File("/home/theophil/Desktop/log1.txt")); }
		// catch (FileNotFoundException fnfe) { logWrite("INITVARS", 2, "Could not find
		// log path. Cannot save log."); }
		if (Global.osName.equalsIgnoreCase("UnknownOS")) {
			sys.log("LIB", 2, "INITVARS: Operating system not supported. Exiting...");
			ThreadAllocation.shutdownVexus(1);
		}
		
		prepareEnvironment();

		// Fetch message of the day (replace env aliases with real values.)
		fetchMOTD();
		
		sys.setFileSeparator(Global.fsep);
	}

	private static final void initializeForLinux() {
		Global.fsRoot = "/";

		// Check if another root folder was specified
		// === SPECIAL ROOT FOLDER SPECIFIED ===
		if (Arrays.asList(Main.argsMain).contains("--root-folder")
				&& Main.argsMain[Arrays.asList(Main.argsMain).indexOf("--root-folder") + 1] != null)
			try {
				Global.DfltDir = new File(Main.argsMain[Arrays.asList(Main.argsMain).indexOf("--root-folder") + 1]);
			} catch (Exception ex) {
				sys.log("");
				ex.printStackTrace();
			}

		if (!FileCheckUtils.isDir(Global.DfltDir))
			Global.DfltDir = new File(Global.fsep + "etc" + Global.fsep + "semicolons");
		// === SPECIAL ROOT FOLDER SPECIFIED END ===

		Global.TempDir = new File(Global.DfltDir.getAbsolutePath() + Global.fsep + "temp");
		Global.BinDir = new File(Global.DfltDir.getAbsolutePath() + Global.fsep + "bin");
		Global.CmdDir = new File(Global.DfltDir.getAbsolutePath() + Global.fsep + "commands");
		Global.DataDir = new File(Global.DfltDir.getAbsolutePath() + Global.fsep + "data");
		Global.HomeDir = new File(System.getProperty("user.home"));
		Global.path = "/";
		Global.javaExec = new File(Global.getJavaHome().getAbsoluteFile() + Global.fsep + "bin" + Global.fsep + "java");
	}

	private static final void initializeForWindows() {
		Global.fsRoot = System.getenv("SYSTEMROOT") + "\\";

		// Check if another root folder was specified
		// === SPECIAL ROOT FOLDER SPECIFIED ===
		if (Arrays.asList(Main.argsMain).contains("--root-folder")
				&& Main.argsMain[Arrays.asList(Main.argsMain).indexOf("--root-folder") + 1] != null)
			try {
				Global.DfltDir = new File(Main.argsMain[Arrays.asList(Main.argsMain).indexOf("--root-folder") + 1]);
			} catch (Exception ex) {
				sys.log("");
				ex.printStackTrace();
			}

		if (!FileCheckUtils.isDir(Global.DfltDir))
			Global.DfltDir = new File("C:" + Global.fsep + "Program Files" + Global.fsep + "SEMICOLONS");
		// === SPECIAL ROOT FOLDER SPECIFIED END ===

		Global.DfltDir = new File("C:" + Global.fsep + "Program Files" + Global.fsep + "SEMICOLONS");
		Global.TempDir = new File(Global.DfltDir.getAbsolutePath() + Global.fsep + "temp");
		Global.BinDir = new File(Global.DfltDir.getAbsolutePath() + Global.fsep + "bin");
		Global.CmdDir = new File(Global.DfltDir.getAbsolutePath() + Global.fsep + "commands");
		Global.DataDir = new File(Global.DfltDir.getAbsolutePath() + Global.fsep + "data");
		Global.HomeDir = new File(System.getProperty("user.home"));
		Global.path = System.getenv("SYSTEMROOT") + "\\";
		Global.javaExec = new File(
				Global.getJavaHome().getAbsoluteFile() + Global.fsep + "bin" + Global.fsep + "java.exe");
	}
	
	/**
	 * Read "message of the day"-file, take out lines starting with // or #, and replace envVars
	 */
	private static final void fetchMOTD() {
		System.err.println("Fetching message of the day (MOTD)");
		
		String motdRaw = "";
		try {
			motdRaw = Files.readString(Paths.get(Global.getDataDir().getAbsolutePath() + Global.fsep + "motd"));
		} catch (IOException | NullPointerException e) {
			sys.log("VARLIB", 3, "Cannot read motd file. Using default message.");
			e.printStackTrace();
		}
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
					sys.log("VLIB", 3, "Replacement of $VAR with actual value failed in MOTD fetching.");
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
		Env.addEnv("$VENUS_ROOT", Global.getDefaultDir().getAbsolutePath());
		Env.addEnv("$VENUS_TMP", Global.getTempDir().getAbsolutePath());
		Env.addEnv("$VENUS_BIN", Global.getBinDir().getAbsolutePath());
		Env.addEnv("$VENUS_CMD", Global.getCmdDir().getAbsolutePath());
		Env.addEnv("$VENUS_DATA", Global.getDataDir().getAbsolutePath());
		Env.addEnv("$JAVA_HOME", Global.getJavaHome().getAbsolutePath());
		Env.addEnv("$MAX_ENV_SIZE", "4096");
		Env.addEnv("$SCROLL_AT_LINES", Integer.toString(Global.CMDLINE_MAX_LINE_COUNT));
		Env.addEnv("$CMDLINE_MAX_LINE_COUNT", "26");
		Env.addEnv("$MAX_CMD_QUEUE_LENGTH", "16");
		Env.addEnv("$$ARG_MT", Boolean.toString(!main.Main.singleThreaded)); // Multithreading enabled
		Env.addEnv("$$ARG_FULLSCREEN", Boolean.toString(main.Main.fullscreen)); // Fullscreen mode on
		Env.addEnv("$$ARG_JFX", Boolean.toString(main.Main.javafxEnabled)); // JavaFX is enabled
	}
}
