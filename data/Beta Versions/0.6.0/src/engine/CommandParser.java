package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import main.Lib;

public class CommandParser {
	//Add second command (after |) to execution queue while executing first one
	private static Object[] executionQueue = new Object[4];
	
	public static Object[] commandSplitArray(String fullCommand) {
		String command = null;
		ArrayList<String> options = new ArrayList<String>(); //Saving option type parameters (e.g. ls -a)
		//Saving option type parameters, that have values to them attached (e.g. format -fs ntfs)
		Map<String, String> optionsWithValues = new HashMap<String, String>();
		ArrayList<String> params = new ArrayList<String>();
		
		//Subcommands to execute, only display result of last one: apt moo | md5sum - -> [WhateverMD5SUM]
		String[] subCommands = fullCommand.split("\\|"); //Support for only 2 commands until now!
		int index = 0;
		for (String subCommand : subCommands) {
			subCommands[index] = subCommand.strip();
			index++;
		}
		
		//=================================SUBCOMMAND ITERATE=================================
		//Iterate over each subCommand
		for (String subCommand : subCommands) {
			if (subCommands.length > 1) {
				Lib.logWrite("CMDPAR", 1, "More than one command.");
				Lib.logWrite("CMDPAR", 2, "Warning: Currently, more than 2 commands separated by '|' aren't supported!");
			}
			if (subCommand.contains(" ")) {
				command = subCommand.split(" ")[0];
				subCommand = subCommand.replaceFirst(command + " ", ""); //subCommand now contains parameters only
			} else {
				command = subCommand;
				subCommand = subCommand.replaceFirst(command, ""); //subCommand now contains parameters only
			}
			
			
			if (!subCommand.isBlank()) {
				//Separate " if existent
				String[] valuesBetweenCmds = null;
				if (subCommand.contains("\"")) {
					String[] valuesBetweenCmdsTmp = subCommand.split("\"");
					if (valuesBetweenCmdsTmp.length % 2 == 1) {
						Lib.logWrite("CMDPAR", 3, "There was an error parsing the command: Uneven number of '\"'");
						HighLevel.shell_write(3, "CMDPAR", "There was an error parsing the command: Uneven number of '\"'");
						return null;
					}
					valuesBetweenCmds = new String[valuesBetweenCmdsTmp.length];
					index = 0;
					//Separate out the actual values between "
					for (String val : valuesBetweenCmdsTmp) {
						if (index % 2 == 1) {
							valuesBetweenCmds[index] = val;
							Lib.logWrite("CMDPAR", 1, val);
						}
						index++;
					}
				}
				
				//Separate options
				if (subCommand.contains("-(-?)*")) {
					for (String option : subCommand.split("-(-)?")) {
						if (valuesBetweenCmds != null) {
							for (String val : valuesBetweenCmds) {
								if (subCommand.contains(option + "(\s)?\"" + val + "\"")) {
									if (option.startsWith("-")) {
										optionsWithValues.put(option.replaceFirst("-", ""), val);
										subCommand = subCommand.replace(option.replaceFirst("-", "") + val, "");
									} else { optionsWithValues.put(option.split(".\"")[0], val); }
									Lib.logWrite("CMDPAR", 1, "Option: " + option + ", Value: " + val);
								}
							}
						} else if (option.strip().contains(" ")) {
							optionsWithValues.put(option.split(" ")[0], option.split(" ")[1]);
							subCommand = subCommand.replace(option.split(" ")[0], "");
							subCommand = subCommand.replace(option.split(" ")[1], "");
							Lib.logWrite("CMDPAR", 1, "Option: " + option.split(" ")[0] + ", Value: " + option.split(" ")[1]);
							
						} else {
							options.add(option);
							params.add(option);
							subCommand = subCommand.replace(option, "");
							Lib.logWrite("CMDPAR", 1, "Option: " + option);
						}
					}
				}
				
				//Separate every other parameter
				if (!subCommand.isBlank()) {
					for (String param : subCommand.split(" ")) {
						params.add(param);
						Lib.logWrite("CMDPAR", 1, "Option: " + param);
						
					}
				}
			}
			if (options.isEmpty())
				options = null;
			if (optionsWithValues.isEmpty())
				optionsWithValues = null;
			if (params.isEmpty())
				params = null;
			
			if (command.isBlank()) {
				return new Object[] {"print", null, null, new ArrayList<String>().add("How did you get here?")};
			}
			
			
			if (subCommands.length > 1) {
				executionQueue = new Object[] {command, options, optionsWithValues, params};
				command = "";
				options.clear();
				optionsWithValues.clear();
				params.clear();
			}
			
			executionQueue[0] = command;
		}
		
		return new Object[] {command, options, optionsWithValues, params};
	}
	
	public static Object[] getExecutionQueue() {
		return executionQueue;
	}
}






//OLD CODE
/*command = fullCommand.split(" ")[0];
if (fullCommand.trim().equalsIgnoreCase(command)) { //If no whitespace after command
	fullCommand = fullCommand.replaceFirst(command, "");
} else { //Else (if whitespace after command e.g. for parameter)
	fullCommand = fullCommand.replaceFirst(command + " ", "");
}

if (!fullCommand.isBlank() && fullCommand.contains("--")) {
	try {
		String fullCommandTemp = fullCommand.substring(fullCommand.indexOf("--"));
		while (fullCommandTemp.contains("--")) {
			String key = fullCommandTemp.split("=")[0].replaceFirst("--", "");
			String value = fullCommandTemp.split("\"")[1];
			String valueKeyPair = fullCommandTemp.split("\"")[0] + fullCommandTemp.split("\"")[1];
			//TODO fix and test with apt moo.
			fullCommandTemp = fullCommandTemp.replace(valueKeyPair, "");
			paramsWithValues.put(key, value);
		}
	} catch (ArrayIndexOutOfBoundsException aioobe) {
		Lib.logWrite("HL", 3, "Parse error");
	}
	
	
	
	for (String cmdPart : fullCommand.split("--")) {
		if (!cmdPart.isBlank()) {
			fullCommand = fullCommand.replace(cmdPart, "");
			cmdPart = cmdPart.replaceAll("\"", "");
			String part1 = cmdPart.split("=")[0];
			String part2 = cmdPart.split("=")[1];
			paramsWithValues.put(part1, part2);
		}
	}
}
if (!fullCommand.isBlank()) {
	for (String cmdPart : fullCommand.split(" ")) {
		if (cmdPart.startsWith("-")) {
			params.add(cmdPart.trim().replaceFirst("-", ""));
		} else {
			params.add(cmdPart.trim());
		}
	}
}

Lib.logWrite("CMDTRANSLATE", 0, "Command: " + command);
for (String param : params) { Lib.logWrite("CMDTRANSLATE", 0, "Parameter: " + param); }
for (String paramKey : paramsWithValues.keySet()) {
	Lib.logWrite("CMDTRANSLATE", 0, "Parameter key: " + paramKey + ", value: " + paramsWithValues.get(paramKey));
}

if (command.isBlank()) {
	command = null;
}
if (params.isEmpty()) {
	params = null;
}
if (paramsWithValues.isEmpty()) {
	paramsWithValues = null;
}

final Object[] cmdContent = new Object[] {command, params, paramsWithValues};
return cmdContent;
}*/