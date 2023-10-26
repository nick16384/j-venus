package internalCommands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import engine.LogLevel;
import engine.sys;
import libraries.Global;
 
public class Cipher_Decrypt
 {
   public static String decrypt(ArrayList<String> params, Map<String, String> paramsWithValues) {
     if (params == null || paramsWithValues == null || params.size() < 1 || paramsWithValues.size() != 2) {
       return "reqParamParseError";
     }
     File ciphertextFile = new File(paramsWithValues.get("inputFile"));
     File keyFile = new File(paramsWithValues.get("keyFile"));
     String ciphertext = "";
     String key = ""; 
     try { ciphertext = Files.readString(ciphertextFile.toPath()); }
     catch (IOException ioe) { sys.log("DECRYPT", LogLevel.WARN, "Error reading input file. aborting."); return null; }
      try { key = Files.readString(keyFile.toPath()); }
     catch (IOException ioe) { sys.log("DECRYPT", LogLevel.WARN, "Error reading key file. aborting."); return null; }
      String plaintext = "";
     if (params.contains("shift2c")) {
       
       sys.log("DECRYPT", LogLevel.DEBUG, "Decryption with Shift2C started...");
       String shiftlist = "";
       Map<String, String> invertedShiftOrder = new HashMap<>();
       Map<String, String> invertedShiftOrderAfter = new HashMap<>();
       sys.log("DECRYPT", LogLevel.STATUS, "Reading SHIFTLIST.txt to assign shift order"); 
       try { shiftlist = Files.readString((new File(String.valueOf(Global.getRootDir().toString()) + "\\Program Sources\\Shift2C\\SHIFTLIST.txt")).toPath()); }
       catch (IOException ioe) { sys.log("DECRYPT", LogLevel.ERR, "Error reading SHIFTLIST.txt, aborting"); return null; }
        shiftlist = shiftlist.trim();
       String[] splittedShiftlist = shiftlist.split("\\<endNorm\\>"); byte b1; int i; String[] arrayOfString1;
       for (i = (arrayOfString1 = splittedShiftlist[0].trim().split("\n")).length, b1 = 0; b1 < i; ) { String value = arrayOfString1[b1];
         value = value.trim().replaceAll("\r", ""); invertedShiftOrder.put(value.split(":")[1], value.split(":")[0]); b1++; }
        for (i = (arrayOfString1 = splittedShiftlist[1].trim().split("\n")).length, b1 = 0; b1 < i; ) { String value = arrayOfString1[b1];
         value = value.trim().replaceAll("\r", ""); invertedShiftOrderAfter.put(value.split(":")[1], value.split(":")[0]); b1++; }
        sys.log("DECRYPT", LogLevel.DEBUG, "Done. Decrypting...");
       sys.log("DECRYPT", LogLevel.DEBUG, "Ciphertext: " + ciphertext);
       sys.log("DECRYPT", LogLevel.DEBUG, "Key: " + key);
       
       char[] ciphertextCharArray = ciphertext.toCharArray();
       String[] ciphertextChars = new String[ciphertext.length()];
       int index = 0; byte b2; int j; char[] arrayOfChar1;
       for (j = (arrayOfChar1 = ciphertextCharArray).length, b2 = 0; b2 < j; ) { char character = arrayOfChar1[b2]; ciphertextChars[index] = Character.toString(character); index++; b2++; }
       
       index = 0; String[] arrayOfString2;
       for (j = (arrayOfString2 = ciphertextChars).length, b2 = 0; b2 < j; ) { String value = arrayOfString2[b2];
         for (int k = 0; k < Integer.parseInt(Character.toString(key.charAt(plaintext.length()))); k++) {
           if (invertedShiftOrder.get(value) != null) {
             if (invertedShiftOrderAfter.get(value) != null) {
               sys.log("DECRYPT", LogLevel.DEBUG, "Decryption, Stage 1, Char " + value + ", Round " + k);
               value = value.replace(value, invertedShiftOrderAfter.get(value.trim())).trim();
             } 
             
             sys.log("DECRYPT", LogLevel.DEBUG, "Decryption, Stage 2, Char " + value + ", Round " + k);
             value = value.replace(value, invertedShiftOrder.get(value)).trim();
           } else {
             sys.log("DECRYPT", LogLevel.DEBUG, "Warning: Character '" + value + "' not present in SHIFTLIST.txt");
           } 
         } 
         plaintext = plaintext.concat(value); b2++; }
       
       plaintext = plaintext.replaceAll(" ", "E");
       
       if (plaintext.length() == key.length() && !plaintext.equalsIgnoreCase(ciphertext)) {
         sys.log("DECRYPT", LogLevel.INFO, "Decryption successful.");
         sys.log("DECRYPT", LogLevel.INFO, "Note that the output can be very weird, if you've used the wrong key.");
       } else {
         sys.log("DECRYPT", LogLevel.ERR, "Decryption failed.");
       } 
       
       if (plaintext.length() < 100) {
         sys.log("DECRYPT",LogLevel.INFO, "Outputxt: " + plaintext);
       }
       sys.log("DECRYPT", LogLevel.INFO, "Saving file to 'decOut.txt'");
       File outFile = new File(String.valueOf(Global.getRootDir().toString()) + "\\Program Sources\\Shift2C\\decOut.txt"); 
       try { outFile.createNewFile(); } catch (IOException ioe) { sys.log("DECRYPT", LogLevel.ERR, "Could not create decryption output file."); }
        try { Files.writeString(outFile.toPath(), ciphertext, new OpenOption[] { StandardOpenOption.APPEND }); }
       catch (IOException ioe) { sys.log("DECRYPT", LogLevel.ERR, "Could not write to decryption output file."); }
        sys.log("DECRYPT", LogLevel.STATUS, " Done.");
     } 
     
     return plaintext;
   }
 }