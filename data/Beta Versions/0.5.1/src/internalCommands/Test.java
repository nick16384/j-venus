package internalCommands;

import engine.HighLevel;

public class Test {
	public static String test(String[] reqParams, String[] optParams) {
		HighLevel.shell_write(1, "TEST", "Text normal, priority 1 (white) \n");
		HighLevel.shell_write(2, "TEST", "Text priority 2 (green + yellow) \n");
		HighLevel.shell_write(3, "TEST", "Text priority 3 or above (yellow) \n");
		HighLevel.shell_write(4, "TEST", "Text priority 4 (orange) \n");
		HighLevel.shell_write(5, "TEST", "Text priority 5 (red) \n");
		
		HighLevel.shell_write(2, "TEST", "\\/ Test error sent \\/");
		
		return "TestError";
	}
}
