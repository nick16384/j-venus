package main;

import java.lang.Exception;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

import engine.HighLevel;
import main.Lib;
import modules.CommandLoader;

public class Main extends JFrame {
	public static void main(String[] args) {
		modules.WatchdogThread.runThread();
		System.out.println("JavaDOS Version " + Lib.getVersion() + " Warning: Beta Version");
		System.out.println("Copyright(C) 2021 The JavaDOS Project. All rights reserved.");
		System.out.println("GNU General Public License: Oracle Corporation");
		System.out.println("");
		Lib.logWrite("MAIN", 0, "Loading internal variables...");
		Lib.initVars();
		Lib.logWrite("MAIN", 0, "Done.");
		Lib.logWrite("MAIN", 0, "Loading main window...");
		Main mainFrame = new Main();
		Lib.logWrite("MAIN", 0, "Done.");
		Lib.logWrite("MAIN", 0, "Lodaing external commands...");
		try { Lib.setExtCommands(CommandLoader.loadCommands()); }
		catch (AccessDeniedException ade) { Lib.logWrite("MAIN", 2, "Access to the destination file is denied."); }
		catch (NoSuchFileException nsfe) { Lib.logWrite("MAIN", 2, "External commands not found."); }
		catch (IOException ioe) { Lib.logWrite("MAIN", 2, "Unhandled IOException while loading external commands."); ioe.printStackTrace(); }
		Lib.logWrite("MAIN", 1, "Finished startup.");
		
	}
	public ArrayList<String> commandHistory = new ArrayList<>();
	public int tabCountInRow = 0;
	Font shellFont = new Font("Consolas", Font.BOLD, 16);
	JPanel mainShellPanel = new JPanel();
	public static JTextPane cmdLine = new JTextPane();
	
	public Main() {
		this.setLayout(null);
		this.setSize(900, 550);
		this.setLocation(350, 150);
		this.setVisible(true);
		this.setTitle("JavaDOS");
		
		mainShellPanel.setLayout(null);
		mainShellPanel.setSize(this.getSize());
		mainShellPanel.setLocation(0, 0);
		mainShellPanel.setVisible(true);
		
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
		cmdLine.setCaretPosition(cmdLine.getText().length());
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
					commandHistory.add(fullCommand);
					
					Lib.logWrite("MAIN", 0, "Sending '" + fullCommand + "' to UI_Interpreter");
					try {
						CommandMain.executeCommand(engine.HighLevel.commandSplitArray(fullCommand));
						engine.HighLevel.commandSplitArray(fullCommand);
					} catch (Exception ex) {
						statuscode = 1;
						Lib.logWrite("MAIN", 4, "FATAL ERROR");
						ex.printStackTrace();
					}
					Lib.logWrite("MAIN", 0, "Command executed with status code: " + statuscode);
				} else if (e.getKeyChar() == KeyEvent.VK_TAB) {
					tabCountInRow++;
					cmdLine.setText(cmdLine.getText().replace("\t", ""));
					//TODO Fix pressing TAB removing color!
					
					if (tabCountInRow > commandHistory.size()) {
						Lib.logWrite("MAIN", 1, "Command history end reached");
					} else if (tabCountInRow == 1) {
						HighLevel.shell_write(1, "HIDDEN", commandHistory.get(commandHistory.size() - tabCountInRow));
					} else {
						//TODO Find some sort of replaceLast() \/ -------------------
						cmdLine.setText(cmdLine.getText().replace(commandHistory.get(commandHistory.size() - tabCountInRow + 1), ""));
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
	}
}