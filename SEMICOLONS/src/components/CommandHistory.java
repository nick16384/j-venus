package components;

import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Collectors;

import engine.LogLevel;
import engine.sys;
import filesystem.InternalFiles;
import libraries.Global;

public class CommandHistory {
	int commandRepeatInRow;
	int commandHistoryIndex;
	LinkedList<String> commandHistory;
	LinkedList<String> newHistoryElements;
	
	public CommandHistory() {
		commandRepeatInRow = 0;
		commandHistoryIndex = 0;
		newHistoryElements = new LinkedList<>();
		commandHistory = new LinkedList<>();
		
		commandHistory.addAll(Arrays.asList(fetchHistoryFile().split("\n")));
	}
	
	private String fetchHistoryFile() {
		String commandHistoryStr =
				InternalFiles.getCmdHistory().readContents();
		String commandHistoryBackupStr =
				InternalFiles.getCmdHistoryBackup().readContents();
		
		// Replace null with empty string
		commandHistoryStr = commandHistoryStr == null ? "" : commandHistoryStr.trim();
		commandHistoryBackupStr =
				commandHistoryBackupStr == null ? "" : commandHistoryStr.trim();
		
		if (!commandHistoryStr.equals(commandHistoryBackupStr))
			sys.log("CMDHIST", LogLevel.CRIT, "Command history and Command history backup file contents mismatch!");
		
		return commandHistoryStr;
	}
	
	public void writeToFile() {
		String newHistoryElementsStr =
				newHistoryElements.stream().parallel()
				.collect(Collectors.joining("\n", "\n", ""));
		String originalHistoryFileContents =
				fetchHistoryFile();
		int maxHistoryElements = Global.DEFAULT_MAX_HISTORY_SIZE;
		try {
			maxHistoryElements =
					Integer.parseInt(InternalFiles.getCmdHistoryMaxLength().readContents());
		} catch (Exception ex) {
			sys.log("CMDHIST", LogLevel.WARN, "Cannot read max history size, using default value "
					+ Global.DEFAULT_MAX_HISTORY_SIZE + ".");
		}
		String newFullHistory = newHistoryElementsStr + "\n" + originalHistoryFileContents;
		newFullHistory = Arrays.asList(newFullHistory.split("\n"))
				.stream()
				.sequential()
				.limit(maxHistoryElements)
				.collect(Collectors.joining("\n", "\n", ""));
		
		InternalFiles.getCmdHistory().writeString(newFullHistory, StandardOpenOption.DSYNC);
	}
	
	public void copyHistoryToBackup() {
		InternalFiles.getCmdHistoryBackup().writeString(fetchHistoryFile(), StandardOpenOption.DSYNC);
	}
	
	public String getNext() {
		commandHistoryIndex--;
		return get(commandHistoryIndex);
	}
	
	public String getCurrent() {
		return get(commandHistoryIndex);
	}
	
	public String getPrevious() {
		commandHistoryIndex++;
		return get(commandHistoryIndex);
	}
	
	public void commandRepeatRequested(boolean forwardRepeat) {
		// Forward and Backward repeat:
		// Forward repeat means the up key was pressed, Backward means the down key has been pressed.
		commandRepeatInRow++;
		commandHistoryIndex -= forwardRepeat ? 1 : -1;
	}
	
	public void resetRowStats() {
		commandRepeatInRow = 0;
		commandHistoryIndex = 0;
	}
	
	public int getRepeatInRow() {
		return commandRepeatInRow;
	}
	
	public String get(int index) {
		String elementAtIndex;
		try {
			elementAtIndex = commandHistory.get(index);
		} catch (IndexOutOfBoundsException ioobe) {
			elementAtIndex = null;
		}
		return elementAtIndex;
	}
	
	// FIXME Fix commandHistory order (latest element first)
	public void add(String fullCommand) {
		if (fullCommand == null || fullCommand.isBlank())
			return;
		if (!commandHistory.isEmpty()
				&& commandHistory.get(commandHistory.size() - 1).trim().equals(fullCommand))
			return;
		
		// Command is neither null, empty nor last element of command history.
		commandHistory.add(0, fullCommand);
		newHistoryElements.add(0, fullCommand);
	}
}
