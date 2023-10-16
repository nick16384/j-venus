package internalCommands;

import engine.InfoType;
import engine.sys;
import filesystem.FileCheckUtils;
import jfxcomponents.ANSI;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import awtcomponents.AWTANSI;
import libraries.Global;
import shell.Shell;

public class File_listDirectory {
	public static String listDirectory(ArrayList<String> params, Map<String, String> paramsWithValues) {
		ArrayList<String> listedElements = new ArrayList<>();
		String listDirName = "";
		File listDir = null;
		
		//Preparation of "listDirName" and "listDir" variable
		
		if (new ParameterChecker(params).checkValid() && FileCheckUtils.isDir(new File(params.get(0)))) {
			listDirName = FileCheckUtils.prefetchFile(new File(params.get(0))).toString();
			listDir = FileCheckUtils.prefetchFile(new File(params.get(0)));
		} else {
			listDirName = Global.getCurrentDir();
			listDir = new File(Global.getCurrentDir());
		}
		sys.log("LSDIR", InfoType.INFO, "Listing directory: '" + listDirName + "'");
		
		if (!FileCheckUtils.isDirStrict(listDir)) {
			return "FileErr_NotADir";
		}
		
		//Actual file listing begins here
		
		for (File file : listDir.listFiles()) {
			String fileString = file.getName();
			
			//Note: On linux, only getCanonicialFile() will reveal, whether some element is a file or folder
			//The getCanonicialFile() method is implemented in the FCU class (FileCheckUtils)
			if (FileCheckUtils.isDirStrict(file)) {
				Shell.println(ANSI.B_Green, fileString);
			} else if (FileCheckUtils.isFileStrict(file)) {
				Shell.println(ANSI.B_Cyan, fileString);
			} else {
				Shell.println(ANSI.B_Magenta, fileString);
			}
		}
		
		return null;
	}
}