package main;

import java.lang.Exception;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

import engine.HighLevel;
import main.Lib;
import modules.CommandLoader;

public class Main extends JFrame {
	public static String[] argsMain;
	public static void main(String[] args) {
		modules.WatchdogThread.runThread();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		argsMain = args;
		System.out.println("JavaDOS Version " + Lib.getVersion() + " Warning: Beta Version");
		System.out.println("Copyright(C) 2021 - 2022 The JavaDOS Project. All rights reserved.");
		System.out.println("GNU General Public License: Oracle Corporation");
		System.out.println("Warning: Log is currently very verbose due to debugging reasons.");
		System.out.println("Will be reduced within alpha versions.");
		System.out.println("");
		System.out.println("Loading internal variables...");
		Lib.initVars();
		Lib.logWrite("MAIN", 0, "Done.");
		Lib.logWrite("MAIN", 1, "Warning: Log is currently very verbose due to debugging reasons.");
		Lib.logWrite("MAIN", 1, "Will be reduced within alpha versions.");
		Lib.logWrite("MAIN", 0, "Loading main window...");
		HighLevel.setActiveJDOSPhase("init");
		Main mainFrame = new Main();
		ImageIcon img = new ImageIcon(Lib.fsep + "home" + Lib.fsep + "theophil" + Lib.fsep + "Desktop"
				+ Lib.fsep + "JavaDOS" + Lib.fsep + "data" + Lib.fsep + "JDOS-Icon.png");
		mainFrame.setIconImage(img.getImage());
		mainFrame.setName("JavaDOS " + Lib.getVersion());
		Lib.logWrite("MAIN", 0, "Done.");
		Lib.logWrite("MAIN", 0, "Loading external commands...");
		try { Lib.setExtCommands(CommandLoader.loadCommands()); }
		catch (AccessDeniedException ade) { Lib.logWrite("MAIN", 2, "Access to the destination file is denied."); }
		catch (NoSuchFileException nsfe) { Lib.logWrite("MAIN", 2, "External commands not found."); }
		catch (IOException ioe) { Lib.logWrite("MAIN", 2, "Unhandled IOException while loading external commands."); ioe.printStackTrace(); }
		try {
			boolean error = modules.LoadStartup.loadAndExecute();
			if (error) {
				Lib.logWrite("STARTUPSCRIPTRUN", -1, "Error when loading startup script: Internal error");
			}
		} catch (IOException ioe) {
			Lib.logWrite("STARTUPSCRIPTRUN", -1, "Error when loading startup script: IOException");
		}
		Lib.logWrite("MAIN", 1, "Finished startup.");
		HighLevel.setActiveJDOSPhase("run");
		
	}
	public ArrayList<String> commandHistory = new ArrayList<>();
	public int tabCountInRow = 0;
	Font shellFont = new Font("Consolas", Font.BOLD, 16);
	JPanel mainShellPanel = new JPanel();
	public static JTextPane cmdLine = new JTextPane();
	
	public Main() {
		GraphicsDevice device = null;
		for (String arg : main.Main.argsMain) {
			if (arg.equalsIgnoreCase("--full-screen")) {
				device = GraphicsEnvironment
				        .getLocalGraphicsEnvironment().getScreenDevices()[0];
				this.setLayout(null);
				//this.setSize(900, 550);
				//this.setExtendedState(JFrame.MAXIMIZED_BOTH);
				this.setUndecorated(true);
				//this.setLocation(350, 150);
				this.setVisible(true);
				this.setTitle("JavaDOS");
				device.setFullScreenWindow(this);
				break;
			}
		}
		if (main.Main.argsMain.length == 0) {
			this.setLayout(null);
			this.setSize(900, 550);
			this.setLocation(350, 150);
			this.setVisible(true);
		}
		
		
		mainShellPanel.setLayout(null);
		mainShellPanel.setSize(this.getSize());
		mainShellPanel.setLocation(0, 0);
		mainShellPanel.setVisible(true);


		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");
		//Set cursor on cmdLine to blankCursor
		cmdLine.setCursor(blankCursor);
		cmdLine.setLayout(null);
		cmdLine.setSize(mainShellPanel.getSize());
		cmdLine.setLocation(0, 0);
		cmdLine.setVisible(true);
		cmdLine.setBackground(Color.BLACK);
		cmdLine.setForeground(Color.WHITE);
		cmdLine.setFont(shellFont);
		cmdLine.setCaretColor(Color.CYAN);
		cmdLine.repaint();
		Lib.cmdLinePrepare(true);
		try { cmdLine.setCaretPosition(cmdLine.getText().length()); } //TODO Fix cursor not in right pos (windows)
		catch (IllegalArgumentException iae) { Lib.logWrite("MAIN", 2, "Could not set cursor position"); }
		cmdLine.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					tabCountInRow = 0;
					//Splitting cmdLine text into command
					String[] lines = cmdLine.getText().split("\n");
					String lastLine = lines[lines.length - 1];
					//if (lastLine) //check lastline multiple prompts
					String fullCommand = lastLine.replace(Lib.getPrompt(), ""); //TODO Fix time in prompt
					int statuscode = 0;
					if (!fullCommand.isBlank()) {
						commandHistory.add(fullCommand);
						
						Lib.logWrite("MAIN", 0, "Sending '" + fullCommand + "' to Command Parser");
						try {
							CommandMain.executeCommand(engine.CommandParser.commandSplitArray(fullCommand));
							engine.CommandParser.commandSplitArray(fullCommand);
						} catch (Exception ex) {
							statuscode = 1;
							Lib.logWrite("MAIN", 4, "FATAL ERROR");
							ex.printStackTrace();
						}
					} else {
						Lib.refreshDateTime();
						Lib.cmdLinePrepare(false);
					}
					Lib.logWrite("MAIN", 0, "Command executed with status code: " + statuscode);
					if (statuscode == 0) {
						Lib.logWrite("MAIN", 0, "-> Command executed successfully");
					} else if (statuscode >= 1) {
						Lib.logWrite("MAIN", -1, "-> Command did not execute or did execute with errors");
					} else if (statuscode == -1) {
						Lib.logWrite("MAIN", 0, "-> Error checking was skipped");
					}
				} else if (e.getKeyChar() == KeyEvent.VK_TAB || e.getKeyChar() == KeyEvent.VK_UP) {
					tabCountInRow++;
					try {
						cmdLine.getStyledDocument().remove(cmdLine.getStyledDocument().getLength() - 1, 1);
					} catch (BadLocationException ble) {
						Lib.logWrite("MAIN", 3, "Command repeat has encountered an error: Could not remove 'TAB'");
					}
					
					if (tabCountInRow > commandHistory.size()) {
						Lib.logWrite("MAIN", 1, "Command history end reached");
					} else if (tabCountInRow == 1) {
						//Write out last command without it getting protected (..., true)
						HighLevel.shell_write(1, "HIDDEN", commandHistory.get(commandHistory.size() - tabCountInRow), true);
					} else {
						//TODO Find some sort of replaceLast() \/ -------------------
						try {
							cmdLine.getStyledDocument().remove(
									cmdLine.getText().indexOf(commandHistory.get(commandHistory.size() - tabCountInRow + 1)),
									commandHistory.get(commandHistory.size() - tabCountInRow + 1).length());
						} catch (BadLocationException ble) {
							Lib.logWrite("MAIN", 3, "Command repeat has encountered an error: Could not remove old command");
						}
						HighLevel.shell_write(1, "HIDDEN", commandHistory.get(commandHistory.size() - tabCountInRow));
					}
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