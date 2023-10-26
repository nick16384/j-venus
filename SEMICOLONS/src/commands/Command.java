package commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import engine.LogLevel;
import engine.sys;
import internalCommands.System_Exec;

public class Command implements Future<String> {
	private String fullCommand;
	private String command;
	private ArrayList<String> params;
	private String returnValue;
	
	private long executionTimeStart = 0;
	private long totalExecutionTime = 0;
	
	/**
	 * Creates a new command and parses its contents.
	 * @param fullCommand
	 * @throws NullPointerException If the command specified is null.
	 * @throws IllegalArgumentException If the command specified is an empty String.
	 * @throws CommandParserException If the command parser had an error processing the command.
	 */
	
	@SuppressWarnings("unchecked")
	public Command(String fullCommand)
			throws NullPointerException, IllegalArgumentException, CommandParserException {
		this.fullCommand = fullCommand;
		if (fullCommand == null)
			throw new NullPointerException("Cannot instantiate Command with null.");
		else if (fullCommand.isEmpty())
			throw new IllegalArgumentException("Cannot instantiate Command with an empty String.");
		
		// This is an Object array, because is contains a String and an ArrayList
		Object[] commandAndParams = CommandParser.parseCmd(fullCommand);
		
		if (commandAndParams == null || commandAndParams.length != 2) {
			sys.log("CMD", LogLevel.ERR, "Command could not be parsed.");
			throw new CommandParserException(
					"Parsing command failed. Maybe, it contains special characters or a special sequence.");
		}
		
		this.command = (String) commandAndParams[0];
		this.params = (ArrayList<String>) commandAndParams[1];
	}
	
	public void executionTimeStartNow() {
		if (executionTimeStart <= 0)
			executionTimeStart = System.currentTimeMillis();
	}
	
	public void executionTimeEndNow() {
		if (totalExecutionTime <= 0)
			totalExecutionTime = System.currentTimeMillis() - executionTimeStart;
	}
	
	public long getExecutionTime() {
		return totalExecutionTime;
	}
	
	public void start() throws IOException {
		CommandManagement.invokeCommand(this);
	}
	
	public String waitForReturnValue() {
		return CommandManagement.waitForReturnValue(this);
	}
	
	public String getFullCommand() {
		return fullCommand;
	}
	public String getCommand() {
		return command;
	}
	public ArrayList<String> getParams() {
		return params;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// Kill external process if running
		System_Exec.killProcessIfRunning();
		CommandManagement.getCommandExecutor().shutdownNow();
		CommandManagement.reinitializeExecutor();
		return true;
	}

	@Override
	public boolean isCancelled() {
		return returnValue != null;
	}

	@Override
	public boolean isDone() {
		return returnValue != null;
	}

	@Override
	public String get() throws InterruptedException, ExecutionException {
		waitForReturnValue();
		return returnValue;
	}
	
	/**
	 * @apiNote Currently the same as get() without timeout.
	 */
	@Override
	public String get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		waitForReturnValue();
		return returnValue;
	}
}
