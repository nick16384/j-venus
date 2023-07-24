package filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import engine.sys;
import libraries.VarLib;

/**
 * An ordinary file object, but the root folder for the object is not the actual root folder.
 * Can be used by VarLib for each File object more convenient  
 * Emulates a "virtual" directory inside a real root directory.
 * The internal directory *must* be accessed with unix format regardless of the underlying OS.
 * Conversion from actual OS location to virtual Linux location (and vice versa) is done internally.
 * The internal root for example is accessed with a "/", while the actual location
 * might be "/etc/semicolons" or "C:\Users\User\Temp"
 */

public class VirtualizedLocation {
	File realLocation;
	
	public VirtualizedLocation(String rootLocation) {
		if (rootLocation == null || rootLocation.startsWith(".")) {
			rootLocation = VarLib.getFSRoot();
			sys.log("VIRTLOC", 2, "Specified location not valid. Using FS root instead.");
		}
		realLocation = new File(rootLocation);
		sys.log("VIRTLOC", 1, "New virtual location created at " + rootLocation);
	}
	
	public boolean createNewFile(VirtualFile virtualPath) {
		String realFileLocation = 
				getActualLocationOf(virtualPath).getAbsolutePath();
		
		try {
			new File(realFileLocation).createNewFile();
		} catch (IOException ioe) {
			sys.log("VIRTLOC", 3, "Creating new file failed. Location: " + realFileLocation);
			ioe.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean appendStringToFile(VirtualFile virtualPath, String text) {
		String realFileLocation = 
				getActualLocationOf(virtualPath).getAbsolutePath();
		if (!filesystem.FileCheckUtils.exists(new File(realFileLocation)))
			return false;
		
		try {
			Files.writeString(Paths.get(realFileLocation), text, StandardOpenOption.APPEND);
		} catch (IOException ioe) {
			sys.log("VIRTLOC", 3, "Writing to file \"" + realFileLocation + "\" unsuccessful.");
			ioe.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean deleteFile(VirtualFile virtualPath) {
		String realFileLocation =
				getActualLocationOf(virtualPath).getAbsolutePath();
		if (!filesystem.FileCheckUtils.exists(new File(realFileLocation))) {
			sys.log("VIRTLOC", 2, "File \""
					+ realFileLocation
					+ "\" doesn't exist, so it cannot be deleted.");
			return false;
		}
		
		if (new File(realFileLocation).delete())
			return true;
		
		sys.log("VIRTLOC", 3, "Error attempting to delete file at \"" + realFileLocation + "\".");
		return false;
	}
	
	public File getActualFile() {
		return realLocation;
	}
	
	public String getAbsolutePath() {
		return realLocation.getAbsolutePath();
	}
	
	public File getActualLocationOf(VirtualFile virtualPath) {
		File realFile = new File(
				convertToCurrentOSFormat(
						realLocation.getAbsolutePath() + virtualPath));
		if (FileCheckUtils.exists(realFile))	
			return realFile;
		else
			return null;
	}
	
	protected static String convertToCurrentOSFormat(String path) {
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
