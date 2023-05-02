package commandProcessing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import engine.AWTANSI;
import engine.sys;
import libraries.Err;
import libraries.OpenLib;
import libraries.VarLib;
import main.Main;

/**
 * Replaced by commandProcessing.CommandManager class
 * Developer's note: This class originally used main.Main.cmdLine access,
 * but was existent until version 22.12 (included), so main.Main.cmdLine
 * was replaced with Main.mainFrame.getCmdLine()
 * @author nick16384
 * @deprecated Class deprecated due to limited single-thread support, which was fixed
 * in new class commandProcessing.CommandManager
 */

@Deprecated
public class CommandMain {
	private static String returnVal = "CmdNotFound"; //If this is never changed, it means that the command was not found.
	  //-> Successfully executed commands return null.
	private static boolean err = false; //Determines whether there was an error in execution or not
	public static boolean silentExecution = false; //Silent execution
	public static boolean noPrompt = false; //No prompt after execution done
	private static boolean disableParallelExecution = false;
	private static boolean SIGTERM = false; //If on, command will detect and shutdown
	private static boolean stopExec = false; //If on, execThread will stop and has to be restarted to work again
	private static String command = "";
	private static String commandType = ""; //Command types: internal, external, system
	private static LinkedList<components.Command> cmdQueue = new LinkedList<>();
	private static components.Command commandObj = null;
	public static ArrayList<String> commandQueue = new ArrayList<>(); 
	private static ArrayList<String> options = new ArrayList<String>();
	private static Map<String, String> optionsWithValues = new HashMap<String, String>();
	private static ArrayList<String> params = new ArrayList<String>();
	//For legacy support only, will be removed in the future
	private static Map<String, String> paramsWithValues = new HashMap<String, String>();
	
