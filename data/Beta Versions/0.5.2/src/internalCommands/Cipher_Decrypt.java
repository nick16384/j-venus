package internalCommands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import engine.HighLevel;
import main.Lib;

public class Cipher_Decrypt {
	public static String decrypt(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if ((params == null) || (paramsWithValues == null) || (params.size() < 1) || (paramsWithValues.size() != 2)) {
			return "reqParamParseError";
		}
		File ciphertextFile = new File(paramsWithValues.get("inputFile"));
		File keyFile = new File(paramsWithValues.get("keyFile"));
		String ciphertext = "";
		String key = "";
		try { ciphertext = Files.readString(ciphertextFile.toPath()); }
		catch (IOException ioe) { System.err.println("INTERN.CIPHER.DECRYPT: Error reading input file. aborting."); return null; }
		try { key = Files.readString(keyFile.toPath()); }
		catch (IOException ioe) { System.err.println("INTERN.CIPHER.DECRYPT: Error reading key file. aborting."); return null; }
		String plaintext = "";
		if (params.contains("shift2c")) {
			//====================================SHIFT2C PREPARATION====================================
			System.out.println("INTERN.CIPHER.DECRYPT: Decryption with Shift2C started...");
			String shiftlist = "";
			Map<String, String> invertedShiftOrder = new HashMap<>();
			Map<String, String> invertedShiftOrderAfter = new HashMap<>();
			System.out.println("INTERN.CIPHER.DECRYPT: Reading SHIFTLIST.txt to assign shift order");
			try { shiftlist = Files.readString(new File(Lib.getDefaultDir().toString() + "\\Program Sources\\Shift2C\\SHIFTLIST.txt").toPath()); }
			catch (IOException ioe) { System.err.println("INTERN.CIPHER.DECRYPT: Error reading SHIFTLIST.txt, aborting"); return null;}
			shiftlist = shiftlist.trim();
			String[] splittedShiftlist = shiftlist.split("\\<" + "endNorm" + "\\>");
			for (String value : splittedShiftlist[0].trim().split("\n")) { 
				value = value.trim().replaceAll("\r", ""); invertedShiftOrder.put(value.split(":")[1], value.split(":")[0]); }
			for (String value : splittedShiftlist[1].trim().split("\n")) { 
				value = value.trim().replaceAll("\r", ""); invertedShiftOrderAfter.put(value.split(":")[1], value.split(":")[0]); }
			System.out.println("INTERN.CIPHER.DECRYPT: Done. Decrypting...");
			System.out.println("INTERN.CIPHER.DECRYPT: Ciphertext: " + ciphertext);
			System.out.println("INTERN.CIPHER.DECRYPT: Key: " + key);
			//====================================DECRYPTION====================================
			char[] ciphertextCharArray = ciphertext.toCharArray();
			String[] ciphertextChars = new String[ciphertext.length()];
			int index = 0;
			for (char character : ciphertextCharArray) { ciphertextChars[index] = Character.toString(character); index++; }
			//Shifting back ciphertext character by character (reversed)
			index = 0;
			for (String value : ciphertextChars) {
				for (int i = 0; i < Integer.parseInt(Character.toString(key.charAt(plaintext.length()))); i++) {
					if (invertedShiftOrder.get(value) != null) {
						if (invertedShiftOrderAfter.get(value) != null) {
							System.out.println("INTERN.CIPHER.DECRYPT: Decryption, Stage 1, Char " + value + ", Round " + i);
							value = value.replace(value, invertedShiftOrderAfter.get(value.trim())).trim();
						}
						//Reverted shifting with reverted operations
						System.out.println("INTERN.CIPHER.DECRYPT: Decryption, Stage 2, Char " + value + ", Round " + i);
						value = value.replace(value, invertedShiftOrder.get(value)).trim();
					} else {
						System.err.println("INTERN.CIPHER.DECRYPT: Warning: Character '" + value + "' not present in SHIFTLIST.txt");
					}
				}
				plaintext = plaintext.concat(value);
			}
			plaintext = plaintext.replaceAll(" ", "E");
			//Decryption done.
			if ((plaintext.length() == key.length()) && (!plaintext.equalsIgnoreCase(ciphertext))) {
				System.out.println("INTERN.CIPHER.DECRYPT: Decryption successful.");
				System.out.println("INTERN.CIPHER.DECRYPT: Note that the output can be very weird, if you've used the wrong key.");
			} else {
				System.err.println("INTERN.CIPHER.DECRYPT: Decryption failed.");
			}
			//====================================OUT FILES SAVE====================================
			if (plaintext.length() < 100) {
				System.out.println("INTERN.CIPHER.DECRYPT: Output/Plaintext: " + plaintext);
			}
			System.out.println("INTERN.CIPHER.DECRYPT: Saving file to 'decOut.txt'");
			File outFile = new File(Lib.getDefaultDir().toString() + "\\Program Sources\\Shift2C\\decOut.txt");
			try { outFile.createNewFile(); } catch (IOException ioe) { System.err.println("INTERN.CIPHER.DECRYPT: Could not create decryption output file."); }
			try { Files.writeString(outFile.toPath(), ciphertext, StandardOpenOption.APPEND); }
			catch (IOException ioe) { System.err.println("INTERN.CIPHER.DECRYPT: Could not write to decryption output file."); }
			System.out.println("INTERN.CIPHER.DECRYPT:  Done.");
		}
		
		return plaintext;
	}
}
