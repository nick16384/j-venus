package filesystem;

public class InternalFiles {
	private static VirtualFile cmdHistory;
	private static VirtualFile cmdHistoryBackup;
	private static VirtualFile cmdHistoryMaxLength;
	private static VirtualFile motd;
	private static VirtualFile motdBackup;
	private static VirtualFile semicolonsIcon;
	private static VirtualFile logFile;
	public static VirtualFile getCmdHistory() {
		return cmdHistory;
	}
	public static void setCmdHistory(VirtualFile cmdHistory) {
		InternalFiles.cmdHistory = cmdHistory;
	}
	public static VirtualFile getCmdHistoryBackup() {
		return cmdHistoryBackup;
	}
	public static void setCmdHistoryBackup(VirtualFile cmdHistoryBackup) {
		InternalFiles.cmdHistoryBackup = cmdHistoryBackup;
	}
	public static VirtualFile getCmdHistoryMaxLength() {
		return cmdHistoryMaxLength;
	}
	public static void setCmdHistoryMaxLength(VirtualFile cmdHistoryMaxLength) {
		InternalFiles.cmdHistoryMaxLength = cmdHistoryMaxLength;
	}
	public static VirtualFile getMotd() {
		return motd;
	}
	public static void setMotd(VirtualFile motd) {
		InternalFiles.motd = motd;
	}
	public static VirtualFile getMotdBackup() {
		return motdBackup;
	}
	public static void setMotdBackup(VirtualFile motdBackup) {
		InternalFiles.motdBackup = motdBackup;
	}
	public static VirtualFile getSemicolonsIcon() {
		return semicolonsIcon;
	}
	public static void setSemicolonsIcon(VirtualFile semicolonsIcon) {
		InternalFiles.semicolonsIcon = semicolonsIcon;
	}
	public static VirtualFile getLogFile() {
		return logFile;
	}
	public static void setLogFile(VirtualFile logFile) {
		InternalFiles.logFile = logFile;
	}
}
