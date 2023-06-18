package internalCommands;

import java.awt.Color;
import java.util.ArrayList;

import engine.JFXANSI;
import engine.sys;
import main.Main;

public class Console_ColorTest {
	public static String colorTest(ArrayList<String> params) {
		if (Main.javafxEnabled) {
			sys.shellPrintln(Color.decode(JFXANSI.cReset.toString()) , "Testing JavaFX colors...");
			runJFxTest();
		} else {
			runAWTTest();
		}
		
		return null;
	}
	
	private static void runJFxTest() {
		
		sys.shellPrintln(Color.decode(JFXANSI.B_Black.toString()) , "B_BLACK");
		sys.shellPrintln(Color.decode(JFXANSI.B_Blue.toString()) , "B_BLUE");
		sys.shellPrintln(Color.decode(JFXANSI.B_Cyan.toString()) , "B_CYAN");
		sys.shellPrintln(Color.decode(JFXANSI.B_Green.toString()) , "B_GREEN");
		sys.shellPrintln(Color.decode(JFXANSI.B_Magenta.toString()) , "B_MAGENTA");
		sys.shellPrintln(Color.decode(JFXANSI.B_Red.toString()) , "B_RED");
		sys.shellPrintln(Color.decode(JFXANSI.B_White.toString()) , "B_WHITE");
		sys.shellPrintln(Color.decode(JFXANSI.B_Yellow.toString()) , "B_YELLOW");
		
		sys.shellPrintln(Color.decode(JFXANSI.D_Black.toString()) , "D_BLACK");
		sys.shellPrintln(Color.decode(JFXANSI.D_Blue.toString()) , "D_BLUE");
		sys.shellPrintln(Color.decode(JFXANSI.D_Cyan.toString()) , "D_CYAN");
		sys.shellPrintln(Color.decode(JFXANSI.D_Green.toString()) , "D_GREEN");
		sys.shellPrintln(Color.decode(JFXANSI.D_Magenta.toString()) , "D_MAGENTA");
		sys.shellPrintln(Color.decode(JFXANSI.D_Red.toString()) , "D_RED");
		sys.shellPrintln(Color.decode(JFXANSI.D_White.toString()) , "D_WHITE");
		sys.shellPrintln(Color.decode(JFXANSI.D_Yellow.toString()) , "D_YELLOW");
	}
	
	private static void runAWTTest() {
		
	}
}
