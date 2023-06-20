package threads;

public interface VexusThread {
	
	public final Thread thread = new Thread();
	
	public void start();
	
	public void suspend();
	
	public boolean isRunning();
}
