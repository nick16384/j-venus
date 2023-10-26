package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import awtcomponents.AWTANSI;
import engine.LogLevel;
import engine.sys;
import jfxcomponents.ANSI;
import libraries.Env;
import libraries.Global;
import libraries.VariableInitializion;
import main.Main;
import shell.Shell;

public class Restart {
	public static String restart (ArrayList<String> params, Map<String, String> paramsWithValues) {
		
		Shell.println(ANSI.B_Magenta, "Restarting session...");
		Main.restartVMIfSupported();
		
		Shell.println(ANSI.B_Magenta, "Shell restart not supported. Probably, your OS is Windows.");
		
		return null;
	}
}
