package jfx.windowManager;

import java.util.Map;
import java.util.HashMap;
import org.fxmisc.richtext.InlineCssTextArea;

import engine.sys;
import javafx.application.Platform;
import main.Main;

/**
 * A normal InlineCssTextArea with the additional feature of being able to partly make the text read-only.
 * @author nick16384
 *
 */

public class PartlyEditableInlineCSSTextArea extends InlineCssTextArea {
	private Thread CheckOverrideThread;
	private int readOnlyToIndex;
	
	private String lastWrittenText;
	private String currentShellText;
	private String textUntilWritable;
	
	public PartlyEditableInlineCSSTextArea(String title) {
		super(title);
		readOnlyToIndex = 0;
		lastWrittenText = "";
		currentShellText = "";
		textUntilWritable = "";
		
		
		// This thread checks, whether the user has overridden read-only text and replaces it again with the original
		CheckOverrideThread = new Thread(() -> {
			while (!Main.ThreadAllocMain.isShutdownSignalActive()) {
				try { Thread.sleep(500); } catch (InterruptedException ie) { ie.printStackTrace(); }
				sys.log("PartlyEditable text area scan");
				// TODO check if a part has been edited and revert
				// TODO add this to main class, otherwise there will be no effect
				// FIXME message does not appear (thread is somehow still inactive)
				
				if (!this.getText().startsWith(textUntilWritable)) {
					Platform.runLater(() -> {
						Map<Integer, String> styles = new HashMap<Integer, String>();
						styles = saveStyle();
						
						sys.log("Read-only text was affected. Reverting.");
						this.clear();
						this.appendText(textUntilWritable);
						
						reapplyStyle(styles);
					});
				}
			}
		});
		CheckOverrideThread.start();
	}
	
	@Override
	public void appendText(String text) {
		lastWrittenText = text;
		currentShellText += text;
		updateReadOnlyToIndex();
		textUntilWritable = currentShellText.substring(0, readOnlyToIndex);
		super.appendText(text);
	}
	
	@Override
	public void clear() {
		currentShellText = "";
		super.clear();
	}
	
	public String getLastWrittenText() {
		return lastWrittenText;
	}
	
	private void updateReadOnlyToIndex() {
		if (readOnlyToIndex > currentShellText.length())
			readOnlyToIndex = currentShellText.length();
	}
	
	public int getReadOnlyToIndex() {
		return readOnlyToIndex;
	}
	
	public void setReadOnlyTo(int newReadOnlyToIndex) {
		if (newReadOnlyToIndex > -1
				&& newReadOnlyToIndex <= this.getLength()) {
			readOnlyToIndex = newReadOnlyToIndex;
		} else {
			sys.log("CLASS:CSSTextArea", 3, "new read-only length index " + newReadOnlyToIndex + " out of bounds.");
		}
	}
	
	// FIXME saveStyle() and reapplyStyle() might be very resource intensive. Maybe change or remove later.
	private Map<Integer, String> saveStyle() {
		Map<Integer, String> styles = new HashMap<Integer, String>();
		for (int i = 0; i < this.getText().length(); i++) {
			sys.log("Style saving progress: " + i + "/" + (this.getText().length() - 1));
			styles.put(i, this.getStyleAtPosition(i));
		}
		return styles;
	}
	
	private void reapplyStyle(Map<Integer, String> styles) {
		for (int i = 0; i < this.getText().length() - 1; i++) {
			sys.log("Style loading progress: " + i + "/" + (this.getText().length() - 1));
			this.setStyle(i > 0 ? i - 1 : i, i, styles.get(i));
		}
	}
}
