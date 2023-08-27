package commandProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import awtcomponents.AWTANSI;
import engine.InfoType;
import engine.sys;
import internalCommands.System_Exec;
import libraries.Err;
import libraries.ErrCodes;
import libraries.VariableInitializion;
import main.Main;
import shell.Shell;
import threads.ThreadAllocation;

public class CommandManager implements threads.InternalThread {
	public static final int MAX_COMMAND_QUEUE_SIZE = 64;
	
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
	private Command currentCommand;
	private BlockingQueue<Command> commandQueue;
	private Thread commandManagerThread;
	
	public CommandManager() {
		returnValues = new HashMap<>();
		silentExecution = false;
		noPrompt = false;
		suspend = false;
		currentCommand = null;
		commandQueue = new ArrayBlockingQueue<>(MAX_COMMAND_QUEUE_SIZE);
		
		commandManagerThread = new Thread(null, new Runnable() {
			public final void run() {
				//Separate thread waiting for commandQueue to run through, while main/Main can do other stuff
				
				while (!ThreadAllocation.isShutdownSignalActive() && !suspend) {
					try {
						currentCommand = commandQueue.take();
					} catch (InterruptedException ie) {
						sys.log("CMGR", InfoType.WARN, "Waiting for command input has been interrupted.");
						ie.printStackTrace();
						continue; // Do not execute, since no command is available for sure.
					}
					
					//==================================== COMMAND EXECUTION ====================================
					sys.log("CMGR", InfoType.DEBUG,
							"---\nRunning command : " + currentCommand.getFullCommand() + "\n---");
					
					isCommandRunning = true;
					try {
						returnValues.put(currentCommand,
								CmdSearch.findCommandAndExecute(
										currentCommand.getCommand(),
										currentCommand.getParams()));
					} catch (Exception ex) {
						returnValues.put(currentCommand, "RuntimeErr");
						Err.shellPrintErr(ex, "Command Runtime error",
								"While the command executed, an exception occurred.");
						ex.printStackTrace();
					}
					isCommandRunning = false;
					
					//ERROR CHECKING =======================================
					if (returnValues.get(currentCommand) == null) {
						cleanup(currentCommand);
						continue;
					}
					// There was an error at this point
					Shell.print("Exception in \"" + currentCommand.getCommand() + "\" : ");
					if (ErrCodes.getErrDesc(returnValues.get(currentCommand)) != null) {
						// Known error
						sys.log("CMGR", InfoType.DEBUG,
								"Error type \"" + returnValues.get(currentCommand) + "\" found.");
						Shell.print(AWTANSI.B_Yellow, ErrCodes.getErrDesc(returnValues.get(currentCommand)));
					} else {
						// Unknown error
						sys.log("CMGR", InfoType.WARN,
								"Command error not found in libraries.ErrCodes: "
										+ returnValues.get(currentCommand));
						Shell.print(AWTANSI.B_Red,
								"Non-regular error code : " + returnValues.get(currentCommand));
					}
					cleanup(currentCommand);
					//================================== COMMAND EXECUTION END ==================================
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
	 * Adds cmd to commandQueue, which will eventually be executed,
	 * if not cleared by clearCmdQueue();
	 * @param fullCommand
	 */
	public void invokeCommand(Command cmd) {
		boolean insertionSuccess;
		insertionSuccess = commandQueue.offer(cmd);
		if (!insertionSuccess)
			sys.log("CMGR", InfoType.WARN,
					"Command " + cmd.getCommand() + " not added to queue, because the capacity limit was reached.");
	}
	public void invokeCommand(String cmd) {
		invokeCommand(new Command(cmd));
	}
	/**
	 * Clears command queue, execution will stop after current command
	 */
	public void clearCmdQueue() {
		commandQueue.clear();
	}
	
	@Override
	public boolean isRunning() {
		return commandManagerThread.isAlive();
	}
	
	public void suspend() {
		sys.log("CMGR", InfoType.INFO, "Suspending Command Manager after current command is finished.");
		suspend = true;
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
		commandQueue.remove(currentCommand);
		if (commandQueue.isEmpty() && !currentCommand.getParams().contains("--noPrompt")) {
			//If empty after clearing last command, disable noPrompt
			noPrompt = false;
		} else {
			noPrompt = true;
		}
		sys.log("CMGR", InfoType.DEBUG, "NoPrompt is enabled: " + noPrompt);
		if (!noPrompt) {
			Shell.print("\n");
			Shell.showPrompt();
		}
	}
	
	public void killCurrentIfRunning() {
		if (isCommandRunning) {
			sys.log("CMGR", InfoType.DEBUG, "Killing current command.");
			System_Exec.forceKill();
			cleanup(currentCommand);
		} else {
			if (!noPrompt)
				Shell.showPrompt();
		}
	}
}
