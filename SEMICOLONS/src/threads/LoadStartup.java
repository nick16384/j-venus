package threads;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import engine.InfoType;
import engine.sys;

/**
 * This class'es intention was to have a series of commands executed after startup.
 * @deprecated The implementation needs to be rewritten, because of ugly code and old API usage.
 */
@Deprecated
public class LoadStartup {
	public static boolean loadAndExecute() throws IOException {
		final File startupFile = new File(libraries.Global.getDataDir().getAbsolutePath()
				+ sys.fsep + "startup.jscr");
		String startupFileString = "";
		
		
		if (true) { //TODO maybe this might not work, test it!!!
			sys.log("STARTUP", InfoType.DEBUG, "Startup script file: " + startupFile.getAbsolutePath());
			startupFileString = Files.readString(Paths.get(startupFile.getAbsolutePath()));
			//Problems with symlinks. File does exist but is referred by a symlink
			sys.log("STARTUP", InfoType.DEBUG, "Read: " + startupFileString);
			
			if (startupFileString.startsWith("# JavaDOS startup script")) {
				startupFileString = startupFileString.replaceFirst("# JavaDOS startup script\n", "");
				for (String command : startupFileString.split("\n")) {
					new components.Command(command + " noErrorChecking").start();
					return false;
				}
			} else {
				sys.log("STARTUP", InfoType.ERR, "The startup file is not selected correctly or it is misconfigured.");
			}
		} else {
			sys.log("STARTUP", InfoType.ERR, "The startup file does not exist.");
		}
		return true;
	}
}
