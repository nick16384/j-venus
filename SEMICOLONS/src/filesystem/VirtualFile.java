package filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;

import engine.LogLevel;
import engine.sys;

public class VirtualFile extends File {
	private VirtualizedLocation rootLocation;
	private String virtualPath;
	private String virtualFileContent;
	
	protected VirtualFile(VirtualizedLocation root, String virtualPath) {
		
		super(root.getAbsolutePath() + virtualPath);
		this.rootLocation = root;
		this.virtualPath = virtualPath;
		virtualFileContent = "";
		
		if (!FileCheckUtils.isDir(this)) {
			sys.log("VIRTFILE", LogLevel.WARN, "Virtual file created, but probably invalid.");
		}
	}
	
	/**
	 * @return The actual location, if it exists. Null otherwise.
	 */
	public String getActualLocation() {
		String actualFile =
				VirtualizedLocation.convertToCurrentOSFormat(rootLocation.getAbsolutePath() + virtualPath);
		return FileCheckUtils.exists(new File(actualFile)) ? actualFile : null;
	}
	
	
	/**
	 * Creates an actual file on the filesystem, if this file doesn't exist yet.
	 * If it already exists, this method does nothing.
	 * @return
	 */
	public boolean createOnFilesystem() {
		if (FileCheckUtils.exists(this.getAbsoluteFile())) {
			sys.log("VIRTFILE", LogLevel.DEBUG, "Virtual file " + this.getAbsolutePath() + " already exists.");
			return true;
		}
		if (!FileCheckUtils.exists(this.getParentFile())) {
			sys.log("VIRTFILE", LogLevel.WARN, "Parent directory of "
					+ this.getAbsolutePath() + " does not exist. Attempting to create.");
			if (this.getParentFile().mkdirs())
				createOnFilesystem(); // Re-attempt with given directory structure.
			else {
				sys.log("VIRTFILE", LogLevel.NONCRIT, "Creating parent directory structure failed.");
				return false; // If directory structure could not be generated, return failure.
			}
		}
		
		try {
			this.createNewFile();
		} catch (IOException ioe) {
			sys.log("VIRTFILE", LogLevel.ERR, "Couldn't create file \"" + getActualLocation() + "\".");
			ioe.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String readContents() {
		String fileContent = null;
		try {
			if (!FileCheckUtils.notLargerThan(this, 512))
				throw new IOException("File is too big to attempt read (more than 512 Megabytes).");
			fileContent = Files.readString(this.toPath());
		} catch (IOException ioe) {
			sys.log("VIRTFILE", LogLevel.ERR, "Cannot read from file " + this.getAbsolutePath());
			sys.log("VIRTFILE", LogLevel.ERR, "Reading from virtual file content instead.");
			ioe.printStackTrace();
			return virtualFileContent;
		}
		return fileContent;
	}
	
	public boolean writeString(String data, OpenOption openOption) {
		try {
			Files.writeString(this.toPath(), data, openOption);
			return true;
		} catch (IOException ioe) {
			sys.log("VIRTFILE", LogLevel.ERR, "Cannot write to file " + this.getAbsolutePath());
			sys.log("VIRTFILE", LogLevel.ERR, "Writing to virtual file content instead.");
			sys.log("VIRTFILE", LogLevel.ERR, "Warning: Appending to virtual file content, OpenOption ignored.");
			virtualFileContent += data;
			ioe.printStackTrace();
			return false;
		}
	}
}
