
package io.github.KawaBaud.launcher.minecraft;

import io.github.KawaBaud.launcher.LauncherConfig;
import io.github.KawaBaud.launcher.LauncherLanguage;
import io.github.KawaBaud.launcher.impl.UTF8ResourceBundle;
import io.github.KawaBaud.launcher.util.LauncherLanguageUtils;
import lombok.Getter;

public enum EState {
	INITIALISE(LauncherLanguageUtils.getESEnumKeys()[0]), CHECK_CACHE(LauncherLanguageUtils.getESEnumKeys()[1]),
	DOWNLOAD_PACKAGES(LauncherLanguageUtils.getESEnumKeys()[2]),
	EXTRACT_PACKAGES(LauncherLanguageUtils.getESEnumKeys()[3]),
	UPDATE_CLASSPATH(LauncherLanguageUtils.getESEnumKeys()[4]), DONE(LauncherLanguageUtils.getESEnumKeys()[5]);

	@Getter
	private final String message;

	EState(String key) {
		String selectedLanguage = (String) LauncherConfig.get(0);
		UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
		this.message = bundle.getString(key);
	}
}
