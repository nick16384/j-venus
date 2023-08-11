package engine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import libraries.Global;

public class CheckInstall {
	/**
	 * This method checks, whether all critical Vexus files exist and
	 * have their appropriate RWX permissions set.
	 * 
	 * Note: This method is not complete yet and may not check some files.
	 * @return null, if files exist and have appropriate permissions. Otherwise, the file's name, which caused the problem.
	 */
	public static String fileCheck() {
		//Saves files, which do not exist OR are configured with wrong permissions.
		//If this is null, all checked files are correct.
		String fileErrors = null;
		
		String fs; //file separator
		String fsroot; //File system root
		
		if (!Global.getCurrentPhase().equals(Runphase.RUN)) {
			//Set default values
			fs = "/";
			fsroot = "/";
		} else {
			fs = Global.fsep;
			fsroot = Global.getFSRoot();
		}
		
		File vxRoot = new File(fsroot + fs + "etc" + fs + "semicolons");
		File vxBin = new File(fsroot + fs + "etc" + fs + "semicolons" + fs + "bin");
		File vxData = new File(fsroot + fs + "etc" + fs + "semicolons" + fs + "data");
		
		if (!Files.exists(vxRoot.toPath(), LinkOption.NOFOLLOW_LINKS)) {
			fileErrors = vxRoot.getAbsolutePath();
			
		} else if (!Files.exists(vxBin.toPath(), LinkOption.NOFOLLOW_LINKS)) {
			fileErrors = vxBin.getAbsolutePath();
			
		} else if (!Files.exists(vxData.toPath(), LinkOption.NOFOLLOW_LINKS)) {
			fileErrors = vxData.getAbsolutePath();
		}
		//filesExist remains null, if none of the above if-statements was changing that
		
		return fileErrors;
	}
}
