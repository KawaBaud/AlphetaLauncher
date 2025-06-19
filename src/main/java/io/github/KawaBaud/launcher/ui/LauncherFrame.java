
package io.github.KawaBaud.launcher.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;
import java.util.Optional;
import javax.swing.JFrame;

public class LauncherFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static LauncherFrame instance;

	public LauncherFrame() {
		super();

		setInstance(this);

		URL iconUrl = Optional.ofNullable(LauncherFrame.class.getClassLoader().getResource("assets/favicon-32x32.png"))
				.orElseThrow(() -> new NullPointerException("iconUrl cannot be null"));
		this.setIconImage(this.getToolkit().getImage(iconUrl));

		this.setLayout(new BorderLayout());

		this.setContentPane(new LauncherPanel());

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(640 + 16, 480 + 39));
		this.setPreferredSize(new Dimension(854 + 16, 480 + 39));

		this.pack();

		this.setLocationRelativeTo(null);
		this.setResizable(true);
	}

	public static LauncherFrame getInstance() {
		return instance;
	}

	private static void setInstance(LauncherFrame lf) {
		instance = lf;
	}

	@Override
	public String getTitle() {
		return "Alpheta Launcher";
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		this.getContentPane().setFont(font);
	}
}
