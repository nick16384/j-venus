package awtcomponents;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.InfoType;
import engine.sys;
import libraries.Global;
import main.Main;

public class AWTWinload {
	public static void awtWinload() {
		sys.log("MAIN", InfoType.INFO, "Loading main AWT window...");
		Main.initAWTWindow();
		//try { Thread.sleep(2000); } catch (InterruptedException ie) { ie.printStackTrace(); }
		sys.log("MAIN", InfoType.STATUS, "Done.");
		sys.log("MAIN", InfoType.INFO, "Setting parameters for mainFrame (icon image, title)...");
		//Set icon image for mainFrame
		try {
			Main.mainFrameAWT.setIconImage(ImageIO.read(
					Global.getDataDir().newVirtualFile("/semicolons-icon.png")));
		} catch (IOException e) {
			sys.log("MAIN", InfoType.ERR, "Could not set icon image. The file probably doesn't exist or is not a supported image file.");
			sys.log("MAIN", InfoType.ERR, "Icon path: "
					+ Global.getDataDir().newVirtualFile("/semicolons-icon.png").getAbsolutePath());
		}
		Main.mainFrameAWT.setName("J-Vexus " + Global.getVersion());
		sys.log("MAIN", InfoType.DEBUG, "Done.");
	}
}
