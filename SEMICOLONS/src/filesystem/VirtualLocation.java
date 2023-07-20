package filesystem;

import java.io.File;
import java.io.IOException;

import engine.sys;
import libraries.VarLib;

/**
 * Emulates a "virtual" directory inside a real root directory.
 * The internal directory *must* be accessed with unix format regardless of the underlying OS.
 * Conversion from actual OS location to virtual Linux location is done internally.
 * The internal root for example is accessed with a "/", while the actual location
 * might be "/etc/semicolons" or "C:\Users\User\Temp"
 */

public class VirtualLocation {
	private File actualLocation;
	
	public VirtualLocation(String rootLoc) {
		if (rootLoc == null || rootLoc.equals(VarLib.getFSRoot()) || rootLoc.startsWith(".")) {
			sys.log("VIRTLOC", 3, "Not a valid location, using filesystem root.");
			rootLoc = VarLib.getFSRoot();
		}
		actualLocation = new File(rootLoc);
		sys.log("VIRTLOC", 1, "New virtual location created at " + actualLocation.getAbsolutePath());
	}
	
	public void createNewFile(String virtualPath) {
		String realFileLocation = 
				convertToCurrentOSFormat(actualLocation.getAbsolutePath() + VarLib.fsep + virtualPath);
		if (!filesystem.FileCheckUtils.exists(new File(realFileLocation)))
			return;
		
		try {
			new File(realFileLocation).createNewFile();
		} catch (IOException ioe) {
			sys.log("VIRTLOC", 3, "Creating new file failed. Location: " + realFileLocation);
			ioe.printStackTrace();
		}
	}
	
	public void writeToFile(String virtualPath, String text) {
		String realFileLocation = 
				convertToCurrentOSFormat(actualLocation.getAbsolutePath() + VarLib.fsep + virtualPath);
		if (!filesystem.FileCheckUtils.exists(new File(realFileLocation)))
			return;
		
	}
	
	private String convertToCurrentOSFormat(String path) {
		if (VarLib.getOSName().equals("Linux")) {
			path = path.replaceAll("\\", "/");
			path = path.replaceFirst("[A-Z]:\\", "/");
		} else if (VarLib.getOSName().equals("Windows")) {
			path = path.replaceAll("/", "\\");
			path = path.replaceFirst("/", VarLib.getFSRoot());
		} else {
			sys.log("VIRTLOC", 2, "Warning: OS file format not implemented yet, assuming unix.");
			path = path.replaceAll("\\", "/");
			path = path.replaceFirst("[A-Z]:\\", "/");
		}
		return path;
	}
}
