package filesystem;

import java.io.File;
import java.io.IOException;

import engine.sys;
import libraries.VarLib;

public class VirtualFile extends File {
	VirtualizedLocation rootLocation;
	String virtualPath;
	
	public VirtualFile(VirtualizedLocation root, String virtualPath) {
		
		// Use specified file if it exists, virtual root otherwise
		// This had to be packed into a ternary operator, since the super() call must
		// be the first one in a constructor.
		super(FileCheckUtils.exists(new File(root.getAbsolutePath() + virtualPath))
						? root.getAbsolutePath() + virtualPath
						: root.getAbsolutePath());
		this.rootLocation = root;
		this.virtualPath = virtualPath;
	}
	
	/**
	 * @return The actual location, if it exists. Null otherwise.
	 */
	public String getActualLocation() {
		String actualFile =
				VirtualizedLocation.convertToCurrentOSFormat(rootLocation.getAbsolutePath() + virtualPath);
		return FileCheckUtils.exists(new File(actualFile)) ? actualFile : null;
	}
	
	public boolean createIfNotExisting() {
		if (!FileCheckUtils.exists(new File(this.getActualLocation()))) {
			try {
				this.createNewFile();
			} catch (IOException ioe) {
				sys.log("VIRTFILE", 3, "Couldn't create file \"" + getActualLocation() + "\".");
				ioe.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
