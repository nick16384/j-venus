package internalCommands;

import java.util.ArrayList;
import java.util.Map;

import engine.sys;
import libraries.OpenLib;
import libraries.VarLib;

public class System_ChangeEnvironment {
	public static String changeEnv(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params.size() == 3) && (params.get(0).equalsIgnoreCase("-add"))) {
			if (VarLib.getEnv(params.get(1)) == null) { //If it doesn't already exist
				sys.shellPrint(0, "HIDDEN", "Adding new environment variable: " + params.get(1)
						+ "\nwith value: " + params.get(2) + "\n");
				VarLib.addEnv(params.get(1), params.get(2));
			} else {
				sys.shellPrint(3, "HIDDEN", "Key " + params.get(1) + " already exists.\n"
						+ "Cannot add two same keys.\n"
						+ "Try 'chEnv -modify' to change already existing variables.\n");
			}
			
		} else if ((params.size() == 3) && (params.get(0).equalsIgnoreCase("-modify"))) {
			if (VarLib.getEnv(params.get(1)) != null) { //Only execute if the value already exists
				sys.shellPrint(0, "HIDDEN", "Modifying variable: " + params.get(1)
					+ "\nOld value: " + VarLib.getEnv(params.get(1))
					+ "\nNew value: " + params.get(2));
				VarLib.changeEnv(params.get(1), params.get(2));
			} else {
				sys.shellPrint(0, "HIDDEN", "Environment variable '" + params.get(1) + "'\n"
						+ "does not exist, so it cannot be modified.\n"
						+ "Create new environemt variables with 'chEnv -add <yourKey> <value>'\n");
			}
			
			
		} else if ((params.size() >= 1) && (params.get(0).equalsIgnoreCase("-update"))) {
			if ((params.size() == 2) && (VarLib.getEnv(params.get(1)) != null)) {
				sys.shellPrint(1, "HIDDEN", "Updating '" + params.get(1) + "'\n");
				OpenLib.updateEnv(params.get(1));
			} else {
				sys.shellPrint(1, "HIDDEN", "Updating all variables.\n");
				OpenLib.updateEnv("$$ALL");
			}
			
		} else {
			sys.shellPrint(0, "HIDDEN", "Changes environent variables\n"
				+ "List all current environment variables with 'env'\n"
				+ "Parameters:\n"
				+ "-add <key> <value>\n"
				+ "-modify <key> <newValue>\n"
				+ "-update <varToUpdate> (Info: <varToUpdate> optional. Keep empty to update all.)\n"
				+ "Special environment variables (non-modifyable):\n"
				+ "$$ALL, $$NULL\n");
		}
		return null;
	}
}
