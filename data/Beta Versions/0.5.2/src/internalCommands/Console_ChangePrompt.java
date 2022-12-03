package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import main.Lib;

public class Console_ChangePrompt {
	public static String changePrompt(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params == null) || (params.size() == 0)) {
			Lib.setPrompt("default");
		} else {
			Lib.setPrompt(params.get(0));
		}
		return null;
	}
}