package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import libraries.OpenLib;
import components.Command;

public class CommandParser {
	//Add second command (after |) to execution queue while executing first one
	private static Command[] executionQueue = new Command[0];
	
	public static Object[] commandSplitArray(String fullCommand) {
		String command = "";
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
		if (subCommands.length > 1) {
			executionQueue = new Command[subCommands.length];
		}
		
		//=================================SUBCOMMAND ITERATE=================================
		//Iterate over each subCommand
		for (String subCommand : subCommands) {
			command = "";
			options = new ArrayList<String>(); //Saving option type parameters (e.g. ls -a)
			//Saving option type parameters, that have values to them attached (e.g. format -fs ntfs)
			optionsWithValues = new HashMap<String, String>();
			params = new ArrayList<String>();
			/*if (subCommands.length > 1) {
				OpenLib.logWrite("CMDPAR", 1, "More than one command.");
				OpenLib.logWrite("CMDPAR", 2, "Warning: Currently, more than 2 commands separated by '|' aren't supported!");
			}*/
			if (subCommand.contains(" ")) {
				command = subCommand.split(" ")[0];
				subCommand = subCommand.replaceFirst(command + " ", ""); //subCommand now contains parameters only
			} else {
				command = subCommand;
				subCommand = subCommand.replaceFirst(command, ""); //subCommand now contains parameters only
			}
			
			
			//TODO First split command by "" instead of -
			//TODO make sysexec finally work
			if (!subCommand.isBlank()) {
				//Separate " if existent
				String[] valuesBetweenCmds = null;
				if (subCommand.contains("\"")) {
					sys.log("CMDPAR", 1, "Detected \" in subCommand");
					String[] valuesBetweenCmdsTmp = subCommand.split("\"");
					if (valuesBetweenCmdsTmp.length % 2 == 1) {
						sys.log("CMDPAR", 3, "There was an error parsing the command: Uneven number of '\"'");
						sys.shellPrint(3, "CMDPAR", "There was an error parsing the command: Uneven number of '\"'");
						return null;
					} else {
						sys.log("CMDPAR", 1, "The number of \"'s is even or there are no strings between them.");
						sys.log("CMDPAR", 1, "Proceeding with command parsing.");
					}
					valuesBetweenCmds = new String[valuesBetweenCmdsTmp.length];
					index = 0;
					//Separate out the actual values between "
					for (String val : valuesBetweenCmdsTmp) {
						if (index % 2 == 1) {
							valuesBetweenCmds[index] = val;
							sys.log("CMDPAR", 1, val);
						}
						index++;
					}
					sys.log("CMDPAR", 1, "Found the following contents between \"'s:");
					for (String val : valuesBetweenCmds) {
						sys.log("CMDPAR", 1, "Value: " + val);
					}
				}
				
				//Separate options
				if (subCommand.contains("-(-?)*")) {
					for (String option : subCommand.split("-(-)?")) {
						if (valuesBetweenCmds != null) {
							for (String val : valuesBetweenCmds) {
								if (subCommand.contains(option + "( )?\"" + val + "\"")) {
									if (option.startsWith("-")) {
										optionsWithValues.put(option.replaceFirst("-", ""), val);
										subCommand = subCommand.replace(option.replaceFirst("-", "") + val, "");
										index = 0; //Remove the element from valuesBetweenCmds
										for (String toRemove : valuesBetweenCmds) {
											if (toRemove.equals(val)) valuesBetweenCmds[index] = null;
											index++;
										}
										// End of removal
									} else { optionsWithValues.put(option.split(".\"")[0], val); }
								} else if (option.strip().equals("\"" + val + "\"")) {
									sys.log("CMDPAR", 1, "Detected a single option with \"s: " + option);
									options.add(option);
									subCommand = subCommand.replaceFirst(option, "");
								}
							}
						} else if (option.strip().contains(" ")) {
							optionsWithValues.put(option.split(" ")[0], option.split(" ")[1]);
							subCommand = subCommand.replace(option.split(" ")[0], "");
							subCommand = subCommand.replace(option.split(" ")[1], "");
						} else {
							options.add(option);
							params.add(option);
							subCommand = subCommand.replace(option, "");
						}
					}
				} else {
					if (valuesBetweenCmds != null) {
						for (String val : valuesBetweenCmds) {
							if (val != null) {
								params.add(val);
							}
						}
					}
				}
				
				//Separate every other parameter
				if (!subCommand.isBlank()) {
					if (params != null && params.isEmpty()) { params = new ArrayList<String>(); }
					for (String param : subCommand.split(" ")) {
						params.add(param);
						sys.log("CMDPAR", 1, "Option: " + param);
						
					}
				}
				
				sys.log("CMDPAR", 1, "Command: " + command);
				sys.log("CMDPAR", 1, "Listing parameters...");
				sys.log("CMDPAR", 1, "Options:");
				if (options != null && !options.isEmpty()) {
					for (String opt : options) {
						sys.log("CMDPAR", 1, "> " + opt);
					}
				} else {
					sys.log("CMDPAR", 1, "Options is empty");
				}
				sys.log("CMDPAR", 1, "Options with values");
				if (optionsWithValues != null && !optionsWithValues.isEmpty()) {
					for (String optWithVal : optionsWithValues.keySet()) {
						sys.log("CMDPAR", 1, "> Key: " + optWithVal + ", Value: " + optionsWithValues.get(optWithVal));
					}
				} else {
					sys.log("CMDPAR", 1, "Options with values is empty");
				}
				sys.log("CMDPAR", 1, "Parameters:");
				if (params != null && !params.isEmpty()) {
					for (String param : params) {
						sys.log("CMDPAR", 1, "> " + param);
					}
				} else {
					sys.log("CMDPAR", 1, "Parameters is empty");
				}
				
			}
			
			if (command.isBlank()) {
				return new Object[] {"print", null, null, new ArrayList<String>().add("\"How did you get here?\"")};
			}
			
			
			if (subCommands.length > 1) {
				index = 0;
				for (String cmd : subCommands) {
					executionQueue[index] = new Command(cmd);
					index++;
				}
				sys.shellPrint(1, "CMDPAR", getExecutionQueueStr());
				command = "";
				if (options != null) options.clear();
				if (optionsWithValues != null) optionsWithValues.clear();
				if (params != null) params.clear();
			}
		}
		
		return new Object[] {command, options, optionsWithValues, params};
	}
	
	public static Object[] getExecutionQueue() {
		return executionQueue;
	}
	
	//TODO work further with executionQueue
	//TODO work on startupscripts
	public static String getExecutionQueueStr() {
		String out = "";
		for (Command cmd : executionQueue) {
			out += cmd.getFullCommand() + "\n";
		}
		out = out + "\n";
		return out;
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