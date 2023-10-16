package filesystem;

/**
 * A VirtualizedLocation, which does not exist on the filesystem, but may be created using
 *  the createOnFilesystem() method.
 */

public class VolatileVirtualizedLocation extends VirtualizedLocation {
	
	//TODO Implement this all
	public VolatileVirtualizedLocation(String rootLocation) {
		super(rootLocation);
		// TODO Auto-generated constructor stub
	}
	
	public VirtualizedLocation createOnFilesystem() {
		return null;
	}
}
