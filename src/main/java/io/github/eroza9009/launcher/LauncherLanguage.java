
package io.github.KawaBaud.launcher;

import io.github.KawaBaud.launcher.impl.UTF8ResourceBundle;
import io.github.KawaBaud.launcher.impl.UTF8ResourceBundle.UTF8Control;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LauncherLanguage {

	private static final Logger LOGGER;
	@Getter
	private static UTF8ResourceBundle bundle;

	static {
		LOGGER = LoggerFactory.getLogger(LauncherLanguage.class);
	}

	private LauncherLanguage() {
	}

	public static UTF8ResourceBundle getUTF8Bundle(String languageCode) {
		String baseName = "assets/lang/messages";
		return Optional.ofNullable(languageCode)
				.map(code -> (UTF8ResourceBundle) ResourceBundle.getBundle(baseName, Locale.forLanguageTag(code),
						new UTF8Control()))
				.orElseGet(() -> (UTF8ResourceBundle) ResourceBundle.getBundle(baseName, new UTF8Control()));
	}

	public static void loadLanguage(String baseName, String languageCode) {
		Objects.requireNonNull(baseName, "baseName cannot be null");
		Objects.requireNonNull(languageCode, "languageCode cannot be null");

		String fileName = String.format("%s_%s.properties", baseName, languageCode);
		URL fileUrl = LauncherLanguage.class.getClassLoader().getResource(String.format("assets/lang/%s", fileName));

		InputStream is = Optional
				.ofNullable(LauncherLanguage.class.getClassLoader()
						.getResourceAsStream(String.format("assets/lang/%s", fileName)))
				.orElseThrow(() -> new NullPointerException("is cannot be null"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			bundle = new UTF8ResourceBundle(br);
		} catch (IOException ioe) {
			LOGGER.error("Cannot load {}", fileUrl, ioe);
		}
	}
}
