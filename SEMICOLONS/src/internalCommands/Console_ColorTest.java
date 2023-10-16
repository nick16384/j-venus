package internalCommands;

import java.awt.Color;
import java.util.ArrayList;

import engine.sys;
import jfxcomponents.ANSI;
import libraries.Global;
import shell.Shell;

public class Console_ColorTest {
	public static String colorTest(ArrayList<String> params) {
		if (Global.javafxEnabled) {
			Shell.println(ANSI.cReset , "Testing JavaFX colors...");
			runJFxTest();
		} else {
			runAWTTest();
		}
		
		return null;
	}
	
	private static void runJFxTest() {
		
		Shell.println(ANSI.B_Black , "B_BLACK");
		Shell.println(ANSI.B_Blue , "B_BLUE");
		Shell.println(ANSI.B_Cyan , "B_CYAN");
		Shell.println(ANSI.B_Green , "B_GREEN");
		Shell.println(ANSI.B_Magenta , "B_MAGENTA");
		Shell.println(ANSI.B_Red , "B_RED");
		Shell.println(ANSI.B_White , "B_WHITE");
		Shell.println(ANSI.B_Yellow , "B_YELLOW");
		
		Shell.println(ANSI.D_Black , "D_BLACK");
		Shell.println(ANSI.D_Blue , "D_BLUE");
		Shell.println(ANSI.D_Cyan , "D_CYAN");
		Shell.println(ANSI.D_Green , "D_GREEN");
		Shell.println(ANSI.D_Magenta , "D_MAGENTA");
		Shell.println(ANSI.D_Red , "D_RED");
		Shell.println(ANSI.D_White , "D_WHITE");
		Shell.println(ANSI.D_Yellow , "D_YELLOW");
	}
	
	private static void runAWTTest() {
		
	}
}
