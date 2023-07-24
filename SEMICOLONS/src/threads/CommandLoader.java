package threads;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import javax.tools.*;

import engine.sys;
import libraries.VarLib;

/**
 * Legacy class: Has been replaced by direct JAR execution in CommandMain,
 * Instead of locating, renaming, and compiling files.
 * JAR format also supports multiple classes, which makes execution much easier.
 * @author nick16384
 * @deprecated
 */

@Deprecated
public class CommandLoader {
	private static boolean alreadyExecuted = false;
	/**
	 * Warning: Deprecated method since 22.09: Has been replaced by JAR execution,
	 * because it supports multiple classes and dependencies and is easier to handle
	 * instead of this mess of code, that does not work.
	 * 
	 * Locates, renames and compiles .jdexe files in ./commands/
	 * Then maps their associated command string with their .class executable in ./bin
	 * @return Map command string -> .class file location
	 * @throws IOException
	 */
	@Deprecated
	public static Map<String, File> loadCommands() throws IOException {
		Map<String, File> extCommands = new HashMap<>();
		File configFileSource = new File(VarLib.getRootDir() + VarLib.fsep + "commands.cfg");
		File commandSource = new File(VarLib.getRootDir() + VarLib.fsep + "commands");
		
		if (alreadyExecuted) {
			sys.log("LDEXTCMDS", 2, "External commands already loaded. Cannot proceed.");
			return null;
		}
		
		alreadyExecuted = true;
		
		//TODO Fix all errors in this to support external commands
		//TODO go down for first error to fix: compile
		
		//======================================LOAD CONFIG FILE========================================
		//Read config file 'commands.cfg'
		sys.log("LDEXTCMDS", 0, "Reading config file...");
        String configFileContent = Files.readString(configFileSource.toPath());
        sys.log("LDEXTCMDS", 0, "Done. Read content:");
        sys.log("LDEXTCMDS", 0, configFileContent + "\n");
        //Loading config file
        if ((configFileContent.startsWith("Type:JDCFG")) && configFileSource.getName().equalsIgnoreCase("commands.cfg")) {
        	configFileContent = configFileContent.substring(configFileContent.indexOf("\n") + 1); //Remove first line
        	sys.log("LDEXTCMDS", 0, "Extracting aliases and paths...");
        	//Split into aliases and paths
        	configFileContent.trim();
        	String[] splittedCFGFile = configFileContent.split("\n");
        	//Iterating over elements to split
        	for (String value : splittedCFGFile) {
        		extCommands.put(value.split(">")[0], new File(value.split(">")[1]
        				.replace("ROOT/commands/", VarLib.getCmdDir().getAbsolutePath() + VarLib.fsep)));
        		sys.log("LDEXTCMDS", 0, "Loaded value: " + value);
        	}
        } else {
        	sys.log("LDEXTCMDS", 2, "Config file format not correct. Cannot proceed loading external commands.");
        	return null;
        }
        sys.log("LDEXTCMDS", 0, "Successfully loaded config file for commands");
        
        //======================================LOAD COMMANDS===========================================
        //Verifying command files
        sys.log("LDEXTCMDS", 0, "Loading commands...");
        sys.log("LDEXTCMDS", 0, "Verifying command files...");
        if (extCommands.size() == commandSource.list().length) {
        	sys.log("LDEXTCMDS", 0, "Verification successful.");
        	File[] commands = new File[extCommands.size()];
        	int index = 0;
        	for (String key : extCommands.keySet()) {
        	    commands[index] = extCommands.get(key);
        	    index++;
        	}
        	//Reading command by command and creating temporary files containing the code
        	for (File command : commands) {
        		File commandTempPath = new File(VarLib.getRootDir().toString() + VarLib.fsep + "temp" + VarLib.fsep + command.getName().trim().replace(".jdexe", ".java"));
        		command = new File(command.toString().trim().replaceAll("\n", ""));
        		sys.log("LDEXTCMDS", 0, "Processing command: " +  command);
        		String commandContent = Files.readString(command.toPath());
        		if (commandContent.startsWith("Type:JDEXE")) {
        			commandContent = commandContent.substring(commandContent.indexOf("\n") + 1);
        			if (commandContent.startsWith("Format:JD_CF1.0")) {
        				commandContent = commandContent.substring(commandContent.indexOf("\n") + 1);
        				String code = commandContent.split("CODE:")[1];
        				//If file exists, delete it to not mess up content
        				if (Files.exists(commandTempPath.toPath(), LinkOption.NOFOLLOW_LINKS)) {
        					Files.delete(commandTempPath.toPath());
        				}
        				//Create the new file and write code to it
        				commandTempPath.createNewFile();
        				Files.writeString(commandTempPath.toPath(), code, StandardOpenOption.APPEND);
        			} else {
        				sys.log("LDEXTCMDS", 2, "Unknown command format. cannot proceed loading external command: " + command);
        			}
        		}
        	}
        } else {
        	sys.log("LDEXTCMDS", 2, "Could not verify commands. Cannot proceed loading external commands.");
        	return null;
        }
        
        //======================================COMPILE COMMANDS========================================
        sys.log("LDEXTCMDS", 0, "Compiling commands...");
        String javaCompilerLocation = "";
        
        if (VarLib.getOSName().equalsIgnoreCase("Windows")) {
        	javaCompilerLocation = VarLib.getJavaHome() + VarLib.fsep + "bin" + VarLib.fsep + "javac.exe";
        } else if (VarLib.getOSName().equalsIgnoreCase("Linux")) {
        	javaCompilerLocation = VarLib.getJavaHome() + VarLib.fsep + "bin" + VarLib.fsep + "javac";
        }
        
        //TODO Fix compiling not working
        //TODO klappt nicht alla fix das!!!
        
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
        	sys.log("LDEXTCMDS", 2, "No compiler found. JDOS needs JDK. cannot compile commands.");
        	return null;
        }
        int compilationResult = 1;
        
