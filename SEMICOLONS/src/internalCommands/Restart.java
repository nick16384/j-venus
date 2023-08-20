package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import awtcomponents.AWTANSI;
import engine.InfoType;
import engine.sys;
import jfxcomponents.JFXANSI;
import libraries.Env;
import libraries.Global;
import libraries.VariableInitializion;
import main.Main;
import shell.Shell;

public class Restart {
	public static String restart (ArrayList<String> params, Map<String, String> paramsWithValues) {
		
		Shell.println(AWTANSI.B_Magenta, "Restarting session...");
		Main.restartVMIfSupported();
		
		Shell.println(AWTANSI.B_Magenta, "Shell restart not supported. Probably, your OS is Windows.");
		
		return null;
	}
}
