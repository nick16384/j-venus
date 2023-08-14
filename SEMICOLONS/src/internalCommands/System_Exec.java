package internalCommands;

import engine.InfoType;
import engine.sys;
import internalCommands.System_Exec;
import shell.Shell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class System_Exec {
	private static Process process;
	private static InputStream stdout;
	private static OutputStream stdin;
	private static BufferedReader stdoutRead;
	private static BufferedWriter stdinWrite;
	private static boolean forceKill = false;
	
	public static String sysexec(ArrayList<String> params) {
		if (!LIB_Utils.checkValid(params))
			return "paramMissing";
		Shell.println("Note, that only process output can be displayed, and no text can be entered.");
		
		String newFullCmd = "";
		for (String prm : params) {
			newFullCmd += prm + " ";
		}
		newFullCmd = newFullCmd.trim();

		return shell_exec(newFullCmd);
	}

	private static String shell_exec(String cmd) {
		ProcessBuilder processBuilder = new ProcessBuilder(cmd.split(" "));
		processBuilder.redirectErrorStream(true);
		
		char[] newData = new char[256]; // Arbitrary default buffer size
		String newDataStr = "";

		try {
			sys.log("SYSEXEC", InfoType.INFO, "Starting command as external process.");
			process = processBuilder.start();
		} catch (IOException ioe) {
			sys.log("SYSEXEC", InfoType.ERR, "Unable to start process: IOException");
			//ioe.printStackTrace();
			return "CmdNotFound";
		}

		// PROCESS INIT ======================================================
		stdout = process.getInputStream();
		stdin = process.getOutputStream();
		stdoutRead = new BufferedReader(new InputStreamReader(stdout));
		stdinWrite = new BufferedWriter(new OutputStreamWriter(stdin));

		// TODO get streams to work and display them (BufferedReaders)

		/*
		 * try { Main.ThreadAllocMain.getSWT().shellStream.transferTo(stdin); } catch
		 * (IOException ioe) { sys.log("SYSEXEC", 3,
		 * "Unable to write into stdin: IOException"); return; }
		 */

		// PROCESS LOOPS ====================================================
		try {
			// Read if process is running, or one time to make sure buffer is empty.
			while (process.isAlive() || newDataStr.isBlank()) {
				if (stdoutRead.ready()) {
					int charCount = stdoutRead.read(newData);
					
					// Only use useful data part of newData (not null bytes)
					newDataStr = String.valueOf(newData, 0, charCount);
					sys.log("SYSEXEC:STDOUT", InfoType.DEBUG, newDataStr);
					Shell.print(newDataStr);
				}
				if (forceKill) { process.descendants().forEach( (p) -> { p.destroy(); } ); process.destroy(); continue; }
				try { Thread.sleep(50); } catch (InterruptedException ie) { ie.printStackTrace(); }
			}
			//TODO make inputstream work (e.g. make [sudo] password be able to be entered)
			Shell.println("---EOF---");
		} catch (IOException ioe) {
			sys.log("SYSEXEC", InfoType.ERR, "Reading stdout failed: IOException");
			return "RuntimeErr";
		}
		//CMGR is hung up
		
		// CLEANING UP ======================================================
		sys.log("SYSEXEC", InfoType.DEBUG, "Closing process streams...");
		try { stdout.close(); } catch (IOException ioe) { sys.log("SYSEXEC", InfoType.ERR, "Fail on stdout."); }
		try { stdoutRead.close(); } catch (IOException ioe) { sys.log("SYSEXEC", InfoType.ERR, "Fail on stdoutRead."); }
		try { stdin.close(); } catch (IOException ioe) { sys.log("SYSEXEC", InfoType.ERR, "Fail on stdin."); }
		try { stdinWrite.close(); } catch (IOException ioe) { sys.log("SYSEXEC", InfoType.ERR, "Fail on stdinWrite."); }
		sys.log("SYSEXEC", InfoType.DEBUG, "Closing process streams done.");
		
		forceKill = false;
		return null;
	}
	
	/**
	 * Forces the currently running process to kill itself
	 */
	public static void forceKill() {
		forceKill = true;
		sys.log("SYSEXEC:KILL", InfoType.DEBUG, "Waiting for process to stop...");
		while (process != null && process.isAlive()) {
			try { Thread.sleep(50); } catch (InterruptedException ie) { ie.printStackTrace(); }
		}
		sys.log("SYSEXEC:KILL", InfoType.DEBUG, "Process stopped.");
	}
}