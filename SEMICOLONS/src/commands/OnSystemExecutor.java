package commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import engine.LogLevel;
import engine.sys;
import javafx.scene.paint.Color;
import jfxcomponents.ANSI;
import libraries.Env;
import libraries.Global;
import shell.Shell;
import threads.ShellWriteThread;

/**
 * Creates and manages a process created on the system and its input / output.
 */

public class OnSystemExecutor {
	public static final String EOF = "\u001a";
	
	private Process process;
	private InputStream stdout;
	private OutputStream stdin;
	private BufferedReader stdoutRead;
	// Latch used to start stdout and stderr read threads at the exact same time
	private CountDownLatch stdoutAndErrLatch = new CountDownLatch(1);
	
	private boolean hasExecutedAlready;
	private ArrayList<String> commandLineArgs;
	private String returnValue = null;
	
	public OnSystemExecutor(ArrayList<String> commandLineArgs) {
		this.commandLineArgs = commandLineArgs;
	}
	
	public OnSystemExecutor(Command command) {
		hasExecutedAlready = false;
		commandLineArgs = new ArrayList<>();
		commandLineArgs.add(command.getCommand());
		commandLineArgs.addAll(command.getParams());
	}
	
	/**
	 * Creates and starts an external process.
	 * This method can only be called once.
	 * @return The return value (null if success or some error code in libraries.ErrCodes)
	 */
	public String execute() {
		if (hasExecutedAlready)
			return "HasExecutedAlready";
		hasExecutedAlready = true;
		
		ProcessBuilder processBuilder = new ProcessBuilder(commandLineArgs);
		// Needs to be redirected, because it's the only way to preserve stream order.
		processBuilder.redirectErrorStream(true);

		try {
			sys.log("SYSEXEC", LogLevel.INFO, "Starting command as external process.");
			processBuilder.directory(new File(Global.getCurrentDir()));
			process = processBuilder.start();
		} catch (IOException ioe) {
			sys.log("SYSEXEC", LogLevel.ERR, "Unable to start process: IOException");
			ioe.printStackTrace();
			return "CmdNotFound_External";
		}

		// PROCESS INIT ======================================================
		stdout = process.getInputStream();
		stdin = process.getOutputStream();
		stdoutRead = new BufferedReader(new InputStreamReader(stdout));
		
		// PROCESS LOOPS ====================================================
		// Thread for reading stdout and writing contents to shell
		Thread readStdoutThread = new Thread(() -> {
			// FIXME Fix output lag (printing a bunch at once instead of progressively)
			try { stdoutAndErrLatch.await(); } catch (InterruptedException ie) { ie.printStackTrace(); }
			readFromStreamAndPrint(stdoutRead, "stdout", ANSI.B_White);
			ShellWriteThread.getUserInputStream().write(EOF);
			sys.log("SYSEXEC:STDOUT:READ", LogLevel.STATUS, "Standard out read Thread has terminated.");
		});
		
		Thread writeStdinThread = new Thread(() -> {
			// Redirect shellStream to stdin
			try { stdoutAndErrLatch.await(); } catch (InterruptedException ie) { ie.printStackTrace(); }
			
			System.err.println("Clearing previous user input...");
			ShellWriteThread.getUserInputStream().clearStreamsData(); // Clear previous user input
			System.err.println("Clearing previous user input done.");
			System.err.println("Copying to stdin...");
			boolean fullTransferSuccess =
					ShellWriteThread.getUserInputStream()
					.blockingCopyTo(stdin);
			System.err.println("Copying to stdin done.");
			returnValue = fullTransferSuccess ? returnValue : "RuntimeErr";
			
			sys.log("SYSEXEC:STDIN:WRITE", LogLevel.STATUS, "stdin write thread has terminated.");
		});
		
		readStdoutThread.start();
		writeStdinThread.start();
		
		try {
			stdoutAndErrLatch.countDown();
			readStdoutThread.join();
			writeStdinThread.join();
		} catch (InterruptedException ie) {
			sys.log("SYSEXEC", LogLevel.NONCRIT, "Waiting for process to finish was interrupted.");
			sys.log("SYSEXEC", LogLevel.NONCRIT, "Stopping early (some output may not be shown).");
			returnValue = "PrematureTermination";
		}
		cleanup();
		
		int processValue = process.exitValue();
		if (processValue == 1)
			returnValue = "ProcessGeneralError";
		else if (processValue > 1)
			returnValue = "ProcessUnspecifiedError";
		
		return returnValue;
	}
	
	/**
	 * Forces the currently running external process to kill itself
	 */
	public void killProcessIfRunning() {
		if (process == null || !process.isAlive())
			return;
		
		// Destroy process children and then the process itself.
		process.descendants().forEach( p -> { p.destroy(); } );
		process.destroy();
		
		sys.log("SYSEXEC:KILL", LogLevel.DEBUG, "Waiting 30 seconds for process to stop...");
		try {
			process.waitFor(30, TimeUnit.SECONDS);
		} catch (InterruptedException ie) {
			sys.log("SYSEXEC", LogLevel.NONCRIT, "Waiting for process termination has been interrupted.");
			sys.log("SYSEXEC", LogLevel.NONCRIT, "Killing process (SIGKILL).");
			ie.printStackTrace();
		}
		if (process != null && process.isAlive())
			process.destroyForcibly();
		sys.log("SYSEXEC:KILL", LogLevel.DEBUG, "Process stopped.");
	}
	
	private void readFromStreamAndPrint(BufferedReader reader, String identifier, Color ansiColor) {
		try {
			while (process.isAlive() || reader.ready()) {
				String newLine = reader.readLine();
				
				// Stream readLine() returns null if the stream has ended.
				if (newLine == null)
					break;
				
				sys.log("SYSEXEC:" + identifier.toUpperCase(), LogLevel.DEBUG, newLine);
				Shell.println(ansiColor, newLine);
			}
			Shell.println(ANSI.B_Yellow, "---EOF:" + identifier.toUpperCase() + "---");
		} catch (IOException ioe) {
			sys.log("SYSEXEC", LogLevel.ERR, "Reading " + identifier + " failed: IOException");
			ioe.printStackTrace();
			returnValue = "RuntimeErr";
		}
	}
	
	/**
	 * Closes all streams / readers and reintialized stdoutAndErrLatch
	 */
	private void cleanup() {
		stdoutAndErrLatch = new CountDownLatch(1);
		sys.log("SYSEXEC", LogLevel.DEBUG, "Closing process streams...");
		try { stdout.close(); }
		catch (IOException ioe) { sys.log("SYSEXEC", LogLevel.ERR, "Fail on stdout."); }
		try { stdoutRead.close(); }
		catch (IOException ioe) { sys.log("SYSEXEC", LogLevel.ERR, "Fail on stdoutRead."); }
		try { stdin.close(); }
		catch (IOException ioe) { sys.log("SYSEXEC", LogLevel.ERR, "Fail on stdin."); }
		sys.log("SYSEXEC", LogLevel.DEBUG, "Closing process streams done.");
	}
}
