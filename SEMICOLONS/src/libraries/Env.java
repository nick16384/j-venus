package libraries;

import java.util.HashMap;
import java.util.Map;

import awtcomponents.AWTANSI;
import components.Shell;
import engine.InfoType;
import engine.Runphase;
import engine.sys;

/**
 * Contains useful functions for creating, modifying and deleting environment variables.
 */

public class Env {
	//Contains environment variables visible by the user and to other commands
	protected static Map<String, String> env = new HashMap<>();
	
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
				Shell.print(2, "HIDDEN", "Maximum environment variable amount exceeded.\n"
						+ "Modify $MAX_ENV_SIZE to change. Warning: values above default (4096)\n"
						+ "Can lead to increased memory usage. Normally, this limit is not reached,\n"
						+ "so please check for loops that add Envs each time. Also you may try\n"
						+ "restarting JavaDOS.");
				return;
			}
		} catch (NullPointerException npe) {
			if (Global.getCurrentPhase().equals(Runphase.RUN)) {
				Shell.print(2, "VARLIB", "Environment variable '$MAX_ENV_SIZE' does not exist.\n"
						+ "Please add it manually or run 'chEnv -update $MAX_ENV_SIZE'.\n");
				sys.log("VARLIB", InfoType.WARN, "Environment variable '$MAX_ENV_SIZE' does not exist.\n"
						+ "Please add it manually or run 'chEnv -update $MAX_ENV_SIZE'.");
				npe.printStackTrace();
			}
		}
		if (key == null) {
			sys.log("VARLIB", InfoType.ERR, "Tried to add envV with key null. Exited with error.");
			Shell.print(3, "VARLIB", "Cannot add envV with key null.\n");
			return;
		} else if (val == null && Global.getCurrentPhase().equals(Runphase.RUN)) {
			sys.log("VARLIB", InfoType.ERR, "Tried to set an envV with value 'null', when in RUN phase. Exited with error.");
			Shell.print(3, "VARLIB", "Cannot set an envV with value 'null', when in RUN phase.\n");
		}
		
		String envMsgOut = "New environment variable '" + key + "' -> '" + val + "' : ";
		env.put(key, val);
		
		if (getEnv(key) != null && getEnv(key).equals(val)) {
			envMsgOut += "SUCCESS";
			if (!Global.getCurrentPhase().equals(Runphase.INIT))
				Shell.print(AWTANSI.B_Green, "Success: " + key + " -> " + val + "\n");
		} else {
			envMsgOut += "FAIL";
			if (!Global.getCurrentPhase().equals(Runphase.INIT)) {
				Shell.print(AWTANSI.B_Yellow, "Could not create envV. Information below:\n");
				Shell.print("Created variable, but validation failed \\/\n"
						+ "Method call key: " + key + "\n"
						+ "Method call value: " + val + "\n"
						+ "envV Value with getEnv() call: " + getEnv(key) + "\n");
				Shell.print(AWTANSI.B_Magenta, "Try 'env' to see, if your envV exists or try again.\n");
			}
		}
		sys.log("VARLIB", InfoType.DEBUG, envMsgOut);
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
				sys.log("VARLIB:CHENV", InfoType.WARN, "Tried to modify special variable or constant ($$*). "
						+ "This incident will be reported.");
				Shell.print(2, "HIDDEN", "Tried to modify special variable or constant ($$*).\n"
						+ "This incident will be reported.\n");
			}
		}
	}
	
	public static void updateEnv(String envName) {
		if (envName.equalsIgnoreCase("$$ALL")) {
			changeEnv("$VERSION", Global.getVersion());
			changeEnv("$PATH", Global.path);
			changeEnv("$FSROOT", Global.getFSRoot());
			changeEnv("$USERNAME", Global.username);
			changeEnv("$HOSTNAME", Global.hostname);
			changeEnv("$VENUS_ROOT", Global.getDefaultDir().getAbsolutePath());
			changeEnv("$VENUS_TMP", Global.getTempDir().getAbsolutePath());
			changeEnv("$VENUS_BIN", Global.getBinDir().getAbsolutePath());
			changeEnv("$VENUS_CMD", Global.getCmdDir().getAbsolutePath());
			changeEnv("$VENUS_DATA", Global.getDataDir().getAbsolutePath());
			changeEnv("$JAVA_HOME", Global.getJavaHome().getAbsolutePath());
			changeEnv("$MAX_ENV_COUNT", "4096");
			changeEnv("$SCROLL_AT_LINES", Integer.toString(Global.CMDLINE_MAX_LINE_COUNT));
			changeEnv("$CMDLINE_MAX_LINE_COUNT", "26");
			changeEnv("$MAX_CMD_QUEUE_LENGTH", "16");
		} else if (getEnv(envName) != null) {
			if (envName.equals("$VERSION")) {
				changeEnv("$VERSION", Global.getVersion());
			} else if (envName.equals("$PATH")) {
				changeEnv("$PATH", Global.path);
			} else if (envName.equals("$FSROOT")) {
				changeEnv("$FSROOT", Global.getFSRoot());
			} else if (envName.equals("$USERNAME")) {
				changeEnv("$USERNAME", Global.username);
			} else if (envName.equals("$HOSTNAME")) {
				changeEnv("$HOSTNAME", Global.hostname);
			} else if (envName.equals("$JDOS_ROOT")) {
				changeEnv("$VENUS_ROOT", Global.getDefaultDir().getAbsolutePath());
			} else if (envName.equals("$JDOS_TMP")) {
				changeEnv("$VENUS_TMP", Global.getTempDir().getAbsolutePath());
			} else if (envName.equals("$JDOS_BIN")) {
				changeEnv("$VENUS_BIN", Global.getBinDir().getAbsolutePath());
			} else if (envName.equals("$JDOS_CMD")) {
				changeEnv("$VENUS_CMD", Global.getCmdDir().getAbsolutePath());
			} else if (envName.equals("$JDOS_DATA")) {
				changeEnv("$VENUS_DATA", Global.getDataDir().getAbsolutePath());
			} else if (envName.equals("$JAVA_HOME")) {
				changeEnv("$JAVA_HOME", Global.getJavaHome().getAbsolutePath());
			} else if (envName.equals("$MAX_ENV_SIZE")) {
				changeEnv("$MAX_ENV_SIZE", "4096");
			} else if (envName.equals("$SCROLL_AT_LINES")) {
				changeEnv("$SCROLL_AT_LINES", Integer.toString(Global.CMDLINE_MAX_LINE_COUNT));
			} else if (envName.equals("$CMDLINE_MAX_LINE_COUNT")) {
				changeEnv("$CMDLINE_MAX_LINE_COUNT", "26");
			} else if (envName.equals("$MAX_CMD_QUEUE_LENGTH")) {
				changeEnv("$MAX_CMD_QUEUE_LENGTH", "16");
			}
		}
	}
}
