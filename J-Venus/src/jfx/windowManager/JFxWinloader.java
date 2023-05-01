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

public class JFxWinloader extends Application {
	private TextArea cmdLine;
	
	public void loadGUI(String[] args) {
		Application.launch(args);
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
			primaryStage.hide();
			primaryStage.show();
		} catch (Exception ex) {
			for(StackTraceElement e: ex.getStackTrace())
		        System.err.println(e);
		}
	}
	
	public TextArea getCmdLine() {
		return this.cmdLine;
	}
	
	public void setText(String text) {
		/*if (cmdLine != null) {
			sys.log("Trying to add new text I guess...");
			Platform.runLater(() -> {
				sys.log("Adding text lol!!!!!");
				cmdLine.appendText(text);
			});
		}*/
		if (cmdLine == null)
			return;
		cmdLine.appendText("show now");

        new Thread(new Runnable() {

            public void run() {
                for (int i = 0; i < 2000; i++) {

                    //this is just for demo purposes
                    try { Thread.sleep(1000); } catch (Exception e) {}

                    // we are not in the event thread currently so we should not update the UI here
                    // this is a good place to do some slow, background loading, e.g. load from a server or from a file system 

                    Platform.runLater(new Runnable() {
                        public void run() {
                            // we are now back in the EventThread and can update the GUI
                            cmdLine.appendText(text);
                        }
                    });
                }
            }
        }).start();
	}
}
