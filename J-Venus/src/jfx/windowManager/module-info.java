/**
 * This module-info file is required in order for JavaFX to work.
 *  I have no clue what it does, but if it doesn't exist, the following
 *  error message appears: "Error: JavaFX runtime components are missing, and are required to run this application"
 * @author nick16384
 *
 */

module jfx.windowManager {
	requires javafx.fxml;
	requires javafx.controls;
	requires java.desktop;
	requires java.compiler;

	opens jfx.windowManager to javafx.graphics, java.desktop, javax.tools;

	exports jfx.windowManager;
}