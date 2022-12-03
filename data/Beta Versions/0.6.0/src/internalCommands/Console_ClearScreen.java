package internalCommands;

import java.util.ArrayList;
import java.util.Map;

public class Console_ClearScreen {
	public static String clearScreen (ArrayList<String> params, Map<String, String> paramsWithValues) {
		
		main.Main.cmdLine.setText("");
		
		return null;
	}
}
