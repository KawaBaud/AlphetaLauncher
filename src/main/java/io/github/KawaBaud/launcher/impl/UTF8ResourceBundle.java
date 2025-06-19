
package io.github.KawaBaud.launcher.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;

public class UTF8ResourceBundle extends ResourceBundle {

	private final Map<String, String> lookupMap;

	public UTF8ResourceBundle() {
		this.lookupMap = new HashMap<>();
	}

	public UTF8ResourceBundle(InputStream is) throws IOException {
		this.lookupMap = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			Properties properties = new Properties();
			properties.load(br);
			properties.stringPropertyNames().forEach(key -> this.lookupMap.put(key, properties.getProperty(key)));
		}
	}

	public UTF8ResourceBundle(Reader reader) throws IOException {
		this.lookupMap = new HashMap<>();

		try (BufferedReader br = new BufferedReader(reader)) {
			Properties properties = new Properties();
			properties.load(br);
			properties.stringPropertyNames().forEach(key -> this.lookupMap.put(key, properties.getProperty(key)));
		}
	}

	@Override
	protected Object handleGetObject(@Nonnull String key) {
		return this.lookupMap.get(key);
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(this.lookupMap.keySet());
	}

	public static class UTF8Control extends Control {

		@Override
		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
				boolean reload) throws IOException {
			String bundleName = this.toBundleName(baseName, locale);
			String resourceName = this.toResourceName(bundleName, "properties");

			try (InputStream is = loader.getResourceAsStream(resourceName)) {
				return new UTF8ResourceBundle(is);
			}
		}
	}
}
