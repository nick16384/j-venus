package internalCommands;

import engine.HighLevel;

public class Terminate {
	public static String terminate(String[] reqParams, String[] optParams) {
		if (modules.WatchdogThread.isThreadAlive()) {
			System.out.println("Sending shutdown signal to WatchdogThread...");
			HighLevel.shell_write(2, "HIDDEN", "Sending shutdown signal and terminating in about 2 seconds...");
			new modules.WatchdogThread().shutdownJavaDOS(0); //TODO maybe make jdos non-static
		} else {
			System.out.println("Exiting manually...");
			System.exit(1);
		}
		return null;
	}
}