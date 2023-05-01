package internalCommands;

import engine.sys;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;



public class File_readText
{
  public static String readText(ArrayList<String> params, Map<String, String> paramsWithValues) {
    if (params == null || params.size() < 1)
      return "ParseErr_TooFewParams"; 
    if (Files.notExists(Paths.get(params.get(0), new String[0]), new LinkOption[] { LinkOption.NOFOLLOW_LINKS }))
      return "FileErr_NotFound"; 
    if (Files.isDirectory(Paths.get(params.get(0), new String[0]), new LinkOption[] { LinkOption.NOFOLLOW_LINKS }))
      return "NotAFile"; 
    if (params.size() >= 2 && !params.contains("ignore") && 
      !Files.isReadable(Paths.get(params.get(0), new String[0])))
      return "FileNotReadable"; 
    if (params.size() >= 2 && !params.contains("ignore") && 
      !Files.isRegularFile(Paths.get(params.get(0), new String[0]), new LinkOption[] { LinkOption.NOFOLLOW_LINKS }))
      return "NoTextFile"; 
    try {
    	return Files.readString(Paths.get(params.get(0), new String[0]));
    } catch (IOException ioe) {
      sys.shellPrint(3, "READTEXT", "Unknown error while reading file content", new boolean[0]);
      return "UnknownFileError";
    }
  }
}