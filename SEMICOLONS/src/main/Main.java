package main;

import java.lang.Exception;

import javax.swing.JFrame;

import java.util.Arrays;
import java.util.LinkedList;

import engine.sys;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import jfx.windowManager.JFxWinloader;
import jfx.windowManager.PartiallyEditableInlineCSSTextArea;
import libraries.OpenLib;
import libraries.VarLib;
import threads.ThreadAllocator;

public class Main extends JFrame {
	public static String[] argsMain;
	public static boolean fullscreen = false;
	public static boolean singleThreaded = false;
	public static boolean javafxEnabled = false;
	
	public static PartiallyEditableInlineCSSTextArea cmdLine;
	public static Font shellFont;
	public static final Color DEFAULT_SHELL_COLOR = Color.LIME;
	
	public static ThreadAllocator ThreadAllocMain;
	public static JFxWinloader jfxWinloader;
	
	public static void main(String[] args) { 
		//==================================== INIT ====================================
		argsMain = args;
		
		System.out.println("[NoLog] Verifying installation files...");
		if (Arrays.asList(args).contains("--no-check-install")) {
			System.out.println("[NoLog] Verification skipped.");
		} else {
			if (engine.CheckInstall.fileCheck() == null) {
				System.out.println("[NoLog] Verification successful.");
			} else {
				System.out.println("[NoLog] Verification not successful.");
				System.out.println("[NoLog] The following file or folder does not exist or has incorrect permission settings:");
				System.out.println("[NoLog] " + engine.CheckInstall.fileCheck());
				System.exit(1);
			}
		}
		
		engine.Init.init(args);
		
		if (Arrays.asList(args).contains("--javafx") || Arrays.asList(args).contains("--jfx")) {
			sys.log("MAIN", 1, "Using experimental JavaFX window loader.");
			try {
				jfxWinloader = new JFxWinloader();
				//TODO load GUI in thread
				javafxEnabled = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			sys.log("MAIN", 1, "Starting with default AWT window loader.");
			awt.windowManager.AWTWinload.awtWinload();
		}
		
		if (ThreadAllocMain.getJFXT().isGUIActive())
			jfxWinloader.clearCmdLine();
		
		sys.setActivePhase("run");
		try { new components.Command("clear --noPrompt").start(); } catch (Exception ex) { ex.printStackTrace(); }
		try { Thread.sleep(200); } catch (InterruptedException ie) { ie.printStackTrace(); }
		sys.setActivePhase("init");
		OpenLib.cmdLinePrepare();
		sys.setActivePhase("run");
		
		//==================================== INIT END ====================================
	}
	public static LinkedList<String> commandHistory = new LinkedList<>();
	public static int tabCountInRow = 0;
	
	public static awt.windowManager.WindowMain mainFrameAWT;
	
	//========================================MAIN===========================================
	public static final void initAWTWindow() {
		sys.log("MAIN", 1, "Creating new WindowMain object.");
		mainFrameAWT = new awt.windowManager.WindowMain("J-Vexus " + VarLib.getVersion());
		sys.log("MAIN", 1, "Attaching KeyListener to mainFrame.");
		awt.windowManager.KeyListenerAttacher.attachKeyListener(mainFrameAWT);
	}
	
	public JFrame getMainWindow() {
		return Main.mainFrameAWT;
	}
	
	//Getting some stats about Venus
	/**
	 * Current Venus runtime in milliseconds since program start (idle watchdog).
	 * @return
	 */
	public static long getRuntime() {
		return System.currentTimeMillis() - ThreadAllocMain.getWDT().getTimeStart();
	}
}