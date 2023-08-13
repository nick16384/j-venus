package jfxcomponents;

import java.util.Map;
import java.util.HashMap;
import org.fxmisc.richtext.InlineCssTextArea;

import engine.InfoType;
import engine.sys;
import javafx.application.Platform;
import main.Main;

/**
 * A normal InlineCssTextArea with the additional feature of being able to partly make the text read-only.
 * @author nick16384
 *
 */

public class PartiallyEditableInlineCSSTextArea extends InlineCssTextArea {
	private int readOnlyToIndex;
	
	private String lastWrittenText;
	private String currentShellText;
	
	public PartiallyEditableInlineCSSTextArea(String title) {
		super(title);
		readOnlyToIndex = 0;
		lastWrittenText = "";
		currentShellText = "";
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
		} else {
			sys.log("CLASS:CSSTextArea", InfoType.ERR, "New read-only length index " + newReadOnlyToIndex + " out of bounds.");
		}
	}
}
