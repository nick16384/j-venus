package filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFileAttributeView;
import engine.sys;

/**
 * This class contains utilities for the following file checks:
 * 1. File (or folder) exists
 * 2. Element is directory / Element is a file
 * 3. File is readable / writable / executable / hidden
 * 4. File is regular file
 * 5. File is symlink
 * 
 * Other non-boolean utilities include:
 * 1. File owner and permissions
 * 
 * Null-checks and subchecks are also included
 * (E.g. checkDir(file) first looks, if a file exists and then checks, if it is a directory.)
 */

public class FileCheckUtils {
	public static final boolean DEFAULT = false; //Default value to return, when checks fail
	public static final Object DEFAULT_NONBOOL = null; //Default value to return for non-boolean methods
	
	public static boolean exists(File file) {
		try {
			if (file == null)
				return false;
			else if (!file.exists() || !file.getCanonicalFile().exists())
				return false;
		} catch (IOException ioe) {
			sys.log("FCU", 3, "Filesystem element existence check failed: IOException");
			ioe.printStackTrace();
			return DEFAULT;
		}
		return true;
	}
	
	public static boolean isDir(File file) {
		try {
			file = prefetchFile(file);
			if (file == null) return DEFAULT;
			if (!exists(file))
				return false;
			else if (!file.isDirectory() || !file.getCanonicalFile().isDirectory())
				return false;
		} catch (IOException ioe) {
			sys.log("FCU", 3, "Directory check failed: IOException");
			ioe.printStackTrace();
			return DEFAULT;
		}
		return true;
	}
	/**
	 * Stricter version of the "isDir()" method using Java NIO.
	 * @param file
	 * @return Whether the supplied element is a directory or not
	 */
	public static boolean isDirStrict(File file) {
		//If normal directory check returns true AND NIO directory check returns true
		try {
			file = prefetchFile(file);
			if (file == null) return DEFAULT;
			if (isDir(file) &&
					(Files.isDirectory(file.toPath(), LinkOption.NOFOLLOW_LINKS)
					|| Files.isDirectory(file.getCanonicalFile().toPath(), LinkOption.NOFOLLOW_LINKS))) {
				return true;
			} else {
				return false;
			}
		} catch (IOException ioe) {
			sys.log("FCU", 3, "Strict directory check failed: IOException");
			ioe.printStackTrace();
			return DEFAULT;
		}
	}
	
	public static boolean isFile(File file) {
		try {
			file = prefetchFile(file);
			if (file == null) return DEFAULT;
			if (!exists(file))
				return false;
			else if (!file.isFile() || !file.getCanonicalFile().isFile())
				return false;
		} catch (IOException ioe) {
			sys.log("FCU", 3, "File check failed: IOException");
			ioe.printStackTrace();
			return DEFAULT;
		}
		return true;
	}
	/**
	 * Stricter version of the "isFile()" method using Java NIO.
	 * @param file
	 * @return Whether the supplied element is a file or not
	 */
	public static boolean isFileStrict(File file) {
		//If normal file check returns true AND NIO file check returns true
		try {
			file = prefetchFile(file);
			if (file == null) return DEFAULT;
			if (isDir(file) &&
					(Files.isRegularFile(file.toPath(), LinkOption.NOFOLLOW_LINKS)
					|| Files.isRegularFile(file.getCanonicalFile().toPath(), LinkOption.NOFOLLOW_LINKS))) {
				return true;
			} else {
				return false;
			}
		} catch (IOException ioe) {
			sys.log("FCU", 3, "Strict file check failed: IOException");
			ioe.printStackTrace();
			return DEFAULT;
		}
	}
	
	public static boolean canRead(File file) {
		if (VarLib.getOSName().equals("Windows"))
			//canRead(), canWrite(), and canExecute() cause problems on Windows
			sys.log("FCU", 2, "Warning: The canRead() method may not work on Windows systems as desired.");
		try {
			file = prefetchFile(file);
			if (file == null) return DEFAULT;
			if (!exists(file))
				return false;
			else if (!Files.isReadable(file.toPath()) || !Files.isReadable(file.getCanonicalFile().toPath()))
				return false;
		} catch (IOException ioe) {
			sys.log("FCU", 3, "Readability check failed: IOException");
			ioe.printStackTrace();
			return DEFAULT;
		}
		return true;
	}
	
