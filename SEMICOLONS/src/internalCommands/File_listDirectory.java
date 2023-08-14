package internalCommands;

import engine.InfoType;
import engine.sys;
import jfxcomponents.JFXANSI;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import awtcomponents.AWTANSI;
import libraries.FileCheckUtils;
import libraries.Global;
import shell.Shell;

public class File_listDirectory {
	public static String listDirectory(ArrayList<String> params, Map<String, String> paramsWithValues) {
		ArrayList<String> listedElements = new ArrayList<>();
		String listDirName = "";
		File listDir = null;
		String finalListingDirs = "";
		String finalListingFiles = "";
		String finalListingUnknown = "";
		
		//Preparation of "listDirName" and "listDir" variable
		
		if (LIB_Utils.checkValid(params) && FileCheckUtils.isDir(new File(params.get(0)))) {
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
				Shell.println(AWTANSI.B_Green, fileString);
			} else if (FileCheckUtils.isFileStrict(file)) {
				Shell.println(AWTANSI.B_Cyan, fileString);
			} else {
				Shell.println(AWTANSI.B_Magenta, fileString);
			}
		}
		
		return null;
	}
}