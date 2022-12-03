package internalCommands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import engine.HighLevel;
import main.Lib;

public class File_listDirectory {
	public static String listDirectory(ArrayList<String> params, Map<String, String> paramsWithValues) {
		ArrayList<String> dirs = new ArrayList<>();
		ArrayList<String> files = new ArrayList<>();
		ArrayList<String> unknown = new ArrayList<>();
		String dir = ""; //The selected directory
		if ((params != null) && (Files.isDirectory(Paths.get(params.get(0)), LinkOption.NOFOLLOW_LINKS))) {
			dir = params.get(0);
		} else {
			dir = Lib.getCurrentDir();
		}
		Lib.logWrite("LSDIR", 0, "Listing directory: '" + dir + "'");
		
		
		if (Files.isDirectory(Paths.get(dir), LinkOption.NOFOLLOW_LINKS)) {
			for (String elem : new File(dir).list()) {
				//if (Files.isDirectory(Paths.get(elem), LinkOption.NOFOLLOW_LINKS)) { //Test for directories
				/*if (new File(elem).isDirectory()) {
					dirs.add(elem);
				} else { //Test for files
					files.add(elem);
				}*/
				if (new File(elem).getAbsoluteFile().isDirectory() &&
						!(new File(elem).getAbsoluteFile().isFile())) {
					files.add(elem);
				} else if ((new File(elem).getAbsoluteFile().isFile()) &&
						!(new File(elem).getAbsoluteFile().isDirectory())) {
					dirs.add(elem);
				} else {
					unknown.add(elem);
				}
			}
			int index = 0;
			for (String fileName : files) {
				if (index <= 3) {
					HighLevel.shell_write(3, "HIDDEN", fileName + "\t");
					index++;
				} else {
					HighLevel.shell_write(3, "HIDDEN", fileName + "\n");
					index = 0;
				}
			}
			for (String dirName : dirs) {
				if (index <= 3) {
					HighLevel.shell_write(2, "HIDDEN", dirName + "\t");
					index++;
				} else {
					HighLevel.shell_write(2, "HIDDEN", dirName + "\n");
					index = 0;
				}
			}
			for (String unknownName : unknown) {
				if (index <= 3) {
					HighLevel.shell_write(4, "HIDDEN", unknownName + "\t");
					index++;
				} else {
					HighLevel.shell_write(4, "HIDDEN", unknownName + "\n");
					index = 0;
				}
			}
		} else {
			Lib.logWrite("LSDIR", 3, "Cannot read directory.");
			return "FilePermissionError";
		}
		
		
		
		return null;
		}
	}
	//OLD CODE
		/*int index = 0;
		
		if ((params != null) && (params.get(0) != null)
				&& (Files.exists(Paths.get(params.get(0)), LinkOption.NOFOLLOW_LINKS))
				&& (Files.isDirectory(Paths.get(params.get(0)), LinkOption.NOFOLLOW_LINKS))
				&& (new File(params.get(0)).list() != null)) { //If a path is given
			
			for (String file : new File(params.get(0)).list()) {
				String fileOrigin = file.toString();
				String[] fileTmpArray = file.split(Lib.fsep);
				file = fileTmpArray[fileTmpArray.length - 1]; //Remove prefix: /usr/bin/jvm -> jvm
				
				//Write directories in green and files in yellow
				if (Files.isDirectory(Paths.get(fileOrigin), LinkOption.NOFOLLOW_LINKS)) {
					if (index == 3) {
						HighLevel.shell_write(2, "HIDDEN", file.toString() + "\n");
						index = 0;
					} else {
						HighLevel.shell_write(2, "HIDDEN", file.toString() + "\t");
					}
				} else {
					if (index == 3) {
						HighLevel.shell_write(3, "HIDDEN", file.toString() + "\n");
						index = 0;
					} else {
						HighLevel.shell_write(3, "HIDDEN", file.toString() + "\t");
					}
				}
				index++;
			}
		} else if (params == null) {
			try {
				Object[] fileListArray = Files.list(Paths.get(Lib.getCurrentDir())).toArray();
				for (Object file : fileListArray) {
					String fileOrigin = file.toString();
					String[] fileTmpArray = file.toString().split(Lib.fsep);
					file = fileTmpArray[fileTmpArray.length - 1]; //Remove prefix: /usr/bin/jvm -> jvm
					
					//Write directories in green and files in yellow
					if (Files.isDirectory(Paths.get(fileOrigin), LinkOption.NOFOLLOW_LINKS)) {
						if (index == 3) {
							HighLevel.shell_write(2, "HIDDEN", file.toString() + "\n");
							index = 0;
						} else {
							HighLevel.shell_write(2, "HIDDEN", file.toString() + "\t");
						}
					} else {
						if (index == 3) {
							HighLevel.shell_write(3, "HIDDEN", file.toString() + "\n");
							index = 0;
						} else {
							HighLevel.shell_write(3, "HIDDEN", file.toString() + "\t");
						}
					}
					index++;
				}
			} catch (IOException ioe) {
				Lib.logWrite("LSDIR", 0, "Could not list directory");
				return "UnknownFileError";
			}
		} else {
			HighLevel.shell_write(3, "HIDDEN", "Directory is empty or \n");
			HighLevel.shell_write(3, "HIDDEN", "Unknown Read Error");
		}
		return null;
	}
}*/
