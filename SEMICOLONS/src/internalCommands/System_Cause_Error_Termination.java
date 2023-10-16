package internalCommands;

import java.util.ArrayList;

import awtcomponents.AWTANSI;
import components.IntentionalVexusErrorException;
import engine.InfoType;
import engine.sys;
import jfxcomponents.ANSI;
import shell.Shell;

public class System_Cause_Error_Termination {
	public static String causeErrTerm (ArrayList<String> params) throws IntentionalVexusErrorException {
		Shell.println(ANSI.B_Red, "Causing intentional system termination!");
		sys.log("CET", InfoType.WARN, "Causing intentional system termination!");
		libraries.Global.setCurrentDir(null);
		throw new IntentionalVexusErrorException("ivee", "This crash was caused by user command.");
	}
}
