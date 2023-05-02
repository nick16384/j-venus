package jfx.windowManager;

import engine.sys;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import libraries.VarLib;
import main.Main;

public class JFxWinloader extends Application {
	private TextArea cmdLine;
	//private String Main.wqtest = "";
	
	public void loadGUI(String[] args) {
		sys.log("JFX", 1, "Running JavaFX init()...");
		try { init(); } catch (Exception ex) { ex.printStackTrace(); }
		sys.log("JFX", 1, "Starting JavaFX application...");
		Application.launch(args);
	}
	
	public void stop() {
		sys.log("JFX", 1, "Stopping JavaFX application...");
		Platform.exit();
		sys.log("JFX", 1, "Stopping JavaFX application done.");
	}
	
	@Override
	public void start(Stage primaryStage) {
		sys.log("JFX", 1, "Loading JavaFX window :)");
		
		try {
			primaryStage.setTitle("J-Venus " + VarLib.getVersion());
			cmdLine = new TextArea();
			cmdLine.setWrapText(true);
			cmdLine.setText("SHELL INIT\n Another bit of text");
			cmdLine.setStyle("-fx-text-fill: green");
			cmdLine.setStyle("-fx-background-color: #aabbcc");
			cmdLine.setStyle("-fx-font: 12pt Consolas");
			cmdLine.relocate(0, 0);
			
			//TODO add Event Listener for Enter input
			//TODO change style to match a console

			StackPane root = new StackPane();
			root.getChildren().add(cmdLine);
			primaryStage.setScene(new Scene(root, 300, 250));
			primaryStage.show();
			
			new Thread(() -> {
				sys.log("JFXT:INTERNAL", 1, "JFXT internal thread started.");
				while (!Main.ThreadAllocMain.getWDT().isShutdownSignalActive()) {
					sys.log("JFXT:INTERNAL", 0, "Main.wqtest: \"" + Main.wqtest + "\"");
					try { Thread.sleep(500); } catch (InterruptedException ie) { ie.printStackTrace(); }
					if (Main.wqtest != null && !Main.wqtest.isBlank()) {
						cmdLine.setEditable(false);
						sys.log("New text inside Main.wqtest!");
						Platform.requestNextPulse();
						Platform.runLater(() -> { cmdLine.appendText(new String(Main.wqtest.toCharArray())); });
						Platform.requestNextPulse();
						Main.wqtest = "";
						cmdLine.setEditable(true);
					}
				}
			}, "JFXT:INTERNAL").start();
			sys.log("JFX", 1, "start(primaryStage) method end reached.");
			
		} catch (Exception ex) {
			for(StackTraceElement e: ex.getStackTrace())
		        System.err.println(e);
		}
	}
	
	public TextArea getCmdLine() {
		return this.cmdLine;
	}
	
	public void appendText(String text) {
		sys.log("JFX", 1, "Appending new text to JFXT Main.wqtest");
		Main.wqtest += text;
		
		if (cmdLine != null) {
			cmdLine.setEditable(false);
			sys.log("New text inside Main.wqtest!");
			Platform.requestNextPulse();
			Platform.runLater(() -> { cmdLine.appendText("Wuast"); });
			Platform.requestNextPulse();
			Main.wqtest = "";
			cmdLine.setEditable(true);
		} else {
			sys.log("DAS ÄH KANN NIX DIGGA");
		}
		
	}
	
	public void clearCmdLine() {
		cmdLine.clear();
	}
}
