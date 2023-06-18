package libraries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import engine.AWTANSI;
import engine.sys;

public class Err {
	public static void shellPrintErr(Exception ex, String errName, String errType) {
		if (ex == null) {
			sys.log("ERRMSG", 2, "Exception is null, so no error message displayed.");
			sys.shellPrintln("Warning: Error message suppressed. See more information in log at \"ERRMSG\".");
			return;
		}
		String dumpStr = "";
		
		if (errName == null) {
			sys.log("ERR", 4, "ERROR (DEF)");
		} else {
			sys.log("ERR", 4, errName);
		}
		sys.shellPrint(4, "ERR", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
				+ ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
		dumpStr += "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
				+ ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
		if (errType == null) {
			sys.shellPrint(4, "ERR", "Runtime error (def):\n");
			dumpStr += "Runtime error (default):\n";
		} else {
			sys.shellPrint(4, "ERR", errType + "\n");
			dumpStr += errType + "\n";
		}
		sys.shellPrint(4, "ERR", ex.getClass() + ": ");
		dumpStr += ex.getClass() + ": ";
		if (!ex.getMessage().equalsIgnoreCase(ex.getLocalizedMessage())) {
			sys.shellPrint(4, "ERR", ex.getMessage() + "\n");
			sys.shellPrint(4, "ERR", "\t" + ex.getLocalizedMessage() + "\n");
		} else {
			sys.shellPrint(4, "ERR", ex.getMessage() + "\n");
		}
		
		dumpStr += ex.getMessage() + " [Message]" + "\n\t"
				+ ex.getLocalizedMessage() + " [Localized Message]\n";
		sys.shellPrint(4, "ERR", "Cause: " + ex.getCause() + "\n");
		dumpStr += "Cause: " + ex.getCause() + "\n";
		ex.printStackTrace();
		sys.shellPrint(4, "ERR", "Full Stacktrace:\n");
		dumpStr += "Full Stacktrace:\n";
		
		for (StackTraceElement traceElement : ex.getStackTrace()) {
			sys.shellPrint(4, "ERR", "-> ");
			sys.shellPrint(4, "ERR", traceElement.getClassName()
					+ "." + traceElement.getMethodName() + "();\n");
			sys.shellPrint(4, "ERR", "\t" + traceElement.getFileName()
					+ ", Line " + traceElement.getLineNumber() + "\n");
			dumpStr += "-> ";
			dumpStr += traceElement.getClassName() + "." + traceElement.getMethodName() + "()\n";
			dumpStr += "\t" + traceElement.getFileName() + ", at Line " + traceElement.getLineNumber() + "\n";
		}
		
		Path dumpFile = Paths.get(VarLib.getDataDir() + VarLib.fsep + "dumps-ex"
				+ VarLib.fsep + "dump_" + VarLib.getDateTime(true) + ".txt");
		try {
			Files.createFile(dumpFile);
			Files.writeString(dumpFile, dumpStr, StandardOpenOption.WRITE);
		} catch (IOException ioe) {
			sys.log("ERR", 3, "Could not create or write to dump file at '" + dumpFile.toFile().getAbsolutePath() + "'");
			ioe.printStackTrace();
			sys.shellPrintln(AWTANSI.D_Yellow,
					"Could not create or write to dump file at '" + dumpFile.toFile().getAbsolutePath() + "'");
			return;
		}
		sys.log("Stacktrace saved at " + dumpFile.toFile().getAbsolutePath());
		sys.shellPrintln(AWTANSI.D_Green, "Stacktrace saved at " + dumpFile.toFile().getAbsolutePath());
	}
}
