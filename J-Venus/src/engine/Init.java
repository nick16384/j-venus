package engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import commandProcessing.CommandMain;
import libraries.OpenLib;
import libraries.VarLib;
import main.Main;
import threads.CommandLoader;
import threads.ThreadAllocator;

/**
 * Contains all initialization code (e.g. preparing envvars).
 * Moved from Main to here for better readability.
 * Info: Can only be executed when in pre-init phase
 * @author nick16384
 *
 */

public class Init {
	/**
	 * Initializes the J-Venus environment.
	 * Note: Moved from Main class to here for better readability.
	 * @param vmArgs
	 * @return Whether initialization was successful or not (true or false)
	 */
	public static boolean init(String[] vmArgsArray) {
		if (!sys.getActivePhase().equals("pre-init")) {
			sys.log("INIT", 3, "Init already done. Cannot do twice or more.\n");
			sys.log("INIT", 1, "If you want to force reinitialization, set the current phase to \"pre-init\".");
			return false;
		}
		//Very complicated way to convert array of main args to ArrayList<String>
		ArrayList<String> vmArgs = Arrays.asList(vmArgsArray).stream().map(x -> (String)x)
                .collect(Collectors.toCollection(ArrayList::new));
		
		if (vmArgs.contains("--help") || vmArgs.contains("-h")) {
			//Display help page and exit with code 0
			System.out.println("J-Venus version " + VarLib.getVersion());
			System.out.println("Usage: venus [OPTION...] [STARTDIR]");
			System.out.println("Info: [STARTDIR] not implemented yet.");
			System.out.println("See list below for all arguments:\n");
			System.out.println("View:");
			System.out.println("\t--fullscreen, --full-screen \t Starts J-Venus in fullscreen mode (no window)\n");
			System.out.println("Debug:");
			System.out.println("\t--single-threaded \t Prevents start of other internal threads e.g. Watchdog.");
			System.out.println("\t--enable-deprecated \t Enable use of deprecated methods and classes.");
			System.out.println("\t--no-check-install \t Don't check for installation files at startup (useful for portable versions).");
			System.out.println("\t--javafx, --jfx \t Use experimental JavaFX GUI loader instead of AWT.");
			System.exit(0);
		}
		if (vmArgs.contains("--single-threaded")) {
			main.Main.singleThreaded = true;
			System.out.println("Warning: Disabling watchdog and other threads is for debugging puposes only.");
			System.out.println("If you are aware of that but having issues with multithreading, try restarting");
			System.out.println("or reinstalling J-Venus. If it's a general problem, please report it.");
			System.out.println("If you were not aware of Multithreading being disabled, please remove");
			System.out.println("the Command Line argument '--single-threaded'!");
		}
		if (!main.Main.singleThreaded) {
			Main.ThreadAllocMain = new ThreadAllocator();
			Main.ThreadAllocMain.launchAll();
		}
		try {
			System.out.println("Startup sleep for everything to finish before init (1 sec).");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sys.setActivePhase("init");
		//TODO patch motd file not found and fix a few formatting errors (extra \n, etc.)
		System.out.print(VarLib.getMOTD());
		System.out.println("");
		System.out.println("Loading internal variables...");
		OpenLib.initVars();
		sys.log("MAIN", 0, "Done.");
		sys.log("MAIN", 1, "Warning: Log is currently very verbose due to debugging reasons.");
		sys.log("MAIN", 1, "Will be reduced within alpha versions.");
		if (vmArgs.contains("--enable-deprecated")) {
			//Load legacy external commands
			sys.log("MAIN", 0, "{Deprecated} Loading external commands...");
			try { VarLib.setExtCommands(CommandLoader.loadCommands()); }
			catch (AccessDeniedException ade) { sys.log("MAIN", 2, "Access to the destination file is denied."); }
			catch (NoSuchFileException nsfe) { sys.log("MAIN", 2, "External commands not found."); }
			catch (IOException ioe) {
			sys.log("MAIN", 2, "Unhandled IOException while loading external commands."); ioe.printStackTrace();
			}
		}
		try {
			sys.log("MAIN", 1, "Loading startup script");
			boolean error = threads.LoadStartup.loadAndExecute();
			if (error) {
				//TODO next: fix startupscripts(ext cmds, startupscr)
				sys.log("STARTUPSCRIPTRUN", -1, "Error when loading startup script: Internal error");
			}
		} catch (IOException ioe) {
			sys.log("STARTUPSCRIPTRUN", -1, "Error when loading startup script: IOException");
			ioe.printStackTrace();
		}
		sys.log("MAIN", 1, "Reinitializing environment...");
		OpenLib.updateEnv("$$ALL");
		sys.log("MAIN", 1, "Done.");
		sys.log("MAIN", 1, "Backing up cmd_history to cmd_history_bak...");
		try {
			Files.writeString(Paths.get(VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history_bak"),
					Files.readString(Paths.get(VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history")),
					StandardOpenOption.SYNC);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		sys.log("MAIN", 1, "Done.");
		while (CommandMain.isExecThreadAlive()) { try { Thread.sleep(100); } catch (InterruptedException ie) {} }
		
		/*sys.log("Initializing main frame...");
		Main mainWindow = new Main();
		sys.log("Done.");
		new modules.ProtectedTextComponent(mainWindow.getMain().getMainWindow().cmdLine).unprotectAllText();*/
		//mainWindow.getMainWindow().cmdLine.setText("");
		//OpenLib.cmdLinePrepare();
		sys.log("MAIN", 1, "Finished initialization part.");
		return true;
	}
}
