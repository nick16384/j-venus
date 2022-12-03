package internalCommands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import engine.HighLevel;
import main.Lib;

public class Cipher_Chksum {
	public static String chksum(ArrayList<String> params, Map<String, String> paramsWithValues) {
		if (params.get(0).equalsIgnoreCase("test-32")) {
			if (!(params.get(1) == null)) {
				//=======================================TEST=======================================
				final String inputStr = params.get(1);
				final int chksumLength = 32;
				String chksumFinal = inputStr;
				HighLevel.shell_write(1, "HIDDEN", "Input to convert:\n" + inputStr + "\n");
				HighLevel.shell_write(2, "HIDDEN", "Processing...\n");
				if (inputStr.length() < chksumLength) {
					int index = 0;
					while (chksumFinal.length() < chksumLength) {
						chksumFinal = chksumFinal.concat(String.valueOf(randomChar(chksumFinal)));
					}
				}
				HighLevel.shell_write(2, "HIDDEN", "\nDone.\n");
				HighLevel.shell_write(1, "HIDDEN", "----------------- CHECKSUM --------------------\n");
				for (int i = 0; i < inputStr.length(); i++) { HighLevel.shell_write(1, "HIDDEN", " "); }
				for (int i = 1; i < chksumFinal.length() - inputStr.length(); i++) { HighLevel.shell_write(1, "HIDDEN", String.valueOf(i)); }
				HighLevel.shell_write(1, "HIDDEN", "\n");
				HighLevel.shell_write(1, "HIDDEN", chksumFinal + "\n");
				HighLevel.shell_write(1, "HIDDEN", "---------------- CHECKSUM END -----------------\n");
				return null;
				
				//=======================================TEST END=======================================
			} else {
				HighLevel.shell_write(3, "CHKSUM", "Please provide a String to apply the checksum algorithm on.");
				HighLevel.shell_write(3, "CHKSUM", "Exiting...");
				return "paramMissing";
			}
		} else if (params.get(0).contains("crc")) {
			HighLevel.shell_write(2, "CHKSUM", "CRC and others still not implemented, please wait for the next update :)");
			return null;
		}
		return "InternalErr";
		
		//TODO add subconsole for commands that run and are interactive
		//TODO add a system so that other commands can pass output to commands
		//TODO e.g. command1 > command2
	}
	private static boolean skipMostOccurrenceCheck = false;
	private static String lastSeed = "";
	private static char randomChar(String seed) { //Returns a specific character with a specific seed
		if (seed.isBlank()) {
			return '#';
		}
		if (seed.equalsIgnoreCase(lastSeed)) {
			skipMostOccurrenceCheck = true;
		}
		seed = lastSeed;
		seed = seed.trim();
		char out = ' ';
		int randNum = 0;
		try {
			randNum = seed.length() * seed.length();
			randNum = randNum + 26;
			randNum = Integer.reverse(randNum);
			while (randNum > seed.length()) {
				randNum = randNum - 4;
			}
			while (randNum < 0) {
				randNum = randNum + 7;
			}
			randNum = Integer.reverse(randNum);
			if (randNum % 2 > 0) { //If number is uneven
				randNum = randNum * randNum + 77;
				randNum = Integer.reverse(randNum);
			} else {
				randNum = (randNum / 2) * 7 + 5;
				randNum = Integer.reverse(randNum);
			}
			while (randNum > seed.length()) {
				randNum = randNum - seed.length() / 5;
			}
			while (randNum < 0) {
				randNum = randNum + seed.length() / 3;
			}
			if (randNum > seed.length()) {
				randNum = seed.length();
				
			} else if (randNum < 0) {
				randNum = randNum * (-1); //Make the positive value of the number
			}
		} catch (Exception e) {
			Lib.logWrite("FASTRAND", 2, "Seed to long");
		}
		
		HighLevel.shell_write(2, "HIDDEN", String.valueOf(randNum) + " ");
		
		
		/*String result = "";
		
		try {
			result =
					main.CommandMain.executeCommand(engine.HighLevel.commandSplitArray("mostOccurChar " + seed));
		} catch (IOException e) {
			Lib.logWrite("CHKSUM", 4, "IOException within CHKSUM. Aborting.");
		}
		
		while (String.valueOf(seed.charAt(randNum)).equalsIgnoreCase(result)
				&& !skipMostOccurrenceCheck) {
			if (!(seed.replace(result, "").equalsIgnoreCase(""))) {
				randomChar(seed.replaceAll(String.valueOf(result), ""));
				//Run method again but without most occurring char
				//If this cycle is repeating too often, set to default char '#'
			} else {
				out = '#';
			} //TODO fix cksum -test-32 irgendwas doesnt work
		}*/
		
		out = seed.charAt(randNum);
		return out;
	}
}
