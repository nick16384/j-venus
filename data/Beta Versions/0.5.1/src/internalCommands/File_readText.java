package internalCommands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import engine.HighLevel;

public class File_readText {
	public static String readText(String[] reqParams, String[] optParams) {
		if (reqParams.length == 0) {
			System.err.println("READTEXT: reqParam Parse error");
			return "reqParamParseError";
		} else if (Files.notExists(Paths.get(reqParams[0]), LinkOption.NOFOLLOW_LINKS)) {
			System.err.println("READTEXT: The specified path does not exist.");
			return "FileNotFound";
		} else if (Files.isDirectory(Paths.get(reqParams[0]), LinkOption.NOFOLLOW_LINKS)) {
			System.err.println("READTEXT: The specified path is a directory.");
			return "NotAFile";
		} else if ((optParams != null) && (optParams.length != 0) && (!optParams[0].equalsIgnoreCase("ignore")) &&
				(!Files.isReadable(Paths.get(reqParams[0])))) {
			System.err.println("READTEXT: The file is not readable");
			return "FilesAccessDenied";
		} else if ((optParams != null) && (optParams.length != 0) && (!optParams[0].equalsIgnoreCase("ignore")) &&
				(!Files.isRegularFile(Paths.get(reqParams[0]), LinkOption.NOFOLLOW_LINKS))) {
			System.err.println("READTEXT: The file has no regular content (text)");
			return "NoTextFile";
		} else {
			try { HighLevel.shell_write(2, "HIDDEN", Files.readString(Paths.get(reqParams[0]))); }
			catch (IOException ioe) {
				System.err.println("READTEXT: IOException while reading file content");
				HighLevel.shell_write(3, "READTEXT", "Unknown error while reading file content");
				return "UnknownFileError";
			}
			return null;
		}
	}
}
