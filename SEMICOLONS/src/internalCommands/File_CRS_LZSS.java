package internalCommands;

import engine.InfoType;
import engine.sys;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import components.Shell;


public class File_CRS_LZSS
{
  public static String crs_lzss(ArrayList<String> params, Map<String, String> paramsWithValues) {
    if (params == null || params.size() < 1 || params.get(0) == null) {
      Shell.print(0, "HIDDEN", "Input string for compression required, but not given.\nType: crs.lzss <YourString>\n", new boolean[0]);
      
      return "paramMissing";
    } 
    String inputString = "";
    String out = "";
    
    LinkedList<Character> searchBuffer = new LinkedList<>();
    
    String lookAheadBuffer = "";



    
    if (Files.exists(Paths.get(params.get(0), new String[0]), new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
      Shell.print(0, "HIDDEN", "Input string represents a file, using file content.\n", new boolean[0]);
      try {
        inputString = Files.readString(Paths.get(params.get(0), new String[0]));
      } catch (IOException ioe) {
        Shell.print(0, "HIDDEN", "Cannot read input file, using provided string.\nDo you have sufficient permission to read: " + 
            (String)params.get(0) + "?\n", new boolean[0]);
      } 
    } else {
      Shell.print(0, "HIDDEN", "Using input string as value to compress.\n", new boolean[0]);
      inputString = params.get(0);
    } 
    if (inputString.length() < 100) {
      Shell.print(0, "HIDDEN", "Input: " + inputString + "\n", new boolean[0]);
    } else {
      Shell.print(0, "HIDDEN", "Input string too large to display.\n", new boolean[0]);
    } 
    
    Shell.print(0, "HIDDEN", "Compressing using the LZSS method...\n", new boolean[0]);
    Shell.print(0, "HIDDEN", "Initializing the LinkedList. ", new boolean[0]);
    for (int i = 0; i <= 127; ) { searchBuffer.add(null); i++; }
     Shell.print(0, "HIDDEN", String.valueOf(searchBuffer.size()) + ", Resuming.\n", new boolean[0]);
    
    int index = 0; byte b; int j; char[] arrayOfChar;
    for (j = (arrayOfChar = inputString.toCharArray()).length, b = 0; b < j; ) { char c = arrayOfChar[b];
      while (searchBuffer.size() > 129) {
        searchBuffer.removeFirst();
      }
      lookAheadBuffer = String.valueOf(lookAheadBuffer) + Character.toString(c);
      if (lookAheadBuffer.length() > 32) {
        searchBuffer.add(Character.valueOf(lookAheadBuffer.charAt(0)));
        lookAheadBuffer = lookAheadBuffer.substring(1);
      } 
      if (searchBuffer.size() >= 1 || inputString.length() <= 32) {





        
        String searchString = "";
        try {
          sys.log("CRS.LZSS", InfoType.STATUS, "");
          if (searchBuffer.contains(Character.valueOf(c))) {

            
            if (lookAheadBuffer.substring(0, lookAheadBuffer.length() - 1).contains(Character.toString(c))) {
              sys.log("CRS.LZSS", InfoType.DEBUG, "Found single match(inefficient)!");
              out = String.valueOf(out) + "<" + index + ",1>";
            } else {
              
              sys.log("CRS.LZSS", InfoType.DEBUG, "Found match!");
              searchString = String.valueOf(searchString) + Character.toString(c); byte b1; int k; char[] arrayOfChar1;
              for (k = (arrayOfChar1 = lookAheadBuffer.toCharArray()).length, b1 = 0; b1 < k; ) { char c1 = arrayOfChar1[b1];
                if (c1 != '.' && 
                  searchString.charAt(searchString.length() - 1) == c1)
                  searchString = String.valueOf(searchString) + Character.toString(c1); 
                b1++; }
              
              out = String.valueOf(out) + "<" + index + "," + searchString.length() + ">";
            } 
          } else {
            sys.log("CRS.LZSS", InfoType.DEBUG, "No match");
            out = String.valueOf(out) + Character.toString(c);
          
          }
        
        }
        catch (NoSuchElementException nsee) {
          nsee.printStackTrace();
          Shell.print(0, "HIDDEN", "Unexpected NoSuchElementException while searching in searchBuffer\nTry again. If not fixed, please report so we can fix the issue.\n", new boolean[0]);
          
          return "InternalErr";
        } catch (IndexOutOfBoundsException ioobe) {
          ioobe.printStackTrace();
          Shell.print(0, "HIDDEN", "Could not check string match, because the index is out of range\nTry again. If not fixed, please report so we can fix the issue.\n", new boolean[0]);
          
          return "InternalErr";
        } 
        out = String.valueOf(out) + "\n";
        
        index++;
      }  b++; }
    
    return out;
  }
}