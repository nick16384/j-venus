package components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import engine.sys;
import libraries.OpenLib;

public class Command {
	private Object[] all = null;
	private String fullCommand = "";
	private String command = "";
	private ArrayList<String> options = new ArrayList<>();
	private Map<String, String> optionsWithValues = new HashMap<>();
	private ArrayList<String> params = new ArrayList<>();
	private Map<String, String> paramsWithValues = new HashMap<>();
	private String returnVal = "";
	
	public Command(String fullCommand) {
		if (fullCommand != null) {
			all = engine.CommandParser.commandSplitArray(fullCommand);
		}
		this.fullCommand = fullCommand;
		try {
			if (all != null && all.length >= 1) {
				command = (String) all[0];
				
				if (all.length >= 2)
					options = (ArrayList<String>) all[1];
				else
					options = null;
				if (all.length >= 3)
					optionsWithValues = (Map<String, String>) all[2];
				else
					optionsWithValues = null;
				if (all.length >= 4)
					params = (ArrayList<String>) all[3];
				else
					params = null;
				if (all.length >= 5)
					paramsWithValues = (Map<String, String>) all[4];
				else
					paramsWithValues = null;
			} else {
				command = "";
			}
		} catch (ClassCastException cce) {
			sys.log("CMD", 2, "ClassCastException while mapping of command or arguments.");
			sys.shellPrint(3, "CMD", "ClassCastException while mapping of command or arguments.");
			command = "";
			options = new ArrayList<String>();
			optionsWithValues = new HashMap<String, String>();
			params = new ArrayList<String>();
			paramsWithValues = new HashMap<String, String>();
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
	public Map<String, String> getOptionsWithValues() {
		return optionsWithValues;
	}
	public Map<String, String> getParamsWithValues() {
		return paramsWithValues;
	}
	public String getFullCommand() {
		return fullCommand;
	}
	public String getReturnValue() {
		return returnVal;
	}
	
	public void start() throws IOException {
		returnVal = main.CommandMain.executeCommand(this);
	}
}
