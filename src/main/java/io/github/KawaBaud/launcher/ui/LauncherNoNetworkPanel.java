
package io.github.KawaBaud.launcher.ui;

import io.github.KawaBaud.launcher.Launcher;
import io.github.KawaBaud.launcher.LauncherConfig;
import io.github.KawaBaud.launcher.LauncherLanguage;
import io.github.KawaBaud.launcher.impl.UTF8ResourceBundle;
import io.github.KawaBaud.launcher.impl.swing.JNotchPanel;
import io.github.KawaBaud.launcher.impl.swing.TransparentJButton;
import io.github.KawaBaud.launcher.minecraft.MinecraftUpdate;
import io.github.KawaBaud.launcher.util.LauncherLanguageUtils;
import io.github.KawaBaud.launcher.util.LauncherUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class LauncherNoNetworkPanel extends JNotchPanel implements ActionListener {

	private static LauncherNoNetworkPanel instance;
	private final JLabel errorLabel;
	private final JLabel playOnlineLabel;
	private final JButton playOfflineButton;
	private final JButton retryButton;
	private final String errorMessage;
	private final transient Object[] errorMessageArgs;

	public LauncherNoNetworkPanel(String key, Object... args) {
		super(true);

		setInstance(this);

		this.errorMessage = key;
		this.errorMessageArgs = args;
		this.errorLabel = new JLabel((String) null, SwingConstants.CENTER);
		this.playOnlineLabel = new JLabel(LauncherLanguageUtils.getLNPPKeys()[5], SwingConstants.LEFT);
		this.playOfflineButton = new TransparentJButton(LauncherLanguageUtils.getLNPPKeys()[6]);
		this.retryButton = new TransparentJButton(LauncherLanguageUtils.getLNPPKeys()[7]);

		this.setLayout(this.getGroupLayout());

		this.errorLabel.setText(this.errorMessage);
		this.errorLabel.setFont(this.getFont().deriveFont(Font.ITALIC, 16F));
		this.errorLabel.setForeground(Color.RED.darker());
		this.playOfflineButton.setEnabled(MinecraftUpdate.isGameCached());
		this.playOnlineLabel.setVisible(!MinecraftUpdate.isGameCached());

		this.playOfflineButton.addActionListener(this);
		this.retryButton.addActionListener(this);

		String selectedLanguage = (String) LauncherConfig.get(0);
		UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
		this.updateComponentTexts(Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());
	}

	public static LauncherNoNetworkPanel getInstance() {
		return instance;
	}

	private static void setInstance(LauncherNoNetworkPanel lnnp) {
		instance = lnnp;
	}

	public void updateComponentTexts(UTF8ResourceBundle bundle) {
		LauncherUtils.setComponentText(bundle, this.errorLabel, this.errorMessage, this.errorMessageArgs);
		LauncherUtils.setComponentText(bundle, this.playOnlineLabel, LauncherLanguageUtils.getLNPPKeys()[5]);
		LauncherUtils.setComponentText(bundle, this.playOfflineButton, LauncherLanguageUtils.getLNPPKeys()[6]);
		LauncherUtils.setComponentText(bundle, this.retryButton, LauncherLanguageUtils.getLNPPKeys()[7]);
	}

	private LayoutManager getGroupLayout() {
		int width = 0;

		JButton[] buttons = new JButton[] { this.playOfflineButton, this.retryButton };
		for (JButton button : buttons) {
			width = Math.max(width, button.getPreferredSize().width);
		}

		GroupLayout gl = new GroupLayout(this);
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		gl.setHorizontalGroup(gl.createParallelGroup()
				.addComponent(this.errorLabel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(gl.createSequentialGroup().addComponent(this.playOnlineLabel, 0, GroupLayout.PREFERRED_SIZE,
						Short.MAX_VALUE))
				.addGroup(gl.createSequentialGroup().addComponent(this.playOfflineButton, 0, width, Short.MAX_VALUE)
						.addComponent(this.retryButton, 0, width, Short.MAX_VALUE)));
		gl.setVerticalGroup(
				gl.createSequentialGroup().addComponent(this.errorLabel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(this.playOnlineLabel))
						.addGroup(gl.createParallelGroup(Alignment.CENTER).addComponent(this.playOfflineButton)
								.addComponent(this.retryButton)));
		return gl;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (Objects.equals(source, this.playOfflineButton)) {
			Launcher.launchMinecraft(null, null, null, LauncherUtils.isNotPremium());
		}
		if (Objects.equals(source, this.retryButton)) {
			LauncherUtils.swapContainers(this.getParent(), new YggdrasilAuthPanel());
		}
	}
}
