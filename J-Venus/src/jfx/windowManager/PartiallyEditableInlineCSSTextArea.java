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

public class PartiallyEditableInlineCSSTextArea extends InlineCssTextArea {
	private Thread CheckOverrideThread;
	private int readOnlyToIndex;
	
	private String lastWrittenText;
	private String currentShellText;
	private String textUntilWritable;
	
	// Saves the style for every single character
	private Map<Integer, String> styles;
	
	// If this is true, the entire text area is temporarily editable
	private boolean inhibitOverrideCheck;
	
	public PartiallyEditableInlineCSSTextArea(String title) {
		super(title);
		readOnlyToIndex = 0;
		lastWrittenText = "";
		currentShellText = "";
		textUntilWritable = "";
		inhibitOverrideCheck = false;
		
		
		// This thread checks, whether the user has overridden read-only text and replaces it again with the original
		CheckOverrideThread = new Thread(() -> {
			while (!Main.ThreadAllocMain.isShutdownSignalActive()) {
				try { Thread.sleep(100); } catch (InterruptedException ie) { ie.printStackTrace(); }
				
				if (this.getText().length() < readOnlyToIndex && !inhibitOverrideCheck) {
					Platform.runLater(() -> {
						try {
							Map<Integer, String> styles = new HashMap<Integer, String>();
							styles = saveStyle();
							
							// TODO Maybe replace last line text only to remove flickering
							// TODO and only replace all, if everything is affected
							
							sys.log("Read-only text was affected. Reverting.");
							this.clear();
							
							super.appendText(textUntilWritable);
							
							reapplyStyle(styles);
							
							this.displaceCaret(readOnlyToIndex);
						} catch (Exception ex) { ex.printStackTrace(); }
					});
				}
			}
		});
		CheckOverrideThread.start();
	}
	
	@Override
	public void appendText(String text) {
		enableInhibitOverrideCheck();
		lastWrittenText = text;
		currentShellText += text;
		super.appendText(text);
		disableInhibitOverrideCheck();
	}
	
	@Override
	public void clear() {
		currentShellText = "";
		super.clear();
	}
	
	public String getLastWrittenText() {
		return lastWrittenText;
	}
	
	public String getCurrentShellTextWithoutUserInput() {
		return currentShellText;
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
			//updateReadOnlyToIndex();
			try { textUntilWritable = currentShellText.substring(0, readOnlyToIndex - 1); }
			catch (Exception ex) { textUntilWritable = currentShellText + " "; }
		} else {
			sys.log("CLASS:CSSTextArea", 3, "New read-only length index " + newReadOnlyToIndex + " out of bounds.");
		}
	}
	
	public void enableInhibitOverrideCheck() {
		inhibitOverrideCheck = true;
	}
	
	public void disableInhibitOverrideCheck() {
		inhibitOverrideCheck = false;
	}
	
	public boolean isInhibitOverrideCheck() {
		return inhibitOverrideCheck;
	}
	
	// FIXME saveStyle() and reapplyStyle() might be very resource intensive. Maybe change or remove later.
	private Map<Integer, String> saveStyle() {
		Map<Integer, String> styles = new HashMap<Integer, String>();
		for (int i = 0; i < this.getText().length(); i++) {
			styles.put(i, this.getStyleAtPosition(i));
		}
		return styles;
	}
	
	private void reapplyStyle(Map<Integer, String> styles) {
		for (int i = 0; i < styles.size() - 1; i++) {
			// IDK why it works with ternary operators, but I won't touch it again, because it works.
			this.setStyle(i > 0 ? i - 1 : i,
					i < styles.size() - 1 ? i + 1 : i,
							styles.get(i));
		}
	}
}
