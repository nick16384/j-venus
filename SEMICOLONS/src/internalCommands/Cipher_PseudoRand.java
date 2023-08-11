package internalCommands;

import java.util.ArrayList;

import awtcomponents.AWTANSI;
import engine.sys;
import main.Main;

public class Cipher_PseudoRand {
	//First internal command to not use paramsWithValues any more
	
	/**
	 * Returns a pseudo random number of the current runtime in ms, or from a given seed.
	 * Works by using integer overflows, which causes unpredictability (hopefully).
	 * Warning: This method uses very unsafe ways to generate pseudo-random numbers.
	 * Do not under any circumstances use for cryptographic or other use cases, that
	 * require unpredictable numbers for keeping secrets!
	 * @param params
	 * @return A pseudo-random integer
	 */
	public static String pseudoRand(ArrayList<String> params) {
		int seed = 0;
		
		if (LIB_Utils.checkValid(params)) {
			try {
				seed = Integer.parseInt(params.get(0));
			} catch (IllegalArgumentException iae) {
				sys.shellPrintln("Usage: pseudorand [seed]\n"
						+ "Info: [seed] can only be an integer between -2,147,483,648 and 2,147,483,647");
				return "ParamErr_WrongType";
			}
		} else {
			seed = (int) main.Main.getRuntime(); //May result in overflow which increases unpredictability
		}
		sys.shellPrintln(AWTANSI.B_Yellow, "Warning: This computation is unsafe for cryptographic use!\n");
		sys.shellPrintln(AWTANSI.B_Cyan, "Seed: " + seed);
		sys.shellPrintln("Generating pseudo-random number:");
		
		//======================== RANDOM NUMBER GENERATION ===========================
		try {
			//seed += 10000;
			if (seed == 0)
				seed = seed + 285976538;
			seed = getLastNDigits((int) (Math.sqrt(Math.abs(seed * 473)) * 666999), 5);
			sys.shellPrintln("Seed initialized: " + seed);
			for (int i = 0; i <= 1000; i++) {
				if (i % 20 == 0) //Display "working dot" every twenty operations
					sys.shellPrint(".");
				seed = randomIteration(seed, i);
			}
		} catch (ArithmeticException | IllegalArgumentException e) {
			sys.log("PSEUDORAND", 3, "Arithmetic or Illegal Argument Exception. Aborting calculation.");
			sys.shellPrintln("Arithmetic or Illegal Arument Exception. Aborting calculation.");
		}
		try { Thread.sleep(50); } catch (InterruptedException ie) { ie.printStackTrace(); }
		sys.shellPrintln(""); //Line break
		//At this point, "seed" is the generated random number
		sys.shellPrintln(AWTANSI.B_White, "Result: " + seed);
		
		return null;
	}
	
	/**
	 * Returns the last n digits of the specified number
	 * @param num Input number
	 * @param n Number of digits to return
	 * @return
	 */
	private static int getLastNDigits(int num, int n) {
		String numAsStr = Integer.toString(num);
		while (numAsStr.length() < n) {
			//If number length is smaller than digit number to return, fill empty space with zeroes
			numAsStr = "0" + numAsStr;
		}
		numAsStr = numAsStr.substring(numAsStr.length() - n);
		return Integer.parseInt(numAsStr);
	}
	
	/**
	 * One single iteration of the random number generation.
	 *  Using more iterations will shuffle numbers more randomly.
	 * @param input Input number.
	 * @return
	 */
	private static int randomIteration(int input, int iterationCount) {
		try {
			input += iterationCount;
			input = Math.abs(input * 473);
			input = getLastNDigits(input, 8);
		} catch (ArithmeticException | IllegalArgumentException e) {
			sys.log("PSEUDORAND", 3, "Arithmetic error during calculation. Iteration count: " + iterationCount);
			sys.shellPrintln(AWTANSI.B_Red, "Arithmetic error (probably divizion by zero) during calculation. "
					+ "Discarding changes.\n"
					+ "Iteration number: " + iterationCount);
			//Add some random number to mitigate chances of a recurring error
			return input + 37;
		}
		return input;
	}
}
