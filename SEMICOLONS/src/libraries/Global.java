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

import awtcomponents.AWTANSI;
import engine.Runphase;
import engine.sys;
import filesystem.InternalFiles;
import filesystem.VirtualizedLocation;
import jfxcomponents.GUIManager;
import main.Main;

/**
 * Class holding all global variables
 */

public class Global {
	protected static final String VERSION = "23.10 IOTA";
	//Message of the day -> start msg before any commands e.g. Welcome to ...
	protected static String motd = "J-Venus Version " + VERSION + " Warning: Beta state, problems may occur.\n"
			+ "Copyleft () 2021 - 2023 The SEMICOLONS Project. All rights reserved.\n"
			+ "GNU General Public License v3. Created with Eclipse Oracle.\n"
			+ "Warning: Log is currently very verbose due to debugging reasons.\n"
			+ "Will be reduced within alpha versions.\n";
	protected static String path = "";
	protected static String dateTime = "NODATETIME";
	protected static String username = "";
	protected static String hostname = "";
	public static final int CMDLINE_MAX_LINE_COUNT = 26;
	public static final int DEFAULT_MAX_HISTORY_SIZE = 4096;
	public static PrintStream consoleLogStream = null;
	public static String consoleString = "";
	protected static Thread mainThread;
	public final static String fsep = FileSystems.getDefault().getSeparator();
	protected static String osName = "UnknownOS";
	protected static Map<String, File> extCommands = new HashMap<>();
	protected static VirtualizedLocation FSRoot;
	protected static VirtualizedLocation RootDir;
	protected static VirtualizedLocation TempDir;
	protected static VirtualizedLocation BinDir;
	
	@Deprecated
	protected static VirtualizedLocation CmdDir;
	
	protected static VirtualizedLocation DataDir;
	protected static VirtualizedLocation HomeDir;
	
	@Deprecated
	protected static File javaHome;
	@Deprecated
	protected static File javaExec;
	protected static Runphase currentPhase = Runphase.PREINIT;
	// Program arguments:
	public static boolean fullscreen = false;
	public static boolean singleThreaded = false;
	public static boolean javafxEnabled = true;
	
	public static String getVersion() {
		return VERSION;
	}
	public static String getCurrentDir() {
		return path;
	}
	public static VirtualizedLocation getRootDir() {
		return RootDir;
	}
	public static VirtualizedLocation getTempDir() {
		return TempDir;
	}
	public static VirtualizedLocation getBinDir() {
		return BinDir;
	}
	@Deprecated
	public static VirtualizedLocation getCmdDir() {
		return CmdDir;
	}
	public static VirtualizedLocation getDataDir() {
		return DataDir;
	}
	public static VirtualizedLocation getHomeDir() {
		return HomeDir;
	}
	public static File getLogFile() {
		return InternalFiles.getLogFile();
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
	public static String getOSName() {
		return osName;
	}
	public static Thread getMainThreadReference() {
		return mainThread;
	}
	public static String getFSRoot() {
		return FSRoot.getAbsolutePath();
	}
	public static void setCurrentDir(String newDir) {
		path = newDir;
	}
	public static void setExtCommands(Map<String, File> newExtCommands) {
		extCommands = newExtCommands;
	}
	
	public static String getDateTime(boolean includeDate) {
		if (includeDate) {
			Global.dateTime = new SimpleDateFormat("yyyy.MM.dd|HH:mm:ss").format(Calendar.getInstance().getTime());
		} else {
			Global.dateTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		}
		return Global.dateTime;
	}
	
	public static void refreshDateTime() {
		dateTime = new SimpleDateFormat("yyyy.MM.dd|HH:mm:ss").format(Calendar.getInstance().getTime());
	}
	
	public static String getHostname() {
		return hostname;
	}
	public static String getUsername() {
		return username;
	}
	
	public static String getMOTD () {
		return motd.isBlank() ? "MOTD is empty\n" : motd;
	}
	
	public static void setNextRunphase() {
		currentPhase = Runphase.getNextPhase(currentPhase);
		if (currentPhase.equals(Runphase.RUN)) {
			synchronized (Runphase.RUN) { Runphase.RUN.notifyAll(); }
		}
		sys.log("--- RUNPHASE CHANGED: " + currentPhase.toString() + " ---\n"
				+ currentPhase.getDescription());
	}
	
	public static void setErrorRunphase() {
		currentPhase = Runphase.ERR;
		sys.log("--- RUNPHASE CHANGED: " + currentPhase.toString() + " ---\n"
				+ currentPhase.getDescription());
	}
	
	public static Runphase getCurrentPhase() {
		return currentPhase;
	}
	
	public static void waitUntilReady() {
		while (!currentPhase.equals(Runphase.RUN)
				|| GUIManager.getCmdLine() == null) {
			synchronized (Runphase.RUN) {
				try { Runphase.RUN.wait(50); }
				catch (InterruptedException ie) { ie.printStackTrace(); }
			}
		}
	}
}
