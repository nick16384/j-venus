package jfx.windowManager;

import engine.sys;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import libraries.VarLib;

public class JFxWinloader extends Application {
	private TextArea cmdLine;
	
	public void loadGUI(String[] args) {
		launch(args);
	}
	
	public void stop() {
		sys.log("JFX", 1, "Stopping JavaFX application...");
		Platform.exit();
		sys.log("JFX", 1, "Stopping JavaFX application done.");
	}
	
	@Override
	public void start(Stage primaryStage) {
		sys.log("Loading JavaFX window :)");
		
		try {
			primaryStage.setTitle("J-Venus " + VarLib.getVersion());
			cmdLine = new TextArea();
			cmdLine.setText("SHELL INIT");
			cmdLine.setStyle("-fx-text-fill: green");
			cmdLine.setStyle("-fx-background-color: #aabbcc");
			cmdLine.setStyle("-fx-font: 12pt Consolas");
			
			//TODO add Event Listener for Enter input
			//TODO change style to match a console

			StackPane root = new StackPane();
			root.getChildren().add(cmdLine);
			primaryStage.setScene(new Scene(root, 300, 250));
			primaryStage.show();
		} catch (Exception ex) {
			for(StackTraceElement e: ex.getStackTrace())
		        System.err.println(e);
		}
	}
	
	public TextArea getCmdLine() {
		return this.cmdLine;
	}
}
