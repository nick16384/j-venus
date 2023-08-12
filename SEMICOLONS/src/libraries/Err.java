package libraries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import awtcomponents.AWTANSI;
import components.Shell;
import engine.sys;

public class Err {
	public static void shellPrintErr(Exception ex, String errName, String errType) {
		if (ex == null) {
			sys.log("ERRMSG", 2, "Exception is null, so no error message displayed.");
			Shell.println("Warning: Error message suppressed. See more information in log at \"ERRMSG\".");
			return;
		}
		String dumpStr = "";
		
		if (errName == null) {
			sys.log("ERR", 4, "ERROR (DEF)");
		} else {
			sys.log("ERR", 4, errName);
		}
		Shell.print(4, "ERR", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
				+ ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
		dumpStr += "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
				+ ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
		if (errType == null) {
			Shell.print(4, "ERR", "Runtime error (def):\n");
			dumpStr += "Runtime error (default):\n";
		} else {
			Shell.print(4, "ERR", errType + "\n");
			dumpStr += errType + "\n";
		}
		Shell.print(4, "ERR", ex.getClass() + ": ");
		dumpStr += ex.getClass() + ": ";
		if (!ex.getMessage().equalsIgnoreCase(ex.getLocalizedMessage())) {
			Shell.print(4, "ERR", ex.getMessage() + "\n");
			Shell.print(4, "ERR", "\t" + ex.getLocalizedMessage() + "\n");
		} else {
			Shell.print(4, "ERR", ex.getMessage() + "\n");
		}
		
		dumpStr += ex.getMessage() + " [Message]" + "\n\t"
				+ ex.getLocalizedMessage() + " [Localized Message]\n";
		Shell.print(4, "ERR", "Cause: " + ex.getCause() + "\n");
		dumpStr += "Cause: " + ex.getCause() + "\n";
		ex.printStackTrace();
		Shell.print(4, "ERR", "Full Stacktrace:\n");
		dumpStr += "Full Stacktrace:\n";
		
		for (StackTraceElement traceElement : ex.getStackTrace()) {
			Shell.print(4, "ERR", "-> ");
			Shell.print(4, "ERR", traceElement.getClassName()
					+ "." + traceElement.getMethodName() + "();\n");
			Shell.print(4, "ERR", "\t" + traceElement.getFileName()
					+ ", Line " + traceElement.getLineNumber() + "\n");
			dumpStr += "-> ";
			dumpStr += traceElement.getClassName() + "." + traceElement.getMethodName() + "()\n";
			dumpStr += "\t" + traceElement.getFileName() + ", at Line " + traceElement.getLineNumber() + "\n";
		}
		
		Path dumpFile = Paths.get(Global.getDataDir() + Global.fsep + "dumps-ex"
				+ Global.fsep + "dump_" + Global.getDateTime(true) + ".txt");
		try {
			Files.createFile(dumpFile);
			Files.writeString(dumpFile, dumpStr, StandardOpenOption.WRITE);
		} catch (IOException ioe) {
			sys.log("ERR", 3, "Could not create or write to dump file at '" + dumpFile.toFile().getAbsolutePath() + "'");
			ioe.printStackTrace();
			Shell.println(AWTANSI.D_Yellow,
					"Could not create or write to dump file at '" + dumpFile.toFile().getAbsolutePath() + "'");
			return;
		}
		sys.log("Stacktrace saved at " + dumpFile.toFile().getAbsolutePath());
		Shell.println(AWTANSI.D_Green, "Stacktrace saved at " + dumpFile.toFile().getAbsolutePath());
	}
}
