package internalCommands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import engine.HighLevel;
import main.Lib;

public class System_Exec {
	public static String sysexec(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if (params != null) {
			String cmd = paramsWithValues.get("exec");
			System.out.println(shell_exec(cmd));
			return null;
		} else {
			return "paramMissing";
		}
	}
	
	//Code from https://stackoverflow.com/questions/5711084
	///java-runtime-getruntime-getting-output-from-executing-a-command-line-program
	public static String shell_exec(String cmd) {
		String out = null;
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			BufferedReader readOut = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			//BufferedReader readErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			
			String line;
			while ((line = readOut.readLine()) != null) {
				out += line;
				HighLevel.shell_write(1, "HIDDEN", line + "\n");
			}
		} catch (Exception e) {
			Lib.logWrite("SYSEXEC", 3, "Error in command execution.");
		}
		return out;
	}
	
	
	/*public static String shell_exec(String cmd)
    {
    String o=null;
    try
      {
      Process p=Runtime.getRuntime().exec(cmd);
      BufferedReader b=new BufferedReader(new InputStreamReader(p.getInputStream()));
      String r;
      while((r=b.readLine())!=null)o+=r; Lib.logWrite("", 1, r);
      }catch(Exception e){o="error";}
    return o;
    }*/
}
