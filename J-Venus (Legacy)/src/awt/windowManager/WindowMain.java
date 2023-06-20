package awt.windowManager;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import engine.sys;
import main.Main;

public class WindowMain extends JFrame {
	private static Font shellFont = new Font("Terminus (TTF)", Font.BOLD, 16);
	public static JPanel mainShellPanel = new JPanel();
	public static JTextPane cmdLine = new JTextPane();
	
	public WindowMain(String titleName) {
		GraphicsDevice device = null;
		if (Arrays.asList(Main.argsMain).contains("--fullscreen") ||
			Arrays.asList(Main.argsMain).contains("--full-screen")) {
			main.Main.fullscreen = true;
		} else {
			main.Main.fullscreen = false;
		}
		
		//TODO maybe remove this later if not working (23.01)
		//dispose();
		
		if (main.Main.fullscreen) {
			//Fullscreen display (entire screen, no window)
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
		} else {
			//Default window style
			this.setLayout(null); //TODO change so that shell resizes with window
			this.setSize(900, 550);
			this.setLocation(350, 150);
			this.setVisible(true);
		}
		dispose();
		//this.setUndecorated(true);
		//this.setBackground(new Color(0, 0, 0, 0));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		//this.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.0f));
		if (TranslucencyCheck.isTranslucencySupported()) {
			sys.log("Translucency is supported on this system.");
			sys.log("HIDDEN", 2, "Not setting translucency. Developer's note: Make this work (not crash, especially Windows)");
			//this.setOpacity(0.2f);
		}
		setVisible(true);
		
		
		mainShellPanel.setLayout(null);
		mainShellPanel.setSize(this.getWidth(), this.getHeight());
		mainShellPanel.setLocation(0, 0);
		mainShellPanel.setBackground(new Color(0, 0, 0, 0));
		//mainShellPanel.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.2f));
		//mainShellPanel.setOpaque(false);
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
		//TODO, big TODO!: Fix cmdLine graphical glitches
		//TODO -> setBackground in "this", "mainShellPanel", and "cmdLine"
		
		//TODO add internal variable formatting to CmdMgr
		cmdLine.setBackground(new Color(0.0f, 0.0f, 0.0f, 1.0f));
		//cmdLine.setOpaque(false);
		cmdLine.setForeground(Color.WHITE);
		cmdLine.setFont(shellFont);
		cmdLine.setCaretColor(Color.CYAN);
		try { cmdLine.setCaretPosition(cmdLine.getText().length()); } //TODO Fix cursor not in right position (windows)
		catch (IllegalArgumentException iae) { sys.log("MAIN", 2, "Could not set cursor position"); }
		
		this.add(mainShellPanel);
		mainShellPanel.add(cmdLine);
	}
	
	public JTextPane getCmdLine() {
		return cmdLine;
	}
}
