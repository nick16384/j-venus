package internalCommands;

import engine.sys;
import internalCommands.System_Exec;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import libraries.OpenLib;
import modules.ShellWriteThread;



public class System_Exec
{
  public static String sysexec(components.Command cmd) {
    if (cmd.getParams() == null) {
    	return "paramMissing";
    }
    
    String command = cmd.getFullCommand().replaceFirst(cmd.getCommand() + " ", "");
    System.out.println(shell_exec(command));
    return null;
  }




  
  private static String shell_exec(String cmd) {
	  String out = "";
	  
	  main.CommandMain.execProcBuild = new ProcessBuilder(cmd.split(" "));
	  main.CommandMain.execProcBuild.redirectErrorStream(true); //Merge error stream with output stream
	  try {
		  main.CommandMain.execProc = main.CommandMain.execProcBuild.start();
	  } catch (IOException ioe) {
		  sys.log("SYSEXEC", 3, "Failed to start new process.");
		  ioe.printStackTrace();
	  }
	  InputStream stdout = main.CommandMain.execProc.getInputStream();
	  OutputStream stdin = main.CommandMain.execProc.getOutputStream();
	  try {
		stdin.write(ShellWriteThread.shellStream.read());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  BufferedReader readerStdOut = new BufferedReader(new InputStreamReader(stdout));
	  //BufferedWriter writerStdIn = new BufferedWriter(new OutputStreamWriter(stdin));
	  try { ShellWriteThread.shellStream.transferTo(stdin); }
	  catch (IOException ioe) { ioe.printStackTrace(); }
	  Scanner scan = new Scanner(stdout);
	  scan.useDelimiter("\n");
	  String current = "";
	  
	  while (scan.hasNext()) {
		  current = scan.next();
		  sys.log("SYSEXEC:OUTPUT", 0, current);
		  sys.shellPrintln(current);
		  out += current;
		  try { ShellWriteThread.shellStream.transferTo(stdin); }
		  catch (IOException ioe) { ioe.printStackTrace(); }
	  }
	  sys.log("SYSEXEC", 1, "Closing streams originating from main.CmdMain.execProc ...");
	  try { scan.close(); } catch (IllegalStateException ise) { sys.log("SYSEXEC", 2, "Scanner already closed."); }
	  try { stdout.close(); } catch (IOException ioe) { sys.log("SYSEXEC", 2, "stdout already closed."); }
	  try { stdin.close(); } catch (IOException ioe) { sys.log("SYSEXEC", 2, "stdin already closed."); }
	  try { readerStdOut.close(); }
	  catch (IOException ioe) { sys.log("SYSEXEC", 2, "Reader for stdout already closed."); }
	  /*try { writerStdIn.close(); }
	  catch (IOException ioe) { OpenLib.logWrite("SYSEXEC", 2, "Writer for stdin already closed."); }*/
	  
	  return out;
    /*String out = null;
    try {
      Process proc = Runtime.getRuntime().exec(cmd);
      BufferedReader readOut = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      OutputStream writeIn = proc.getOutputStream();
      
      
      String line;
      
      while ((line = readOut.readLine()) != null) {
        out = String.valueOf(out) + line;
        sys.shellPrint(1, "HIDDEN", String.valueOf(line) + "\n");
      } 
    } catch (Exception e) {
      OpenLib.logWrite("SYSEXEC", 3, "Error in command execution.");
    } 
    return out;
  }*/
  }
}