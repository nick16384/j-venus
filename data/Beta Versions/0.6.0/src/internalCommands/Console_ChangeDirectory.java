package internalCommands;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Map;

import main.Lib;
import engine.HighLevel;

public class Console_ChangeDirectory {
	public static String changeDirectory(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if (params == null) { //set to file system root
			Lib.setCurrentDir(new File(Lib.getFSRoot()).getAbsolutePath());
			
		} else if (params.get(0).equals("..")) { //Set one layer up: /something/whatever -> /something
			Lib.setCurrentDir(new File(Lib.getCurrentDir().replace(Lib.getCurrentDir().split(Lib.fsep)[1], ""))
					.getAbsolutePath()); //One layer up
			//TODO fix CDing into .. breaks javados with message "directory not valid"
			
		} else if (new File(Lib.getCurrentDir() + Lib.fsep + params.get(0)).isDirectory()) { //Does the folder exist in the current dir?
			Lib.setCurrentDir(new File(Lib.getCurrentDir() + Lib.fsep + params.get(0)).getAbsolutePath());
			
		} else if (new File(params.get(0)).isDirectory()) { //Is the folder in the filesystem root
			Lib.setCurrentDir(new File(params.get(0)).getAbsolutePath());
			
		} else { //Set to same folder
			//Do nothing
			return "FileWarn";
		}
		//TODO fix CDing in onw layer makes: / -> //home -> ///home/theophil -> ...!
		return null;
	}
}

//OLD CODE
		/*if (params == null) { //Is there any given path?
			Lib.setCurrentDir(dir);
			return null;
			
		} else if (!(new File(dir).exists())
				&& !(new File(params.get(0) + Lib.fsep + params.get(0)).exists())) {
				//Check whether the directory is available in root or in the current working directory
			HighLevel.shell_write(3, "INTERN.CONSOLE.CHDIR", "The path '" + params.get(0) + "' does not exist.");
			Lib.logWrite("CHDIR", -1, "The path '" + params.get(0) + "' does not exist."); return "DirectoryNotFound";
			
		} else if (!(new File(params.get(0)).isDirectory()))
				&& !Files.isDirectory(Paths.get(Lib.getCurrentDir() + Lib.fsep + params.get(0)), LinkOption.NOFOLLOW_LINKS)) {
			//Check whether params.get(0) or params.get(0) in the working directory is a directory
			HighLevel.shell_write(3, "INTERN.CONSOLE.CHDIR", "The path '" + params.get(0) + "' does not point to a directory"); //TODO Add these errors to all internal cmds
			Lib.logWrite("CHDIR", -1, "The path '" + params.get(0) + "' is not a directory."); return "NotADirectory";
			
		} else if ((params.get(0).equals("..")) && !(Lib.getCurrentDir().equalsIgnoreCase(Lib.getFSRoot()))) {
			Lib.setCurrentDir(Lib.getCurrentDir().split(Lib.fsep)[0]);
			return null;
			
		} else { //Set the working directory
			String fileString = Lib.getCurrentDir();
			if (params.get(0).startsWith(Lib.fsep)) { //If the directory is not in the current working directory
				fileString = new File(params.get(0)).getAbsolutePath(); //Just to standardize formatting (/home/ -> /home ...)
			} else {
				fileString = new File(Lib.getCurrentDir() + Lib.fsep + params.get(0)).getAbsolutePath();
			}
			Lib.setCurrentDir(fileString);
			Lib.logWrite("CHDIR", 0, "Changed working directory to " + fileString);
			return null;
		}*/