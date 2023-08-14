package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.InfoType;
import engine.sys;
import libraries.Global;
import main.Main;


public class Console_ClearScreen
{
  public static String clearScreen(ArrayList<String> params, Map<String, String> paramsWithValues) {
	  
	  if (Global.javafxEnabled && Main.jfxWinloader.getCmdLine() != null) {
		  Main.jfxWinloader.clearCmdLine();
	  } else if (Main.mainFrameAWT != null) {
		  new components.ProtectedTextComponent(Main.mainFrameAWT.getCmdLine()).unprotectAllText();
		  Main.mainFrameAWT.getCmdLine().setText("");
	  } else {
		  sys.log("CLS", InfoType.ERR, "Clearing screen failed, because components are still null.");
	  }
	  return null;
  }
}