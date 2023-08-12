package internalCommands;

import components.Shell;
import components.TestException;
import engine.sys;
import java.util.ArrayList;
import java.util.Map;



public class System_Generate_Stacktrace
{
  public static String generateStacktrace(ArrayList<String> params, Map<String, String> paramsWithValues) throws Exception {
    if (params != null && params.size() >= 1) {
      RuntimeException ex = 
        (RuntimeException) Class.forName(params.get(0)).getConstructor(new Class[] { String.class }).newInstance(new Object[] { params.get(0) });
      throw ex;
    } 
    Shell.print(1, "STACKTRC", "Exception name must be fully qualified name, for example: java.lang.NullPointerException instead of NullPointerException.\n", new boolean[0]);
    
    throw new TestException("TestCode", "This is a test exception.");
  }
}