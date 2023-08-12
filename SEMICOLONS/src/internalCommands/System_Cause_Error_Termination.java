package internalCommands;

import java.util.ArrayList;

import awtcomponents.AWTANSI;
import components.IntentionalVexusErrorException;
import components.Shell;
import engine.sys;

public class System_Cause_Error_Termination {
	public static String causeErrTerm (ArrayList<String> params) throws IntentionalVexusErrorException {
		Shell.println(AWTANSI.B_Red, "Causing intentional system termination!");
		sys.log("CET", 2, "Causing intentional system termination!");
		libraries.Global.setCurrentDir(null);
		throw new IntentionalVexusErrorException("ivee", "This crash was caused by user command.");
	}
}
