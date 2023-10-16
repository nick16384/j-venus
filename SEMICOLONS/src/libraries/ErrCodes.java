package libraries;

/**
 * Contains all Command errors with error descriptions
 * @author nick16384
 */
public enum ErrCodes {
	//TODO Add error codes:
	//e.g. NotAFile -> <Error Message>
	
	CmdNotFound ("The specified command was not found."),
	ParamErr_WrongType ("A parameter was specified, but another (data) type was expected."),
	ParseErr_MapFail ("Command parser warn: Mapping of strings to internal variables not successful."),
	ParseErr_PrefetchErr ("Command parser warn: An error occurred while fetching command data (probably envVars)."),
	ParseErr_Null ("Command parser warn: Object provided (Command) contains null."),
	ParseErr_TooManyParams ("Command parser warn: Number of parameters exceeds maximum allowed."),
	ParseErr_TooFewParams ("Command parser warn: Too few parameters specified."),
	ParseErr ("Unspecified parser error."),
	FileErr_NotAFile ("File warn: Expected file, but got directory."),
	FileErr_NotADir ("File warn: Expected directory, but got file."),
	FileErr_NotFound ("File warn: The specified file or directory was not found."),
	FileErr_NoPerm ("File warn: The file exists, but Vexus doesn't have sufficient permissions for the operation."),
	FileErr_SymLink ("File warn: The specified file is a symlink, but the command does not support them."),
	FileErr ("Unspecified file error."),
	TestErr ("Test error, nothing went wrong :)"),
	ThreadErr_WT1_Off ("Internal thread warn: Watchdog 1 inactive. Restart, Expect non-detected internal issues."),
	ThreadErr_WT2_Off ("Internal thread warn: Watchdog 2 inactive. Restart, Expect non-detected internal issues."),
	ThreadErr_CUI_Off ("Internal thread warn: User input checker thread inactive. CTRL + C does not work. Restart."),
	ThreadErr_CM_Off ("Internal thread warn: Command Manager Thread inactive. Restart, commands won't work."),
	ThreadErr_SWT_Off ("Internal thread warn: Shell Write Thread inactive. Restart, Shell output is interrupted. View log."),
	ShellModeNonNormal ("Shell mode is not \"normal\". This could potentially mean, that an internal error occured. Restart."),
	RuntimeErr ("Command runtime warn: Command exited with an exception."),
	PrematureTermination ("Command execution was terminated prematurely, therefore the command could not finish.");
	
	public final String description;
	
	private ErrCodes(String description) {
		this.description = description;
	}
	
	/**
	 * Returns the error description for the specified error code.
	 * @param errName
	 * @return
	 */
	public static final String getErrDesc(String errName) {
		for (ErrCodes ec : values()) {
			if (ec.name().equals(errName)) {
				return ec.description;
			}
		}
		return null;
	}
}
