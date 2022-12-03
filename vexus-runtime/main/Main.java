package main;

import java.lang.Exception;
import java.net.URL;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import engine.ANSI;
import engine.sys;
import libraries.Err;
import libraries.OpenLib;
import libraries.VarLib;
import modules.CommandLoader;

public class Main extends JFrame {
	public static String[] argsMain;
	public static boolean fullscreen = false;
	public static boolean singleThreaded = false;
	public static void main(String[] args) {
		//==================================== INIT ====================================
		argsMain = args;
		engine.Init.init(args);
		sys.log("MAIN", 0, "Loading main window...");
		Main mainFrame = new Main("J-Vexus " + VarLib.getVersion());
		sys.log("MAIN", 1, "Done.");
		sys.log("MAIN", 1, "Setting parameters for mainFrame (icon image, title)...");
		//Set icon image for mainFrame
		try {
			mainFrame.setIconImage(ImageIO.read(
					new File(VarLib.fsep + "etc" + VarLib.fsep +
							"vexus" + VarLib.fsep + "data" + VarLib.fsep + "vexus-icon.png")));
		} catch (IOException e) {
			sys.log("MAIN", 3, "Could not set image icon. See below for details.");
			sys.log("MAIN", 3, "Icon path: " +
					VarLib.fsep + "etc" + VarLib.fsep +
					"vexus" + VarLib.fsep + "data" + VarLib.fsep + "vexus-icon.png");
			e.printStackTrace();
		}
		mainFrame.setName("J-Vexus " + VarLib.getVersion());
		sys.log("MAIN", 0, "Done.");
		//==================================== INIT END ====================================
	}
	public LinkedList<String> commandHistory = new LinkedList<>();
	public int tabCountInRow = 0;
	Font shellFont = new Font("Consolas", Font.BOLD, 16);
	JPanel mainShellPanel = new JPanel();
	public static JTextPane cmdLine = new JTextPane();
	
