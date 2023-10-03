package engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import filesystem.InternalFiles;
import filesystem.VirtualFile;
import libraries.Env;
import libraries.Global;
import shell.Shell;
import threads.ThreadAllocation;

/**
 * Contains all initialization code (e.g. preparing environment variables).
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
		if (!Global.getCurrentPhase().equals(Runphase.PREINIT)) {
			sys.log("INIT", InfoType.ERR, "Init already done. Cannot do twice or more.\n");
			sys.log("INIT", InfoType.INFO, "If you want to force reinitialization, set the current phase to \"pre-init\".");
			return false;
		}
		//Very complicated way to convert array of main args to ArrayList<String>
		ArrayList<String> vmArgs = Arrays.asList(vmArgsArray).stream().map(x -> (String)x)
                .collect(Collectors.toCollection(ArrayList::new));
		
		if (vmArgs.contains("--help") || vmArgs.contains("-h")) {
			//Display help page and exit with code 0
			System.out.println("J-Venus version " + Global.getVersion());
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
			System.out.println("\t--root-folder \t Set another root folder instead of the default defined one.");
			System.out.println("\t--debug, -v, --verbose  Enable debugging and status messages.");
			System.out.println("\t--silent, --quiet \t Only show warnings and errors.");
			System.out.println("\t--log-file \t\t Set the log file location on the filesystem.");
			System.exit(0);
		}
		if (vmArgs.contains("--single-threaded")) {
			libraries.Global.singleThreaded = true;
			System.out.println("Warning: Disabling watchdog and other threads is for debugging puposes only.");
			System.out.println("If you are aware of that but having issues with multithreading, try restarting");
			System.out.println("or reinstalling J-Venus. If it's a general problem, please report it.");
			System.out.println("If you were not aware of Multithreading being disabled, please remove");
			System.out.println("the Command Line argument '--single-threaded'!");
		}
		if (vmArgs.contains("--debug") || vmArgs.contains("-v") || vmArgs.contains("--verbose")) {
			InfoType.DEBUG.enable(); InfoType.STATUS.enable();
		}
		if (vmArgs.contains("--silent") || vmArgs.contains("--quiet")) {
			InfoType.DEBUG.disable(); InfoType.STATUS.disable(); InfoType.INFO.disable();
		}
		if (!libraries.Global.singleThreaded) {
			ThreadAllocation.launchAll();
		}
		
		Global.setNextRunphase(); // Init
		sys.log("INIT", InfoType.INFO, "Loading internal variables...");
		
		libraries.VariableInitializion.initializeAll();
		
		sys.log("INIT", InfoType.STATUS, "Done.");
		sys.log("INIT", InfoType.INFO, "Warning: Log is currently very verbose due to debugging reasons.");
		sys.log("INIT", InfoType.INFO, "Will be reduced within alpha versions.");
		if (vmArgs.contains("--enable-deprecated")) {
			sys.log("INIT", InfoType.STATUS, "No deprecated features to enable.");
		}
		try {
			sys.log("INIT", InfoType.INFO, "Loading startup script");
			boolean error = threads.LoadStartup.loadAndExecute();
			if (error) {
				//TODO next: fix startupscripts(ext cmds, startupscr)
				sys.log("STARTUPSCRIPTRUN", InfoType.ERR, "Error when loading startup script: Internal error");
			}
		} catch (IOException ioe) {
			sys.log("STARTUPSCRIPTRUN", InfoType.ERR, "Error when loading startup script: IOException");
			ioe.printStackTrace();
		}
		
		sys.log("INIT", InfoType.INFO, "Creating virtual files...");
		// Virtual files are only set up to prevent NullPointerExceptions.
		// Any class is allowed at any time to change whatever file to a more recent version.
		InternalFiles.setCmdHistory(Global.getDataDir().newVirtualFile("/cmd_history"));
		InternalFiles.setCmdHistoryBackup(Global.getDataDir().newVirtualFile("/cmd_history_bak"));
		InternalFiles.setCmdHistoryMaxLength(Global.getDataDir().newVirtualFile("/cmd_history_max_length"));
		InternalFiles.setMotd(Global.getDataDir().newVirtualFile("/motd"));
		InternalFiles.setMotdBackup(Global.getDataDir().newVirtualFile("/motd_bak"));
		InternalFiles.setSemicolonsIcon(Global.getDataDir().newVirtualFile("/semicolons-icon.jpg"));
		// Create any non-existing files:
		InternalFiles.getCmdHistory().createOnFilesystem();
		InternalFiles.getCmdHistoryBackup().createOnFilesystem();
		InternalFiles.getCmdHistoryMaxLength().createOnFilesystem();
		InternalFiles.getMotd().createOnFilesystem();
		InternalFiles.getMotdBackup().createOnFilesystem();
		InternalFiles.getSemicolonsIcon().createOnFilesystem();
		
		sys.log("INIT", InfoType.INFO, "Reinitializing environment...");
		Env.updateEnv("$$ALL");
		sys.log("INIT", InfoType.INFO, "Done.");
		
		sys.log("INIT", InfoType.INFO, "Enabling command history...");
		Shell.initializeCommandHistory();
		sys.log("INIT", InfoType.INFO, "Done.");
		
		/*sys.log("Initializing main frame...");
		Main mainWindow = new Main();
		sys.log("Done.");
		new modules.ProtectedTextComponent(mainWindow.getMain().getMainWindow().cmdLine).unprotectAllText();*/
		//mainWindow.getMainWindow().cmdLine.setText("");
		//OpenLib.cmdLinePrepare();
		sys.log("INIT", InfoType.INFO, "Finished initialization part.");
		return true;
	}
}
