package internalCommands;

import internalCommands.System_Exec;

import java.util.ArrayList;

import commands.OnSystemExecutor;

public class System_Exec {
	private static OnSystemExecutor executor;
	
	public static String sysexec(ArrayList<String> params) {
		if (!new ParameterChecker(params).checkValid())
			return "paramMissing";
		
		// Note: "params" will be taken as external command to be executed.
		executor = new OnSystemExecutor(params);
		return executor.execute();
	}

	public static void killProcessIfRunning() {
		executor.killProcessIfRunning();
	}
}