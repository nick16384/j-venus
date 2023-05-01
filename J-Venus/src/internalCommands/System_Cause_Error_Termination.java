package internalCommands;

import java.util.ArrayList;

import components.IntentionalVexusErrorException;
import engine.AWTANSI;
import engine.sys;
import main.Main;

public class System_Cause_Error_Termination {
	public static String causeErrTerm (ArrayList<String> params) throws IntentionalVexusErrorException {
		sys.shellPrintln(AWTANSI.B_Red, "Causing intentional system termination!");
		sys.log("CET", 2, "Causing intentional system termination!");
		libraries.VarLib.setCurrentDir(null);
		sys.setActivePhase("pre-init");
		throw new IntentionalVexusErrorException("ivee", "This crash was caused by user command.");
	}
}
