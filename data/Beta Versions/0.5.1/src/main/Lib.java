/**
 * This class contains useful utilities and stores a variety of variables
 */

package main;

import java.io.File;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import engine.HighLevel;

public class Lib {
	private static final String ver = "0.5.1";
	private static String path = "";
	private static String dateTime = "NODATETIME";
	private static String defaultPrompt = dateTime + "||" + path + "~>";
	private static String prompt = dateTime + "||" + path + "~>";
	private static Thread mainThread;
	public final static String fsep = FileSystems.getDefault().getSeparator();
	private static String osName = "UnknownOS";
	private static Map<String, File> extCommands = new HashMap<>();
	private static File jDOSDefDir;
	private static File jDOSTempDir;
	private static File jDOSBinDir;
	private static File jDOSCmdDir;
	private static File javaHome;
	
	public static void initVars() {
		if (System.getProperty("java.home") == null) {
			logWrite("LIB", 2, "INITVARS: The JAVA_HOME variable was not found.");
			logWrite("LIB", 2, "INITVARS: JavaDOS needs JDK installed in order to work. Exiting...");
			new modules.WatchdogThread().shutdownJavaDOS(1);
		}
		osName = System.getProperty("os.name");
		if (osName.equalsIgnoreCase("Linux")) { //If operating system is linux...
			jDOSDefDir = new File(System.getenv("HOME") + fsep + "Desktop" + fsep + "JavaDOS");
			jDOSTempDir = new File(jDOSDefDir.getAbsolutePath() + fsep + "temp");
			jDOSBinDir = new File(jDOSDefDir.getAbsolutePath() + fsep + "bin");
			jDOSCmdDir = new File(jDOSDefDir.getAbsolutePath() + fsep + "commands");
			path = "/";
		} else if (osName.contains("Windows")) { //If operating system is any Windows version...
			jDOSDefDir = new File(System.getenv("USERPROFILE") + fsep + "Desktop" + fsep + "JavaDOS");
			jDOSTempDir = new File(jDOSDefDir.getAbsolutePath() + fsep + "temp");
			jDOSBinDir = new File(jDOSDefDir.getAbsolutePath() + fsep + "bin");
			jDOSCmdDir = new File(jDOSDefDir.getAbsolutePath() + fsep + "commands");
			path = System.getenv("SYSTEMROOT") + "\\";
		}
		javaHome = new File(System.getProperty("java.home"));
		mainThread = Thread.currentThread();
		if (osName.equalsIgnoreCase("UnknownOS")) {
			logWrite("LIB", 2, "INITVARS: Operating system not supported. Exiting...");
			new modules.WatchdogThread().shutdownJavaDOS(1);
		}
	}
	
	public static void verifyFiles() {
		logWrite("LIB", 2, "LIB: Cannot verify files");
	}
	
