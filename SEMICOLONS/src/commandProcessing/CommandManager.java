package commandProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import awtcomponents.AWTANSI;
import components.Command;
import components.Shell;
import engine.InfoType;
import engine.sys;
import internalCommands.System_Exec;
import libraries.Err;
import libraries.ErrCodes;
import libraries.VariableInitializion;
import main.Main;
import threads.ThreadAllocation;

public class CommandManager implements threads.InternalThread {
	//
	//-> Successfully executed commands return null.
	private Map<Command, String> returnValues;
	public boolean silentExecution; //Silent execution
	public boolean noPrompt; //No prompt after execution is done
	private boolean suspend;
	private boolean isCommandRunning;
	//private static boolean disableParallelExecution = false;
	//private static boolean SIGTERM = false; //If on, command may detect and shutdown (if supported)
	//private static boolean stopExec = false; //If on, execThread will stop and has to be restarted to work again
	private String command;
	private LinkedList<components.Command> cmdQueue;
	public ArrayList<String> commandQueue; 
	private ArrayList<String> params;
	private Thread commandManagerThread;
	
	public CommandManager() {
		returnValues = new HashMap<>();
		silentExecution = false;
		noPrompt = false;
		suspend = false;
		command = "";
		cmdQueue = new LinkedList<>();
		params = new ArrayList<>();
		
		commandManagerThread = new Thread(null, new Runnable() {
			public final void run() {
				//Separate thread waiting for cmdQueue to run through, while main/Main can do other stuff
				
				while (!ThreadAllocation.isShutdownSignalActive() && !suspend) {
					if (cmdQueue.isEmpty()) {
						try { Thread.sleep(50); } catch (InterruptedException ie) { ie.printStackTrace(); }
						continue;
					}
					//If commands ARE in cmdQueue:
					//======================================= COMMAND EXECUTION =======================================
					for (Command cmdCurrent : cmdQueue) {
						sys.log("---\nRunning new command: " + cmdCurrent.getFullCommand() + "\n---");
						//Assign internal variables
						try {
							sys.log("CMDMGR", InfoType.DEBUG, "Mapping parameters to internal variables.");
							command = cmdCurrent.getCommand();
							params = cmdCurrent.getParams();
						} catch (ClassCastException cce) {
							returnValues.put(cmdCurrent, "ParseErr_MapFail");
							command = null;
						}
						
						//EXECUTE COMMAND =======================================
						isCommandRunning = true;
						try {
							if (command != null) {
								returnValues.put(cmdCurrent, commandProcessing.CmdSearch.findCommandAndExecute(command, params));
							} else {
								sys.log("CMGR", InfoType.WARN, "A previous error prevents the command from running.");
							}
						} catch (Exception e) {
							returnValues.put(cmdCurrent, "RuntimeErr");
							Err.shellPrintErr(e, "Command Runtime error", "While the command executed, an exception occurred.");
							e.printStackTrace();
						}
						isCommandRunning = false;
						
						//ERROR CHECKING =======================================
						if (returnValues.get(cmdCurrent) != null) {
							Shell.print("\"" + cmdCurrent.getCommand() + "\": ");
							if (ErrCodes.getErrDesc(returnValues.get(cmdCurrent)) == null) {
								//Unknown error
								sys.log("Command error not found in main.ErrCodes: " + returnValues.get(cmdCurrent));
								Shell.print(AWTANSI.B_Red, "Unknown error (not specified in main.ErrCodes): "
								+ returnValues.get(cmdCurrent));
							} else {
								//Known error
								sys.log("CMDMGR", InfoType.INFO, "Command error found in main.ErrCodes: " + returnValues.get(cmdCurrent));
								sys.log("CMDMGR", InfoType.INFO, "Error description: " + ErrCodes.getErrDesc(returnValues.get(cmdCurrent)));
								Shell.println(ErrCodes.valueOf(returnValues.get(cmdCurrent)) + " : "
										+ ErrCodes.getErrDesc(returnValues.get(cmdCurrent)));
							}
						}
						
						//Clear up, and write new shell line =======================================
						cleanup(cmdCurrent);
					}
					//======================================= COMMAND EXECUTION END =======================================
				}
				sys.log("Command Manager Thread shutdown");
			}
		}, "CMGR");
	}
	@Override
	public void start() {
		if (commandManagerThread.isAlive()) {
			return;
		} else {
			commandManagerThread.start();
			commandManagerThread.setPriority(6); //0 is min. priority, 10 is max. priority
		}
	}
	/**
	 * Adds cmd to cmdQueue, which will eventually be executed,
	 * if not cleared by clearCmdQueue();
	 * @param fullCommand
	 */
	public void invokeCommand(Command cmd) {
		cmdQueue.add(cmd);
	}
	public void invokeCommand(String cmd) {
		cmdQueue.add(new Command(cmd));
	}
	/**
	 * Swaps first Command in cmdQueue with the provided one (cmd)
	 * If cmd is not in cmdQueue, it is added and the swapped with the first one
	 * @param cmd
	 */
	public void executeImmediately(Command cmd) {
		if (cmdQueue.contains(cmd)) {
			Collections.swap(cmdQueue, 0, cmdQueue.indexOf(cmd));
		} else {
			cmdQueue.add(cmd);
			Collections.swap(cmdQueue, 0, cmdQueue.lastIndexOf(cmd));
		}
	}
	/**
	 * Clears command queue, execution will stop after current command
	 */
	public void clearCmdQueue() {
		cmdQueue.clear();
	}
	
	@Override
	public boolean isRunning() {
		return commandManagerThread.isAlive();
	}
	
	public void suspend() {
		sys.log("CMGR", InfoType.ERR, "Suspend method not implemented (not in use for this class).");
	}
	
	public boolean isCommandRunning() {
		return isCommandRunning;
	}
	
	/**
	 * This method is used for cleaning up after command execution and
	 *  add a new command line.
	 * @param currentCommand
	 */
	private void cleanup(Command currentCommand) {
		cmdQueue.remove(currentCommand);
		if (cmdQueue.isEmpty() && !params.contains("--noPrompt")) {
			//If empty after clearing last command, disable noPrompt
			noPrompt = false;
		} else {
			noPrompt = true;
		}
		sys.log("CMGR", InfoType.DEBUG, "NoPrompt is enabled: " + noPrompt);
		//Wait 500ms for any text printing to finish -> CmdLine will scroll to last line eventually
		try { Thread.sleep(100); } catch (InterruptedException ie) { ie.printStackTrace(); }
		if (!noPrompt) {
			Shell.showPrompt();
		}
	}
	
	public void killCurrent() {
		sys.log("CMGR", InfoType.WARN, "Killing current command.");
		System_Exec.forceKill();
		cleanup(cmdQueue.getFirst());
	}
}
