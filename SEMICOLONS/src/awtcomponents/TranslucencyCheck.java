package awtcomponents;

import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;

/**
 * Checks whether the OS support transparent JFrames or not
 * @author nick16384
 *
 */

public class TranslucencyCheck {
	public static boolean isTranslucencySupported() {
		GraphicsEnvironment ge =
				GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		return gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT);
	}
}
