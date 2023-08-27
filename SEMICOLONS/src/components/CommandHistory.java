package components;

import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import engine.InfoType;
import engine.sys;
import filesystem.InternalFiles;

public class CommandHistory {
	int commandRepeatInRow;
	int commandHistoryIndex;
	ArrayList<String> commandHistory;
	ArrayList<String> newHistoryElements;
	
	public CommandHistory() {
		commandRepeatInRow = 0;
		commandHistoryIndex = 0;
		newHistoryElements = new ArrayList<>();
		commandHistory = new ArrayList<>();
		
		String commandHistoryStr =
				InternalFiles.getCmdHistory().readContents();
		String commandHistoryBackupStr =
				InternalFiles.getCmdHistoryBackup().readContents();
		
		// Replace null with empty string
		commandHistoryStr = commandHistoryStr == null ? "" : commandHistoryStr.trim();
		commandHistoryBackupStr =
				commandHistoryBackupStr == null ? "" : commandHistoryStr.trim();
		
		if (!commandHistoryStr.equals(commandHistoryBackupStr))
			sys.log("CMDHIST", InfoType.CRIT, "Command history and Command history backup file contents mismatch!");
		
		commandHistory.addAll(Arrays.asList(commandHistoryStr.trim().split("\n")));
	}
	
	public void writeToFile() {
		InternalFiles.getCmdHistory().writeString(
				"\n" + newHistoryElements.stream().parallel()
				.sorted(Collections.reverseOrder())
				.collect(Collectors.joining("\n")),
				StandardOpenOption.APPENDTOBEGINNING);
		
		InternalFiles.getCmdHistoryBackup().writeString(
				"\n" + commandHistory.stream().parallel()
				.sorted(Collections.reverseOrder())
				.collect(Collectors.joining("\n")),
				StandardOpenOption.WRITE);
	}
	
	public String getNext() {
		commandHistoryIndex++;
		return get(commandHistoryIndex);
	}
	
	public String getCurrent() {
		return get(commandHistoryIndex);
	}
	
	public String getPrevious() {
		commandHistoryIndex--;
		return get(commandHistoryIndex);
	}
	
	public void commandRepeatRequested(boolean forwardRepeat) {
		// Forward and Backward repeat:
		// Forward repeat means the up key was pressed, Backward means the down key has been pressed.
		commandRepeatInRow++;
		commandHistoryIndex += forwardRepeat ? 1 : -1;
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
	
	public void add(String fullCommand) {
		if (fullCommand == null || fullCommand.isBlank())
			return;
		if (!commandHistory.isEmpty()
				&& commandHistory.get(commandHistory.size() - 1).trim().equals(fullCommand))
			return;
		
		// Command is neither null, empty nor last element of command history.
		commandHistory.add(fullCommand);
		newHistoryElements.add(fullCommand);
	}
}
