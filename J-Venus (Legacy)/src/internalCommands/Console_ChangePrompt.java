package internalCommands;

import java.util.ArrayList;
import java.util.Map;
import libraries.VarLib;



public class Console_ChangePrompt
{
  public static String changePrompt(ArrayList<String> params, Map<String, String> paramsWithValues) {
    if (params == null || params.size() == 0) {
      VarLib.setPromptPattern("default");
    } else {
      VarLib.setPromptPattern(params.get(0));
    } 
    return null;
  }
}