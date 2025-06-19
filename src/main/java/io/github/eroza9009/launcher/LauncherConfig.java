
package io.github.KawaBaud.launcher;

import io.github.KawaBaud.launcher.impl.LinkedProperties;
import io.github.KawaBaud.launcher.util.LauncherUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LauncherConfig {

	private static final Logger LOGGER;
	private static final Map<String, Object> PROPERTIES_MAP;

	static {
		LOGGER = LoggerFactory.getLogger(LauncherConfig.class);

		PROPERTIES_MAP = new LinkedHashMap<>();
		PROPERTIES_MAP.put("selectedLanguage", "en");
		PROPERTIES_MAP.put("showBetaVersionsSelected", true);
		PROPERTIES_MAP.put("showAlphaVersionsSelected", false);
		PROPERTIES_MAP.put("showInfdevVersionsSelected", false);
		PROPERTIES_MAP.put("selectedVersion", "b1.1_02");
		PROPERTIES_MAP.put("microsoftProfileId", null);
		PROPERTIES_MAP.put("microsoftProfileName", null);
		PROPERTIES_MAP.put("microsoftAccessToken", null);
		PROPERTIES_MAP.put("microsoftAccessTokenExpiresIn", 0L);
		PROPERTIES_MAP.put("microsoftRefreshToken", null);
		PROPERTIES_MAP.put("microsoftClientToken", UUID.randomUUID().toString().replace("-", "")); // 10
	}

	private LauncherConfig() {
	}

	private static Path getFilePath() {
		String userName = System.getProperty("user.name");
		String fileName = String.format("%s_%s.properties", "launcher", userName);

		Path filePath = LauncherUtils.WORKING_DIRECTORY_PATH.resolve(fileName);
		if (!Files.exists(filePath)) {
			try {
				Files.createFile(filePath);
			} catch (IOException ioe) {
				LOGGER.error("Cannot create {}", filePath, ioe);
				return null;
			}
		}
		return filePath;
	}

	private static Object[] getKeys() {
		return PROPERTIES_MAP.keySet().toArray();
	}

	public static Object get(int index) {
		return PROPERTIES_MAP.get(getKeys()[index].toString());
	}

	public static void set(int index, Object value) {
		PROPERTIES_MAP.put(getKeys()[index].toString(), value);
	}

	public static void loadConfig() {
		Path filePath = getFilePath();
		Objects.requireNonNull(filePath, "filePath cannot be null");

		URI filePathUri = filePath.toUri();

		LinkedProperties properties = new LinkedProperties();

		try (InputStream is = Files.newInputStream(filePath)) {
			properties.load(is);
			properties.forEach((key, value) -> PROPERTIES_MAP.put((String) key, value));
		} catch (FileNotFoundException fnfe) {
			LOGGER.error("Cannot find {}", filePathUri, fnfe);
		} catch (IOException ioe) {
			LOGGER.error("Cannot load {}", filePathUri, ioe);
		} finally {
			if (properties.isEmpty()) {
				saveConfig();
			}
		}
	}

	public static void saveConfig() {
		Path filePath = getFilePath();
		Objects.requireNonNull(filePath, "filePath cannot be null");

		URI filePathUri = filePath.toUri();

		try (OutputStream os = Files.newOutputStream(filePath)) {
			LinkedProperties properties = new LinkedProperties();

			PROPERTIES_MAP.keySet().forEach(key -> {
				Object value = PROPERTIES_MAP.get(key);
				properties.put(key, Optional.ofNullable(value).map(Object::toString).orElse(""));
			});
			properties.store(os, "Alpheta Launcher");

			os.flush();
		} catch (FileNotFoundException fnfe) {
			LOGGER.error("Cannot find {}", filePathUri, fnfe);
		} catch (IOException ioe) {
			LOGGER.error("Cannot save {}", filePathUri, ioe);
		}
	}
}
