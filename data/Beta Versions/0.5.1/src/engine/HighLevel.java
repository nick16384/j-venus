package engine;

import java.util.ArrayList;

public class HighLevel {
	public static ArrayList<String> uiTranslate (String fullCommand) {
		ArrayList<String> content = new ArrayList<String>(); //Create Content as ArrayList
		
		String[] commandRaw = fullCommand.split("\\(");
		String command = commandRaw[0];
		String[] reqParams;
		String[] optParams;
		try { reqParams = commandRaw[1].split("\\)")[0].split(", "); }
		catch (ArrayIndexOutOfBoundsException aioobe) { reqParams = null;  System.err.println("Info: No required Parameters"); }
		try {
			optParams = fullCommand.split("; ")[1].split(" -");
			optParams[0] = optParams[0].substring(1); //Slight correction to remove '-'
		} catch (ArrayIndexOutOfBoundsException aioobe) { optParams = null; System.out.println("No optional Parameters"); }
		
		content.add(0, command);
		try { for (String element : reqParams) { content.add(element.concat("<reqParam>")); } }
		catch (NullPointerException npe) { System.err.println("HIGHLEVEL: No required Parameters, program may not work."); }
		try { for (String element : optParams) { content.add(element.concat("<optParam>")); } }
		catch (NullPointerException npe) { System.out.println("HIGHLEVEL: No optional Parameters."); }
		
		return content;
	}
	//TODO public boolean requestLLmethod (String methodName, ...) {}
	public static void shell_write(int priority, String auth, String message) {
		LowLevel.shell_write(priority, auth, message);
	}
}
