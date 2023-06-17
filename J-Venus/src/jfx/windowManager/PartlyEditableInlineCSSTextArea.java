package jfx.windowManager;

import org.fxmisc.richtext.InlineCssTextArea;

import engine.sys;

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
	
	public PartlyEditableInlineCSSTextArea(String title) {
		super(title);
		readOnlyToIndex = 0;
		CheckOverrideThread = new Thread(() -> {
			while (true) {
				try { Thread.sleep(50); } catch (InterruptedException ie) { ie.printStackTrace(); }
				sys.log("PartlyEditable text area scan");
				// TODO check if a part has been edited and revert
				// TODO add this to main class, otherwise there will be no effect
				// FIXME message does not appear (thread is somehow still inactive)
				
				String textUntilWritable = this.getText().substring(0, readOnlyToIndex);
				
				if (!this.getText().startsWith(currentShellText)) {
					this.clear();
					this.appendText(lastWrittenText);
				}
			}
		});
		
		CheckOverrideThread.start();
	}
	
	@Override
	public void appendText(String text) {
		lastWrittenText = text;
		currentShellText += text;
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
}
