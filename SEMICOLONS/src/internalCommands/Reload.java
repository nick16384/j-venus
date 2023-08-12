package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import components.Shell;
import engine.sys;
import libraries.Env;
import libraries.Global;
import libraries.VariableInitializion;
import main.Main;

public class Reload {
	public static String reload (ArrayList<String> params, Map<String, String> paramsWithValues) {
		
		if ((params != null) && (params.size() >= 1) && (params.get(0).equalsIgnoreCase("full"))) {
			sys.log("RELOAD", 1, "Restarting JavaDOS with everything loaded...");
			sys.log("RELOAD", 2, "Performing extended reload.");
			//Perform restart
			sys.log("RELOAD", 1, "Reloading SEMICOLONS...");
			Main.mainFrameAWT.getCmdLine().setText("");
			VariableInitializion.initializeAll();
			Env.updateEnv("$$ALL");
			Global.refreshDateTime();
			Shell.showPrompt();
		} else {
			sys.log("RELOAD", 1, "Reloading J-Vexus...");
			sys.log("RELOAD", 2, "A full reinit is only possible via a restart, not with reload.\n"
					+ "This message is since 23.08.");
			
			// This part does not work because of API changes since 23.08
			/*sys.setActivePhase("pre-init");
			Main.mainFrameAWT.getCmdLine().setText("");
			sys.setActivePhase("init");*/
			VariableInitializion.initializeAll();
			Env.updateEnv("$$ALL");
			Global.refreshDateTime();
			Shell.showPrompt();
		}
		
		return null;
	}
}