	public static ProcessBuilder execProcBuild = null;
	public static Process execProc = null;
	private static Thread execThread = new Thread( new Runnable() {
		public final void run() {
			while (!stopExec) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException ie) {
					sys.log("CMDEXEC", 1, "Woken up, executing commands.");
					executeCommand();
				}
			}
			sys.log("EXECTHR", 2, "execThread stopping, because stopExec is true.");
		}
	});
	
	public static void terminate() {
		try {
			//Destroy (kill) process and if alive after 1 second, kill forcibly (SIGKILL)
			SIGTERM = true;
			if (commandType.equals("internal")) {
				sys.log("EXECTERM", 0, "Command is executed internally inside a thread.");
				sys.shellPrintln(AWTANSI.D_Magenta, "Thread interrupt.");
				try { Thread.sleep(1000); } catch (InterruptedException ie) { sys.log("CMDMAIN", 2, "ie50"); }
				execThread.stop();
			} else {
				sys.log("EXECTERM", 0, "Command is running externally as separate process.");
				sys.shellPrintln(AWTANSI.D_Magenta, "Process interrupt.");
				execProc.destroy();
				try { Thread.sleep(1000); } catch (InterruptedException ie) { sys.log("CMDMAIN", 2, "ie50"); }
				if (execProc != null && execProc.isAlive()) {
					sys.log("EXECTERM", 2, "SIGTERM/SIGINT could not stop process. Destroying with SIGKILL.");
					sys.shellPrintln(AWTANSI.B_Magenta, "Process kill.");
					execProc.destroyForcibly();
				}
			}
			OpenLib.refreshDateTime();
			OpenLib.cmdLinePrepare();
			Main.mainFrame.getCmdLine().setEditable(true);
		} catch (SecurityException se) {
			sys.log("EXECTERM", 4, "Could not terminate execution process.");
		}
	}
	
	/**
	 * Places cmd to the command queue, which is then executed sequentially inside execThread.
	 * Use immediateExecute(cmd) for direct execution. For more details, check the JavaDoc of immediateExecute().
	 * @param cmd
	 * @return
	 * @throws IOException
	 */
	//============================================================================================================
	//============================================================================================================
	public static String executeCommand(components.Command cmd) throws IOException {
		try {
			//TODO implement command queue and add current cmd to the fist entry, if multiple commands are to be executed
			//TODO rename the rest of the entire project from JavaDOS / JDOS to J-Vexus / Vexus
			//TODO add motd file and parser instead of rewriting it every time and to add customizability
			cmdQueue.add(cmd);
			cmd = cmdQueue.get(0);
			cmdQueue.remove(0);
			commandObj = cmd;
			command = cmd.getCommand();
			commandQueue.add(cmd.getFullCommand());
			params = cmd.getParams();
			//TODO if cmdQueue is not empty, set noPrompt to true (inside execThread)
			
		} catch (ClassCastException cce) {
			sys.log("CMDMAIN", 3, "Mapping of parameters to internal variables failed. Not executing.");
			return "ParseError";
		}
		paramsWithValues = optionsWithValues;
		if (params != null && params.contains("--silent")) { silentExecution = true; }
		if (params != null && params.contains("--noPrompt")) { noPrompt = true; }
		
		if (params != null && params.contains("--disableParallelExecution")) disableParallelExecution = true;
		
		if (execThread.isAlive()) {
			return "ThreadStillActive";
		}
		
		if (!execThread.isAlive() || !(disableParallelExecution)) {
			if (execThread.isAlive()) { noPrompt = true; }
			execThread = new Thread( new Runnable() {
				public final void run() {
					executeCommand();
				}
			});
			sys.log("CMDMAIN", 1, "Starting new thread for command execution...");
			execThread.start();
			return "Started";
		} else {
			sys.log("CMDMAIN", 3, "Deprecated error message (since 22.08):");
			sys.log("CMDMAIN", 3, "Cannot execute command. A command is already running.");
			sys.log("CMDMAIN", 3, "Please wait for execution to finish!");
			sys.log("CMDMAIN", 3, "To force execution, type '-forceExecThread' (not recommended!)");
			sys.shellPrint(3, "CMDMAIN", "Deprecated error message (since 22.08):\n");
			sys.shellPrint(3, "CMDMAIN", "Cannot execute command. A command is already running.\n");
			sys.shellPrint(3, "CMDMAIN", "Please wait for execution to finish.\n");
			sys.shellPrint(3, "CMDMAIN", "To force execution, type '-forceExecThread' (not recommended!)\n");
			
			return "ThreadExecError";
		}
		
	}
		
	
	//===============================EXEC METHOD FOR THREADS=============================
	private static String executeCommand() {
		Main.mainFrame.getCmdLine().setEditable(false);
		//Replace all environment-variable-references with their values
		ArrayList<String> allCmdElements = new ArrayList<>();
		allCmdElements.add(command);
		allCmdElements.addAll(params);
		int iterations = 0;
		for (String prm : allCmdElements) {
			String prmOld = prm;
			if (prm.contains("$")) {
				String env = "$" + prm.split("\\$", 2)[1].split(" ")[0];
				sys.log("CMDMAIN", 1, "Searching for environment variable '" + env + "'");
				if (VarLib.getEnv(env) != null) {
					sys.log("CMDMAIN", 1, "Found! " + env + " -> " + VarLib.getEnv(env));
					prm = prm.replace(env, VarLib.getEnv(env));
				} else if (env.equals("$$NULL")) {
					sys.log("CMDMAIN", 2, "Found, but trying to break stuff: $$NULL\n");
					prm = null;
				}
				else {
					sys.log("CMDMAIN", 1, "Not found. Keeping String.");
				}
			}
			if (!(prmOld.equals(prm))) {
				if (iterations < 1) {
					command = prm;
				} else {
					params.set(params.indexOf(prmOld), prm);
				}
			}
			iterations++;
		}
		//=================================VARIABLE PREPARATION==================================
		//Splitting full command into command, required Parameters(params) and optional Parameters(paramsWithValues)
		
		
		//==================================PRE-returnVal-CHECKING==================================
		if (command.isBlank()) {
			sys.log("CMDMAIN", 2, "Command is empty");
			sys.log("CMDMAIN", 0, "");
			OpenLib.refreshDateTime();
			OpenLib.cmdLinePrepare();
			Main.mainFrame.getCmdLine().setEditable(true);
			return "";
		} else if (sys.getActivePhase().equalsIgnoreCase("pre-init")) {
			sys.log("CMDMAIN", 3, "JavaDOS is still in pre-init phase. Cannot execute commands.");
			OpenLib.refreshDateTime();
			OpenLib.cmdLinePrepare();
			Main.mainFrame.getCmdLine().setEditable(true);
			return "PhaseNotRun";
		}
		
		//==================================COMMAND EXECUTION====================================
		try {
			if (!sys.getCurrentShellMode().equalsIgnoreCase("normal")) {
				if (command.equalsIgnoreCase("exit")) {
					internalCommands.Terminate.terminate(params, paramsWithValues);
				} else {
					sys.log("CMDMAIN", 2, "Warning: JavaDOS is in native/legacy mode and normal commands cannot be executed.");
					returnVal = "ShellModeNotNormal";
				}
			} else { //Shellmode is normal
				//sys.shellPrintln(ANSI.B_Blue, "Intern");
				commandType = "internal";
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
				} else if (command.equalsIgnoreCase("chEnv") || command.equalsIgnoreCase("changeEnv")) {
					returnVal = internalCommands.System_ChangeEnvironment.changeEnv(params, paramsWithValues);
				} else if (command.equalsIgnoreCase("env") || command.equalsIgnoreCase("getEnv")) {
					returnVal = internalCommands.System_getEnvironment.getEnv(params, paramsWithValues);
				} else if (command.equalsIgnoreCase("readText") || command.equalsIgnoreCase("read") || command.equalsIgnoreCase("cat")) {
					returnVal = internalCommands.File_readText.readText(params, paramsWithValues);
				} else if (command.equalsIgnoreCase("dir") || command.equalsIgnoreCase("ls")) {
					returnVal = internalCommands.File_listDirectory.listDirectory(params, paramsWithValues);
				} else if (command.equalsIgnoreCase("pwd")) {
					returnVal = internalCommands.File_PrintWorkingDir.pwd(params, paramsWithValues);
				} else if (command.equalsIgnoreCase("chprompt") || command.equalsIgnoreCase("prompt")) {
					returnVal = internalCommands.Console_ChangePrompt.changePrompt(params, paramsWithValues);
				} else if (command.split("\\.")[0].equalsIgnoreCase("crs")) {
					if (command.split("\\.").length > 1) {
						if (command.split("\\.")[1].equalsIgnoreCase("lzss")) {
							returnVal = internalCommands.File_CRS_LZSS.crs_lzss(params, paramsWithValues);
						}
					} else {
						sys.shellPrint(0, "HIDDEN", "Compress, Reduce, and shrink collection:\n"
								+ "crs.lzss\n");
					}
				} else if (command.equalsIgnoreCase("genStacktrace")) {
					returnVal = internalCommands.System_Generate_Stacktrace.generateStacktrace(params, paramsWithValues);
				} else if (command.equalsIgnoreCase("shellmode")) {
					engine.sys.setShellMode(params.get(0));
					returnVal = null;
				} else if (command.equalsIgnoreCase("chksum") || command.equals("cksum")) {
					sys.shellPrint(3, "CHKSUM", "Warning: checksum calculation with TEST-32 method can cause\n"
						+ "JavaDOS to hang long periods of time. Please be patient.\n");
					returnVal = internalCommands.Cipher_Chksum.chksum(params, paramsWithValues);
				} else if (command.equalsIgnoreCase("mostOccurChar") || command.equalsIgnoreCase("highestOccurrenceChar")) {
					returnVal = internalCommands.Cipher_General_HighestOccurrenceChar.highestOccurrenceChar(params, paramsWithValues);
				} else if (command.equalsIgnoreCase("debug") || command.equalsIgnoreCase("dbg")) {
					returnVal = internalCommands.System_Debug.debug(params, paramsWithValues);
				} else if (command.equalsIgnoreCase("sysexec")) {
					commandType = "system";
					returnVal = internalCommands.System_Exec.sysexec(params);
				} else {
					//Command is not internal (external, binary or not existent)
					commandType = "external";
					sys.shellPrintln(AWTANSI.B_Blue, "Extern");
					sys.log("CMDMAIN", 1, "Trying to execute command as .jar file in /commands/");
					//==================================EXT. COMMAND SYNTAX CHECK====================================
					if (new File(commandObj.getCommand()).isFile()) {
						sys.shellPrintln(AWTANSI.B_Blue, "Extern : fullPath");
						execProcBuild = new ProcessBuilder(VarLib.getJavaExec().getAbsolutePath(),
								"-jar", commandObj.getFullCommand());
					} else if (new File(commandObj.getCommand() + ".jar").isFile()) {
						sys.shellPrintln(AWTANSI.B_Blue, "Extern : fullPathWithoutJar");
						if (commandObj.getFullCommand().contains(" ")) {
							sys.shellPrintln(AWTANSI.B_Blue, "Extern : fullPathWithoutJar : params");
							execProcBuild = new ProcessBuilder(VarLib.getJavaExec().getAbsolutePath(),
									"-jar", commandObj.getFullCommand().replaceFirst(" ", ".jar "));
						} else {
							sys.shellPrintln(AWTANSI.B_Blue, "Extern : fullPathWithoutJar : noParams");
							execProcBuild = new ProcessBuilder(VarLib.getJavaExec().getAbsolutePath(),
									"-jar", commandObj.getFullCommand() + ".jar");
						}
					} else if (new File(VarLib.getCmdDir().getAbsolutePath() + VarLib.fsep
							+ commandObj.getCommand()).isFile()) {
						sys.shellPrintln(AWTANSI.B_Blue, "Extern : noPath");
						execProcBuild = new ProcessBuilder(VarLib.getJavaExec().getAbsolutePath(),
								"-jar", VarLib.getCmdDir().getAbsolutePath() + VarLib.fsep + commandObj.getFullCommand());
					} else if (new File(VarLib.getCmdDir().getAbsolutePath() + VarLib.fsep
							+ commandObj.getCommand() + ".jar").isFile()) {
						sys.shellPrintln(AWTANSI.B_Blue, "Extern : noPathWithoutJar");
						if (commandObj.getFullCommand().contains(" ")) {
							sys.shellPrintln(AWTANSI.B_Blue, "Extern : noPathWithoutJar : params");
							execProcBuild = new ProcessBuilder(VarLib.getJavaExec().getAbsolutePath(),
									"-jar", VarLib.getCmdDir().getAbsolutePath()
									+ VarLib.fsep + commandObj.getFullCommand().replaceFirst(" ", ".jar "));
						} else {
							sys.shellPrintln(AWTANSI.B_Blue, "Extern : noPathWithoutJar : noParams");
							execProcBuild = new ProcessBuilder(VarLib.getJavaExec().getAbsolutePath(),
									"-jar", VarLib.getCmdDir().getAbsolutePath()
									+ VarLib.fsep + commandObj.getFullCommand() + ".jar");
						}
					} else {
						commandType = "system";
					}
					//============================EXT. COMMAND SYNTAX CHECK COMPLETE==================================
					//==================================EXT. COMMAND RUN==============================================
					if (commandType.equals("external")) {
						try {
							sys.shellPrintln(AWTANSI.D_Cyan, "----------------------------------------------------------");
							sys.shellPrintln(AWTANSI.D_Cyan, "Debug information:");
							sys.shellPrintln("Commandline: " + execProcBuild.command().get(0) + " "
									+ execProcBuild.command().get(1) + " " + execProcBuild.command().get(2));
							sys.shellPrintln(execProcBuild.toString());
							execProc = execProcBuild.start();
							sys.shellPrintln(execProc.info().toString());
							sys.shellPrintln(AWTANSI.D_Cyan, "----------------------------------------------------------");
						} catch (IOException ioe) {
							sys.log("CMDMAIN", 3, "Command '" + commandObj.getFullCommand() + "' could not be executed. "
									+ "Try using the full command path (e.g. \"/etc/vexus/commands/exec.jar\" instead of \"exec\")");
						}
					}
					//Command is neither internal nor external and is executed as system binary
					if (execProc == null || !execProc.isAlive() || execProc.info().toString().equals("[]")) {
						//Process if null, not alive or execProc.info() is empty
						commandType = "system";
						sys.shellPrintln(AWTANSI.B_Blue, "System");
						sys.log("CMDMAIN", 1, "Trying to execute \n'"
								+ commandObj.getFullCommand()
								+ "'\n with SYSEXEC");
						returnVal = internalCommands.System_Exec.sysexec(params);
					}
				}
			}
			//===========================CATCH ANY EXCEPTION IN EXEC=============================
		} catch (Exception e) {
			Err.shellPrintErr(e, "RUNTIME ERROR", "Command runtime (execution) exception");
		}
		
		if (returnVal != null) {
			sys.shellPrint(1, "HIDDEN", returnVal + "\n");
		}
		//===================================ERROR RESOLVING=====================================
		if ((params != null) && (params.size() >= 1) && (params.get(0) != null)) {
			if (params.get(0).equalsIgnoreCase("noErrorChecking")) {
				OpenLib.refreshDateTime();
				OpenLib.cmdLinePrepare();
				Main.mainFrame.getCmdLine().setEditable(true);
				return "";
			}
		}
		if ((returnVal != null) && !(execThread.isAlive())) {
			if (returnVal.equalsIgnoreCase("CmdNotFound")) {
				err = true;
				sys.log("CMDMAIN", 2, "The specified command was not found as internal or external command.");
			} else if (returnVal.equalsIgnoreCase("FileNotFound")) {
				err = true;
				sys.log("CMDMAIN", 2, "The specified file or directory was not found.");
			} else if (returnVal.equalsIgnoreCase("UnknownFileError")) {
				err = true;
				sys.log("CMDMAIN", 2, "There was an unknown file operation returnVal.");
			} else if (returnVal.equalsIgnoreCase("TestError")) {
				err = true;
				sys.log("CMDMAIN", 0, "Test Error. Nothing went wrong :)");
			} else if (returnVal.equalsIgnoreCase("Watchdog1Inactive")) {
				err = true;
				sys.log("CMDMAIN", 4, "Watchdog Thread 1 not running!");
				sys.log("CMDMAIN", 4, "Please reset JavaDOS or Reinstall!");
			} else if (returnVal.equalsIgnoreCase("Watchdog2Inactive")) {
				err = true;
				sys.log("CMDMAIN", 4, "Watchdog Thread 2 not running!");
				sys.log("CMDMAIN", 4, "Please reset JavaDOS or Reinstall!");
			} else if (returnVal.equalsIgnoreCase("ShellModeNotNormal")) {
				err = true;
				//Something
				//TODO Extend shellmodes and legacy/native control to lowlevel
			} else if (returnVal.equalsIgnoreCase("paramMissing")
					|| returnVal.equalsIgnoreCase("reqParamParseError")) {
				err = true;
				sys.log("CMDMAIN", 4, "A required parameter was not specified.");
			}
		}
		if (err) {
			sys.shellPrint(1, "HIDDEN", "\n");
			sys.shellPrint(3, "HIDDEN", "Something went wrong whilst executing the command. \n");
			sys.shellPrint(3, "HIDDEN", "The following Error occured: " + returnVal + "\n");
			sys.shellPrint(3, "HIDDEN", "-> See log for further information.");
		}
		//====================================FINALIZATION=======================================
		if (silentExecution == true) { silentExecution = false; }
		if (!noPrompt) {
			OpenLib.refreshDateTime();
			OpenLib.cmdLinePrepare();
			Main.mainFrame.getCmdLine().setEditable(true);
		} else {
			noPrompt = false;
		}
		return returnVal;
	}
	
	public static boolean isExecThreadAlive() {
		return execThread.isAlive();
	}
	public static boolean isStop() {
		return SIGTERM;
	}
}