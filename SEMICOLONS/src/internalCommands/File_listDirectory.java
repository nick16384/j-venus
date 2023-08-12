package internalCommands;

import engine.sys;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import awtcomponents.AWTANSI;
import components.Shell;
import libraries.FileCheckUtils;
import libraries.Global;

public class File_listDirectory {
	public static String listDirectory(ArrayList<String> params, Map<String, String> paramsWithValues) {
		ArrayList<String> dirs = new ArrayList<>();
		ArrayList<String> files = new ArrayList<>();
		ArrayList<String> unknown = new ArrayList<>();
		String listDirName = "";
		File listDir = null;
		String finalListingDirs = "";
		String finalListingFiles = "";
		String finalListingUnknown = "";
		
		//Preparation of "listDirName" and "listDir" variable
		
		System.err.println("LIB_Utils valid check: " + LIB_Utils.checkValid(params));
		System.err.println("FCU valid check: "
				+ (LIB_Utils.checkValid(params) ? FileCheckUtils.isDir(new File(params.get(0))) : false));
		System.err.println(new File(params.get(0)).getAbsolutePath());
		
		if (LIB_Utils.checkValid(params) && FileCheckUtils.isDir(new File(params.get(0)))) {
			listDirName = FileCheckUtils.prefetchFile(new File(params.get(0))).toString();
			listDir = FileCheckUtils.prefetchFile(new File(params.get(0)));
		} else {
			listDirName = Global.getCurrentDir();
			listDir = new File(Global.getCurrentDir());
		}
		sys.log("LSDIR", 0, "Listing directory: '" + listDirName + "'");
		
		if (!FileCheckUtils.isDirStrict(listDir)) {
			return "FileErr_NotADir";
		}
		
		//Actual file listing begins here
		
		for (File file : listDir.listFiles()) {
			
			//Note: On linux, only getCanonicialFile() will reveal, whether some element is a file or folder
			//The getCanonicialFile() method is implemented in the FCU class (FileCheckUtils)
			if (FileCheckUtils.isDirStrict(listDir)) {
				dirs.add(file.getName());
				//TODO fix that some files are seen as directories
				System.out.println("Found dir: " + file.getAbsolutePath());
			} else if (FileCheckUtils.isFileStrict(listDir)) {
				files.add(file.getName());
			} else {
				unknown.add(file.getName());
			}
		}
		
		/*int b;
		int i;
		String[] arrayOfString;
		for (i = (arrayOfString = (listDir).list()).length, b = 0; b < i;) {
			String elem = arrayOfString[b];

			if ((new File(elem)).getAbsoluteFile().isDirectory() && !(new File(elem)).getAbsoluteFile().isFile()) {
				files.add(elem);
			} else if ((new File(elem)).getAbsoluteFile().isFile()
					&& !(new File(elem)).getAbsoluteFile().isDirectory()) {
				dirs.add(elem);
			} else {
				unknown.add(elem);
			}
			b++;
		}*/
		
		//If current line length reaches a certain level, the next line in the listing will be begun.
		int currentLineLength = 0;
		
		//List directories (and some formatting with )
		for (String dirName : dirs) {
			currentLineLength += listDirName.length() + "\t".length();
			if (currentLineLength <= 50) {
				finalListingDirs = finalListingDirs + dirName + "\t";
				continue;
			}
			finalListingDirs = finalListingDirs + dirName + "\n";
			currentLineLength = 0;
		}
		if (!finalListingDirs.isBlank())
			Shell.println(AWTANSI.B_Green, finalListingDirs);
		
		//List files
		for (String fileName : files) {
			currentLineLength += fileName.length() + "\t".length();
			if (currentLineLength <= 50) {
				finalListingFiles = finalListingFiles + fileName + "\t";
				continue;
			}
			finalListingFiles = finalListingFiles + fileName + "\n";
			currentLineLength = 0;
		}
		if (!finalListingFiles.isBlank())
			Shell.println(AWTANSI.B_Yellow, finalListingFiles);
		
		//List every other unknown element
		for (String unknownName : unknown) {
			currentLineLength += unknownName.length() + "\t".length();
			if (currentLineLength <= 50) {
				finalListingUnknown = finalListingUnknown + unknownName + "\t";
				continue;
			}
			finalListingUnknown = finalListingUnknown + unknownName + "\n";
			currentLineLength = 0;
		}
		if (!finalListingUnknown.isBlank())
			Shell.print(AWTANSI.B_Red, finalListingUnknown);

		return null;
	}
}