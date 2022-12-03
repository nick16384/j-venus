package internalCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import engine.HighLevel;
import main.Lib;

import java.security.SecureRandom;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class Cipher_Encrypt {
	public static String encrypt(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params == null) || (paramsWithValues == null) || (params.size() < 1) || (paramsWithValues.size() != 2)) {
			Lib.logWrite("ENCRYPT", -1, "reqParam Parse error");
			return "reqParamParseError";
		}
		String ciphertext = "";
		String plaintext = "";
		File plaintextFile = new File(paramsWithValues.get("inputFile"));
		try { plaintext = Files.readString(plaintextFile.toPath()); }
		catch (IOException ioe) { Lib.logWrite("ENCRYPT", -1, "Error reading input file, aborting."); return "FileRWError"; }
		
		if (params.contains("shift2c")) {
			//====================================SHIFT2C PREPARATION====================================
			Lib.logWrite("ENCRYPT", 0, "Encryption with Shift2C started...");
			Lib.logWrite("ENCRYPT", 0, "Text to encrypt: " + plaintext);
			String key = "";
			SecureRandom random = new SecureRandom();
			plaintext = plaintext.replaceAll(" ", "E");
			plaintext = plaintext.toUpperCase();
			//if ((optParams != null) && (optParams[0].length() == plaintext.length())) {
				Lib.logWrite("ENCRYPT", 0, "Using given key.");
				key = paramsWithValues.get("key");
			/*} else {
				Lib.logWrite("ENCRYPT", 0, "Creating key...");
				for (int i = 0; i < plaintext.length(); i++) {
					key = key.concat(Integer.toString(random.nextInt(10)));
				}
				if (key.length() == plaintext.length()) {
					Lib.logWrite("ENCRYPT", 0, "Key created successfully!");
				} else {
					Lib.logWrite("ENCRYPT", -1, "Creating key failed, aborting.");
					return null;
				}
			}*/
			//====================================ENCRYPTION====================================
			//Reading and splitting SHIFTLIST.txt
			String shiftlist = "";
			Map<String, String> shiftOrder = new HashMap<>();
			Map<String, String> shiftOrderAfter = new HashMap<>();
			Lib.logWrite("ENCRYPT", 0, "Reading SHIFTLIST.txt to assign shift order");
			try { shiftlist = Files.readString(new File(Lib.getDefaultDir().toString() + "\\Program Sources\\Shift2C\\SHIFTLIST.txt").toPath()); }
			catch (IOException ioe) { Lib.logWrite("ENCRYPT", -1, "Error reading SHIFTLIST.txt, aborting"); return null;}
			Lib.logWrite("ENCRYPT", -1, shiftlist);
			shiftlist = shiftlist.trim();
			Lib.logWrite("ENCRYPT", -1, shiftlist);
			String[] splittedShiftlist = shiftlist.split("\\<" + "endNorm" + "\\>");
			for (String value : splittedShiftlist[0].trim().split("\n")) {
				value = value.replaceAll("\r", ""); shiftOrder.put(value.split(":")[0], value.split(":")[1]); }
			for (String value : splittedShiftlist[1].trim().split("\n")) {
				value = value.replaceAll("\r", ""); shiftOrderAfter.put(value.split(":")[0], value.split(":")[1]); }
			Lib.logWrite("ENCRYPT", 0, "Reading shiftlist done.");
			//Starting actual encryption
			Lib.logWrite("ENCRYPT", 0, "Encrypting...");
			Lib.logWrite("ENCRYPT", 0, "Prepared text to encrypt " + plaintext);
			//Creating String array consisting out of characters in plaintext
			char[] plaintextCharArray = plaintext.toCharArray();
			String[] plaintextChars = new String[plaintext.length()];
			int index = 0;
			for (char character : plaintextCharArray) { plaintextChars[index] = Character.toString(character); index++; }
			//Shifting(encrypting) plaintext character by character
			for (String value : plaintextChars) {
				for (int i = 0; i < Integer.parseInt(Character.toString(key.charAt(ciphertext.length()))); i++) {
					if (shiftOrder.get(value) != null) {
						Lib.logWrite("ENCRYPT", 0, "Encryption, Stage 1, Char " + value + ", Round " + i);
						value = value.replace(value, shiftOrder.get(value));
						value = value.trim();
						if (shiftOrderAfter.get(value) != null) { 
							Lib.logWrite("ENCRYPT", 0, "Encryption, Stage 2, Char " + value + ", Round " + i);
							value = value.replace(value, shiftOrderAfter.get(value));
							value = value.trim();
							//End of shifting
						}
					} else {
						Lib.logWrite("ENCRYPT", -1, "Warning: Character: " + value + " not present in SHIFTLIST.txt");
					}
				}
				ciphertext = ciphertext.concat(value);
			}
			if ((ciphertext.length() == key.length()) && (!ciphertext.equalsIgnoreCase(plaintext))) {
				Lib.logWrite("ENCRYPT", 0, "Encryption successful.");
			} else {
				Lib.logWrite("ENCRYPT", -1, "Encryption failed.");
			}
			//====================================OUT FILES SAVE====================================
			if (ciphertext.length() < 100) {
				Lib.logWrite("ENCRYPT", 0, "Output/Ciphertext: " + ciphertext);
				Lib.logWrite("ENCRYPT", 0, "Key: " + key);
			}
			Lib.logWrite("ENCRYPT", 0, "Saving files to 'key.txt' and 'out.txt'");
			File outFile = new File(Lib.getDefaultDir().toString() + "\\Program Sources\\Shift2C\\out.txt");
			File keyFile = new File(Lib.getDefaultDir().toString() + "\\Program Sources\\Shift2C\\key.txt");
			try { outFile.createNewFile(); } catch (IOException ioe) { Lib.logWrite("ENCRYPT", -1, "Could not create output file."); }
			try { keyFile.createNewFile(); } catch (IOException ioe) { Lib.logWrite("ENCRYPT", -1, "Could not create key file."); }
			try { Files.writeString(outFile.toPath(), ciphertext, StandardOpenOption.APPEND); }
			catch (IOException ioe) { Lib.logWrite("ENCRYPT", -1, "Could not write to output file."); }
			try { Files.writeString(keyFile.toPath(), key, StandardOpenOption.APPEND); }
			catch (IOException ioe) { Lib.logWrite("ENCRYPT", -1, "Could not write to key file."); }
			Lib.logWrite("ENCRYPT", 0, "Done.");
			
		}
		return ciphertext;
	}
}
