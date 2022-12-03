package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import engine.HighLevel;

public class CommandMain {
	private static String returnVal = "CmdNotFound"; //If this is never changed, it means that the command was not found.
	  //-> Successfully executed commands return null.
	private static boolean err = false; //Determines whether there was an error in execution or not
	public static boolean silentExecution = false; //Silent execution
	public static boolean noPrompt = false; //No prompt after execution done
	private static String command = "";
	private static ArrayList<String> options = new ArrayList<String>();
	private static Map<String, String> optionsWithValues = new HashMap<String, String>();
	private static ArrayList<String> params = new ArrayList<String>();
	//For legacy support only, will be removed in the future
	private static Map<String, String> paramsWithValues = new HashMap<String, String>();
	
	private static Thread execThread = new Thread( new Runnable() {
		public final void run() {
			executeCommand();
		}
	});
	
	public static void forceTerminate() {
		try {
			execThread.stop();
			Lib.refreshDateTime();
			Lib.cmdLinePrepare(false);
			Main.cmdLine.setEditable(true);
		} catch (SecurityException se) {
			Lib.logWrite("EXECTERM", 4, "Could not terminate execution thread.");
		}
	}
	
	//============================================================================================================
	//============================================================================================================
	public static String executeCommand(Object[] cmdParts) throws IOException {
		command = cmdParts[0].toString();
		options = (ArrayList<String>) cmdParts[1];
		optionsWithValues = (Map<String, String>) cmdParts[2];
		params = (ArrayList<String>) cmdParts[3];
		paramsWithValues = optionsWithValues;
		if (params != null && params.contains("silent")) { silentExecution = true; }
		if (params != null && params.contains("noPrompt")) { noPrompt = true; }
		
		if (!execThread.isAlive() || (params.contains("forceExecThread"))) {
			execThread = new Thread( new Runnable() {
				public final void run() {
					executeCommand();
				}
			});
			Lib.logWrite("CMDMAIN", 1, "Starting new thread for command execution...");
			execThread.start();
			return "Started";
		} else {
			Lib.logWrite("CMDMAIN", 3, "Cannot execute command. A command is already running.");
			Lib.logWrite("CMDMAIN", 3, "Please wait for execution to finish!");
			Lib.logWrite("CMDMAIN", 3, "To force execution, type '-forceExecThread' (not recommended!)");
			HighLevel.shell_write(4, "CMDMAIN", "Cannot execute command. A command is already running.\n");
			HighLevel.shell_write(4, "CMDMAIN", "Please wait for execution to finish.\n");
			HighLevel.shell_write(4, "CMDMAIN", "To force execution, type '-forceExecThread' (not recommended!)\n");
			
			return "ThreadExecError";
		}
		
	}
		
	
	//===============================EXEC METHOD FOR THREADS=============================
	private static String executeCommand() {
		
		
		Main.cmdLine.setEditable(false);
		//=================================VARIABLE PREPARATION==================================
		//Splitting full command into command, required Parameters(params) and optional Parameters(paramsWithValues)
		
		
		//==================================PRE-returnVal-CHECKING==================================
		if (command.isBlank()) {
			Lib.logWrite("CMDMAIN", 2, "Command is empty");
			Lib.logWrite("CMDMAIN", 0, "");
			Lib.refreshDateTime();
			Lib.cmdLinePrepare(false);
			Main.cmdLine.setEditable(true);
			return "";
		} else if (HighLevel.getActiveJDOSPhase().equalsIgnoreCase("pre-init")) {
			Lib.logWrite("CMDMAIN", 3, "JavaDOS is still in pre-init phase. Cannot execute commands.");
			Lib.refreshDateTime();
			Lib.cmdLinePrepare(false);
			Main.cmdLine.setEditable(true);
			return "PhaseNotRun";
		}
		
		//==================================COMMAND EXECUTION====================================
		if (HighLevel.getCurrentShellMode().equalsIgnoreCase("normal")) {
			if (command.equalsIgnoreCase("encrypt")) {
				returnVal = internalCommands.Cipher_Encrypt.encrypt(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("decrypt")) {
				returnVal = internalCommands.Cipher_Decrypt.decrypt(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("print")) {
				returnVal = internalCommands.Console_Print.print(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("clear") || command.equalsIgnoreCase("cls")) {
				returnVal = internalCommands.Console_ClearScreen.clearScreen(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("reload") || command.equalsIgnoreCase("reset")) {
				returnVal = internalCommands.Reload.reload(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("terminate") || command.equalsIgnoreCase("stop ") || command.equalsIgnoreCase("exit")
					|| (command.equalsIgnoreCase("end-session"))) {
				returnVal = internalCommands.Terminate.terminate(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("cd") || command.equalsIgnoreCase("chdir")) {
				returnVal = internalCommands.Console_ChangeDirectory.changeDirectory(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("test")) {
				returnVal = internalCommands.Test.test(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("readText") || command.equalsIgnoreCase("read") || command.equalsIgnoreCase("cat")) {
				returnVal = internalCommands.File_readText.readText(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("dir") || command.equalsIgnoreCase("ls")) {
				returnVal = internalCommands.File_listDirectory.listDirectory(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("chprompt") || command.equalsIgnoreCase("prompt")) {
				returnVal = internalCommands.Console_ChangePrompt.changePrompt(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("shellmode")) {
				engine.HighLevel.setShellMode(params.get(0));
				returnVal = null;
			} else if (command.equalsIgnoreCase("chksum") || command.equals("cksum")) {
				HighLevel.shell_write(3, "CHKSUM", "Warning: checksum calculation with TEST-32 method can cause\n");
				HighLevel.shell_write(3, "CHKSUM", "JavaDOS to hang long periods of time. Please be patient.\n");
				returnVal = internalCommands.Cipher_Chksum.chksum(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("mostOccurChar") || command.equalsIgnoreCase("highestOccurrenceChar")) {
				returnVal = internalCommands.Cipher_General_HighestOccurrenceChar.highestOccurrenceChar(params, paramsWithValues);
			} else if (command.equalsIgnoreCase("sysexec")) {
				returnVal = internalCommands.System_Exec.sysexec(params, paramsWithValues);
			} else { //Try executing the command in the system
				String exec = "sysexec --exec=\"" + command;
				if (params != null) {
					for (String param : params) {
						exec = exec.concat(" " + param);
					} //Not implementing paramsWithValues here, because the syntax of them is JDOS-specific
				}
				exec = exec.concat("\"");
				try {
					returnVal = executeCommand(engine.CommandParser.commandSplitArray(exec));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Lib.logWrite("CMDMAIN", 3, "I/O Exception sysexec cmdmain");
					e.printStackTrace();
				}
			}
		} else { //Shellmode is not normal
			if (command.equalsIgnoreCase("exit")) {
				internalCommands.Terminate.terminate(params, paramsWithValues);
			} else {
				Lib.logWrite("CMDMAIN", 2, "Warning: JavaDOS is in native/legacy mode and normal commands cannot be executed.");
				returnVal = "ShellModeNotNormal";
			}
		}
		
		if (returnVal != null) {
			HighLevel.shell_write(1, "HIDDEN", "- " + returnVal + " -\n");
		}
		//===================================ERROR RESOLVING=====================================
		if ((params != null)) {
			if (params.get(0).equalsIgnoreCase("noErrorChecking")) {
				Lib.refreshDateTime();
				Lib.cmdLinePrepare(false);
				Main.cmdLine.setEditable(true);
				return "";
			}
		}
		if ((returnVal != null) && !(execThread.isAlive())) {
			if (returnVal.equalsIgnoreCase("CmdNotFound")) {
				err = true;
				Lib.logWrite("CMDMAIN", 2, "The specified command was not found as internal or external command.");
			} else if (returnVal.equalsIgnoreCase("FileNotFound")) {
				err = true;
				Lib.logWrite("CMDMAIN", 2, "The specified file or directory was not found.");
			} else if (returnVal.equalsIgnoreCase("UnknownFileError")) {
				err = true;
				Lib.logWrite("CMDMAIN", 2, "There was an unknown file operation returnVal.");
			} else if (returnVal.equalsIgnoreCase("TestError")) {
				err = true;
				Lib.logWrite("CMDMAIN", 0, "Test Error. Nothing went wrong :)");
			} else if (returnVal.equalsIgnoreCase("Watchdog1Inactive")) {
				err = true;
				Lib.logWrite("CMDMAIN", 4, "Watchdog Thread 1 not running!");
				Lib.logWrite("CMDMAIN", 4, "Please reset JavaDOS or Reinstall!");
			} else if (returnVal.equalsIgnoreCase("Watchdog2Inactive")) {
				err = true;
				Lib.logWrite("CMDMAIN", 4, "Watchdog Thread 2 not running!");
				Lib.logWrite("CMDMAIN", 4, "Please reset JavaDOS or Reinstall!");
			} else if (returnVal.equalsIgnoreCase("ShellModeNotNormal")) {
				err = true;
				//Something
				//TODO Extend shellmodes and legacy/native control to lowlevel
			} else if (returnVal.equalsIgnoreCase("paramMissing")) {
				err = true;
				Lib.logWrite("CMDMAIN", 4, "A required parameter was not specified.");
			}
		}
		if (err) {
			HighLevel.shell_write(1, "HIDDEN", "\n");
			HighLevel.shell_write(3, "HIDDEN", "Something went wrong whilst executing the command. \n");
			HighLevel.shell_write(3, "HIDDEN", "The following Error occured: " + returnVal + "\n");
			HighLevel.shell_write(3, "HIDDEN", "-> See log for further information.");
		}
		//====================================FINALIZATION=======================================
		if (silentExecution == true) { silentExecution = false; }
		if (!noPrompt) {
			Lib.refreshDateTime();
			Lib.cmdLinePrepare(false);
			Main.cmdLine.setEditable(true);
		}
		return returnVal;
	}
}