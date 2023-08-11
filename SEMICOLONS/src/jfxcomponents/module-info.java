

module jfx.windowManager {
	requires javafx.fxml;
	requires javafx.controls;
	requires java.desktop;
	requires java.compiler;
	requires org.fxmisc.richtext;
	requires org.fxmisc.flowless;

	opens jfxcomponents to javafx.graphics, java.desktop, javax.tools, org.fxmisc.richtext;

	exports jfxcomponents;
}