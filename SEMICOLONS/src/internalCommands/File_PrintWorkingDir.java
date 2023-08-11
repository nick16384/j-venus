package internalCommands;

import java.util.ArrayList;
import java.util.Map;
import libraries.Global;



public class File_PrintWorkingDir
{
  public static String pwd(ArrayList<String> params, Map<String, String> paramsWithValues) {
    return Global.getCurrentDir();
  }
}