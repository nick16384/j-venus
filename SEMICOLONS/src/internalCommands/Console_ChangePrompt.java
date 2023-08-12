package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import components.Shell;
import libraries.Global;



public class Console_ChangePrompt
{
  public static String changePrompt(ArrayList<String> params, Map<String, String> paramsWithValues) {
    if (params == null || params.size() == 0) {
      Shell.setPromptPattern("default");
    } else {
      Shell.setPromptPattern(params.get(0));
    } 
    return null;
  }
}