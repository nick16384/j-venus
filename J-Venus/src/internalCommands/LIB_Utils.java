package internalCommands;

import java.util.ArrayList;

/**
 * This class contains useful methods for internal commands, apart from file-specific use cases
 * (e.g. null-checks)
 * @author theophil
 *
 */

public class LIB_Utils {
	/**
	 * Checks these three conditions on the specified ArrayList:
	 * 1. Is the list null?
	 * 2. Is the list empty?
	 * 3. Does the list contain null elements?
	 * @return Whether the conditions are met or not.
	 */
	public static boolean checkValid(ArrayList<String> listIn) {
		if (listIn != null && listIn.size() >= 1) {
			for (String listElem : listIn) {
				if (listElem == null)
					return false;
			}
			return true;
		} else {
			return false;
		}
	}
}
