package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import engine.HighLevel;

public class CommandMain {
	protected static int executeCommand(Object[] cmdParts) throws IOException {
		Main.cmdLine.setEditable(false);
		//=================================VARIABLE PREPARATION==================================
		//Splitting full command into command, required Parameters(params) and optional Parameters(paramsWithValues)
		String error = "CmdNotFound"; //If this is never changed, it means that the command was not found.
									  //-> Successfully executed commands return null.
		final String command = cmdParts[0].toString();
		final ArrayList<String> params = (ArrayList) cmdParts[1];
		final Map<String, String> paramsWithValues = (Map) cmdParts[2];
		
		if (command.isBlank()) {
			Lib.logWrite("CMDMAIN", 2, "Command is empty");
			Lib.logWrite("CMDMAIN", 0, "");
			Lib.refreshDateTime();
			Lib.cmdLinePrepare(false);
			Main.cmdLine.setEditable(true);
			return 1;
		}
		
		//==================================COMMAND EXECUTION====================================
		if (command.equalsIgnoreCase("encrypt")) {
			error = internalCommands.Cipher_Encrypt.encrypt(params, paramsWithValues);
		} else if (command.equalsIgnoreCase("decrypt")) {
			error = internalCommands.Cipher_Decrypt.decrypt(params, paramsWithValues);
		} else if (command.equalsIgnoreCase("print")) {
			error = internalCommands.Console_Print.print(params, paramsWithValues);
		} else if (command.equalsIgnoreCase("clear") || command.equalsIgnoreCase("cls")) {
			error = internalCommands.Console_ClearScreen.clearScreen(params, paramsWithValues);
		} else if (command.equalsIgnoreCase("reload") || command.equalsIgnoreCase("reset")) {
			error = internalCommands.Reload.reload(params, paramsWithValues); return 0;
		} else if (command.equalsIgnoreCase("terminate") || command.equalsIgnoreCase("stop ") || command.equalsIgnoreCase("exit")) {
			error = internalCommands.Terminate.terminate(params, paramsWithValues);
		} else if (command.equalsIgnoreCase("cd") || command.equalsIgnoreCase("chdir")) {
			error = internalCommands.Console_ChangeDirectory.changeDirectory(params, paramsWithValues);
		} else if (command.equalsIgnoreCase("test")) {
			error = internalCommands.Test.test(params, paramsWithValues);
		} else if (command.equalsIgnoreCase("readText") || command.equalsIgnoreCase("read")) {
			error = internalCommands.File_readText.readText(params, paramsWithValues);
		} else if (command.equalsIgnoreCase("dir") || command.equalsIgnoreCase("ls")) {
			error = internalCommands.File_listDirectory.listDirectory(params, paramsWithValues);
		} else if (command.equalsIgnoreCase("chprompt") || command.equalsIgnoreCase("prompt")) {
			error = internalCommands.Console_ChangePrompt.changePrompt(params, paramsWithValues);
		}
		
		//===================================ERROR RESOLVING=====================================
		if (error != null) {
			HighLevel.shell_write(1, "HIDDEN", "\n");
			HighLevel.shell_write(3, "HIDDEN", "Something went wrong whilst executing the command. \n");
			HighLevel.shell_write(3, "HIDDEN", "The following error occured: " + error + "\n");
			HighLevel.shell_write(3, "HIDDEN", "-> See log for further information.");
			if (error.equalsIgnoreCase("CmdNotFound")) {
				Lib.logWrite("CMDMAIN", 2, "The specified command was not found as internal or external command.");
			} else if (error.equalsIgnoreCase("FileNotFound")) {
				Lib.logWrite("CMDMAIN", 2, "The specified file or directory was not found.");
			} else if (error.equalsIgnoreCase("UnknownFileError")) {
				Lib.logWrite("CMDMAIN", 2, "There was an unknown file operation error.");
			} else if (error.equalsIgnoreCase("TestError")) {
				Lib.logWrite("CMDMAIN", 0, "Test error. Nothing went wrong :)");
			} else if (error.equalsIgnoreCase("Watchdog1Inactive")) {
				Lib.logWrite("CMDMAIN", 4, "Watchdog Thread 1 not running!");
				Lib.logWrite("CMDMAIN", 4, "Please reset JavaDOS or Reinstall!");
			} else if (error.equalsIgnoreCase("Watchdog2Inactive")) {
				Lib.logWrite("CMDMAIN", 4, "Watchdog Thread 2 not running!");
				Lib.logWrite("CMDMAIN", 4, "Please reset JavaDOS or Reinstall!");
			}
		} else {
			Lib.logWrite("CMDMAIN", 0, "Command executed successfully");
		}
		//====================================FINALIZATION=======================================
		Lib.refreshDateTime();
		Lib.cmdLinePrepare(false);
		Main.cmdLine.setEditable(true);
		return 0;
	}
}