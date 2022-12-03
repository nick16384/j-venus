package internalCommands;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import engine.sys;
import libraries.OpenLib;
import libraries.VarLib;





public class Console_ChangeDirectory
{
  public static String changeDirectory(ArrayList<String> params, Map<String, String> paramsWithValues) {
    if (params == null) {
    	sys.log("CHDIR", 2, "No parameters provided, staying in directory.");
    	VarLib.setCurrentDir((new File(VarLib.getFSRoot())).getAbsolutePath());
    }
    else if (((String)params.get(0)).equals("..")) {
    	sys.log("CHDIR", 1, "Going down one layer.");
    	VarLib.setCurrentDir((new File(VarLib.getCurrentDir().replace(VarLib.getCurrentDir().split(VarLib.fsep)[1], "")))
    			.getAbsolutePath());
    
    }
    else if ((new File(VarLib.getCurrentDir() + VarLib.fsep + (String)params.get(0))).isDirectory()) {
    	sys.log("CHDIR", 1, "CD'ing into directory of current directory.");
    	VarLib.setCurrentDir((new File(VarLib.getCurrentDir() + VarLib.fsep + (String)params.get(0))).getAbsolutePath());
    }
    else if ((new File(params.get(0))).isDirectory()) {
    	sys.log("CHDIR", 1, "Changing directory to absolute path.");
    	VarLib.setCurrentDir((new File(params.get(0))).getAbsolutePath());
    }
    else {
      
      return "FileWarn";
    } 
    
    sys.log("CHDIR", 1, "Updating the $PATH variable.");
    OpenLib.updateEnv("$PATH");
    return null;
  }
}