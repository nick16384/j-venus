package internalCommands;

import main.Lib;
import main.Main;

public class Reload {
	public static String reload (String[] reqParams, String[] optParams) {
		
		System.out.println("RELOAD: Reloading JavaDOS...");
		Main.cmdLine.setText("");
		Lib.initVars();
		Lib.refreshDateTime();
		Lib.cmdLinePrepare(true);
		
		return null;
	}
}
