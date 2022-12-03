package internalCommands;

import java.util.ArrayList;
import java.util.Map;
import main.Main;


public class Console_ClearScreen
{
  public static String clearScreen(ArrayList<String> params, Map<String, String> paramsWithValues) {
	  
	  new modules.ProtectedTextComponent(main.Main.cmdLine).unprotectAllText();
	  Main.cmdLine.setText("");
	  return null;
  }
}