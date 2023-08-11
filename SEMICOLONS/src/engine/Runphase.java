package engine;

import libraries.ErrCodes;

public enum Runphase {
	
	PREINIT ("The first phase: Prepares variables for the actual INIT to run."),
	INIT ("Second phase: Active until everything is loaded."),
	RUN ("Third and most dominant phase: Active until either an error occurs or the STOP phase is reached."),
	ERR ("Error phase: Indicates a non-recoverable or unsafe error has occured and shutdown is needed."),
	STOP ("Final phase (Assuming no error): Indicates that SEMICOLONS is shutting down.");
	
	public final String description;

	private Runphase(String description) {
		this.description = description;
	}
	
	public final String getDescription() {
		return this.description;
	}
	
	/**
	 * Gets the appropriate phase object from a name string
	 * @param phaseName
	 * @return Runphase object
	 */
	public static final Runphase fromString(String phaseName) {
		for (Runphase phase : values()) {
			if (phase.name().equals(phaseName)) {
				return phase;
			}
		}
		return null;
	}
	
	/**
	 * Get the phase after the specified one (both STOP and ERR return null)
	 * @param current
	 * @return
	 */
	public static final Runphase getNextPhase(Runphase current) {
		switch (current) {
			case PREINIT: return INIT;
			case INIT: return RUN;
			case RUN: return STOP;
			default: return null;
		}
	}
}
