package threads;

import java.awt.event.KeyEvent;

import awtcomponents.AWTANSI;
import engine.InfoType;
import engine.sys;
import libraries.VariableInitializion;
import main.Main;
import shell.Shell;

public class CheckUserInputThread implements InternalThread {
	private boolean suspend = false;
	private Thread checkUserInputThread;
	
	protected CheckUserInputThread() {
		checkUserInputThread = new Thread(null, new Runnable() {
			public final void run() {
				while (!ThreadAllocation.isShutdownSignalActive() && !suspend) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ie) {
						sys.log("", InfoType.ERR, "err checkuserinputthread interrupted");
					}
					while (engine.Keyboard.isKeyPressed(KeyEvent.VK_CONTROL)) {
						try { Thread.sleep(50); }
						catch (InterruptedException ie) {
							System.err.println("err chk_usr_inp");
							ie.printStackTrace();
						}
						
						if (engine.Keyboard.isKeyPressed(KeyEvent.VK_C)) {
							sys.log("CHKINP", InfoType.INFO, "User pressed CTRL + C");
							sys.log("CHKINP", InfoType.INFO, "Forcing execution thread termination!");
							Shell.print(AWTANSI.D_Cyan, "^C");
							//TODO Add some form of command termination in CommandManager
							if (ThreadAllocation.getCMGR().isCommandRunning())
								ThreadAllocation.getCMGR().killCurrent();
							else
								Shell.showPrompt();
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
