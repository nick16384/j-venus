package commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import engine.InfoType;
import engine.sys;

public class Command implements Future {
	private String fullCommand;
	private String command;
	private ArrayList<String> params;
	private String returnValue;
	
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
			sys.log("CMD", InfoType.ERR, "Command could not be parsed.");
			throw new CommandParserException(
					"Parsing command failed. Maybe, it contains special characters or a special sequence.");
		}
		
		this.command = (String) commandAndParams[0];
		this.params = (ArrayList<String>) commandAndParams[1];
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}
}
