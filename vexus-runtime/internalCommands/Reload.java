package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.sys;
import libraries.OpenLib;
import main.Main;

public class Reload {
	public static String reload (ArrayList<String> params, Map<String, String> paramsWithValues) {
		
		if ((params != null) && (params.size() >= 1) && (params.get(0).equalsIgnoreCase("full"))) {
			sys.log("RELOAD", 1, "Restarting JavaDOS with everything loaded...");
			sys.log("RELOAD", 2, "Performing extended reload.");
			//Perform restart
			sys.log("RELOAD", 1, "Reloading JavaDOS...");
			Main.cmdLine.setText("");
			OpenLib.initVars();
			OpenLib.updateEnv("$$ALL");
			OpenLib.verifyFiles();
			OpenLib.refreshDateTime();
			OpenLib.cmdLinePrepare();
		} else {
			sys.log("RELOAD", 1, "Reloading JavaDOS...");
			Main.cmdLine.setText("");
			sys.setActivePhase("init");
			OpenLib.initVars();
			OpenLib.updateEnv("$$ALL");
			OpenLib.refreshDateTime();
			OpenLib.cmdLinePrepare();
		}
		
		return null;
	}
}
