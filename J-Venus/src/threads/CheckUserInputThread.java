package threads;

import java.awt.event.KeyEvent;

import org.w3c.dom.events.Event;

import engine.AWTANSI;
import engine.sys;
import libraries.OpenLib;
import main.Main;

public class CheckUserInputThread implements VexusThread {
	private boolean suspend = false;
	private Thread checkUserInputThread;
	
	protected CheckUserInputThread() {
		checkUserInputThread = new Thread(null, new Runnable() {
			public final void run() {
				while (!Main.ThreadAllocMain.isShutdownSignalActive() && !suspend) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ie) {
						sys.log("", 3, "err checkuserinputthread interrupted");
					}
					while (engine.Keyboard.isKeyPressed(KeyEvent.VK_CONTROL)) {
						try { Thread.sleep(50); }
						catch (InterruptedException ie) {
							System.err.println("err chk_usr_inp");
							ie.printStackTrace();
						}
						
						if (engine.Keyboard.isKeyPressed(KeyEvent.VK_C)) {
							sys.log("CHKINP", 3, "User pressed CTRL + C");
							sys.log("CHKINP", 3, "Forcing execution thread termination!");
							sys.shellPrint(AWTANSI.D_Cyan, "^C");
							//TODO Add some form of command termination in CommandManager
							if (Main.ThreadAllocMain.getCMGR().isCommandRunning())
								Main.ThreadAllocMain.getCMGR().killCurrent();
							else
								OpenLib.cmdLinePrepare();
						}
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//TODO check for e.g. CTRL + C and force command stop
			}
		}, "CUIT");
	}
	
	public final void start() {
		checkUserInputThread.start();
	}
	
	public final boolean isRunning() {
		return checkUserInputThread.isAlive();
	}
	
	public final void suspend() {
		this.suspend = true;
	}
}
