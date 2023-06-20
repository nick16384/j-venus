/**
 * This class contains useful utilities and stores a variety of variables
 */

package libraries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import engine.AWTANSI;
import engine.sys;
import main.Main;

public class OpenLib {
	public static void initVars() {
		if (System.getProperty("java.home") == null) {
			System.err.println("INITVARS: The JAVA_HOME variable was not found.");
			System.err.println("INITVARS: J-Vexus needs JDK installed in order to work. Exiting...");
			Main.ThreadAllocMain.shutdownVexus(1);
		}
		VarLib.javaHome = new File(System.getProperty("java.home"));
		VarLib.osName = System.getProperty("os.name");
		VarLib.username = System.getProperty("user.name");
		try { VarLib.hostname = java.net.InetAddress.getLocalHost().getHostName(); }
		catch (UnknownHostException uhe) {
			sys.log("INITVARS", 2, "Could not determine hostname for this machine, using default 'LOCAL'");
			VarLib.hostname = "LOCAL";
		}
		
		if (VarLib.osName.equalsIgnoreCase("Linux")) { //If operating system is linux...
			VarLib.fsRoot = "/";
			
			// Check if another root folder was specified
			// === SPECIAL ROOT FOLDER SPECIFIED ===
			if (Arrays.asList(Main.argsMain).contains("--root-folder")
					&& Main.argsMain[Arrays.asList(Main.argsMain).indexOf("--root-folder") + 1] != null)
				try {
					VarLib.DfltDir
						= new File(Main.argsMain[Arrays.asList(Main.argsMain).indexOf("--root-folder") + 1]);
				} catch (Exception ex) { sys.log(""); ex.printStackTrace(); }
			
			if (!FileCheckUtils.isDir(VarLib.DfltDir))
				VarLib.DfltDir = new File(VarLib.fsep + "etc" + VarLib.fsep + "semicolons");
			// === SPECIAL ROOT FOLDER SPECIFIED END ===
			
			VarLib.TempDir = new File(VarLib.DfltDir.getAbsolutePath() + VarLib.fsep + "temp");
			VarLib.BinDir = new File(VarLib.DfltDir.getAbsolutePath() + VarLib.fsep + "bin");
			VarLib.CmdDir = new File(VarLib.DfltDir.getAbsolutePath() + VarLib.fsep + "commands");
			VarLib.DataDir = new File(VarLib.DfltDir.getAbsolutePath() + VarLib.fsep + "data");
			VarLib.HomeDir = new File(System.getProperty("user.home"));
			VarLib.path = "/";
			VarLib.javaExec = new File(VarLib.getJavaHome().getAbsoluteFile()
					+ VarLib.fsep + "bin" + VarLib.fsep + "java");
		} else if (VarLib.osName.contains("Windows")) { //If operating system is any Windows version...
			VarLib.fsRoot = System.getenv("SYSTEMROOT") + "\\";
			
			// Check if another root folder was specified
			// === SPECIAL ROOT FOLDER SPECIFIED ===
			if (Arrays.asList(Main.argsMain).contains("--root-folder")
					&& Main.argsMain[Arrays.asList(Main.argsMain).indexOf("--root-folder") + 1] != null)
				try {
					VarLib.DfltDir
						= new File(Main.argsMain[Arrays.asList(Main.argsMain).indexOf("--root-folder") + 1]);
				} catch (Exception ex) { sys.log(""); ex.printStackTrace(); }
			
			if (!FileCheckUtils.isDir(VarLib.DfltDir))
				VarLib.DfltDir = new File("C:" + VarLib.fsep + "Program Files" + VarLib.fsep + "SEMICOLONS");
			// === SPECIAL ROOT FOLDER SPECIFIED END ===
			
			VarLib.DfltDir = new File("C:" + VarLib.fsep + "Program Files" + VarLib.fsep + "SEMICOLONS");
			VarLib.TempDir = new File(VarLib.DfltDir.getAbsolutePath() + VarLib.fsep + "temp");
			VarLib.BinDir = new File(VarLib.DfltDir.getAbsolutePath() + VarLib.fsep + "bin");
			VarLib.CmdDir = new File(VarLib.DfltDir.getAbsolutePath() + VarLib.fsep + "commands");
			VarLib.DataDir = new File(VarLib.DfltDir.getAbsolutePath() + VarLib.fsep + "data");
			VarLib.HomeDir = new File(System.getProperty("user.home"));
			VarLib.path = System.getenv("SYSTEMROOT") + "\\";
			VarLib.javaExec = new File(VarLib.getJavaHome().getAbsoluteFile()
					+ VarLib.fsep + "bin" + VarLib.fsep + "java.exe");
		}
		//Define log file, and create log stream
		String logfileName = VarLib.getDateTime(true).replace("|", "_") + ".log";
		VarLib.logfile = new File(VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "logs" + VarLib.fsep + logfileName);
		try { VarLib.consoleLogStream = new PrintStream(VarLib.getLogFile()); }
		catch (FileNotFoundException fnfe) { System.err.println("Log file could not be created under $ROOT/data/logs"); }
		catch (NullPointerException npe) { System.err.println("NullPointerException while creating log stream."); }
		if (VarLib.consoleLogStream == null) {
			System.err.println("Could not create log file. Logs won't be saved.");
		} else {
			System.out.println("Saving log file to " + VarLib.getLogFile().getAbsolutePath());
			sys.log("INITVARS", 0, "Log file at " + VarLib.getLogFile().getAbsolutePath());
		}
		
		VarLib.mainThread = Thread.currentThread();
		//try { consoleStream = new PrintStream(new File("/home/theophil/Desktop/log1.txt")); }
		//catch (FileNotFoundException fnfe) { logWrite("INITVARS", 2, "Could not find log path. Cannot save log."); }
		if (VarLib.osName.equalsIgnoreCase("UnknownOS")) {
			sys.log("LIB", 2, "INITVARS: Operating system not supported. Exiting...");
			Main.ThreadAllocMain.shutdownVexus(1);
		}
		VarLib.addEnv("$$NULL", null);
		VarLib.addEnv("$$ALL", "REF-ALL-VAR");
		VarLib.addEnv("$VERSION", VarLib.getVersion());
		VarLib.addEnv("$PATH", VarLib.path);
		VarLib.addEnv("$FSROOT", VarLib.getFSRoot());
		VarLib.addEnv("$USERNAME", VarLib.username);
		VarLib.addEnv("$HOSTNAME", VarLib.hostname);
		VarLib.addEnv("$VENUS_ROOT", VarLib.getDefaultDir().getAbsolutePath());
		VarLib.addEnv("$VENUS_TMP", VarLib.getTempDir().getAbsolutePath());
		VarLib.addEnv("$VENUS_BIN", VarLib.getBinDir().getAbsolutePath());
		VarLib.addEnv("$VENUS_CMD", VarLib.getCmdDir().getAbsolutePath());
		VarLib.addEnv("$VENUS_DATA", VarLib.getDataDir().getAbsolutePath());
		VarLib.addEnv("$JAVA_HOME", VarLib.getJavaHome().getAbsolutePath());
		VarLib.addEnv("$MAX_ENV_SIZE", "4096");
		VarLib.addEnv("$SCROLL_AT_LINES", Integer.toString(VarLib.CMDLINE_MAX_LINE_COUNT));
		VarLib.addEnv("$CMDLINE_MAX_LINE_COUNT", "26");
		VarLib.addEnv("$MAX_CMD_QUEUE_LENGTH", "16");
		VarLib.addEnv("$$ARG_MT", Boolean.toString(!main.Main.singleThreaded)); //Multithreading enabled
		VarLib.addEnv("$$ARG_FULLSCREEN", Boolean.toString(main.Main.fullscreen)); //Fullscreen mode on
		VarLib.addEnv("$$ARG_JFX", Boolean.toString(main.Main.javafxEnabled)); //JavaFX is enabled
		
		//Fetch message of the day (replace env aliases with real values.)
		VarLib.fetchMOTD();
	}
	
