package libraries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import awtcomponents.AWTANSI;
import engine.InfoType;
import engine.sys;
import filesystem.VirtualFile;
import shell.Shell;

public class Err {
	public static void shellPrintErr(Exception ex, String errName, String errType) {
		if (ex == null) {
			sys.log("ERRMSG", InfoType.WARN, "Exception is null, so no error message displayed.");
			Shell.println("Warning: Error message suppressed. See more information in log at \"ERRMSG\".");
			return;
		}
		String stacktrace = "";
		
		if (errName == null) {
			sys.log("ERR", InfoType.CRIT, "ERROR (DEF)");
		} else {
			sys.log("ERR", InfoType.CRIT, errName);
		}
		Shell.print(4, "ERR", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
				+ ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
		stacktrace += "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
				+ ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
		if (errType == null) {
			Shell.print(4, "ERR", "Runtime error (def):\n");
			stacktrace += "Runtime error (default):\n";
		} else {
			Shell.print(4, "ERR", errType + "\n");
			stacktrace += errType + "\n";
		}
		Shell.print(4, "ERR", ex.getClass() + ": ");
		stacktrace += ex.getClass() + ": ";
		if (!ex.getMessage().equalsIgnoreCase(ex.getLocalizedMessage())) {
			Shell.print(4, "ERR", ex.getMessage() + "\n");
			Shell.print(4, "ERR", "\t" + ex.getLocalizedMessage() + "\n");
		} else {
			Shell.print(4, "ERR", ex.getMessage() + "\n");
		}
		
		stacktrace += ex.getMessage() + " [Message]" + "\n\t"
				+ ex.getLocalizedMessage() + " [Localized Message]\n";
		Shell.print(4, "ERR", "Cause: " + ex.getCause() + "\n");
		stacktrace += "Cause: " + ex.getCause() + "\n";
		ex.printStackTrace();
		Shell.print(4, "ERR", "Full Stacktrace:\n");
		stacktrace += "Full Stacktrace:\n";
		
		for (StackTraceElement traceElement : ex.getStackTrace()) {
			Shell.print(4, "ERR", "-> ");
			Shell.print(4, "ERR", traceElement.getClassName()
					+ "." + traceElement.getMethodName() + "();\n");
			Shell.print(4, "ERR", "\t" + traceElement.getFileName()
					+ ", Line " + traceElement.getLineNumber() + "\n");
			stacktrace += "-> ";
			stacktrace += traceElement.getClassName() + "." + traceElement.getMethodName() + "()\n";
			stacktrace += "\t" + traceElement.getFileName() + ", at Line " + traceElement.getLineNumber() + "\n";
		}
		
		VirtualFile stacktraceFile = Global.getDataDir().newVirtualFile(
				"/stacktraces/stacktrace_" + Global.getDateTime(true) + ".txt");
		stacktraceFile.createOnFilesystem();
		stacktraceFile.writeString(stacktrace, StandardOpenOption.WRITE);
		
		sys.log("Stacktrace saved at " + stacktraceFile.getAbsolutePath());
		Shell.println(AWTANSI.D_Green, "Stacktrace saved at " + stacktraceFile.getAbsolutePath());
	}
}
