package modules;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import components.Command;
import engine.sys;
import libraries.OpenLib;

public class LoadStartup {
	public static boolean loadAndExecute() throws IOException {
		final File startupFile = new File(libraries.VarLib.getDataDir().getAbsolutePath()
				+ libraries.VarLib.fsep + "startup.jscr");
		String startupFileString = "";
		
		
		if (true) { //TODO maybe this might not work, test it!!!
			sys.log("STARTUP", 0, "Startup script file: " + startupFile.getAbsolutePath());
			startupFileString = Files.readString(Paths.get(startupFile.getAbsolutePath()));
			//Problems with symlinks. File does exist but is referred by a symlink
			sys.log("STARTUP", 0, "Read: " + startupFileString);
			
			if (startupFileString.startsWith("# JavaDOS startup script")) {
				startupFileString = startupFileString.replaceFirst("# JavaDOS startup script\n", "");
				for (String command : startupFileString.split("\n")) {
					main.CommandMain.executeCommand(new components.Command(command + " noErrorChecking"));
					return false;
				}
			} else {
				sys.log("STARTUP", 3, "The startup file is not selected correctly or it is misconfigured.");
			}
		} else {
			sys.log("STARTUP", 3, "The startup file does not exist.");
		}
		return true;
	}
}