	public static void updateEnv(String envName) {
		if (envName.equalsIgnoreCase("$$ALL")) {
			VarLib.changeEnv("$VERSION", VarLib.getVersion());
			VarLib.changeEnv("$PATH", VarLib.path);
			VarLib.changeEnv("$FSROOT", VarLib.getFSRoot());
			VarLib.changeEnv("$USERNAME", VarLib.username);
			VarLib.changeEnv("$HOSTNAME", VarLib.hostname);
			VarLib.changeEnv("$VENUS_ROOT", VarLib.getDefaultDir().getAbsolutePath());
			VarLib.changeEnv("$VENUS_TMP", VarLib.getTempDir().getAbsolutePath());
			VarLib.changeEnv("$VENUS_BIN", VarLib.getBinDir().getAbsolutePath());
			VarLib.changeEnv("$VENUS_CMD", VarLib.getCmdDir().getAbsolutePath());
			VarLib.changeEnv("$VENUS_DATA", VarLib.getDataDir().getAbsolutePath());
			VarLib.changeEnv("$JAVA_HOME", VarLib.getJavaHome().getAbsolutePath());
			VarLib.changeEnv("$MAX_ENV_COUNT", "4096");
			VarLib.changeEnv("$SCROLL_AT_LINES", Integer.toString(VarLib.CMDLINE_MAX_LINE_COUNT));
			VarLib.changeEnv("$CMDLINE_MAX_LINE_COUNT", "26");
			VarLib.changeEnv("$MAX_CMD_QUEUE_LENGTH", "16");
		} else if (VarLib.getEnv(envName) != null) {
			if (envName.equals("$VERSION")) {
				VarLib.changeEnv("$VERSION", VarLib.getVersion());
			} else if (envName.equals("$PATH")) {
				VarLib.changeEnv("$PATH", VarLib.path);
			} else if (envName.equals("$FSROOT")) {
				VarLib.changeEnv("$FSROOT", VarLib.getFSRoot());
			} else if (envName.equals("$USERNAME")) {
				VarLib.changeEnv("$USERNAME", VarLib.username);
			} else if (envName.equals("$HOSTNAME")) {
				VarLib.changeEnv("$HOSTNAME", VarLib.hostname);
			} else if (envName.equals("$JDOS_ROOT")) {
				VarLib.changeEnv("$VENUS_ROOT", VarLib.getDefaultDir().getAbsolutePath());
			} else if (envName.equals("$JDOS_TMP")) {
				VarLib.changeEnv("$VENUS_TMP", VarLib.getTempDir().getAbsolutePath());
			} else if (envName.equals("$JDOS_BIN")) {
				VarLib.changeEnv("$VENUS_BIN", VarLib.getBinDir().getAbsolutePath());
			} else if (envName.equals("$JDOS_CMD")) {
				VarLib.changeEnv("$VENUS_CMD", VarLib.getCmdDir().getAbsolutePath());
			} else if (envName.equals("$JDOS_DATA")) {
				VarLib.changeEnv("$VENUS_DATA", VarLib.getDataDir().getAbsolutePath());
			} else if (envName.equals("$JAVA_HOME")) {
				VarLib.changeEnv("$JAVA_HOME", VarLib.getJavaHome().getAbsolutePath());
			} else if (envName.equals("$MAX_ENV_SIZE")) {
				VarLib.changeEnv("$MAX_ENV_SIZE", "4096");
			} else if (envName.equals("$SCROLL_AT_LINES")) {
				VarLib.changeEnv("$SCROLL_AT_LINES", Integer.toString(VarLib.CMDLINE_MAX_LINE_COUNT));
			} else if (envName.equals("$CMDLINE_MAX_LINE_COUNT")) {
				VarLib.changeEnv("$CMDLINE_MAX_LINE_COUNT", "26");
			} else if (envName.equals("$MAX_CMD_QUEUE_LENGTH")) {
				VarLib.changeEnv("$MAX_CMD_QUEUE_LENGTH", "16");
			}
		}
	}
	
