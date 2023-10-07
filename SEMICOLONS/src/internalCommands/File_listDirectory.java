package internalCommands;

import engine.InfoType;
import engine.sys;
import filesystem.FileCheckUtils;
<<<<<<< HEAD
import jfxcomponents.JFXANSI;
=======
>>>>>>> 90664cc5e3f79d38ab54e22e5d5fe99879274032

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

<<<<<<< HEAD
import awtcomponents.AWTANSI;
import libraries.Global;
import shell.Shell;
=======
import libraries.VarLib;
>>>>>>> 90664cc5e3f79d38ab54e22e5d5fe99879274032

public class File_listDirectory {
	public static String listDirectory(ArrayList<String> params, Map<String, String> paramsWithValues) {
		ArrayList<String> listedElements = new ArrayList<>();
		String listDirName = "";
		File listDir = null;
		
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