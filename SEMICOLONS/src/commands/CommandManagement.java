package commands;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import awtcomponents.AWTANSI;
import engine.LogLevel;
import engine.sys;
import jfxcomponents.ANSI;
import libraries.Err;
import libraries.ErrCodes;
import shell.Shell;
import threads.ThreadAllocation;

public class CommandManagement {
	public static final int MAX_COMMAND_QUEUE_SIZE = 64;
	
	private static Thread commandManagementThread;
	private static BlockingQueue<Command> commandQueue;
	private static ExecutorService commandExecutor;
	private static Map<Command, String> returnValues;
	private static Command currentCommand;
	private static volatile boolean disablePrompt;
	
	private static volatile boolean suspend = false;
	
	public static void initialize() {
		commandQueue = new ArrayBlockingQueue<>(MAX_COMMAND_QUEUE_SIZE);
		commandExecutor = Executors.newSingleThreadExecutor();
		returnValues = new HashMap<>();
		disablePrompt = false;
		
		commandManagementThread = new Thread(() -> {
			while (!sys.isShutdownSignalActive() && !suspend) {
				try {
					currentCommand = commandQueue.take();
				} catch (InterruptedException ie) {
					sys.log("CMGR", LogLevel.WARN, "Waiting for command input has been interrupted.");
					ie.printStackTrace();
					continue; // Do not execute, since no command is available for sure.
				}
				
				// Continues here IF a command is available
				commandExecutor.execute(() -> {
					sys.log("CMGR", LogLevel.DEBUG, "Launching " + currentCommand.getCommand());
					
					synchronized (commandExecutor) {
						currentCommand.executionTimeStartNow();
						executeCommand();
						currentCommand.executionTimeEndNow();
						sys.log("CMGR", LogLevel.STATUS,
								"Command '" + currentCommand.getCommand() + "'"
								+ " took " + currentCommand.getExecutionTime() + "ms to execute.");
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
			sys.log("CMGR", LogLevel.DEBUG,
					"Error type \"" + returnValues.get(currentCommand) + "\" found.");
			Shell.print(ANSI.B_Yellow, ErrCodes.getErrDesc(returnValues.get(currentCommand)));
		} else {
			// Unknown error
			sys.log("CMGR", LogLevel.WARN,
					"Command error not found in libraries.ErrCodes: "
							+ returnValues.get(currentCommand));
			Shell.print(ANSI.B_Red,
					"Non-regular error code : " + returnValues.get(currentCommand));
		}
		cleanup();
	}
	
	private static void cleanup() {
		Command lastCommand = commandQueue.poll();
		
		if (lastCommand != null && !lastCommand.equals(currentCommand))
			sys.log("CMGR", LogLevel.CRIT, "Last command executed does not match queue head!");
		
		if (!disablePrompt) {
			sys.log("CMGR", LogLevel.STATUS, "Command "
					+ currentCommand.getCommand()
					+ " requested no prompt to be shown.");
			Shell.print("\n");
			Shell.showPrompt();
		}
		disablePrompt = false;
	}
	
	protected static ExecutorService getCommandExecutor() {
		return commandExecutor;
	}
	
	protected static void reinitializeExecutor() {
		commandExecutor.shutdownNow();
		commandExecutor = Executors.newSingleThreadExecutor();
	}
	
	public static void killCurrentIfRunning() {
		sys.log("CMGR", LogLevel.INFO, "Terminating command execution.");
		currentCommand.cancel(true);
	}
	
	public static void suspend() {
		sys.log("CMGR", LogLevel.DEBUG, "Suspending command manager thread.");
		suspend = true;
	}
	
	public static void invokeCommand(Command cmd) {
		boolean insertionSuccess = commandQueue.offer(cmd);
		sys.log("CMGR", LogLevel.DEBUG, "Command invokation: "
				+ cmd.getCommand() + " : " + (insertionSuccess ? "SUCCESS" : "FAIL"));
	}
	
	public static void invokeCommand(Command cmd, boolean disablePromptTemporary) {
		disablePrompt = true;
		invokeCommand(cmd);
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
