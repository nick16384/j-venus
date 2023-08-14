package internalCommands;

import java.awt.Color;
import java.util.ArrayList;

import engine.sys;
import jfxcomponents.JFXANSI;
import libraries.Global;
import shell.Shell;

public class Console_ColorTest {
	public static String colorTest(ArrayList<String> params) {
		if (Global.javafxEnabled) {
			Shell.println(Color.decode(JFXANSI.cReset.toString()) , "Testing JavaFX colors...");
			runJFxTest();
		} else {
			runAWTTest();
		}
		
		return null;
	}
	
	private static void runJFxTest() {
		
		Shell.println(Color.decode(JFXANSI.B_Black.toString()) , "B_BLACK");
		Shell.println(Color.decode(JFXANSI.B_Blue.toString()) , "B_BLUE");
		Shell.println(Color.decode(JFXANSI.B_Cyan.toString()) , "B_CYAN");
		Shell.println(Color.decode(JFXANSI.B_Green.toString()) , "B_GREEN");
		Shell.println(Color.decode(JFXANSI.B_Magenta.toString()) , "B_MAGENTA");
		Shell.println(Color.decode(JFXANSI.B_Red.toString()) , "B_RED");
		Shell.println(Color.decode(JFXANSI.B_White.toString()) , "B_WHITE");
		Shell.println(Color.decode(JFXANSI.B_Yellow.toString()) , "B_YELLOW");
		
		Shell.println(Color.decode(JFXANSI.D_Black.toString()) , "D_BLACK");
		Shell.println(Color.decode(JFXANSI.D_Blue.toString()) , "D_BLUE");
		Shell.println(Color.decode(JFXANSI.D_Cyan.toString()) , "D_CYAN");
		Shell.println(Color.decode(JFXANSI.D_Green.toString()) , "D_GREEN");
		Shell.println(Color.decode(JFXANSI.D_Magenta.toString()) , "D_MAGENTA");
		Shell.println(Color.decode(JFXANSI.D_Red.toString()) , "D_RED");
		Shell.println(Color.decode(JFXANSI.D_White.toString()) , "D_WHITE");
		Shell.println(Color.decode(JFXANSI.D_Yellow.toString()) , "D_YELLOW");
	}
	
	private static void runAWTTest() {
		
	}
}
