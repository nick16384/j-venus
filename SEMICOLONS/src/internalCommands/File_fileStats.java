package internalCommands;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import awtcomponents.AWTANSI;
import engine.InfoType;
import engine.sys;
import filesystem.FileCheckUtils;
import jfxcomponents.ANSI;
import libraries.Global;
import shell.Shell;

/**
 * Displays information about files / folders
 * Created 07/19/2022
 * @return
 */

public class File_fileStats {
	public static String fileStats (ArrayList<String> params, Map<String, String> paramsWithValues) {
		Shell.println("Params are valid: " + new ParameterChecker(params).checkValid());
		if (!new ParameterChecker(params).checkValid()) {
			sys.log("FSTAT", InfoType.STATUS, "No parameter given, reading working directory permission.");
			Shell.println(ANSI.B_White, "Info: No file name supplied:\n"
					+ "Checking permissions for working directory.");
			Shell.println(ANSI.B_Green, "Element \"" + Global.getCurrentDir() + "\": "
					+ FileCheckUtils.getPermissions(new File(Global.getCurrentDir())));
		}
		//Parameters are valid, check if element exists globally
		else if (FileCheckUtils.exists(new File(params.get(0)))) {
			sys.log("FSTAT", InfoType.DEBUG, "File \"" + params.get(0) + "\" exists at root level.");
			Shell.println(ANSI.B_Green, "Element \""
					+ params.get(0) + "\": " + FileCheckUtils.getPermissions(new File(params.get(0))));
		}
		//Check if element exists locally (inside working directory)
		else if (FileCheckUtils.exists(new File(Global.getCurrentDir() + sys.fsep + params.get(0)))) {
			sys.log("FSTAT", InfoType.DEBUG, "File \"" + params.get(0) +  "\" exists inside working directory as \""
					+ Global.getCurrentDir() + sys.fsep + params.get(0) + "\".");
			Shell.println(ANSI.B_Green, "Element \""
					+ Global.getCurrentDir() + sys.fsep + params.get(0)
					+ "\": " + FileCheckUtils.getPermissions(new File(params.get(0))));
		}
		//Supplied file / folder name not valid
		else {
			sys.log("FSTAT", InfoType.ERR, "Specified file \"" + params.get(0) + "\" does not exist.");
			Shell.print(ANSI.B_Yellow, "The file or folder \"" + params.get(0) + "\" does not exist.");
			return "FileErr_NotFound";
		}
		return null;
	}
}

//TODO fix line 20 error