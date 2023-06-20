package components;

import java.io.IOException;

import engine.sys;

/**
 * Protocols are chains of commands intended for different uses and with additional properties.
 * @author nick16384
 *
 */

public class Protocol {
	private String name = "";
	private int severity = 0;
	private Command[] commands;
	//private String returnVal = "";
	public Protocol(String name, int severity, Command[] commands) {
		this.name = name;
		this.severity = severity;
		if (commands != null)
			this.commands = commands;
	}
	
	public void launch() {
		sys.log("PRTCL", 0, "--------------------------------------------------------------------");
		sys.log("PRTCL", 1, "Protocol test launch:");
		sys.log("PRTCL", 0, "Name: " + name);
		sys.log("PRTCL", 1, "Protocol level(" + severity + ") in most cases means:");
		//TODO make enum "commandSets" for different severities and do not launch command if severity does not include cmd
		if (severity < 0) { sys.log("PRTCL", 0, "Protocol severity can change."); }
		else if (severity == 0) { sys.log("PRTCL", 0, "Protocol is executed under normal circumstances."); }
		else if (severity == 1) { sys.log("PRTCL", 0, "Protocol is for informational purpose."); }
		else if (severity == 2) { sys.log("PRTCL", 0, "Protocol may fix slight issues and throw warnings."); }
		else if (severity == 3) { sys.log("PRTCL", 0, "Protocol is exclusively fixing slight to medium errors."); }
		else if (severity == 4) { sys.log("PRTCL", 0, "Protocol is used to fix harder errors (or attempt to)."); }
		else if (severity == 5) { sys.log("PRTCL", 0, "Protocol may only show errors."); }
		else if (severity == 6) { sys.log("PRTCL", 0, "Protocol is allowed to suspend current execution."); }
		else if (severity == 7) {
			sys.log("PRTCL", 0, "Protocol may entirely stop J-Venus execution and/or");
			sys.log("PRTCL", 0, "do actions (e.g. native mode) involving that.");
		} else if (severity == 8) { sys.log("PRTCL", 0, "If JVM is allowed to, Protocol may change system files."); }
		else if (severity > 8) { sys.log("PRTCL", 2, "Severity is above specified max. level (8)"); }
		
		//Launch
		//int index = 0;
		sys.log("PRTCL", 0, "Command to execute after each other:");
		for (Command cmd : commands) { sys.log("PRTCL", 0, "> " + cmd.getFullCommand()); }
		sys.log("PRTCL", 0, "Executing...");
		for (Command cmd : commands) {
			try {
				cmd.start();
			} catch (IOException ioe) {
				sys.log("PRTCL", 2, "IOException when executing command: " + cmd);
			}
		}
		sys.log("PRTCL", 0, "--------------------------------------------------------------------");
	}
}