	public static void cmdLinePrepare(boolean InitPhase) {
		Main.cmdLine.setEditable(false);
		refreshPrompt();
		if (InitPhase) {
			
			HighLevel.shell_write(1, "HIDDEN", "JavaDOS Version: " + ver + " Warning: Beta Version, Problems may occur! \n");
			HighLevel.shell_write(1, "HIDDEN", "Copyright(C) 2021 The JavaDOS Project. All rights reserved. \n");
			HighLevel.shell_write(1, "HIDDEN", "GNU General Public License: Oracle Corporation \n");
			HighLevel.shell_write(1, "HIDDEN", "\n");
			HighLevel.shell_write(1, "HIDDEN", "General System information: \n");
			HighLevel.shell_write(1, "HIDDEN", "Running on: " + osName + "\n");
			HighLevel.shell_write(1, "HIDDEN", "Set Install Folder: " + jDOSDefDir.getAbsolutePath());
			HighLevel.shell_write(1, "HIDDEN", "\n");
			HighLevel.shell_write(1, "HIDDEN", "\n");
			HighLevel.shell_write(1, "HIDDEN", prompt);
		} else {
			HighLevel.shell_write(1, "HIDDEN", "\n");
			
			try {
				if (!Main.cmdLine.getText().isBlank()) { //Just a big bunch of code to add an extra empty line(if not existent)
					if (!Main.cmdLine.getText().split("\n")[Main.cmdLine.getText().split("\n").length - 1].isBlank()) { //You don't have to understand this
						HighLevel.shell_write(1, "HIDDEN", "\n");
					} else { logWrite("LIB", 2, "Is blank"); }
				}
			} catch (ArrayIndexOutOfBoundsException aioobe) {
				logWrite("LIB", 2, "LIB: You shouldn't see this, but if so, an ArrayIndexOutOfBoundsException occured. This is referred as nope.");
			} // End of the extra line adding
			
			HighLevel.shell_write(1, "HIDDEN", prompt);
			
		}
		Main.cmdLine.setEditable(true);
	}
	public static void refreshDateTime() {
		dateTime = new SimpleDateFormat("yyyy.MM.dd|HH:mm:ss").format(Calendar.getInstance().getTime());
	}
	public static String getDateTime(boolean includeDate) {
		if (includeDate) {
			dateTime = new SimpleDateFormat("yyyy.MM.dd|HH:mm:ss").format(Calendar.getInstance().getTime());
		} else {
			dateTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		}
		return dateTime;
	}
	private static void refreshPrompt() {
		if (prompt.equals(defaultPrompt)) {
			defaultPrompt = dateTime + "||" + path + "~>";
			prompt = dateTime + "||" + path + "~>";
		} else {
			logWrite("LIB", 0, "Prompt doesn#t require refreshing, because it has no changing parts.");
		}
	}
	public static void logWrite(String auth, int status, String message) {
		//Following values are available as status:
		//-1: Any(unknown) error
		//0: Finished successfully
		//1: Info (Shows information or hints for usage)
		//2: Warning (might be no error but shows e.g. that an old version is used)
		//3: Non-critical error (program can continue but there may be other errors)
		//4: Critical error (program must stop e.g. security issue or no sufficient permissions)
		//5: Irreversible critical error(fatal) (Damages still persist after JDOS shutdown)
		if (status == -1) {
			System.out.println("[ " + getDateTime(false) + ", -1/ERR, " + auth + " ]: " + message);
		} else if (status == 0) {
			System.out.println("[ " + getDateTime(false) + ", " + auth + " ]: " + message);
		} else if (status == 1) {
			System.out.println("[ " + getDateTime(false) + ", " + "1/INFO, " + auth + " ]: " + message);
		} else if (status == 2) {
			System.err.println("[ " + getDateTime(false) + ", " + "2/WARN, " + auth + " ]: " + message);
		} else if (status == 3) {
			System.err.println("[ " + getDateTime(false) + ", " + "3/NONCRIT, " + auth + " ]: " + message);
		} else if (status == 4) {
			System.err.println("[ " + getDateTime(false) + ", " + "4/CRIT, " + auth + " ]: " + message.toUpperCase());
		} else if (status == 5) {
			System.err.println("[ " + getDateTime(false) + ", " + "5/FATAL, " + auth +  " ]: " + message.toUpperCase());
		}
	}
	
	public static String getVersion() {
		return ver;
	}
	public static String getCurrentDir() {
		return path;
	}
	public static File getDefaultDir() {
		return jDOSDefDir;
	}
	public static File getTempDir() {
		return jDOSTempDir;
	}
	public static File getBinDir() {
		return jDOSBinDir;
	}
	public static File getCmdDir() {
		return jDOSCmdDir;
	}
	public static Map<String, File> getExtCommands() {
		return extCommands;
	}
	public static File getJavaHome() {
		return javaHome;
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
	public static void setCurrentDir(String newDir) {
		path = newDir;
	}
	public static void setExtCommands(Map<String, File> newExtCommands) {
		extCommands = newExtCommands;
	}
	public static void setPrompt(String newPrompt) {
		if (newPrompt.equalsIgnoreCase("default")) {
			prompt = defaultPrompt;
		} else {
			prompt = newPrompt;
		}
	}
}
