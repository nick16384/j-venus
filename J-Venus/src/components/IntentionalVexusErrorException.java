package components;

/**
 * This exception may be thrown, if an intentional abnormal termination was caused.
 */
public class IntentionalVexusErrorException extends Exception {
	String code;
	
	public IntentionalVexusErrorException(String code, String msg) {
		super(msg);
		this.code = code;
	}
}
