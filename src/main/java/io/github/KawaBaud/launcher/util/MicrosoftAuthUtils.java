
package io.github.KawaBaud.launcher.util;

import io.github.KawaBaud.launcher.LauncherConfig;
import io.github.KawaBaud.launcher.auth.MicrosoftAuth;
import io.github.KawaBaud.launcher.auth.MicrosoftAuthTask;
import io.github.KawaBaud.launcher.auth.MicrosoftAuthWorker;
import io.github.KawaBaud.launcher.ui.LauncherPanel;
import io.github.KawaBaud.launcher.ui.MicrosoftAuthPanel;
import java.util.Objects;
import org.json.JSONObject;

public final class MicrosoftAuthUtils {

	public static final String AZURE_CLIENT_ID;

	static {
		AZURE_CLIENT_ID = "e1a4bd01-2c5f-4be0-8e6a-84d71929703b";
	}

	private MicrosoftAuthUtils() {
	}

	public static void executeMicrosoftAuthWorker(String clientId) {
		Objects.requireNonNull(clientId, "clientId cannot be null");
		if (clientId.isEmpty()) {
			throw new IllegalArgumentException("clientId cannot be empty");
		}

		JSONObject consumersDeviceCode = MicrosoftAuth.acquireDeviceCode(clientId);
		if (Objects.isNull(consumersDeviceCode)) {
			return;
		}
		String[] deviceCodeResponse = MicrosoftAuthTask.getDeviceCodeResponse(consumersDeviceCode);

		LauncherUtils.swapContainers(LauncherPanel.getInstance(),
				new MicrosoftAuthPanel(deviceCodeResponse[0], deviceCodeResponse[2], deviceCodeResponse[3]));

		new MicrosoftAuthWorker(clientId, deviceCodeResponse[1], deviceCodeResponse[3], deviceCodeResponse[4])
				.execute();
	}

	public static boolean isAccessTokenExpired() {
		String refreshToken = (String) LauncherConfig.get(9);
		if (Objects.isNull(refreshToken)) {
			return false;
		}
		if (refreshToken.isEmpty()) {
			return false;
		}

		long currentTimeSecs = System.currentTimeMillis() / 1000L;
		String accessTokenExpiresIn = (String) LauncherConfig.get(8);
		long accessTokenExpiresInSecs = Long.parseLong(accessTokenExpiresIn) / 1000L;
		long expiresIn = accessTokenExpiresInSecs - currentTimeSecs;
		return expiresIn <= 900;
	}

	public static void refreshAccessToken() {
		String refreshToken = (String) LauncherConfig.get(9);

		JSONObject consumersToken = MicrosoftAuth.refreshToken(AZURE_CLIENT_ID, refreshToken);
		if (Objects.isNull(consumersToken)) {
			return;
		}
		String[] tokenResponse = MicrosoftAuthTask.getRefreshTokenResponse(consumersToken);

		JSONObject userAuthenticate = MicrosoftAuth.acquireXBLToken(tokenResponse[0]);
		if (Objects.isNull(userAuthenticate)) {
			return;
		}
		String[] xblTokenResponse = MicrosoftAuthTask.getXBLTokenResponse(userAuthenticate);

		JSONObject xstsAuthorize = MicrosoftAuth.acquireXSTSToken(xblTokenResponse[1]);
		if (Objects.isNull(xstsAuthorize)) {
			return;
		}
		String xstsTokenResponse = MicrosoftAuthTask.getXSTSTokenResponse(xstsAuthorize);

		JSONObject authenticateLoginWithXbox = MicrosoftAuth.acquireAccessToken(xblTokenResponse[0], xstsTokenResponse);
		if (Objects.isNull(authenticateLoginWithXbox)) {
			return;
		}

		String[] accessTokenResponse = MicrosoftAuthTask.getAccessTokenResponse(authenticateLoginWithXbox);
		LauncherConfig.set(7, accessTokenResponse[0]);
		LauncherConfig.set(8, accessTokenResponse[1]);
		LauncherConfig.set(9, tokenResponse[1]);
		LauncherConfig.saveConfig();
	}
}