        //Compiling each file in JavaDOS/temp/
        for (String commandTempFile : VarLib.getTempDir().list()) {
        	compilationResult = compiler.run(null, null, null, VarLib.getTempDir() + VarLib.fsep + commandTempFile);
        	Files.copy(Paths.get(commandTempFile), VarLib.getBinDir().toPath(), StandardCopyOption.REPLACE_EXISTING);
        	//TODO Fix copy not working ^^^^
        }
        
        //Checking for errors
        if (compilationResult != 0) {
        	sys.log("LDEXTCMDS", 2, "LDCMDS: Failed to compile commands. Error code: " + compilationResult);
        } else {
        	sys.log("LDEXTCMDS", 0, "LDCMDS: Compilation completed successfully.");
        }
        
        Files.copy(Paths.get(VarLib.getRootDir() + VarLib.fsep + "temp" + VarLib.fsep + "*.class"), 
    			Paths.get(VarLib.getRootDir() + VarLib.fsep + "bin"), StandardCopyOption.REPLACE_EXISTING);
        
        sys.log("LDEXTCMDS", 0, "Compiling done.");
        sys.log("LDEXTCMDS", 0, "Deleting temporary files...");
        File[] allTempFiles = VarLib.getTempDir().listFiles();
        for (File file : allTempFiles) {
        	Files.delete(file.toPath());
        }
        if (VarLib.getTempDir().list().length == 0) { sys.log("LDEXTCMDS", 0, "Successfully deleted all temporary files."); }
        File[] binFiles = VarLib.getTempDir().listFiles();
        //TODO Assign compiled commands to Map extCommands
        if ((binFiles.length <= 0) && (!extCommands.isEmpty())) {
        	int index = 0;
        	for (String key : extCommands.keySet()) {
        	    //TODO Fix extCommands.put(key, binFiles[index]);
        	    index++;
        	}
        } else {
        	sys.log("LDEXTCMDS", 2, "No class files found to assign");
        }
    	sys.log("LDEXTCMDS", 0, "Assigned all commands.");
        
    	sys.log("LDEXTCMDS", 2, "WARNING: Beta command loader, not ready for use!");
		return extCommands;
	}
}
