package internalCommands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import engine.HighLevel;
import main.Lib;

public class File_listDirectory {
	public static String listDirectory(ArrayList<String> params, Map<String, String> paramsWithValues) {
		int index = 0;
		
		if ((params != null) && (params.size() > 0) && (params.get(0) != null)
				&& (Files.exists(Paths.get(params.get(0)), LinkOption.NOFOLLOW_LINKS))
				&& (Files.isDirectory(Paths.get(params.get(0)), LinkOption.NOFOLLOW_LINKS))) { //If a path is given
			
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
		} else {
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
				System.err.println("Could not list directory");
				return "UnknownFileError";
			}
		}
		return null;
	}
}
