
package io.github.KawaBaud.launcher;

import io.github.KawaBaud.launcher.ui.LauncherFrame;
import io.github.KawaBaud.launcher.ui.LauncherPanel;
import io.github.KawaBaud.launcher.ui.MinecraftAppletWrapper;
import io.github.KawaBaud.launcher.util.MicrosoftAuthUtils;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import javax.swing.SwingUtilities;

public class Launcher {

	public static void main(String... args) {
		ELookAndFeel.setLookAndFeel();

		LauncherConfig.loadConfig();
		LauncherLanguage.loadLanguage("messages",
				Objects.nonNull(LauncherConfig.get(0)) ? (String) LauncherConfig.get(0) : ELanguage.USER_LANGUAGE);

		SwingUtilities.invokeLater(() -> new LauncherFrame().setVisible(true));

		boolean microsoftAccessTokenExpired = MicrosoftAuthUtils.isAccessTokenExpired();
		if (microsoftAccessTokenExpired) {
			MicrosoftAuthUtils.refreshAccessToken();
		}
	}

	public static void launchMinecraft(String username, String accessToken, String profileId, boolean demo) {
		if (Objects.isNull(username) || username.isEmpty()) {
			username = String.format("Player%s", System.currentTimeMillis() % 1000L);
		}
		if (Objects.isNull(profileId) || profileId.isEmpty()) {
			profileId = UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8)).toString().replace("-", "");
		}

		String sessionId = new StringBuilder().append("token:").append(accessToken).append(":").append(profileId)
				.toString();

		MinecraftAppletWrapper maw = new MinecraftAppletWrapper(username, sessionId, demo);
		maw.init();

		LauncherFrame.getInstance().remove(LauncherPanel.getInstance());
		LauncherFrame.getInstance().setContentPane(maw);
		LauncherFrame.getInstance().revalidate();
		LauncherFrame.getInstance().repaint();

		maw.start();
		LauncherFrame.getInstance().setTitle("Minecraft");
	}
}
