package internalCommands;

import java.util.ArrayList;
import java.util.Map;
import libraries.Global;



public class Console_ChangePrompt
{
  public static String changePrompt(ArrayList<String> params, Map<String, String> paramsWithValues) {
    if (params == null || params.size() == 0) {
      Global.setPromptPattern("default");
    } else {
      Global.setPromptPattern(params.get(0));
    } 
    return null;
  }
}