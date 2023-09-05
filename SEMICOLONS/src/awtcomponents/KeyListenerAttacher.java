package awtcomponents;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import javax.swing.text.BadLocationException;

import engine.InfoType;
import engine.sys;
import filesystem.InternalFiles;
import libraries.Err;
import libraries.VariableInitializion;
import libraries.Global;
import main.Main;
import shell.Shell;
import threads.ThreadAllocation;

/**
 * Attached a KeyListener for ENTER and TAB for WindowMain.cmdLine
 *
 */

public class KeyListenerAttacher {
	public static void attachKeyListener(WindowMain mainWindow) {
		
		//JTextPane WindowMain.cmdLine = mainWindow.getCmdLine();
		WindowMain.cmdLine.addKeyListener(new KeyListener() {
			
			
			//TODO Use global key event and add to "keyPress" variable so that SWT can more easily detect what was typed newly.
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					main.Main.tabCountInRow = 0;
					//UPDATE SHELL STREAM ==============================================================================
					ThreadAllocation.getSWT().updateShellStream();
					//END UPDATE SHELL STREAM ==========================================================================
					//Splitting WindowMain.cmdLine text into command
					String[] lines = WindowMain.cmdLine.getText().split("\n");
					String[] lines2 = new String[0];
					try {
						lines2 =
								WindowMain.cmdLine.getDocument().getText(0, WindowMain.cmdLine.getDocument().getLength()).split("\n");
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String lastLine = lines[lines.length - 1];
					String lastLine2 = lines2[lines2.length - 1];
					//if (lastLine) //check lastline multiple prompts
					
					System.err.println("VarLib prompt: " + Shell.getPrompt());
					System.err.println("Last line length: " + lastLine.length());
					System.err.println("Last line content: " + lastLine);
					System.err.println("Last line 2 length: " + lastLine2.length());
					System.err.println("Last line 2 content: " + lastLine2);
					System.err.println("Prompt length: " + Shell.getPrompt().length());
					
					//Extract full command from last line (Remove prompt)
					//Dev. note: VarLib.getPrompt() contains ANSI excapes, but cmdLine.getText() doesn't, so
					//all ANSI escape chars had to be cleared out by the regex shown.
					String fullCommand = lastLine.substring(
							Shell.getPrompt().replaceAll("\u001B\\[[\\d;]*[^\\d;]","").length(), lastLine.length());
					
					//if (fullCommand.contains(VarLib.getPrompt())) { fullCommand = fullCommand.split("\\$ ")[1]; }
					if (!fullCommand.isBlank()) {
						if (fullCommand.contains(" && ")) {
							sys.log("MAIN", InfoType.WARN, "Info: Found multiple commands connected with '&&'.");
							sys.log("MAIN", InfoType.WARN, "This is still experimental: Expect errors.");
							Shell.println("Using experimental command interconnect: '&&'");
							for (String subCommand : fullCommand.split(" && ")) {
								sys.log("MAIN", InfoType.DEBUG, "Running '" + fullCommand + "'");
								sys.log("Subcommand: " + subCommand);
								try {
									commands.CommandOld cmd = new commands.CommandOld(subCommand);
									cmd.start();
									sys.log("New thread started (subCommand placed into cmdQueue)");
									//For returnVal, try:
									//CommandMain.executeCommand(new components.Command(fullCommand));
								} catch (Exception ex) {
									//Error information is printed to stdout and shell
									Err.shellPrintErr(ex, "FATAL ERROR", "Non-caught JVM exception in class CmdMain");
								}
							}
						} else {
							sys.log("MAIN", InfoType.DEBUG, "Sending '" + fullCommand + "' to Command Parser");
							try {
								new commands.CommandOld(fullCommand).start();
								//For returnVal, try:
								//CommandMain.executeCommand(new components.Command(fullCommand));
							} catch (Exception ex) {
								Err.shellPrintErr(ex, "FATAL ERROR", "Non-caught JVM exception in class CmdMain");
							}
						}
						//=========================ADD FULLCMD TO HISTORY===============================
						main.Main.commandHistory.add(fullCommand);
						try {
							String history = InternalFiles.getCmdHistoryASCII().readContents();
							int max_history_size =
									Integer.parseInt(
											InternalFiles.getCmdHistoryASCIIMaxLength().readContents().trim());
							//Remove first entry of history until size of entries is below count in cmd_history_max_length
							while (history.split("\n").length > max_history_size) {
								InternalFiles.getCmdHistoryASCII().writeString(
										history.replaceFirst(history.split("\n")[0], ""), StandardOpenOption.WRITE);
							}
							InternalFiles.getCmdHistoryASCII().writeString(fullCommand + "\n", StandardOpenOption.APPEND);
						} catch (NumberFormatException nfe) {
							sys.log("MAIN", InfoType.WARN, "Parsing cmd_history_max_length failed. Check file exists" +
									" and contains a number below 2.147.483.647");
						}
						//============================END ADD FULLCMD TO HISTORY==============================
					} else {
						Shell.showPrompt();
					}
				} else if (e.getKeyChar() == KeyEvent.VK_TAB) {
					//========================================COMMAND REPEAT============================================
					main.Main.tabCountInRow++;
					try {
						new components.ProtectedTextComponent(WindowMain.cmdLine).unprotectAllText();
						//Remove last edited line in WindowMain.cmdLine, reappend without last entered command
						WindowMain.cmdLine.getStyledDocument().remove(WindowMain.cmdLine.getText().lastIndexOf("\n") + 1,
								WindowMain.cmdLine.getText().substring(WindowMain.cmdLine.getText().lastIndexOf("\n")).length());
						new components.ProtectedTextComponent(WindowMain.cmdLine).protectText(0, WindowMain.cmdLine.getText().length());
					} catch (BadLocationException ble) {
						sys.log("KLA", InfoType.ERR, "Command repeat error: Could not remove last line.");
					} catch (NullPointerException npe) {
						sys.log("KLA", InfoType.ERR, "Command repeat error: main.mainFrame is null.");
					}
					Shell.showPrompt();
					main.Main.commandHistory.clear();
					//Add all entries of cmd_history to LinkedList main.Main.commandHistory
					main.Main.commandHistory.addAll(
							Arrays.asList(
									InternalFiles.getCmdHistoryASCII().readContents().split("\n")));
					
					if (main.Main.tabCountInRow > main.Main.commandHistory.size()) {
						Toolkit.getDefaultToolkit().beep();
						sys.log("MAIN", InfoType.INFO, "Command history end reached");
					} else if (main.Main.tabCountInRow == 1) {
						//Write out last command without it getting protected (..., true)
						sys.log("REPEAT", InfoType.DEBUG, "Command repeat: "
								+ main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow));
						Shell.print(1, "HIDDEN", main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow), true);
					} else {
						//TODO Find some sort of replaceLast() \/ -------------------
						/*try {
							WindowMain.cmdLine.getStyledDocument().remove(
									WindowMain.cmdLine.getText().indexOf(main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow + 1)),
									main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow + 1).length());
						} catch (BadLocationException ble) {
							OpenLib.logWrite("MAIN", 3, "Command repeat error: Could not remove old command");
						}*/
						sys.log("REPEAT", InfoType.DEBUG, "Command repeat(" + main.Main.tabCountInRow + "): "
								+ main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow));
						Shell.print(1, "HIDDEN", main.Main.commandHistory.get(main.Main.commandHistory.size() - main.Main.tabCountInRow), true);
					}
					//========================================COMMAND REPEAT END============================================
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
	}
}
