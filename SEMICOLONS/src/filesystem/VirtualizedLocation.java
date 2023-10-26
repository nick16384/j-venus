package filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import engine.LogLevel;
import engine.sys;
import libraries.Global;

/**
 * An ordinary file object, but the root folder for the object is not the actual root folder.
 * Can be used by Global for each File object more convenient  
=======
import engine.sys;
import libraries.VarLib;

/**
 * An ordinary file object, but the root folder for the object is not the actual root folder.
 * Can be used by VarLib for each File object more convenient  
>>>>>>> 90664cc5e3f79d38ab54e22e5d5fe99879274032
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
			rootLocation = Global.getFSRoot();
			sys.log("VIRTLOC", LogLevel.WARN, "Specified location not valid. Using FS root instead.");
		}
		realLocation = new File(rootLocation);
		sys.log("VIRTLOC", LogLevel.DEBUG, "New virtual location created at " + rootLocation);
	}
	
	public boolean createNewFile(VirtualFile virtualPath) {
		String realFileLocation = 
				getActualLocationOf(virtualPath).getAbsolutePath();
		
		try {
			new File(realFileLocation).createNewFile();
		} catch (IOException ioe) {
			sys.log("VIRTLOC", LogLevel.ERR, "Creating new file failed. Location: " + realFileLocation);
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
			sys.log("VIRTLOC", LogLevel.ERR, "Writing to file \"" + realFileLocation + "\" unsuccessful.");
			ioe.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean deleteFile(VirtualFile virtualPath) {
		String realFileLocation =
				getActualLocationOf(virtualPath).getAbsolutePath();
		if (!filesystem.FileCheckUtils.exists(new File(realFileLocation))) {
			sys.log("VIRTLOC", LogLevel.WARN, "File \""
					+ realFileLocation
					+ "\" doesn't exist, so it cannot be deleted.");
			return false;
		}
		
		if (new File(realFileLocation).delete())
			return true;
		
		sys.log("VIRTLOC", LogLevel.ERR, "Error attempting to delete file at \"" + realFileLocation + "\".");
		return false;
	}
	
	public File getActualFile() {
		return realLocation;
	}
	
	public String getAbsolutePath() {
		return realLocation.getAbsolutePath();
	}
	
	public String getAbsolutePathWithTrailingFSep() {
		return realLocation.getAbsolutePath() + sys.fsep;
	}
	
	public File getActualLocationOf(VirtualFile virtualPath) {
		File realFile = new File(
				convertToCurrentOSFormat(
						realLocation.getAbsolutePath() + virtualPath.getAbsolutePath()));
		if (FileCheckUtils.exists(realFile))	
			return realFile;
		else
			return null;
	}
	
	public VirtualFile newVirtualFile(String virtualRoot) {
		if (virtualRoot == null)
			return null;
		
		File realFile = new File(
				convertToCurrentOSFormat(
						realLocation.getAbsolutePath() + virtualRoot));
		if (!FileCheckUtils.exists(realFile))
			sys.log("VIRTLOC", LogLevel.WARN,
					"New virtual file ("+ realFile.getAbsolutePath() +") does not exist (yet).");
		
		return new VirtualFile(this, virtualRoot);
	}
	
	protected static String convertToCurrentOSFormat(String path) {
		if (Global.getOSName().equals("Linux")) {
			path = path.replaceFirst("[A-Z]:\\\\", "/");
			path = path.replaceAll("\\\\", "/");
		} else if (Global.getOSName().equals("Windows")) {
			path = path.replaceAll("/", "\\");
			path = path.replaceFirst("/", Global.getFSRoot());
		} else {
			sys.log("VIRTLOC", LogLevel.WARN, "Warning: OS file format not implemented yet, assuming unix.");
			path = path.replaceAll("\\", "/");
			path = path.replaceFirst("[A-Z]:\\", "/");
		}
		sys.log("VIRTLOC", LogLevel.DEBUG, "After conversion to OS format : " + path);
		return path;
	}
}
