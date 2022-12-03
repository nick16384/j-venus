package internalCommands;

import java.io.File;
import java.nio.file.*;

import main.Lib;
import engine.HighLevel;

public class Console_ChangeDirectory {
	public static String changeDirectory(String[] reqParams, String[] optParams) {
		if (reqParams.length != 1) { //Is there any given path?
			System.err.println("INTERN.CONSOLE.CHDIR: reqParam Parse error."); //TODO add this to all failed reqParam checks!!
			return "reqParamParseInvalid";
		} else if ((Files.notExists(Paths.get(reqParams[0]), LinkOption.NOFOLLOW_LINKS))
				&& Files.notExists(Paths.get(Lib.getCurrentDir() + Lib.fsep + reqParams[0]), LinkOption.NOFOLLOW_LINKS)) {
				//Check whether the directory is available in root or in the current working directory
			HighLevel.shell_write(3, "INTERN.CONSOLE.CHDIR", "The path '" + reqParams[0] + "' does not exist.");
			System.err.println("INTERN.CONSOLE.CHDIR: The path '" + reqParams[0] + "' does not exist."); return "DirectoryNotFound";
		} else if ((!Files.isDirectory(Paths.get(reqParams[0]), LinkOption.NOFOLLOW_LINKS))
				&& !Files.isDirectory(Paths.get(Lib.getCurrentDir() + Lib.fsep + reqParams[0]), LinkOption.NOFOLLOW_LINKS)) {
			//Check whether reqParams[0] or reqParams[0] in the working directory is a directory
			HighLevel.shell_write(3, "INTERN.CONSOLE.CHDIR", "The path '" + reqParams[0] + "' does not point to a directory"); //TODO Add these errors to all internal cmds
			System.err.println("INTERN.CONSOLE.CHDIR: The path '" + reqParams[0] + "' is not a directory."); return "NotADirectory";
		} else { //Set the working directory
			String fileString = Lib.getCurrentDir();
			if (reqParams[0].startsWith(Lib.fsep)) { //If the directory is not in the current working directory
				fileString = new File(reqParams[0]).getAbsolutePath(); //Just to standardize formatting (/home/ -> /home ...)
			} else {
				fileString = new File(Lib.getCurrentDir() + Lib.fsep + reqParams[0]).getAbsolutePath();
			}
			Lib.setCurrentDir(fileString);
			System.out.println("INTERN.CONSOLE.CHDIR: Changed working directory to " + fileString);
			return null;
		}
	}
}
