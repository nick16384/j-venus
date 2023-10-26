package engine;

/**
 * LogLevel represents the importance / fatality of a log message
 * @apiNote DEBUG and STATUS are internally the same.
 */

public enum LogLevel {
	ERR(-1, true),
	DEBUG(0, false), STATUS(0, false),
	INFO(1, true),
	WARN(2, true),
	NONCRIT(3, true),
	CRIT(4, true),
	FATAL(5, true);
	
	private int priority; // Integer priority representation: Listing found in engine.Logging
	private boolean show; // Whether to show these types of messages
	
	private LogLevel(int priority, boolean showByDefault) {
		this.priority = priority;
		this.show = showByDefault;
	}
	
	public int asInt() {
		return this.priority;
	}
	
	public void disable() {
		this.show = false;
	}
	
	public void enable() {
		this.show = true;
	}
	
	public boolean isEnabled() {
		return show;
	}
}
