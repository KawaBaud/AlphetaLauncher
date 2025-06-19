
package io.github.KawaBaud.launcher.util;

import io.github.KawaBaud.launcher.EPlatform;
import io.github.KawaBaud.launcher.LauncherConfig;
import io.github.KawaBaud.launcher.impl.UTF8ResourceBundle;
import io.github.KawaBaud.launcher.impl.swing.JGroupBox;
import io.github.KawaBaud.launcher.ui.LauncherNoNetworkPanel;
import io.github.KawaBaud.launcher.ui.LauncherPanel;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.awt.Container;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LauncherUtils {

	public static final Pattern JWT_PATTERN;
	public static final Pattern UUID_PATTERN;
	public static final Path WORKING_DIRECTORY_PATH;
	private static final Logger LOGGER;
	@Getter
	@Setter
	private static boolean notPremium;
	@Getter
	private static Boolean outdated;

	static {
		LOGGER = LoggerFactory.getLogger(LauncherUtils.class);

		JWT_PATTERN = Pattern.compile("^[A-Za-z0-9-_]+?" + "\\.[A-Za-z0-9-_]+?" + "\\.[A-Za-z0-9-_]+$");
		UUID_PATTERN = Pattern.compile(
				"^[A-Fa-f0-9]{8}?" + "[A-Fa-f0-9]{4}?" + "[A-Fa-f0-9]{4}?" + "[A-Fa-f0-9]{4}?" + "[A-Fa-f0-9]{12}$");

		WORKING_DIRECTORY_PATH = getWorkingDirectoryPath();
		notPremium = false;
		outdated = null;
	}

	private LauncherUtils() {
	}

	public static String decodeFromBase64(int index) {
		if (Objects.isNull(LauncherConfig.get(index))) {
			return "";
		}

		String value = LauncherConfig.get(index).toString();

		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		return bytes.length == 0 ? "" : new String(Base64.getDecoder().decode(bytes));
	}

	public static String encodeToBase64(String value) {
		Objects.requireNonNull(value, "value cannot be null");
		if (value.isEmpty()) {
			throw new IllegalArgumentException("value cannot be empty");
		}

		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		return bytes.length == 0 ? "" : Base64.getEncoder().encodeToString(bytes);
	}

	public static GenericUrl[] getGenericUrls() {
		GenericUrl[] urls = new GenericUrl[3];
		urls[0] = new GenericUrl(new StringBuilder().append("https://signup.live.com/").append("signup")
				.append("?client_id=000000004420578E").append("&cobrandid=8058f65d-ce06-4c30-9559-473c9275a65d")
				.append("&lic=1").append("&uaid=e6e4ffd0ad4943ab9bf740fb4a0416f9").append("&wa=wsignin1.0").toString());
		urls[1] = new GenericUrl(new StringBuilder().append("https://api.github.com/").append("repos/")
				.append("KawaBaud/").append("AlphetaLauncher/").append("releases").toString());
		urls[2] = new GenericUrl(new StringBuilder().append("https://github.com/").append("KawaBaud/")
				.append("AlphetaLauncher/").append("releases/").append("latest").toString());
		return urls;
	}

	public static String[] getProxyHostAndPort() {
		String selectedVersion = (String) LauncherConfig.get(4);

		String fileName = "assets/versions.json";
		URL fileUrl = LauncherUtils.class.getClassLoader().getResource(fileName);

		InputStream is = Optional.ofNullable(LauncherUtils.class.getClassLoader().getResourceAsStream(fileName))
				.orElseThrow(() -> new NullPointerException("is cannot be null"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			JSONObject json = new JSONObject(br.lines().collect(Collectors.joining()));

			JSONArray versionArray;
			if (selectedVersion.startsWith("inf")) {
				versionArray = json.getJSONArray("legacy_infdev");
			} else if (selectedVersion.startsWith("a")) {
				versionArray = json.getJSONArray("legacy_alpha");
			} else if (selectedVersion.startsWith("b")) {
				versionArray = json.getJSONArray("legacy_beta");
			} else {
				versionArray = json.getJSONArray("legacy_release");
			}

			if (Objects.nonNull(versionArray)) {
				for (int i = 0; i < versionArray.length(); i++) {
					JSONObject version = versionArray.getJSONObject(i);
					String versionId = version.getString("versionId");
					if (Objects.equals(versionId, selectedVersion)) {
						String proxyHost = version.getString("bcProxyHost");
						int proxyPort = version.getInt("bcProxyPort");
						return new String[] { proxyHost, String.valueOf(proxyPort) };
					}
				}
			}
		} catch (IOException ioe) {
			LOGGER.error("Cannot read {}", fileUrl, ioe);
		}
		return new String[] { null, null };
	}

	public static Path getWorkingDirectoryPath() {
		String userHome = System.getProperty("user.home", ".");
		String appData = System.getenv("APPDATA");

		Map<EPlatform, Path> directoryMap = new EnumMap<>(EPlatform.class);
		directoryMap.put(EPlatform.LINUX, Paths.get(userHome, ".minecraft"));
		directoryMap.put(EPlatform.MACOS, Paths.get(userHome, "Library", "Application Support", "minecraft"));
		directoryMap.put(EPlatform.WINDOWS, Paths.get(appData, ".minecraft"));

		Path dirPath = directoryMap.get(EPlatform.getOSName());
		if (!Files.exists(dirPath)) {
			try {
				Files.createDirectories(dirPath);
			} catch (IOException ioe) {
				LOGGER.error("Cannot create {}", dirPath, ioe);
				return null;
			}
		}
		return dirPath;
	}

	public static boolean isOutdated() {
		// if (Objects.isNull(outdated)) {
		// SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
		// @Override
		// protected Boolean doInBackground() {
		// HttpTransport transport = new NetHttpTransport();

		// HttpRequestFactory factory = transport.createRequestFactory();
		// try {
		// HttpRequest request = factory
		// .buildGetRequest(getGenericUrls()[1].set("accept",
		// "application/vnd.github+json")
		// .set("X-GitHub-Api-Version", "2022-11-28"));
		// HttpResponse response = request.execute();

		// String body = response.parseAsString();
		// JSONArray array = new JSONArray(body);
		// String tagName = array.getJSONObject(0).getString("tag_name");
		// String implVersion = this.getClass().getPackage().getImplementationVersion();
		// if (Objects.isNull(implVersion)) {
		// implVersion = "1.99.9999_99"; // This can be used for testing purposes
		// }
		// return Objects.compare(implVersion, tagName, String::compareTo) < 0;
		// } catch (UnknownHostException uhe) {
		// LauncherUtils.swapContainers(LauncherPanel.getInstance(),
		// new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1],
		// uhe.getMessage()));
		// } catch (IOException ioe) {
		// return false;
		// }
		// return false;
		// }
		// };
		// worker.execute();

		// try {
		// outdated = worker.get();
		// } catch (InterruptedException ie) {
		// Thread.currentThread().interrupt();

		// LOGGER.error("Interrupted while checking for updates", ie);
		// } catch (ExecutionException ee) {
		// Throwable cause = ee.getCause();

		// LOGGER.error("Error while checking for updates", cause);
		// } finally {
		// worker.cancel(true);
		// }
		// }
		return false;
	}

	public static void swapContainers(Container c1, Container c2) {
		if (SwingUtilities.isEventDispatchThread()) {
			c1.removeAll();
			c1.add(c2);
			c1.revalidate();
			c1.repaint();
		} else {
			SwingUtilities.invokeLater(() -> {
				c1.removeAll();
				c1.add(c2);
				c1.revalidate();
				c1.repaint();
			});
		}
	}

	public static void setContainerTitle(UTF8ResourceBundle bundle, Container c, String key, Object... args) {
		if (c instanceof JFrame) {
			JFrame frame = (JFrame) c;
			frame.setTitle(MessageFormat.format(bundle.getString(key), args));
		}
		if (c instanceof JDialog) {
			JDialog dialog = (JDialog) c;
			dialog.setTitle(MessageFormat.format(bundle.getString(key), args));
		}
	}

	public static void setComponentText(UTF8ResourceBundle bundle, JComponent c, String key, Object... args) {
		if (c instanceof AbstractButton) {
			AbstractButton button = (AbstractButton) c;
			button.setText(MessageFormat.format(bundle.getString(key), args));
		}
		if (c instanceof JGroupBox) {
			JGroupBox groupBox = (JGroupBox) c;
			groupBox.setTitledBorder(MessageFormat.format(bundle.getString(key), args));
		}
		if (c instanceof JLabel) {
			JLabel label = (JLabel) c;
			label.setText(MessageFormat.format(bundle.getString(key), args));
		}
	}

	public static void openBrowser(String url) {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(new URI(url));
			}
		} catch (IOException ioe) {
			LOGGER.error("Cannot browse {}", url, ioe);
		} catch (URISyntaxException urise) {
			LOGGER.error("Cannot parse {} as URI", url, urise);
		}
	}

	public static void openDesktop(Path p) {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(p.toFile());
			}
		} catch (IOException ioe) {
			LOGGER.error("Cannot open {}", p, ioe);
		}
	}
}
