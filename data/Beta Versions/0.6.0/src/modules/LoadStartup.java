package modules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LoadStartup {
	public static boolean loadAndExecute() throws IOException {
		final Path startupFile = Paths.get(main.Lib.getBinDir() + main.Lib.fsep + "startup.jscr");
		String startupFileString = "";
		ArrayList<String> commands = new ArrayList<String>();
		
		if ((Files.exists(startupFile, LinkOption.NOFOLLOW_LINKS)) &&
				(Files.isRegularFile(startupFile, LinkOption.NOFOLLOW_LINKS))) { //TODO maybe this might not work, test it!!!
			startupFileString = Files.readString(startupFile);
			
			if (startupFileString.startsWith("# JavaDOS startup script")) {
				startupFileString = startupFileString.replaceFirst("# JavaDOS startup script\n", "");
				for (String command : startupFileString.split("\n")) {
					main.CommandMain.executeCommand(new Object[] {command, new ArrayList<String>(List.of("noErrorChecking")), new ArrayList<String>()});
					return false;
				}
			} else {
				System.err.println("test2");
			}
		} else {
			System.err.println("test3");
		}
		return true;
	}
}
