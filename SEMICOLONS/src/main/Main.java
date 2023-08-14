package main;

import java.lang.Exception;

import javax.swing.JFrame;

import java.util.Arrays;
import java.util.LinkedList;

import engine.InfoType;
import engine.sys;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import jfxcomponents.JFxWinloader;
import jfxcomponents.PartiallyEditableInlineCSSTextArea;
import libraries.VariableInitializion;
import shell.Shell;
import libraries.Global;
import threads.ThreadAllocation;

public class Main extends JFrame {
	public static String[] argsMain;
	
	public static PartiallyEditableInlineCSSTextArea cmdLine;
	public static Font shellFont;
	public static final Color DEFAULT_SHELL_COLOR = Color.LIME;
	
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
		
		if (Arrays.asList(args).contains("--awt")) {
			Global.javafxEnabled = false;
			sys.log("MAIN", InfoType.INFO, "Using deprecated AWT window loader.");
			awtcomponents.AWTWinload.awtWinload();
		} else {
			sys.log("MAIN", InfoType.INFO, "Using default JavaFX window loader.");
			try {
				jfxWinloader = new JFxWinloader();
				//TODO load GUI in thread
				Global.javafxEnabled = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try { Thread.sleep(1000); } catch (InterruptedException ie) { ie.printStackTrace(); }
		Shell.showPrompt();
		Global.setNextRunphase(); // RUN
		
		//==================================== INIT END ====================================
	}
	public static LinkedList<String> commandHistory = new LinkedList<>();
	public static int tabCountInRow = 0;
	
	public static awtcomponents.WindowMain mainFrameAWT;
	
	//========================================MAIN===========================================
	public static final void initAWTWindow() {
		sys.log("MAIN", InfoType.DEBUG, "Creating new WindowMain object.");
		mainFrameAWT = new awtcomponents.WindowMain("J-Vexus " + Global.getVersion());
		sys.log("MAIN", InfoType.DEBUG, "Attaching KeyListener to mainFrame.");
		awtcomponents.KeyListenerAttacher.attachKeyListener(mainFrameAWT);
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
		return System.currentTimeMillis() - ThreadAllocation.getWDT().getTimeStart();
	}
}