	public static boolean canWrite(File file) {
		if (VarLib.getOSName().equals("Windows"))
			//canRead(), canWrite(), and canExecute() cause problems on Windows
			sys.log("FCU", 2, "Warning: The canWrite() method may not work on Windows systems as desired.");
		try {
			file = prefetchFile(file);
			if (file == null) return DEFAULT;
			if (!exists(file))
				return false;
			else if (!Files.isWritable(file.toPath()) || !Files.isWritable(file.getCanonicalFile().toPath()))
				return false;
		} catch (IOException ioe) {
			sys.log("FCU", 3, "Writability check failed: IOException");
			ioe.printStackTrace();
			return DEFAULT;
		}
		return true;
	}
	
	public static boolean canExecute(File file) {
		if (VarLib.getOSName().equals("Windows"))
			//canRead(), canWrite(), and canExecute() cause problems on Windows
			sys.log("FCU", 2, "Warning: The canExecute() method may not work on Windows systems as desired.");
		try {
			file = prefetchFile(file);
			if (file == null) return DEFAULT;
			if (!exists(file))
				return false;
			else if (!Files.isExecutable(file.toPath()) || !Files.isExecutable(file.getCanonicalFile().toPath()))
				return false;
		} catch (IOException ioe) {
			sys.log("FCU", 3, "Executability check failed: IOException");
			ioe.printStackTrace();
			return DEFAULT;
		}
		return true;
	}
	
	public static boolean isHidden(File file) {
		try {
			file = prefetchFile(file);
			if (file == null) return DEFAULT;
			if (!exists(file))
				return false;
			else if (!file.isHidden() || !file.getCanonicalFile().isHidden())
				return false;
		} catch (IOException ioe) {
			sys.log("FCU", 3, "Hidden file / folder check failed: IOException");
			ioe.printStackTrace();
			return DEFAULT;
		}
		return true;
	}
	
	public static boolean isRegularFile(File file) {
		try {
			file = prefetchFile(file);
			if (file == null) return DEFAULT;
			if (!exists(file))
				return false;
			else if (!isFile(file))
				return false;
			else if (!Files.isRegularFile(file.toPath(), LinkOption.NOFOLLOW_LINKS)
					|| !Files.isRegularFile(file.getCanonicalFile().toPath(), LinkOption.NOFOLLOW_LINKS))
				return false;
		} catch (IOException ioe) {
			sys.log("FCU", 3, "File regularity check failed: IOException");
			ioe.printStackTrace();
			return DEFAULT;
		}
		return true;
	}
	
	public static boolean isSymlink(File file) {
		try {
			file = prefetchFile(file);
			if (file == null) return DEFAULT;
			if (!exists(file))
				return false;
			else if (file.getAbsolutePath().equals(file.getCanonicalPath()))
				//If a location is a symlink, getCanonicialPath() will return the location, it is pointing
				//to. This will cause getAbsolutePath() and getCanonicialPath() to differ.
				return false;
			
		} catch (IOException ioe) {
			sys.log("FCU", 3, "Symlink check failed: IOException");
			ioe.printStackTrace();
			return DEFAULT;
		}
		return true;
	}
	
	// ======================== NON-BOOLEAN CHECKS ========================
	
	public static String getOwner(File file) {
		if (!exists(file))
			return null;
		else if (!canRead(file))
			return null;
		try {
			return Files.getOwner(file.toPath(), LinkOption.NOFOLLOW_LINKS).getName();
		} catch (IOException ioe) {
			sys.log("FCU", 3, "Checking file / folder ownership failed: IOException");
			ioe.printStackTrace();
			return (String) DEFAULT_NONBOOL;
		}
	}
	
	/**
	 * Returns the specified file or folder permissions of the current user
	 * in the POSIX-Format:
	 * dash is no permission,
	 * r = read permission,
	 * w = write permission,
	 * x = execution permission
	 * 
	 * Examples:
	 * File "C:\Users\User1\test.txt" has rwxrwxr-x (Windows)
	 * File "/etc/someapp/someapp.conf" has r-xr-xr-x (Linux)
	 * (First three user, Second three group, Third three others)
	 * @param file Input file to start with
	 * @return File permissions in POSIX-Format
	 * 
	 * @apiNote Warning to Eclipse:
	 * A backslash followed by a small "u" in a seeming comment throws syntax errors. Please fix.
	 */
	
