
package io.github.KawaBaud.launcher.auth;

import io.github.KawaBaud.launcher.ui.LauncherNoNetworkPanel;
import io.github.KawaBaud.launcher.ui.LauncherPanel;
import io.github.KawaBaud.launcher.ui.MicrosoftAuthPanel;
import io.github.KawaBaud.launcher.util.LauncherLanguageUtils;
import io.github.KawaBaud.launcher.util.LauncherUtils;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.GenericData;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import javax.swing.JProgressBar;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MicrosoftAuth {

	private static final Logger LOGGER;

	static {
		LOGGER = LoggerFactory.getLogger(MicrosoftAuth.class);
	}

	private MicrosoftAuth() {
	}

	private static GenericUrl[] getGenericUrls() {
		GenericUrl[] urls = new GenericUrl[7];
		urls[0] = new GenericUrl(new StringBuilder().append("https://login.microsoftonline.com/").append("consumers/")
				.append("oauth2/").append("v2.0/").append("devicecode").toString());
		urls[1] = new GenericUrl(new StringBuilder().append("https://login.microsoftonline.com/").append("consumers/")
				.append("oauth2/").append("v2.0/").append("token").toString());
		urls[2] = new GenericUrl(new StringBuilder().append("https://user.auth.xboxlive.com/").append("user/")
				.append("authenticate").toString());
		urls[3] = new GenericUrl(new StringBuilder().append("https://xsts.auth.xboxlive.com/").append("xsts/")
				.append("authorize").toString());
		urls[4] = new GenericUrl(new StringBuilder().append("https://api.minecraftservices.com/")
				.append("authentication/").append("login_with_xbox").toString());
		urls[5] = new GenericUrl(new StringBuilder().append("https://api.minecraftservices.com/")
				.append("entitlements/").append("mcstore").toString());
		urls[6] = new GenericUrl(new StringBuilder().append("https://api.minecraftservices.com/").append("minecraft/")
				.append("profile").toString());
		return urls;
	}

	public static JSONObject acquireDeviceCode(String clientId) {
		GenericData data = new GenericData();
		data.put("client_id", clientId);
		data.put("response_type", "code");
		data.put("scope", "XboxLive.signin offline_access");

		HttpTransport transport = new NetHttpTransport();
		HttpContent content = new UrlEncodedContent(data);

		HttpRequestFactory factory = transport.createRequestFactory();
		try {
			HttpRequest request = factory.buildPostRequest(getGenericUrls()[0], content);
			HttpResponse response = request.execute();
			return new JSONObject(response.parseAsString());
		} catch (UnknownHostException uhe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
			LauncherUtils.setNotPremium(false);
		} catch (IOException ioe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));
			LauncherUtils.setNotPremium(true);

			LOGGER.error("Cannot acquire device code", ioe);
		}
		return null;
	}

	public static JSONObject acquireToken(String clientId, String deviceCode) {
		GenericData data = new GenericData();
		data.put("client_id", clientId);
		data.put("device_code", deviceCode);
		data.put("grant_type", "urn:ietf:params:oauth:grant-type:device_code");

		HttpTransport transport = new NetHttpTransport();
		HttpContent content = new UrlEncodedContent(data);

		HttpRequestFactory factory = transport.createRequestFactory();
		try {
			HttpRequest request = factory.buildPostRequest(getGenericUrls()[1], content);
			HttpResponse response = request.execute();
			return new JSONObject(response.parseAsString());
		} catch (UnknownHostException uhe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
			LauncherUtils.setNotPremium(false);
		} catch (IOException ioe) {
			String message = ioe.getMessage();
			message = message.substring(message.indexOf("\n") + 1);
			message = message.substring(message.indexOf("\n") + 1);

			JSONObject jsonMessage = new JSONObject(message);
			handleTokenException(ioe, jsonMessage);
		}
		return null;
	}

	private static void handleTokenException(IOException ioe, JSONObject object) {
		if (object.has("error")) {
			String error = object.getString("error");
			switch (error) {
			case "authorization_pending":
				JProgressBar progressBar = MicrosoftAuthPanel.getInstance().getExpiresInProgressBar();
				progressBar.setValue(progressBar.getValue() - 1);
				break;
			case "invalid_grant":
				break;
			default:
				LauncherUtils.swapContainers(LauncherPanel.getInstance(),
						new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));
				LauncherUtils.setNotPremium(true);

				LOGGER.error("Cannot acquire token", ioe);
				break;
			}
		}
	}

	public static JSONObject refreshToken(String clientId, String refreshToken) {
		GenericData data = new GenericData();
		data.put("client_id", clientId);
		data.put("refresh_token", refreshToken);
		data.put("grant_type", "refresh_token");

		HttpTransport transport = new NetHttpTransport();
		HttpContent content = new UrlEncodedContent(data);

		HttpRequestFactory factory = transport.createRequestFactory();
		try {
			HttpRequest request = factory.buildPostRequest(getGenericUrls()[1], content);
			HttpResponse response = request.execute();
			return new JSONObject(response.parseAsString());
		} catch (UnknownHostException uhe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
			LauncherUtils.setNotPremium(false);
		} catch (IOException ioe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));
			LauncherUtils.setNotPremium(true);

			LOGGER.error("Cannot refresh access token", ioe);
		}
		return null;
	}

	public static JSONObject acquireXBLToken(String accessToken) {
		JSONObject properties = new JSONObject();
		properties.put("AuthMethod", "RPS");
		properties.put("SiteName", "user.auth.xboxlive.com");
		properties.put("RpsTicket", String.format("d=%s", accessToken));

		JSONObject data = new JSONObject();
		data.put("Properties", properties);
		data.put("RelyingParty", "http://auth.xboxlive.com");
		data.put("TokenType", "JWT");

		HttpTransport transport = new NetHttpTransport();
		HttpContent content = new ByteArrayContent("application/json",
				data.toString().getBytes(StandardCharsets.UTF_8));

		HttpRequestFactory factory = transport.createRequestFactory();
		try {
			HttpRequest request = factory.buildPostRequest(getGenericUrls()[2], content);
			HttpResponse response = request.execute();
			return new JSONObject(response.parseAsString());
		} catch (UnknownHostException uhe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
			LauncherUtils.setNotPremium(false);
		} catch (IOException ioe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));
			LauncherUtils.setNotPremium(true);

			LOGGER.error("Cannot acquire Xbox Live token", ioe);
		}
		return null;
	}

	public static JSONObject acquireXSTSToken(String token) {
		JSONObject properties = new JSONObject();
		properties.put("SandboxId", "RETAIL");
		properties.put("UserTokens", new String[] { token });

		JSONObject data = new JSONObject();
		data.put("Properties", properties);
		data.put("RelyingParty", "rp://api.minecraftservices.com/");
		data.put("TokenType", "JWT");

		HttpTransport transport = new NetHttpTransport();
		HttpContent content = new ByteArrayContent("application/json",
				data.toString().getBytes(StandardCharsets.UTF_8));

		HttpRequestFactory factory = transport.createRequestFactory();
		try {
			HttpRequest request = factory.buildPostRequest(getGenericUrls()[3], content);
			HttpResponse response = request.execute();
			return new JSONObject(response.parseAsString());
		} catch (UnknownHostException uhe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
			LauncherUtils.setNotPremium(false);
		} catch (IOException ioe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));
			LauncherUtils.setNotPremium(true);

			LOGGER.error("Cannot acquire XSTS token", ioe);
		}
		return null;
	}

	public static JSONObject acquireAccessToken(String uhs, String token) {
		JSONObject body = new JSONObject();
		body.put("identityToken", String.format("XBL3.0 x=%s;%s", uhs, token));
		body.put("ensureLegacyEnabled", true);

		HttpTransport transport = new NetHttpTransport();
		HttpContent content = new ByteArrayContent("application/json",
				body.toString().getBytes(StandardCharsets.UTF_8));

		HttpRequestFactory factory = transport.createRequestFactory();
		try {
			HttpRequest request = factory.buildPostRequest(getGenericUrls()[4], content);
			HttpResponse response = request.execute();
			return new JSONObject(response.parseAsString());
		} catch (UnknownHostException uhe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
			LauncherUtils.setNotPremium(false);
		} catch (IOException ioe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));
			LauncherUtils.setNotPremium(true);

			LOGGER.error("Cannot acquire Minecraft access token", ioe);
		}
		return null;
	}

	public static JSONObject checkEntitlementsMcStore(String accessToken) {
		HttpTransport transport = new NetHttpTransport();

		HttpRequestFactory factory = transport.createRequestFactory();
		try {
			HttpRequest request = factory.buildGetRequest(getGenericUrls()[5]);
			request.getHeaders().setAuthorization(String.format("Bearer %s", accessToken));

			HttpResponse response = request.execute();
			return new JSONObject(response.parseAsString());
		} catch (UnknownHostException uhe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
			LauncherUtils.setNotPremium(false);
		} catch (IOException ioe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));
			LauncherUtils.setNotPremium(true);

			LOGGER.error("Cannot check Minecraft Store entitlements", ioe);
		}
		return null;
	}

	public static JSONObject acquireMinecraftProfile(String accessToken) {
		HttpTransport transport = new NetHttpTransport();

		HttpRequestFactory factory = transport.createRequestFactory();
		try {
			HttpRequest request = factory.buildGetRequest(getGenericUrls()[6]);
			request.getHeaders().setAuthorization(String.format("Bearer %s", accessToken));

			HttpResponse response = request.execute();
			return new JSONObject(response.parseAsString());
		} catch (UnknownHostException uhe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
			LauncherUtils.setNotPremium(false);
		} catch (IOException ioe) {
			LauncherUtils.swapContainers(LauncherPanel.getInstance(),
					new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));
			LauncherUtils.setNotPremium(true);

			LOGGER.error("Cannot acquire Minecraft profile", ioe);
		}
		return null;
	}
}
