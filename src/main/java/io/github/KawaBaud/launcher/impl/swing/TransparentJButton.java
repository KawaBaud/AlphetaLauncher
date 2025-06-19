
package io.github.KawaBaud.launcher.impl.swing;

import java.awt.Toolkit;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.UIManager;

public class TransparentJButton extends JButton {

	public TransparentJButton(String text) {
		super(text);
	}

	@Override
	public boolean isOpaque() {
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		String id = UIManager.getLookAndFeel().getID();

		boolean windows = Objects.equals(id, "Windows");
		Boolean winXpStyleThemeActive = (Boolean) defaultToolkit.getDesktopProperty("win.xpstyle.themeActive");
		boolean windowsClassic = UIManager.getLookAndFeel().getName().equals("Windows Classic");
		return windows && (!winXpStyleThemeActive || windowsClassic);
	}
}