	public static String getPermissions(File file) {
		if (!exists(file))
			return null;
		try {
			String permStr = ""; //Final permission string (e.g. rwxr-x--x)
			
			//If OS is Linux, POSIXFileAttributes help get the format.
			if (libraries.VarLib.getOSName().equals("Linux")) {
				//Get POSIX file attributes and convert them to a string
			    PosixFileAttributeView posixView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);
			    String permStrTempSrc = posixView.readAttributes().permissions().toString();
			    StringBuilder permStrTemp = new StringBuilder("---------");
			    if (permStrTempSrc.contains("OWNER_READ"))
			    	permStrTemp.setCharAt(0, 'r');
			    if (permStrTempSrc.contains("OWNER_WRITE"))
			    	permStrTemp.setCharAt(1, 'w');
			    if (permStrTempSrc.contains("OWNER_EXECUTE"))
			    	permStrTemp.setCharAt(2, 'x');
			    if (permStrTempSrc.contains("GROUP_READ"))
			    	permStrTemp.setCharAt(3, 'r');
			    if (permStrTempSrc.contains("GROUP_WRITE"))
			    	permStrTemp.setCharAt(4, 'w');
			    if (permStrTempSrc.contains("GROUP_EXECUTE"))
			    	permStrTemp.setCharAt(5, 'x');
			    if (permStrTempSrc.contains("OTHER_READ"))
			    	permStrTemp.setCharAt(6, 'r');
			    if (permStrTempSrc.contains("OTHER_WRITE"))
			    	permStrTemp.setCharAt(7, 'w');
			    if (permStrTempSrc.contains("OTHER_EXECUTE"))
			    	permStrTemp.setCharAt(8, 'x');
			    
			    permStr = permStrTemp.toString();
			} else {
				//If OS is not Linux, file permissions have to be acquired each and then put together.
				StringBuilder permUser = new StringBuilder("---");
				StringBuilder permGroup = new StringBuilder("---");
				StringBuilder permOther = new StringBuilder("---");
				
				if (canRead(file))
					permUser.setCharAt(0, 'r');
				if (canWrite(file))
					permUser.setCharAt(1, 'w');
				if (canExecute(file))
					permUser.setCharAt(2, 'x');
				
				permStr = String.join(permUser, permGroup, permOther);
			}
			return permStr;
			
		} catch (IOException ioe) {
			sys.log("FCU", 3, "Checking file / folder permissions failed: IOException");
			ioe.printStackTrace();
			return (String) DEFAULT_NONBOOL;
		}
		
		//TODO Add ! prefix for e.g. rm and chmod to modify every file except the one with a ! before it inside a directory
		//Also let users specify multiple exclusions by using e.g. !file1,file2,filewhatever
		
		//TODO add FileCheckUtils functions to all internal commands, instead of their own implementation
	}
	
	/**
	 * Converts "startFile" into the standardized file format.
	 *  (Examples: "./tmp/sometextfile.txt" -> "/home/user/Downloads/tmp/sometextfile.txt" |
	 *   "~/Desktop" -> "/home/user/Desktop/" | "/etc/semicolons/data" -> "/etc/semicolons/data/")
	 * @param startFile
	 * @return The same file with the standardized format, if "startFile" is valid; null otherwise.
	 */
	public static File prefetchFile(File startFile) {
		if (!exists(startFile))
			return null;
		
		String fileLocationString = startFile.getAbsolutePath();
		
		// TODO implement file format conversion (magic)
		
		// Replace leading dot with current / workspace location
		if (fileLocationString.startsWith(".")) {
			fileLocationString = fileLocationString.replaceFirst(".", VarLib.getCurrentDir());
			sys.log("FCU", 1, "File prefetch: Replaced leading dot: " + fileLocationString);
		}
		
		if (fileLocationString.startsWith("~")) {
			fileLocationString = fileLocationString.replaceFirst("~", VarLib.getHomeDir().getAbsolutePath());
			sys.log("FCU", 1, "File prefetch: Replaced leading \"~\" (home): " + fileLocationString);
		}
		
		if (!fileLocationString.startsWith("/")
				&& exists(new File(VarLib.getCurrentDir() + fileLocationString))) {
			fileLocationString = VarLib.getCurrentDir() + VarLib.fsep + fileLocationString;
			sys.log("FCU", 1, "File prefetch: Found file location inside workspace: " + fileLocationString);
		}
		
		fileLocationString = fileLocationString.replaceAll("//", "/");
		
		sys.log("FCU", 0, "Final prefetch: " + fileLocationString);
		
		// TODO 1. Change Eclipse workspace variable, so dot is not automatically replaced by
		// TODO 2. the eclipse workspace, when "new File(".")" is called. -> check via "ls home"
		
		// TODO add "prefetchFile()" to all internal commands using it.
		
		return new File(fileLocationString);
	}
}
