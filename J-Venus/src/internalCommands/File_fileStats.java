package internalCommands;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import engine.AWTANSI;
import engine.sys;
import libraries.FileCheckUtils;
import libraries.VarLib;

/**
 * Displays information about files / folders
 * Created 07/19/2022
 * @return
 */

public class File_fileStats {
	public static String fileStats (ArrayList<String> params, Map<String, String> paramsWithValues) {
		sys.shellPrintln("Params are valid: " + LIB_Utils.checkValid(params));
		if (!LIB_Utils.checkValid(params)) {
			sys.log("FSTAT", 0, "No parameter given, reading working directory permission.");
			sys.shellPrintln(AWTANSI.B_White, "Info: No file name supplied:\n"
					+ "Checking permissions for working directory.");
			sys.shellPrintln(AWTANSI.B_Green, "Element \"" + VarLib.getCurrentDir() + "\": "
					+ FileCheckUtils.getPermissions(new File(VarLib.getCurrentDir())));
		}
		//Parameters are valid, check if element exists globally
		else if (FileCheckUtils.exists(new File(params.get(0)))) {
			sys.log("FSTAT", 0, "File \"" + params.get(0) + "\" exists at root level.");
			sys.shellPrintln(AWTANSI.B_Green, "Element \""
					+ params.get(0) + "\": " + FileCheckUtils.getPermissions(new File(params.get(0))));
		}
		//Check if element exists locally (inside working directory)
		else if (FileCheckUtils.exists(new File(VarLib.getCurrentDir() + VarLib.fsep + params.get(0)))) {
			sys.log("FSTAT", 0, "File \"" + params.get(0) +  "\" exists inside working directory as \""
					+ VarLib.getCurrentDir() + VarLib.fsep + params.get(0) + "\".");
			sys.shellPrintln(AWTANSI.B_Green, "Element \""
					+ VarLib.getCurrentDir() + VarLib.fsep + params.get(0)
					+ "\": " + FileCheckUtils.getPermissions(new File(params.get(0))));
		}
		//Supplied file / folder name not valid
		else {
			sys.log("FSTAT", 3, "Specified file \"" + params.get(0) + "\" does not exist.");
			sys.shellPrint(AWTANSI.B_Yellow, "The file or folder \"" + params.get(0) + "\" does not exist.");
			return "FileErr_NotFound";
		}
		return null;
	}
}

//TODO fix line 20 error