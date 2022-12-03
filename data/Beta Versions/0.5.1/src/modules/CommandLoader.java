package modules;

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

import main.Lib;

public class CommandLoader {
	private static boolean alreadyExecuted = false;
	public static Map<String, File> loadCommands() throws IOException {
		Map<String, File> extCommands = new HashMap<>();
		File configFileSource = new File(Lib.getDefaultDir() + Lib.fsep + "commands.cfg");
		File commandSource = new File(Lib.getDefaultDir() + Lib.fsep + "commands");
		
		if (alreadyExecuted) {
			Lib.logWrite("LDEXTCMDS", 2, "External commands already loaded. Cannot proceed.");
			return null;
		}
		
		alreadyExecuted = true;
		
		//TODO Fix all errors in this to support external commands!!!!
		//TODO go down for first error to fix: compile
		
		//======================================LOAD CONFIG FILE========================================
		//Read config file 'commands.cfg'
		Lib.logWrite("LDEXTCMDS", 0, "Reading config file...");
        String configFileContent = Files.readString(configFileSource.toPath());
        Lib.logWrite("LDEXTCMDS", 0, "Done. Read content:");
        Lib.logWrite("LDEXTCMDS", 0, configFileContent + "\n");
        //Loading config file
        if ((configFileContent.startsWith("Type:JDCFG")) && configFileSource.getName().equalsIgnoreCase("commands.cfg")) {
        	configFileContent = configFileContent.substring(configFileContent.indexOf("\n") + 1); //Remove first line
        	Lib.logWrite("LDEXTCMDS", 0, "Extracting aliases and paths...");
        	//Split into aliases and paths
        	configFileContent.trim();
        	String[] splittedCFGFile = configFileContent.split("\n");
        	//Iterating over elements to split
        	for (String value : splittedCFGFile) {
        		extCommands.put(value.split(">")[0], new File(value.split(">")[1]
        				.replace("ROOT/commands\\", Lib.getCmdDir().getAbsolutePath() + Lib.fsep)));
        		Lib.logWrite("LDEXTCMDS", 0, "Loaded value: " + value);
        	}
        } else {
        	Lib.logWrite("LDEXTCMDS", 2, "Config file format not correct. Cannot proceed loading external commands.");
        	return null;
        }
        Lib.logWrite("LDEXTCMDS", 0, "Successfully loaded config file for commands");
        
        //======================================LOAD COMMANDS===========================================
        //Verifying command files
        Lib.logWrite("LDEXTCMDS", 0, "Loading commands...");
        Lib.logWrite("LDEXTCMDS", 0, "Verifying command files...");
        if (extCommands.size() == commandSource.list().length) {
        	Lib.logWrite("LDEXTCMDS", 0, "Verification successful.");
        	File[] commands = new File[extCommands.size()];
        	int index = 0;
        	for (String key : extCommands.keySet()) {
        	    commands[index] = extCommands.get(key);
        	    index++;
        	}
        	//Reading command by command and creating temporary files containing the code
        	for (File command : commands) {
        		File commandTempPath = new File(Lib.getDefaultDir().toString() + Lib.fsep + "temp" + Lib.fsep + command.getName().trim().replace(".jdexe", ".java"));
        		command = new File(command.toString().trim().replaceAll("\n", ""));
        		Lib.logWrite("LDEXTCMDS", 0, "Processing command: " +  command);
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
        				Lib.logWrite("LDEXTCMDS", 2, "Unknown command format. cannot proceed loading external command: " + command);
        			}
        		}
        	}
        } else {
        	Lib.logWrite("LDEXTCMDS", 2, "Could not verify commands. Cannot proceed loading external commands.");
        	return null;
        }
        
        //======================================COMPILE COMMANDS========================================
        Lib.logWrite("LDEXTCMDS", 0, "Compiling commands...");
        String javaCompilerLocation = "";
        
        if (Lib.getOSName().equalsIgnoreCase("Windows")) {
        	javaCompilerLocation = Lib.getJavaHome() + Lib.fsep + "bin" + Lib.fsep + "javac.exe";
        } else if (Lib.getOSName().equalsIgnoreCase("Linux")) {
        	javaCompilerLocation = Lib.getJavaHome() + Lib.fsep + "bin" + Lib.fsep + "javac";
        }
        
        //TODO Fix compiling not working
        //TODO klappt nicht alla fix das!!!
        
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
        	Lib.logWrite("LDEXTCMDS", 2, "No compiler found. JDOS needs JDK. cannot compile commands.");
        	return null;
        }
        int compilationResult = 1;
        
        //Compiling each file in JavaDOS/temp/
        for (String commandTempFile : Lib.getTempDir().list()) {
        	compilationResult = compiler.run(null, null, null, Lib.getTempDir() + Lib.fsep + commandTempFile);
        	Files.copy(Paths.get(commandTempFile), Lib.getBinDir().toPath(), StandardCopyOption.REPLACE_EXISTING);
        	//TODO Fix copy not working ^^^^
        }
        
        //Checking for errors
        if (compilationResult != 0) {
        	Lib.logWrite("LDEXTCMDS", 2, "LDCMDS: Failed to compile commands. Error code: " + compilationResult);
        } else {
        	Lib.logWrite("LDEXTCMDS", 0, "LDCMDS: Compilation completed successfully.");
        }
        
        Files.copy(Paths.get(Lib.getDefaultDir() + Lib.fsep + "temp" + Lib.fsep + "*.class"), 
    			Paths.get(Lib.getDefaultDir() + Lib.fsep + "bin"), StandardCopyOption.REPLACE_EXISTING);
        
        Lib.logWrite("LDEXTCMDS", 0, "Compiling done.");
        Lib.logWrite("LDEXTCMDS", 0, "Deleting temporary files...");
        File[] allTempFiles = Lib.getTempDir().listFiles();
        for (File file : allTempFiles) {
        	Files.delete(file.toPath());
        }
        if (Lib.getTempDir().list().length == 0) { Lib.logWrite("LDEXTCMDS", 0, "Successfully deleted all temporary files."); }
        File[] binFiles = Lib.getTempDir().listFiles();
        //TODO Assign compiled commands to Map extCommands
        if ((binFiles.length <= 0) && (!extCommands.isEmpty())) {
        	int index = 0;
        	for (String key : extCommands.keySet()) {
        	    //TODO Fix extCommands.put(key, binFiles[index]);
        	    index++;
        	}
        } else {
        	Lib.logWrite("LDEXTCMDS", 2, "No class files found to assign");
        }
    	Lib.logWrite("LDEXTCMDS", 0, "Assigned all commands.");
        
    	Lib.logWrite("LDEXTCMDS", 2, "WARNING: Beta command loader, not ready for use!");
		return extCommands;
	}
}
