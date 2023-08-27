package shell;

/**
 * This class acts as a double buffer system for two strings:
 * One string is modified while the other one being read from. 
 * This prevents unintentional duplicate or missing text. 
 * Only one buffer may be active (being read from) at a time.
 * 
 * @since v23.08
 */

public class DoubleTextBuffer {
	private static String buffer0;
	private static String buffer1;
	private static boolean bufferZeroActive;
	
	public DoubleTextBuffer() {
		buffer0 = "";
		buffer1 = "";
		bufferZeroActive = true;
	}
	
	public synchronized void appendToInactive(String text) {
		if (text == null)
			return;
		
		if (bufferZeroActive)
			buffer1 += text;
		else
			buffer0 += text;
	}
	
	public String readFromActive() {
		return (bufferZeroActive ? buffer0 : buffer1);
	}
	
	public String readFromInactive() {
		return (bufferZeroActive ? buffer1 : buffer0);
	}
	
	public synchronized void swapActive() {
		bufferZeroActive = !bufferZeroActive;
	}
	
	public synchronized void clearActive() {
		if (bufferZeroActive)
			buffer0 = "";
		else
			buffer1 = "";
	}
}
