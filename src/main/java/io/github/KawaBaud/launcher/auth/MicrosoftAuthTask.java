
package io.github.KawaBaud.launcher.auth;

import io.github.KawaBaud.launcher.Launcher;
import io.github.KawaBaud.launcher.LauncherConfig;
import io.github.KawaBaud.launcher.ui.MicrosoftAuthPanel;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import org.json.JSONArray;
import org.json.JSONObject;

public class MicrosoftAuthTask implements Runnable {

	private final ScheduledExecutorService service;
	private final String clientId;
	private final String deviceCode;

	public MicrosoftAuthTask(ScheduledExecutorService service, String clientId, String deviceCode) {
		this.service = service;
		this.clientId = clientId;
		this.deviceCode = deviceCode;
	}

	public static String[] getDeviceCodeResponse(JSONObject object) {
		String userCode = object.getString("user_code");
		String deviceCode = object.getString("device_code");
		String verificationUri = object.getString("verification_uri");
		String expiresIn = String.valueOf(object.getInt("expires_in"));
		String interval = String.valueOf(object.getInt("interval"));
		return new String[] { userCode, deviceCode, verificationUri, expiresIn, interval };
	}

	private static String[] getTokenResponse(JSONObject object) {
		MicrosoftAuthPanel.getInstance().getEnterCodeInBrowserLabel().setVisible(false);
		MicrosoftAuthPanel.getInstance().getUserCodeLabel().setVisible(false);
		MicrosoftAuthPanel.getInstance().getExpiresInProgressBar().setIndeterminate(true);
		MicrosoftAuthPanel.getInstance().getOpenInBrowserButton().setVisible(false);

		String accessToken = object.getString("access_token");
		String refreshToken = object.getString("refresh_token");
		return new String[] { accessToken, refreshToken };
	}

	public static String[] getRefreshTokenResponse(JSONObject object) {
		String accessToken = object.getString("access_token");
		String refreshToken = object.getString("refresh_token");
		return new String[] { accessToken, refreshToken };
	}

	public static String[] getXBLTokenResponse(JSONObject object) {
		JSONObject displayClaims = object.getJSONObject("DisplayClaims");
		JSONArray xui = displayClaims.getJSONArray("xui");

		String uhs = xui.getJSONObject(0).getString("uhs");
		String token = object.getString("Token");
		return new String[] { uhs, token };
	}

	public static String getXSTSTokenResponse(JSONObject object) {
		return object.getString("Token");
	}

	public static String[] getAccessTokenResponse(JSONObject object) {
		String accessToken = object.getString("access_token");
		int expiresIn = object.getInt("expires_in");
		long expiresInMillis = System.currentTimeMillis() + (expiresIn * 1000L);
		return new String[] { accessToken, String.valueOf(expiresInMillis) };
	}

	private static boolean isItemNameEqualToGameMinecraft(JSONObject object) {
		JSONArray items = object.getJSONArray("items");
		if (!items.isEmpty()) {
			for (Object item : items) {
				String name = ((JSONObject) item).getString("name");
				if (Objects.equals(name, "game_minecraft")) {
					return true;
				}
			}
		}
		return false;
	}

	private static String[] getMinecraftProfileResponse(JSONObject object) {
		String id = object.getString("id");
		String name = object.getString("name");
		return new String[] { id, name };
	}

	@Override
	public void run() {
		if (!MicrosoftAuthPanel.getInstance().isShowing()) {
			service.shutdown();
		}

		JSONObject consumersToken = MicrosoftAuth.acquireToken(clientId, deviceCode);
		if (Objects.isNull(consumersToken)) {
			return;
		}

		String[] tokenResponse = getTokenResponse(consumersToken);
		if (tokenResponse.length == 0) {
			return;
		}

		JSONObject userAuthenticate = MicrosoftAuth.acquireXBLToken(tokenResponse[0]);
		if (Objects.isNull(userAuthenticate)) {
			return;
		}

		String[] xblTokenResponse = getXBLTokenResponse(userAuthenticate);
		if (xblTokenResponse.length == 0) {
			return;
		}

		JSONObject xstsAuthorize = MicrosoftAuth.acquireXSTSToken(xblTokenResponse[1]);
		if (Objects.isNull(xstsAuthorize)) {
			return;
		}

		String xstsTokenResponse = getXSTSTokenResponse(xstsAuthorize);
		if (Objects.isNull(xstsTokenResponse)) {
			return;
		}

		JSONObject authenticateLoginWithXbox = MicrosoftAuth.acquireAccessToken(xblTokenResponse[0], xstsTokenResponse);
		if (Objects.isNull(authenticateLoginWithXbox)) {
			return;
		}

		String[] accessTokenResponse = getAccessTokenResponse(authenticateLoginWithXbox);
		LauncherConfig.set(7, accessTokenResponse[0]);
		LauncherConfig.set(8, accessTokenResponse[1]);
		LauncherConfig.set(9, tokenResponse[1]);

		JSONObject entitlementsMcStore = MicrosoftAuth.checkEntitlementsMcStore(accessTokenResponse[0]);
		if (Objects.isNull(entitlementsMcStore)) {
			return;
		}

		boolean itemNameEqualToGameMinecraft = isItemNameEqualToGameMinecraft(entitlementsMcStore);
		if (!itemNameEqualToGameMinecraft) {
			LauncherConfig.set(5, null);
			LauncherConfig.set(6, null);
			LauncherConfig.saveConfig();

			Launcher.launchMinecraft(null, accessTokenResponse[0], null, true);
		} else {
			JSONObject minecraftProfile = MicrosoftAuth.acquireMinecraftProfile(accessTokenResponse[0]);
			if (Objects.isNull(minecraftProfile)) {
				return;
			}

			String[] minecraftProfileResponse = getMinecraftProfileResponse(minecraftProfile);
			LauncherConfig.set(5, minecraftProfileResponse[0]);
			LauncherConfig.set(6, minecraftProfileResponse[1]);
			LauncherConfig.saveConfig();

			Launcher.launchMinecraft(minecraftProfileResponse[1], accessTokenResponse[0], minecraftProfileResponse[0],
					false);
		}
		service.shutdown();
	}
}
