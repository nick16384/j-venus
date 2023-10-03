package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.InfoType;
import engine.sys;
import jfxcomponents.GUIManager;
import libraries.Global;
import main.Main;
import shell.Shell;


public class Console_ClearScreen
{
  public static String clearScreen(ArrayList<String> params, Map<String, String> paramsWithValues) {
	  
	  if (Global.javafxEnabled && GUIManager.getCmdLine() != null) {
		  Shell.clearCmdLine();
	  } else if (Main.mainFrameAWT != null) {
		  new components.ProtectedTextComponent(Main.mainFrameAWT.getCmdLine()).unprotectAllText();
		  Main.mainFrameAWT.getCmdLine().setText("");
	  } else {
		  sys.log("CLS", InfoType.ERR, "Clearing screen failed, because components are still null.");
	  }
	  return null;
  }
}