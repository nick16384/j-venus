package modules;

import java.awt.event.KeyEvent;

import org.w3c.dom.events.Event;

import engine.ANSI;
import engine.sys;
import libraries.OpenLib;

public class CheckUserInputThread {
	private static final Thread checkUserInputThread = new Thread( new Runnable() {
		public final void run() {
			while (!WatchdogThread.shutdownSignal) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ie) {
					sys.log("", 3, "err checkuserinputthread interrupted");
				}
				while (engine.Keyboard.isKeyPressed(KeyEvent.VK_CONTROL)) {
					try { Thread.sleep(50); }
					catch (InterruptedException ie) {
						System.err.println("err chkusrinp");
						ie.printStackTrace();
					}
					
					if (engine.Keyboard.isKeyPressed(KeyEvent.VK_C)) {
						sys.log("CHKINP", 3, "User pressed CTRL + C");
						sys.log("CHKINP", 3, "Forcing execution thread termination!");
						sys.shellPrint(ANSI.D_Cyan, "C^");
						main.CommandMain.terminate();
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
