package internalCommands;

import engine.InfoType;
import engine.sys;
import internalCommands.System_Exec;
import jfxcomponents.ANSI;
import shell.Shell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import awtcomponents.AWTANSI;

public class System_Exec {
	private static Process process;
	private static InputStream stdout;
	private static OutputStream stdin;
	private static BufferedReader stdoutRead;
	private static BufferedWriter stdinWrite;
	private static volatile boolean forceKill = false;
	
	public static String sysexec(ArrayList<String> params) {
		if (!new ParameterChecker(params).checkValid())
			return "paramMissing";
		Shell.println(ANSI.B_Yellow, "Note, that only process output can be displayed, and no text can be entered.\n");
		Shell.println(ANSI.D_Cyan, "<DISPLAY: PROC: STDOUT + STDERR>\n");
		
		// Note: "params" will be taken as external command to be executed.
		return shell_exec(params);
	}

	private static String shell_exec(ArrayList<String> commandLineArgs) {
		ProcessBuilder processBuilder = new ProcessBuilder(commandLineArgs);
		processBuilder.redirectErrorStream(true);

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
		
		// TODO Add support for stdinWrite (start two separate threads)
		
		// PROCESS LOOPS ====================================================
		try {
			while (process.isAlive() || stdoutRead.ready()) {
				int newCharInt = stdoutRead.read();
				
				System.err.println("Data: " + newCharInt);
				
				// Stream read returns -1 if the stream has ended.
				if (newCharInt == -1)
					break;
				
				stdinWrite.append("yeet\n");
				String newCharStr = Character.toString((char) newCharInt);
				
				sys.log("SYSEXEC:STDOUT", InfoType.DEBUG, newCharStr);
				Shell.print(ANSI.B_White, newCharStr);
				
				if (forceKill) {
					// Kill all process children and then the process itself
					process.descendants().forEach( p -> { p.destroy(); } );
					process.destroy();
					continue;
				}
			}
			//TODO make inputstream work (e.g. make [sudo] password be able to be entered)
			Shell.println(ANSI.B_Yellow, "---EOF---");
		} catch (IOException ioe) {
			sys.log("SYSEXEC", InfoType.ERR, "Reading stdout failed: IOException");
			return "RuntimeErr";
		}
		
		// CLEANING UP ======================================================
		sys.log("SYSEXEC", InfoType.DEBUG, "Closing process streams...");
		try { stdout.close(); } catch (IOException ioe) { sys.log("SYSEXEC", InfoType.ERR, "Fail on stdout."); }
		try { stdoutRead.close(); } catch (IOException ioe) { sys.log("SYSEXEC", InfoType.ERR, "Fail on stdoutRead."); }
		try { stdin.close(); } catch (IOException ioe) { sys.log("SYSEXEC", InfoType.ERR, "Fail on stdin."); }
		try { stdinWrite.close(); } catch (IOException ioe) { sys.log("SYSEXEC", InfoType.ERR, "Fail on stdinWrite."); }
		sys.log("SYSEXEC", InfoType.DEBUG, "Closing process streams done.");
		
		forceKill = false;
		System.err.println("Finished");
		return null;
	}
	
	/**
	 * Forces the currently running external process to kill itself
	 */
	public static void killProcessIfRunning() {
		if (process == null || !process.isAlive())
			return;
		
		forceKill = true;
		sys.log("SYSEXEC:KILL", InfoType.DEBUG, "Waiting 30 seconds for process to stop...");
		try {
			process.waitFor(30, TimeUnit.SECONDS);
		} catch (InterruptedException ie) {
			sys.log("SYSEXEC", InfoType.NONCRIT, "Waiting for process termination has been interrupted.");
			sys.log("SYSEXEC", InfoType.NONCRIT, "Killing process (SIGKILL).");
			ie.printStackTrace();
		}
		if (process != null && process.isAlive())
			process.destroyForcibly();
		sys.log("SYSEXEC:KILL", InfoType.DEBUG, "Process stopped.");
	}
}