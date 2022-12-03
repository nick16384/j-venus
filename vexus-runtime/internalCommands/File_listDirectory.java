package internalCommands;

import engine.sys;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import libraries.OpenLib;
import libraries.VarLib;






public class File_listDirectory
{
  public static String listDirectory(ArrayList<String> params, Map<String, String> paramsWithValues) {
    ArrayList<String> dirs = new ArrayList<>();
    ArrayList<String> files = new ArrayList<>();
    ArrayList<String> unknown = new ArrayList<>();
    String dir = "";
    if (params != null && Files.isDirectory(Paths.get(params.get(0), new String[0]), new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
      dir = params.get(0);
    } else {
      dir = VarLib.getCurrentDir();
    } 
    sys.log("LSDIR", 0, "Listing directory: '" + dir + "'");

    
    if (Files.isDirectory(Paths.get(dir, new String[0]), new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
      byte b; int i; String[] arrayOfString; for (i = (arrayOfString = (new File(dir)).list()).length, b = 0; b < i; ) { String elem = arrayOfString[b];





        
        if ((new File(elem)).getAbsoluteFile().isDirectory() && 
          !(new File(elem)).getAbsoluteFile().isFile()) {
          files.add(elem);
        } else if ((new File(elem)).getAbsoluteFile().isFile() && 
          !(new File(elem)).getAbsoluteFile().isDirectory()) {
          dirs.add(elem);
        } else {
          unknown.add(elem);
        }  b++; }
      
      int index = 0;
      for (String fileName : files) {
        if (index <= 3) {
          sys.shellPrint(3, "HIDDEN", String.valueOf(fileName) + "\t", new boolean[0]);
          index++; continue;
        } 
        sys.shellPrint(3, "HIDDEN", String.valueOf(fileName) + "\n", new boolean[0]);
        index = 0;
      } 
      
      for (String dirName : dirs) {
        if (index <= 3) {
          sys.shellPrint(2, "HIDDEN", String.valueOf(dirName) + "\t", new boolean[0]);
          index++; continue;
        } 
        sys.shellPrint(2, "HIDDEN", String.valueOf(dirName) + "\n", new boolean[0]);
        index = 0;
      } 
      
      for (String unknownName : unknown) {
        if (index <= 3) {
          sys.shellPrint(4, "HIDDEN", String.valueOf(unknownName) + "\t", new boolean[0]);
          index++; continue;
        } 
        sys.shellPrint(4, "HIDDEN", String.valueOf(unknownName) + "\n", new boolean[0]);
        index = 0;
      } 
    } else {
      
      sys.log("LSDIR", 3, "Cannot read directory.");
      return "FilePermissionError";
    } 


    
    return null;
  }
}