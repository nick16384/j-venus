package internalCommands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import engine.InfoType;
import engine.sys;
import libraries.Global;


public class Cipher_Encrypt {
   public static String encrypt(ArrayList<String> params, Map<String, String> paramsWithValues) {
     if (params == null || paramsWithValues == null || params.size() < 1 || paramsWithValues.size() != 2) {
       sys.log("ENCRYPT", InfoType.ERR, "reqParam Parse error");
       return "reqParamParseError";
     } 
     String ciphertext = "";
    String plaintext = "";
    File plaintextFile = new File(paramsWithValues.get("inputFile")); 
    try { plaintext = Files.readString(plaintextFile.toPath()); }
    catch (IOException ioe) { sys.log("ENCRYPT", InfoType.ERR, "Error reading input file, aborting."); return "FileRWError"; }
    
    if (params.contains("shift2c")) {
      
      sys.log("ENCRYPT", InfoType.STATUS, "Encryption with Shift2C started...");
      sys.log("ENCRYPT", InfoType.DEBUG, "Text to encrypt: " + plaintext);
      String key = "";
      //SecureRandom random = new SecureRandom();
      plaintext = plaintext.replaceAll(" ", "E");
      plaintext = plaintext.toUpperCase();
      
      sys.log("ENCRYPT", InfoType.INFO, "Using given key.");
      key = paramsWithValues.get("key");













      
      String shiftlist = "";
      Map<String, String> shiftOrder = new HashMap<>();
      Map<String, String> shiftOrderAfter = new HashMap<>();
      sys.log("ENCRYPT", InfoType.STATUS, "Reading SHIFTLIST.txt to assign shift order"); 
      try { shiftlist = Files.readString((new File(String.valueOf(Global.getDefaultDir().toString()) + "\\Program Sources\\Shift2C\\SHIFTLIST.txt")).toPath()); }
      catch (IOException ioe) { sys.log("ENCRYPT", InfoType.ERR, "Error reading SHIFTLIST.txt, aborting"); return null; }
       sys.log("ENCRYPT", InfoType.ERR, shiftlist);
      shiftlist = shiftlist.trim();
      sys.log("ENCRYPT", InfoType.ERR, shiftlist);
      String[] splittedShiftlist = shiftlist.split("\\<endNorm\\>"); byte b1; int i; String[] arrayOfString1;
      for (i = (arrayOfString1 = splittedShiftlist[0].trim().split("\n")).length, b1 = 0; b1 < i; ) { String value = arrayOfString1[b1];
        value = value.replaceAll("\r", ""); shiftOrder.put(value.split(":")[0], value.split(":")[1]); b1++; }
       for (i = (arrayOfString1 = splittedShiftlist[1].trim().split("\n")).length, b1 = 0; b1 < i; ) { String value = arrayOfString1[b1];
        value = value.replaceAll("\r", ""); shiftOrderAfter.put(value.split(":")[0], value.split(":")[1]); b1++; }
       sys.log("ENCRYPT", InfoType.STATUS, "Reading shiftlist done.");
      
      sys.log("ENCRYPT", InfoType.DEBUG, "Encrypting...");
      sys.log("ENCRYPT", InfoType.DEBUG, "Prepared text to encrypt " + plaintext);
      
      char[] plaintextCharArray = plaintext.toCharArray();
      String[] plaintextChars = new String[plaintext.length()];
      int index = 0; byte b2; int j; char[] arrayOfChar1;
      for (j = (arrayOfChar1 = plaintextCharArray).length, b2 = 0; b2 < j; ) { char character = arrayOfChar1[b2]; plaintextChars[index] = Character.toString(character); index++; b2++; }
       String[] arrayOfString2;
      for (j = (arrayOfString2 = plaintextChars).length, b2 = 0; b2 < j; ) { String value = arrayOfString2[b2];
        for (int k = 0; k < Integer.parseInt(Character.toString(key.charAt(ciphertext.length()))); k++) {
          if (shiftOrder.get(value) != null) {
            sys.log("ENCRYPT", InfoType.DEBUG, "Encryption, Stage 1, Char " + value + ", Round " + k);
            value = value.replace(value, shiftOrder.get(value));
            value = value.trim();
            if (shiftOrderAfter.get(value) != null) {
              sys.log("ENCRYPT", InfoType.DEBUG, "Encryption, Stage 2, Char " + value + ", Round " + k);
              value = value.replace(value, shiftOrderAfter.get(value));
              value = value.trim();
            } 
          } else {
            
            sys.log("ENCRYPT", InfoType.WARN, "Warning: Character: " + value + " not present in SHIFTLIST.txt");
          } 
        } 
        ciphertext = ciphertext.concat(value); b2++; }
      
      if (ciphertext.length() == key.length() && !ciphertext.equalsIgnoreCase(plaintext)) {
        sys.log("ENCRYPT", InfoType.INFO, "Encryption successful.");
      } else {
        sys.log("ENCRYPT", InfoType.ERR, "Encryption failed.");
      } 
      
      if (ciphertext.length() < 100) {
        sys.log("ENCRYPT", InfoType.INFO, "Output/Ciphertext: " + ciphertext);
        sys.log("ENCRYPT", InfoType.INFO, "Key: " + key);
      } 
      sys.log("ENCRYPT", InfoType.INFO, "Saving files to 'key.txt' and 'out.txt'");
      File outFile = new File(String.valueOf(Global.getDefaultDir().toString()) + "\\Program Sources\\Shift2C\\out.txt");
      File keyFile = new File(String.valueOf(Global.getDefaultDir().toString()) + "\\Program Sources\\Shift2C\\key.txt"); 
      try { outFile.createNewFile(); } catch (IOException ioe) { sys.log("ENCRYPT", InfoType.ERR, "Could not create output file."); }
       try { keyFile.createNewFile(); } catch (IOException ioe) { sys.log("ENCRYPT", InfoType.ERR, "Could not create key file."); }
       try { Files.writeString(outFile.toPath(), ciphertext, new OpenOption[] { StandardOpenOption.APPEND }); }
      catch (IOException ioe) { sys.log("ENCRYPT", InfoType.ERR, "Could not write to output file."); }
       try { Files.writeString(keyFile.toPath(), key, new OpenOption[] { StandardOpenOption.APPEND }); }
      catch (IOException ioe) { sys.log("ENCRYPT", InfoType.ERR, "Could not write to key file."); }
       sys.log("ENCRYPT", InfoType.DEBUG, "Done.");
    } 
    
    return ciphertext;
  }
}