package internalCommands;

import engine.LogLevel;
import engine.sys;
import internalCommands.Cipher_Chksum;
import shell.Shell;

import java.util.ArrayList;
import java.util.Map;



public class Cipher_Chksum
{
  public static String chksum(ArrayList<String> params, Map<String, String> paramsWithValues) {
    if (((String)params.get(0)).equalsIgnoreCase("test-32")) {
      if (params.get(1) != null) {
        
        String inputStr = params.get(1);
        int chksumLength = 32;
        String chksumFinal = inputStr;
        Shell.print(1, "HIDDEN", "Input to convert:\n" + inputStr + "\n", new boolean[0]);
        Shell.print(2, "HIDDEN", "Processing...\n", new boolean[0]);
        if (inputStr.length() < 32) {
          int index = 0;
          while (chksumFinal.length() < 32) {
            chksumFinal = chksumFinal.concat(String.valueOf(randomChar(chksumFinal)));
          }
        } 
        Shell.print(2, "HIDDEN", "\nDone.\n", new boolean[0]);
        Shell.print(1, "HIDDEN", "----------------- CHECKSUM --------------------\n", new boolean[0]); int i;
        for (i = 0; i < inputStr.length(); ) { Shell.print(1, "HIDDEN", " ", new boolean[0]); i++; }
         for (i = 1; i < chksumFinal.length() - inputStr.length(); ) { Shell.print(1, "HIDDEN", String.valueOf(i), new boolean[0]); i++; }
         Shell.print(1, "HIDDEN", "\n", new boolean[0]);
        Shell.print(1, "HIDDEN", String.valueOf(chksumFinal) + "\n", new boolean[0]);
        Shell.print(1, "HIDDEN", "---------------- CHECKSUM END -----------------\n", new boolean[0]);
        return null;
      } 

      
      Shell.print(3, "CHKSUM", "Please provide a String to apply the checksum algorithm on.", new boolean[0]);
      Shell.print(3, "CHKSUM", "Exiting...", new boolean[0]);
      return "paramMissing";
    } 
    if (((String)params.get(0)).contains("crc")) {
      Shell.print(2, "CHKSUM", "CRC and others still not implemented, please wait for the next update :)", new boolean[0]);
      return null;
    } 
    return "InternalErr";
  }


  
  //private static boolean skipMostOccurrenceCheck = false;
  
  private static String lastSeed = "";
  private static char randomChar(String seed) {
    if (seed.isBlank()) {
      return '#';
    }
    /*if (seed.equalsIgnoreCase(lastSeed)) {
      skipMostOccurrenceCheck = true;
    }*/
    seed = lastSeed;
    seed = seed.trim();
    char out = ' ';
    int randNum = 0;
    try {
      randNum = seed.length() * seed.length();
      randNum += 26;
      randNum = Integer.reverse(randNum);
      while (randNum > seed.length()) {
        randNum -= 4;
      }
      while (randNum < 0) {
        randNum += 7;
      }
      randNum = Integer.reverse(randNum);
      if (randNum % 2 > 0) {
        randNum = randNum * randNum + 77;
        randNum = Integer.reverse(randNum);
      } else {
        randNum = randNum * 5;
        randNum = Integer.reverse(randNum);
      } 
      while (randNum > seed.length()) {
        randNum -= seed.length() / 5;
      }
      while (randNum < 0) {
        randNum += seed.length() / 3;
      }
      if (randNum > seed.length()) {
        randNum = seed.length();
      }
      else if (randNum < 0) {
        randNum *= -1;
      } 
    } catch (Exception e) {
      sys.log("FASTRAND", LogLevel.WARN, "Seed to long");
    } 
    
    Shell.print(2, "HIDDEN", String.valueOf(String.valueOf(randNum)) + " ", new boolean[0]);
    out = seed.charAt(randNum);
    return out;
  }
}