	//========================================MAIN===========================================
	public Main(String titleName) {
		GraphicsDevice device = null;
		if (Arrays.asList(Main.argsMain).contains("--fullscreen") ||
				Arrays.asList(Main.argsMain).contains("--full-screen")) {
			fullscreen = true;
		} else {
			fullscreen = false;
		}
		
		if (fullscreen) {
			device = GraphicsEnvironment
			        .getLocalGraphicsEnvironment().getScreenDevices()[0];
			this.setLayout(null);
			//this.setSize(900, 550);
			//this.setExtendedState(JFrame.MAXIMIZED_BOTH);
			this.setUndecorated(true);
			//this.setLocation(350, 150);
			this.setVisible(true);
			this.setTitle(titleName); //Constructor name
			device.setFullScreenWindow(this);
		}
		if (main.Main.argsMain.length == 0) {
			this.setLayout(null);
			this.setSize(900, 550);
			this.setLocation(350, 150);
			this.setVisible(true);
		}
		
		
		mainShellPanel.setLayout(null);
		mainShellPanel.setSize(this.getWidth(), this.getHeight());
		mainShellPanel.setLocation(0, 0);
		mainShellPanel.setVisible(true);
		
		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");
		//Set cursor on cmdLine to blankCursor
		//cmdLine.setCursor(blankCursor);
		cmdLine.setLayout(null);
		cmdLine.setSize(mainShellPanel.getWidth(), mainShellPanel.getHeight() - 32);
		cmdLine.setLocation(0, 0);
		cmdLine.setVisible(true);
		cmdLine.setBackground(Color.BLACK);
		cmdLine.setForeground(Color.WHITE);
		cmdLine.setFont(shellFont);
		cmdLine.setCaretColor(Color.CYAN);
		cmdLine.repaint();
		try { cmdLine.setCaretPosition(cmdLine.getText().length()); } //TODO Fix cursor not in right pos (windows)
		catch (IllegalArgumentException iae) { sys.log("MAIN", 2, "Could not set cursor position"); }
		cmdLine.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					tabCountInRow = 0;
					//UPDATE SHELL STREAM ==============================================================================
					modules.ShellWriteThread.updateShellStream();
					//END UPDATE SHELL STREAM ==========================================================================
					if (CommandMain.execProc != null && CommandMain.execProc.isAlive()) {
						return;
					}
					//Splitting cmdLine text into command
					String[] lines = cmdLine.getText().split("\n");
					String lastLine = lines[lines.length - 1];
					//if (lastLine) //check lastline multiple prompts
					String fullCommand = lastLine.replace(VarLib.getPrompt(), "");
					//if (fullCommand.contains(VarLib.getPrompt())) { fullCommand = fullCommand.split("\\$ ")[1]; }
					if (!fullCommand.isBlank()) {
						
						sys.log("MAIN", 0, "Sending '" + fullCommand + "' to Command Parser");
						try {
							new components.Command(fullCommand).start();
							//For returnVal, try:
							//CommandMain.executeCommand(new components.Command(fullCommand));
						} catch (Exception ex) {
							Err.shellPrintErr(ex, "FATAL ERROR", "Non-caught JVM exception in class CmdMain");
						}
						//=========================ADD FULLCMD TO HISTORY===============================
						commandHistory.add(fullCommand);
						try {
							String history = Files.readString(Paths.get(
									VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history"));
							int max_history_size = Integer.parseInt(Files.readString(Paths.get(
									VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history_max_length")).trim());
							//Remove first entry of history until size of entries is below count in cmd_history_max_length
							while (history.split("\n").length > max_history_size) {
								Files.writeString(Paths.get(
										VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history"),
										history.replaceFirst(history.split("\n")[0], ""),
										StandardOpenOption.WRITE);
							}
							Files.writeString(Paths.get(
									VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history"),
									fullCommand + "\n", StandardOpenOption.APPEND);
						} catch (IOException ioe) {
							sys.log("MAIN", 2, "IOException while writing to cmd history.");
						} catch (NumberFormatException nfe) {
							sys.log("MAIN", 2, "Parsing cmd_history_max_length failed. Check file exists" +
									" and contains a number below 2.147.483.647");
						}
						//============================END ADD FULLCMD TO HISTORY==============================
					} else {
						OpenLib.cmdLinePrepare();
					}
				} else if (e.getKeyChar() == KeyEvent.VK_TAB || e.getKeyChar() == KeyEvent.VK_UP) {
					//========================================COMMAND REPEAT============================================
					tabCountInRow++;
					try {
						new modules.ProtectedTextComponent(cmdLine).unprotectAllText();
						//Remove last edited line in cmdLine, reappend without last entered command
						cmdLine.getStyledDocument().remove(cmdLine.getText().lastIndexOf("\n") + 1,
								cmdLine.getText().substring(cmdLine.getText().lastIndexOf("\n")).length());
						new modules.ProtectedTextComponent(cmdLine).protectText(0, cmdLine.getText().length());
					} catch (BadLocationException ble) {
						sys.log("MAIN", 3, "Command repeat error: Could not remove last line.");
					}
					OpenLib.cmdLinePrepare();
					commandHistory.clear();
					try {
						//Add all entries of cmd_history to LinkedList commandHistory
						commandHistory.addAll(Arrays.asList(Files.readString(Paths.get(
								VarLib.getDataDir().getAbsolutePath() + VarLib.fsep + "cmd_history")).split("\n")));
					} catch (IOException ioe) {
						//TODO edit command history and TAB repeating further
						sys.log("MAIN", 3, "CMD History read fail. commandHistory<LinkedList> is empty now.");
					}
					
					if (tabCountInRow > commandHistory.size()) {
						Toolkit.getDefaultToolkit().beep();
						sys.log("MAIN", 1, "Command history end reached");
					} else if (tabCountInRow == 1) {
						//Write out last command without it getting protected (..., true)
						sys.log("REPEAT", 0, "Command repeat: "
								+ commandHistory.get(commandHistory.size() - tabCountInRow));
						sys.shellPrint(1, "HIDDEN", commandHistory.get(commandHistory.size() - tabCountInRow), true);
					} else {
						//TODO Find some sort of replaceLast() \/ -------------------
						/*try {
							cmdLine.getStyledDocument().remove(
									cmdLine.getText().indexOf(commandHistory.get(commandHistory.size() - tabCountInRow + 1)),
									commandHistory.get(commandHistory.size() - tabCountInRow + 1).length());
						} catch (BadLocationException ble) {
							OpenLib.logWrite("MAIN", 3, "Command repeat error: Could not remove old command");
						}*/
						sys.log("REPEAT", 0, "Command repeat(" + tabCountInRow + "): "
								+ commandHistory.get(commandHistory.size() - tabCountInRow));
						sys.shellPrint(1, "HIDDEN", commandHistory.get(commandHistory.size() - tabCountInRow), true);
					}
					//========================================COMMAND REPEAT END============================================
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
		
		this.add(mainShellPanel);
		mainShellPanel.add(cmdLine);
		repaint();
		this.repaint();
		mainShellPanel.repaint();
	}
}