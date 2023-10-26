package filesystem;

public class NoWritePermissionException extends Exception {
	
	public NoWritePermissionException(String message) {
		super(message);
	}
}
