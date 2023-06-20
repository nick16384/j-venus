package internalCommands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Cipher_General_HighestOccurrenceChar
{
  public static String highestOccurrenceChar(ArrayList<String> params, Map<String, String> paramsWithValues) {
    char result = ' ';
    int count = 0;
    
    if (params != null) {


      
      char[] chars = ((String)params.get(0)).toCharArray();
      ArrayList<Character> used = new ArrayList<>(); byte b; int i; char[] arrayOfChar1;
      for (i = (arrayOfChar1 = chars).length, b = 0; b < i; ) { char c = arrayOfChar1[b];
        if (!used.contains(Character.valueOf(c)))
          used.add(Character.valueOf(c));  b++; }
      
      for (Iterator<Character> iterator = used.iterator(); iterator.hasNext(); ) { char c = ((Character)iterator.next()).charValue();
        
        int tempcount = 0; byte b1; int j; char[] arrayOfChar;
        for (j = (arrayOfChar = chars).length, b1 = 0; b1 < j; ) { char c1 = arrayOfChar[b1];
          if (c1 == c)
            tempcount++;  b1++; }
         if (tempcount > count) {
          
          result = c;
          count = tempcount;
        }  }
      
      return String.valueOf(result);
    } 
    return "reqParamMissing";
  }
}