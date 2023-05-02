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
	//private TextArea Main.cmdLine;
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
			Main.cmdLine = new TextArea();
			Main.cmdLine.setWrapText(true);
			Main.cmdLine.setText("SHELL INIT\n Another bit of text");
			Main.cmdLine.setStyle("-fx-text-fill: green");
			Main.cmdLine.setStyle("-fx-background-color: #aabbcc");
			Main.cmdLine.setStyle("-fx-font: 12pt Consolas");
			Main.cmdLine.relocate(0, 0);
			
			//TODO add Event Listener for Enter input
			//TODO change style to match a console

			StackPane root = new StackPane();
			root.getChildren().add(Main.cmdLine);
			primaryStage.setScene(new Scene(root, 900, 550));
			primaryStage.show();
			
			sys.log("JFX", 1, "start(primaryStage) method end reached.");
			
		} catch (Exception ex) {
			for(StackTraceElement e: ex.getStackTrace())
		        System.err.println(e);
		}
	}
	
	public TextArea getCmdLine() {
		return Main.cmdLine;
	}
	
	public void appendText(String text) {
		sys.log("JFX", 1, "Appending new text to cmdLine. Length: " + text.length());
		
		if (Main.cmdLine != null) {
			Main.cmdLine.setEditable(false);
			Platform.requestNextPulse();
			// Enqueue cmdLine write in JavaFX thread
			Platform.runLater(() -> {
				 Main.cmdLine.appendText(text);
			});
			Platform.requestNextPulse();
			Main.cmdLine.setEditable(true);
		} else {
			sys.log("JFX", 3, "Appending text not possible, because Main.cmdLine is null.");
		}
	}
	
	public void clearCmdLine() {
		Main.cmdLine.clear();
	}
}