	public static void verifyFiles() {
		sys.log("LIB", 2, "LIB: Cannot verify files");
	}
	
	public static void cmdLinePrepare() {
		if (!Main.javafxEnabled)
			Main.mainFrameAWT.getCmdLine().setEditable(false);
		if (sys.getActivePhase().equals("init")) {
			
			sys.shellPrint(VarLib.getMOTD()); //Print message of the day, when in init phase
			VarLib.prompt = VarLib.getPromptWithPattern(VarLib.promptPattern);
			sys.shellPrint(1, "HIDDEN", VarLib.prompt);
			
		} else if (sys.getActivePhase().equals("run")) {
			
			VarLib.prompt = VarLib.getPromptWithPattern(VarLib.promptPattern);
			sys.shellPrint(AWTANSI.B_Green, "\n" + VarLib.prompt);
			
		} else {
			sys.log("LIB", 4, "Shell prepare was called during pre-init. Doing nothing, but this");
			sys.log("LIB", 4, "is unusual and should not be seen multiple times.");
			sys.log("LIB", 4, "Although, it's just a beta version by now, so it's just like that :)");
		}
		if (!Main.javafxEnabled)
			Main.mainFrameAWT.getCmdLine().setEditable(true);
	}
	public static void refreshDateTime() {
		VarLib.dateTime = new SimpleDateFormat("yyyy.MM.dd|HH:mm:ss").format(Calendar.getInstance().getTime());
	}
	public static void logWrite(String auth, int status, String message) {
		//Following values are available as status:
		//-1: Any(unknown) error
		//0: Finished successfully
		//1: Info (Shows information or hints for usage)
		//2: Warning (might be no error but shows e.g. that an old version is used)
		//3: Non-critical error (program can continue but there may be other errors)
		//4: Critical error (program must stop e.g. security issue or no sufficient permissions)
		//5: Irreversible critical error(fatal) (Damages still persist after Vexus shutdown)
		long runtime;
		try { runtime = System.currentTimeMillis() - Main.ThreadAllocMain.getWDT().getTimeStart(); }
		catch (NullPointerException npe) { runtime = -1; }
		if (status == -1) {
			System.out.println("[ " + runtime + ", -1/ERR, " + auth + " ]: " + message);
		} else if (status == 0) {
			System.out.println("[ " + runtime + ", " + auth + " ]: " + message);
		} else if (status == 1) {
			System.out.println("[ " + runtime + ", " + "1/INFO, " + auth + " ]: " + message);
		} else if (status == 2) {
			System.err.println("[ " + runtime + ", " + "2/WARN, " + auth + " ]: " + message);
		} else if (status == 3) {
			System.err.println("[ " + runtime + ", " + "3/NONCRIT, " + auth + " ]: " + message);
		} else if (status == 4) {
			System.err.println("[ " + runtime + ", " + "4/CRIT, " + auth + " ]: " + message);
		} else if (status == 5) {
			System.err.println("[ " + runtime + ", " + "5/FATAL, " + auth +  " ]: " + message.toUpperCase());
		}
		
		if (VarLib.consoleLogStream != null) {
			if (status == -1) {
				VarLib.consoleLogStream.println("[ " + VarLib.getDateTime(false)
					+ ", -1/ERR, " + auth + " ]: " + message);
			} else if (status == 0) {
				VarLib.consoleLogStream.println("[ " + VarLib.getDateTime(false)
					+ ", " + auth + " ]: " + message);
			} else if (status == 1) {
				VarLib.consoleLogStream.println("[ " + VarLib.getDateTime(false)
					+ ", " + "1/INFO, " + auth + " ]: " + message);
			} else if (status == 2) {
				VarLib.consoleLogStream.println("[ " + VarLib.getDateTime(false)
					+ ", " + "2/WARN, " + auth + " ]: " + message);
			} else if (status == 3) {
				VarLib.consoleLogStream.println("[ " + VarLib.getDateTime(false)
					+ ", " + "3/NONCRIT, " + auth + " ]: " + message);
			} else if (status == 4) {
				VarLib.consoleLogStream.println("[ " + VarLib.getDateTime(false)
					+ ", " + "4/CRIT, " + auth + " ]: " + message.toUpperCase());
			} else if (status == 5) {
				VarLib.consoleLogStream.println("[ " + VarLib.getDateTime(false)
					+ ", " + "5/FATAL, " + auth +  " ]: " + message.toUpperCase());
			}
		}
	}
}
