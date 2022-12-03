package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import main.Lib;
import main.Main;

public class Reload {
	public static String reload (ArrayList<String> params, Map<String, String> paramsWithValues) {
		
		if ((params != null) && (params.size() >= 1) && (params.get(0).equalsIgnoreCase("full"))) {
			Lib.logWrite("RELOAD", 1, "Restarting JavaDOS with everything loaded...");
			Lib.logWrite("RELOAD", 2, "Parameter 'full' still not functional. Performing normal reload.");
			//Perform restart
			Lib.logWrite("RELOAD", 1, "Reloading JavaDOS...");
			Main.cmdLine.setText("");
			Lib.initVars();
			Lib.refreshDateTime();
			Lib.cmdLinePrepare(true);
		} else {
			Lib.logWrite("RELOAD", 1, "Reloading JavaDOS...");
			Main.cmdLine.setText("");
			Lib.initVars();
			Lib.refreshDateTime();
			Lib.cmdLinePrepare(true);
		}
		
		return null;
	}
}
