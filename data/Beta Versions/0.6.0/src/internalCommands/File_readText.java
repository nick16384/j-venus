package internalCommands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import engine.HighLevel;

public class File_readText {
	public static String readText(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params == null) || (params.size() < 1)) {
			return "reqParamParseError";
		} else if (Files.notExists(Paths.get(params.get(0)), LinkOption.NOFOLLOW_LINKS)) {
			return "FileNotFound";
		} else if (Files.isDirectory(Paths.get(params.get(0)), LinkOption.NOFOLLOW_LINKS)) {
			return "NotAFile";
		} else if ((params.size() >= 2) && (!params.contains("ignore")) &&
				(!Files.isReadable(Paths.get(params.get(0))))) {
			return "FileNotReadable";
		} else if ((params.size() >= 2) && (!params.contains("ignore")) &&
				(!Files.isRegularFile(Paths.get(params.get(0)), LinkOption.NOFOLLOW_LINKS))) {
			return "NoTextFile";
		} else {
			try { HighLevel.shell_write(2, "HIDDEN", Files.readString(Paths.get(params.get(0)))); }
			catch (IOException ioe) {
				HighLevel.shell_write(3, "READTEXT", "Unknown error while reading file content");
				return "UnknownFileError";
			}
			return null;
		}
	}
}
