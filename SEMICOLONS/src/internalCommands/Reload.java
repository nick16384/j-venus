package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.InfoType;
import engine.sys;
import libraries.Env;
import libraries.Global;
import libraries.VariableInitializion;
import main.Main;
import shell.Shell;

public class Reload {
	public static String reload (ArrayList<String> params, Map<String, String> paramsWithValues) {
		
		if ((params != null) && (params.size() >= 1) && (params.get(0).equalsIgnoreCase("full"))) {
			sys.log("RELOAD", InfoType.INFO, "Restarting JavaDOS with everything loaded...");
			sys.log("RELOAD", InfoType.WARN, "Performing extended reload.");
			//Perform restart
			sys.log("RELOAD", InfoType.INFO, "Reloading SEMICOLONS...");
			Main.mainFrameAWT.getCmdLine().setText("");
			VariableInitializion.initializeAll();
			Env.updateEnv("$$ALL");
			Global.refreshDateTime();
			Shell.showPrompt();
		} else {
			sys.log("RELOAD", InfoType.INFO, "Reloading J-Vexus...");
			sys.log("RELOAD", InfoType.WARN, "A full reinit is only possible via a restart, not with reload.\n"
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
