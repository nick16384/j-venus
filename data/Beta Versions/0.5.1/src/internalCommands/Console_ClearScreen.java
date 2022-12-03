package internalCommands;

public class Console_ClearScreen {
	public static String clearScreen (String[] reqParams, String[] optParams) {
		
		main.Main.cmdLine.setText("");
		
		return null;
	}
}
