package commands;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import awtcomponents.AWTANSI;
import engine.InfoType;
import engine.sys;
import libraries.Err;
import libraries.ErrCodes;
import shell.Shell;
import threads.ThreadAllocation;

public class CommandManagement {
	public static final int MAX_COMMAND_QUEUE_SIZE = 64;
	
	private static Thread commandManagementThread;
	private static BlockingQueue<Command> commandQueue;
	private static Executor commandExecutor;
	private static Map<Command, String> returnValues;
	private static Command currentCommand;
	
	private static volatile boolean suspend = false;
	
	public static void initialize() {
		commandQueue = new ArrayBlockingQueue<>(MAX_COMMAND_QUEUE_SIZE);
		commandExecutor = Executors.newSingleThreadExecutor();
		returnValues = new HashMap<>();
		
		commandManagementThread = new Thread(() -> {
			while (!sys.isShutdownSignalActive() && !suspend) {
				try {
					currentCommand = commandQueue.take();
				} catch (InterruptedException ie) {
					sys.log("CMGR", InfoType.WARN, "Waiting for command input has been interrupted.");
					ie.printStackTrace();
					continue; // Do not execute, since no command is available for sure.
				}
				
				// Continues here IF a command is available
				commandExecutor.execute(() -> {
					sys.log("CMGR", InfoType.DEBUG, "Launching " + currentCommand.getCommand());
					
					synchronized (commandExecutor) {
						executeCommand();
						doErrorCheckingAndCleanup();
						commandExecutor.notify();
					}
				});
			}
		}, "CMGR");
		
		commandManagementThread.start();
	}
	
	private static void executeCommand() {
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
	}
	
	private static void doErrorCheckingAndCleanup() {
		if (returnValues.get(currentCommand) == null) {
			cleanup();
			return;
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
		cleanup();
	}
	
	private static void cleanup() {
		Command lastCommand = commandQueue.poll();
		
		if (lastCommand != null && !lastCommand.equals(currentCommand))
			sys.log("CMGR", InfoType.CRIT, "Last command executed does not match queue head!");
		
		if (!currentCommand.getParams().contains("--noPrompt")) {
			sys.log("CMGR", InfoType.STATUS, "Command "
					+ currentCommand.getCommand()
					+ " requested no prompt to be shown.");
			Shell.print("\n");
			Shell.showPrompt();
		}
	}
	
	public static void killCurrentIfRunning() {
		currentCommand.cancel(true);
	}
	
	public static void suspend() {
		sys.log("CMGR", InfoType.DEBUG, "Suspending command manager thread.");
		suspend = true;
	}
	
	public static void invokeCommand(Command cmd) {
		boolean insertionSuccess = commandQueue.offer(cmd);
		sys.log("CMGR", InfoType.DEBUG, "Command invokation: "
				+ cmd.getCommand() + " : " + (insertionSuccess ? "SUCCESS" : "FAIL"));
	}
	
	public static synchronized String waitForReturnValue(Command cmd) {
		while (returnValues.get(cmd) == null) {
			try { commandExecutor.wait(50); }
			catch (InterruptedException ie) { ie.printStackTrace(); }
		}
		return returnValues.get(cmd);
	}
	
	/**
	 * Checks whether the command management thread (not command executor!) is running.
	 * @return
	 */
	public static boolean isRunning() {
		return commandManagementThread != null && commandManagementThread.isAlive();
	}
}
