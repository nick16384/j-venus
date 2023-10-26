package awtcomponents;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.LogLevel;
import engine.sys;
import libraries.Global;
import main.Main;

public class AWTWinload {
	public static void awtWinload() {
		sys.log("MAIN", LogLevel.INFO, "Loading main AWT window...");
		Main.initAWTWindow();
		//try { Thread.sleep(2000); } catch (InterruptedException ie) { ie.printStackTrace(); }
		sys.log("MAIN", LogLevel.STATUS, "Done.");
		sys.log("MAIN", LogLevel.INFO, "Setting parameters for mainFrame (icon image, title)...");
		//Set icon image for mainFrame
		try {
			Main.mainFrameAWT.setIconImage(ImageIO.read(
					Global.getDataDir().newVirtualFile("/semicolons-icon.png")));
		} catch (IOException e) {
			sys.log("MAIN", LogLevel.ERR, "Could not set icon image. The file probably doesn't exist or is not a supported image file.");
			sys.log("MAIN", LogLevel.ERR, "Icon path: "
					+ Global.getDataDir().newVirtualFile("/semicolons-icon.png").getAbsolutePath());
		}
		Main.mainFrameAWT.setName("J-Vexus " + Global.getVersion());
		sys.log("MAIN", LogLevel.DEBUG, "Done.");
	}
}
