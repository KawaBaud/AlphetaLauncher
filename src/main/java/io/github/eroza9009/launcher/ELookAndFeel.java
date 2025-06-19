
package io.github.KawaBaud.launcher;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ELookAndFeel {
	GTK("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"), AQUA("com.apple.laf.AquaLookAndFeel"),
	WINDOWS("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

	private static final Logger LOGGER;

	static {
		LOGGER = LoggerFactory.getLogger(ELookAndFeel.class);
	}

	@Getter
	private final String className;

	ELookAndFeel(String className) {
		this.className = className;
	}

	public static void setLookAndFeel() {
		try {
			if (EPlatform.isLinux()) {
				UIManager.setLookAndFeel(GTK.getClassName());
			}
			if (EPlatform.isMacOS()) {
				UIManager.setLookAndFeel(AQUA.getClassName());
			}
			if (EPlatform.isWindows()) {
				UIManager.setLookAndFeel(WINDOWS.getClassName());
			}
		} catch (ClassNotFoundException cnfe) {
			LOGGER.error("Cannot find {} on '{}'", cnfe.getMessage(), EPlatform.OS_NAME, cnfe);
		} catch (InstantiationException ie) {
			LOGGER.error("Cannot instantiate {} on '{}'", ie.getMessage(), EPlatform.OS_NAME, ie);
		} catch (IllegalAccessException iae) {
			LOGGER.error("Cannot access {} on '{}'", iae.getMessage(), EPlatform.OS_NAME, iae);
		} catch (UnsupportedLookAndFeelException ulafe) {
			LOGGER.error("{} unsupported on '{}'", ulafe.getMessage(), EPlatform.OS_NAME, ulafe);
		}
	}
}
