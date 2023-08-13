package components;

import java.io.IOException;
import java.util.ArrayList;

import engine.InfoType;
import engine.sys;
import main.Main;
import threads.ThreadAllocation;

public class Command {
	private Object[] all = null;
	private String fullCommand = "";
	private String command = "";
	private ArrayList<String> options = new ArrayList<>();
	//private Map<String, String> optionsWithValues = new HashMap<>();
	private ArrayList<String> params = new ArrayList<>();
	//private Map<String, String> paramsWithValues = new HashMap<>();
	private String returnVal = "";
	
	/**
	 * Creates new Command object and parses it's content.
	 * @param fullCommand
	 * @throws NullPointerException When fullCommand is null
	 * @throws IllegalArgumentException When the list of arguments is somehow not readable (e.g. empty String)
	 */
	public Command(String fullCommand) throws NullPointerException, IllegalArgumentException {
		if (fullCommand == null) {
			throw new NullPointerException("fullCommand is null. Cannot parse.");
		}
		if (fullCommand.isEmpty()) {
			throw new IllegalArgumentException("fullCommand is an empty string (\"\" or whitespace only). Cannot parse.");
		}
		all = engine.CommandParser.parseCmd(fullCommand);
		
		this.fullCommand = fullCommand;
		try {
			if (all != null && all.length >= 1) {
				command = (String) all[0];
				
				if (all.length == 2)
					params = (ArrayList<String>) all[1];
			} else {
				command = "";
			}
		} catch (ClassCastException cce) {
			sys.log("CMD", InfoType.WARN, "ClassCastException while mapping of command or arguments.");
			Shell.print(3, "CMD", "ClassCastException while mapping of command or arguments.");
			command = "";
			options = new ArrayList<String>();
			params = new ArrayList<String>();
			cce.printStackTrace();
		}
	}
	
	public String getCommand() {
		return command;
	}
	public ArrayList<String> getOptions() {
		return options;
	}
	public ArrayList<String> getParams() {
		return params;
	}
	public String getFullCommand() {
		return fullCommand;
	}
	public String getReturnValue() {
		return returnVal;
	}
	
	public void start() throws IOException {
		ThreadAllocation.getCMGR().invokeCommand(this);
		//returnVal = main.CommandMain.executeCommand(this);
	}
}
