package commands;

import java.util.ArrayList;

import engine.InfoType;
import engine.sys;
import internalCommands.*;
import shell.Shell;

public class CmdSearch {
	private static String returnVal = "";
	public static String findCommandAndExecute(String command, ArrayList<String> params) throws Exception {
		command = command.trim();
		
		Shell.print("\n");
		
		switch (command) {
		//All commands derived from package internalCommands
		//Console / shell commands
		case "echo":
		case "print": ex(Console_Print.print(params, null)); break;
		case "cls":
		case "clear": ex(Console_ClearScreen.clearScreen(params, null)); break;
		case "cd":
		case "chdir": ex(Console_ChangeDirectory.changeDirectory(params, null)); break;
		case "alias": ex(Console_Alias.alias(params)); break;
		case "ctest":
		case "colortest": ex(Console_ColorTest.colorTest(params)); break;
		
		//System execution and debug
		case "env":
		case "getenv": ex(System_getEnvironment.getEnv(params, null)); break;
		case "chenv":
		case "chEnv":
		case "changeEnv": ex(System_ChangeEnvironment.changeEnv(params, null)); break;
		case "sysexec": ex(System_Exec.sysexec(params)); break;
		case "dbg":
		case "debug":
		case "sysdbg":
		case "sysdebug": ex(System_Debug.debug(params, null)); break;
		case "crash":
		case "causeTerm":
		case "causeTemination":
		case "intentionalCrash": ex(System_Cause_Error_Termination.causeErrTerm(params)); break;
		case "stacktrace":
		case "genStacktrace": ex(System_Generate_Stacktrace.generateStacktrace(params, null)); break;
		
		//Internal affecting commands
		case "restart": ex(Restart.restart(params, null)); break;
		case "exit":
		case "stop":
		case "terminate": ex(Terminate.terminate(params, null)); break;
		case "test": ex(Test.test(params, null)); break;
		
		//File operations
		case "ls":
		case "dir":
		case "list":
		case "lsdir": ex(File_listDirectory.listDirectory(params, null)); break;
		case "cat":
		case "read":
		case "readtext": ex(File_readText.readText(params, null)); break;
		case "pwd":
		case "printWorkingDir":
		case "printWorkingDirectory": ex(File_PrintWorkingDir.pwd(params, null)); break;
		case "stat":
		case "stats":
		case "fstat":
		case "fstats":
		case "filestat":
		case "filestats": ex(File_fileStats.fileStats(params, null)); break;
		case "huffman":
		case "huffmancode": ex(File_CRS_HuffmanCode.hoffmanCode(params, null)); break;
		case "lzss": ex(File_CRS_LZSS.crs_lzss(params, null)); break;
		
		//Cipher operations
		case "encrypt": ex(Cipher_Encrypt.encrypt(params, null)); break;
		case "decrypt": ex(Cipher_Decrypt.decrypt(params, null)); break;
		case "highestOccurrence":
		case "mostOccurrences": ex(Cipher_General_HighestOccurrenceChar.highestOccurrenceChar(params, null)); break;
		case "cksum":
		case "chksum": ex(Cipher_Chksum.chksum(params, null)); break;
		case "rand":
		case "random":
		case "randNum":
		case "pseudoRand": ex(Cipher_PseudoRand.pseudoRand(params)); break;
		default: execExternal(command, params);
		}
		return returnVal;
	}
	/**
	 * Mini function just to make switch case statements shorter and
	 * make code a little bit more readable.
	 * @param retVal
	 */
	private static void ex(String retVal) {
		returnVal = retVal;
	}
	
	private static void execExternal(String command, ArrayList<String> params) {
		// EXTERNAL JAR ================================================
		//TODO implement external jar execution (classes should still exist)
		sys.log("External .jar execution to be implemented!");
		
		// SYSTEM BINARY ===============================================
		sys.log("CMDEXEC", InfoType.INFO, "Running command as external system process.");
		ArrayList<String> newCommandArray = new ArrayList<String>();
		newCommandArray.add(command);
		newCommandArray.addAll(params);
		ex(System_Exec.sysexec(newCommandArray));
		if (returnVal == null || !returnVal.equals("CmdNotFound"))
			return;
		
		//If the command wasn't found to this point, it doesn't exist
		returnVal = "CmdNotFound";
	}
}
