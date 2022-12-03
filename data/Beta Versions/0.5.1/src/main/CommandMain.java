package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import engine.HighLevel;

public class CommandMain {
	protected static int executeCommand(ArrayList<String> commandContent) throws IOException {
		Main.cmdLine.setEditable(false);
		//=================================VARIABLE PREPARATION==================================
		//Splitting full command into command, required Parameters(reqParams) and optional Parameters(optParams)
		String error = "CmdNotFound"; //If this is never changed, it means that the command was not found.
									  //-> Successfully executed commands return null.
		if (commandContent.get(0).isBlank()) {
			Lib.logWrite("CMDMAIN", 2, "Command is empty");
			Lib.logWrite("CMDMAIN", 0, "");
			Lib.refreshDateTime();
			Lib.cmdLinePrepare(false);
			Main.cmdLine.setEditable(true);
			return 1;
		}
		String command = commandContent.get(0);
		int reqParamLength = 0;
		int optParamLength = 0;
		for (String element : commandContent) { if (element.contains("<reqParam>")) { reqParamLength++; } } //Count reqParams
		for (String element : commandContent) { if (element.contains("<optParam>")) { optParamLength++; } }
		String[] reqParams = new String[reqParamLength];
		String[] optParams = new String[optParamLength];
		
		//TODO Maybe bring back UITranslate here, because creating a directory
		//TODO called '<optParams>' breaks JavaDOS
		
		int index = 0;
		try {
			for (String element : commandContent) {
				if (element.contains("<reqParam>")) 
					{ reqParams[index] = element.replace("<reqParam>", ""); index++; } //Write reqParam to Array
			}
			index = 0;
			for (String element : commandContent) {
				if (element.contains("<optParam>")) 
					{ optParams[index] = element.replace("<optParam>", ""); index++; } //Write reqParam to Array
			}
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			Lib.logWrite("CMDMAIN", 0, "CMDMAIN: OutOfBoundsException while command processing");
		}
		
		//==================================COMMAND EXECUTION====================================
		if (command.equalsIgnoreCase("encrypt")) {
			error = internalCommands.Cipher_Encrypt.encrypt(reqParams, optParams);
		} else if (command.equalsIgnoreCase("decrypt")) {
			error = internalCommands.Cipher_Decrypt.decrypt(reqParams, optParams);
		} else if (command.equalsIgnoreCase("print")) {
			error = internalCommands.Console_Print.print(reqParams, optParams);
		} else if (command.equalsIgnoreCase("clear") || command.equalsIgnoreCase("cls")) {
			error = internalCommands.Console_ClearScreen.clearScreen(reqParams, optParams);
		} else if (command.equalsIgnoreCase("reload") || command.equalsIgnoreCase("reset")) {
			error = internalCommands.Reload.reload(reqParams, optParams); return 0;
		} else if (command.equalsIgnoreCase("terminate") || command.equalsIgnoreCase("stop ") || command.equalsIgnoreCase("exit")) {
			error = internalCommands.Terminate.terminate(reqParams, optParams);
		} else if (command.equalsIgnoreCase("cd") || command.equalsIgnoreCase("chdir")) {
			error = internalCommands.Console_ChangeDirectory.changeDirectory(reqParams, optParams);
		} else if (command.equalsIgnoreCase("test")) {
			error = internalCommands.Test.test(reqParams, optParams);
		} else if (command.equalsIgnoreCase("readText") || command.equalsIgnoreCase("read")) {
			error = internalCommands.File_readText.readText(reqParams, optParams);
		} else if (command.equalsIgnoreCase("dir") || command.equalsIgnoreCase("ls")) {
			error = internalCommands.File_listDirectory.listDirectory(reqParams, optParams);
		} else if (command.equalsIgnoreCase("chprompt") || command.equalsIgnoreCase("prompt")) {
			error = internalCommands.Console_ChangePrompt.changePrompt(reqParams, optParams);
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