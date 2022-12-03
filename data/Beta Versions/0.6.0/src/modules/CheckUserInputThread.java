package modules;

import java.awt.event.KeyEvent;

import org.w3c.dom.events.Event;

import main.Lib;

public class CheckUserInputThread {
	private static final Thread checkUserInputThread = new Thread( new Runnable() {
		public final void run() {
			while (!WatchdogThread.shutdownSignal) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ie) {
					Lib.logWrite("", 3, "err checkuserinputthread interrupted");
				}
				while (engine.Keyboard.isKeyPressed(KeyEvent.VK_CONTROL)) {
					try { Thread.sleep(50); } catch (InterruptedException ie) { System.err.println("err chkusrinp"); }
					if (engine.Keyboard.isKeyPressed(KeyEvent.VK_C)) { //TODO fix ctrl detection works but c not
						Lib.logWrite("CHKINP", 3, "User pressed CTRL + C");
						Lib.logWrite("CHKINP", 3, "Forcing execution thread termination!");
						main.CommandMain.forceTerminate();
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
	});
	
	public static final void startThread() {
		checkUserInputThread.start();
	}
	
	public static final boolean isThreadAlive() {
		return checkUserInputThread.isAlive();
	}
}
