
package io.github.KawaBaud.launcher;

import lombok.Getter;

public enum ELanguage {
	BG("Български"), CS("Čeština"), DE("Deutsch"), EN("English"), ES("Español"), ET("Eesti"), FI("Suomi"),
	FR("Français"), HU("Magyar"), IT("Italiano"), JA("日本語"), PL("Polski"), RU("Русский");

	public static final String USER_LANGUAGE;

	static {
		USER_LANGUAGE = System.getProperty("user.language");
	}

	@Getter
	private final String languageName;

	ELanguage(String languageName) {
		this.languageName = languageName;
	}
}
