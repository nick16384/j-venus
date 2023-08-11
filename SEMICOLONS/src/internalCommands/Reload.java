package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.sys;
import libraries.VariableInitializion;
import main.Main;

public class Reload {
	public static String reload (ArrayList<String> params, Map<String, String> paramsWithValues) {
		
		if ((params != null) && (params.size() >= 1) && (params.get(0).equalsIgnoreCase("full"))) {
			sys.log("RELOAD", 1, "Restarting JavaDOS with everything loaded...");
			sys.log("RELOAD", 2, "Performing extended reload.");
			//Perform restart
			sys.log("RELOAD", 1, "Reloading J-Vexus...");
			Main.mainFrameAWT.getCmdLine().setText("");
			VariableInitializion.initVars();
			VariableInitializion.updateEnv("$$ALL");
			VariableInitializion.verifyFiles();
			VariableInitializion.refreshDateTime();
			VariableInitializion.cmdLinePrepare();
		} else {
			sys.log("RELOAD", 1, "Reloading J-Vexus...");
			sys.setActivePhase("pre-init");
			Main.mainFrameAWT.getCmdLine().setText("");
			sys.setActivePhase("init");
			VariableInitializion.initVars();
			VariableInitializion.updateEnv("$$ALL");
			VariableInitializion.refreshDateTime();
			VariableInitializion.cmdLinePrepare();
		}
		
		return null;
	}
}
