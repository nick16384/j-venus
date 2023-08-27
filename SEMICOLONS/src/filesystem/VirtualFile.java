package filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import engine.InfoType;
import engine.sys;

public class VirtualFile extends File {
	VirtualizedLocation rootLocation;
	String virtualPath;
	
	protected VirtualFile(VirtualizedLocation root, String virtualPath) {
		
		super(root.getAbsolutePath() + virtualPath);
		this.rootLocation = root;
		this.virtualPath = virtualPath;
		
		if (!FileCheckUtils.isDir(this)) {
			sys.log("VIRTFILE", InfoType.WARN, "Virtual file created, but probably invalid.");
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
		if (FileCheckUtils.exists(new File(this.getActualLocation()))) {
			sys.log("VIRTFILE", InfoType.DEBUG, "Virtual file " + this.getAbsolutePath() + " already exists.");
			return true;
		}
		
		try {
			this.createNewFile();
		} catch (IOException ioe) {
			sys.log("VIRTFILE", InfoType.ERR, "Couldn't create file \"" + getActualLocation() + "\".");
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
			sys.log("VIRTFILE", InfoType.ERR, "Cannot read from file " + this.getAbsolutePath());
			ioe.printStackTrace();
		}
		return fileContent;
	}
	
	public boolean writeString(String data, OpenOption openOption) {
		try {
			Files.writeString(this.toPath(), data, openOption);
			return true;
		} catch (IOException ioe) {
			sys.log("VIRTFILE", InfoType.ERR, "Cannot write to file " + this.getAbsolutePath());
			ioe.printStackTrace();
			return false;
		}
	}
}