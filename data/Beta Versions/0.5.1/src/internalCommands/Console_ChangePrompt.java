package internalCommands;

import main.Lib;

public class Console_ChangePrompt {
	public static String changePrompt(String[] reqParams, String[] optParams) {
		if ((reqParams == null) || (reqParams.length == 0)) {
			Lib.setPrompt("default");
		} else {
			Lib.setPrompt(reqParams[0]);
		}
		return null;
	}
}