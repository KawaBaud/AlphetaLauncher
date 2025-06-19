package io.github.KawaBaud.launcher.util;

import io.github.KawaBaud.launcher.minecraft.EState;
import io.github.KawaBaud.launcher.ui.LauncherNoNetworkPanel;
import io.github.KawaBaud.launcher.ui.MicrosoftAuthPanel;
import io.github.KawaBaud.launcher.ui.MinecraftAppletWrapper;
import io.github.KawaBaud.launcher.ui.YggdrasilAuthPanel;
import io.github.KawaBaud.launcher.ui.options.LanguageGroupBox;
import io.github.KawaBaud.launcher.ui.options.OptionsDialog;
import io.github.KawaBaud.launcher.ui.options.OptionsPanel;
import io.github.KawaBaud.launcher.ui.options.VersionGroupBox;

public final class LauncherLanguageUtils {

	private LauncherLanguageUtils() {
	}

	public static String[] getESEnumKeys() {
		return new String[] { "es_enum.initialise", "es_enum.checkCache", "es_enum.downloadPackages",
				"es_enum.extractPackages", "es_enum.updateClasspath", "es_enum.done" };
	}

	public static String[] getLGBKeys() {
		return new String[] { "lgb.title", "lgb.setLanguageLabel" };
	}

	public static String[] getLNPPKeys() {
		return new String[] { "lnnp.errorLabel.signin", "lnnp.errorLabel.signin_null",
				"lnnp.errorLabel.signin_outdated", "lnnp.errorLabel.signin_2148916233",
				"lnnp.errorLabel.signin_2148916238", "lnnp.playOnlineLabel", "lnnp.playOfflineButton",
				"lnnp.retryButton" };
	}

	public static String[] getGAWKeys() {
		return new String[] { "maw.updaterStarted", "maw.updaterErrored", "maw.taskStateMessage.error",
				"maw.taskProgressMessage" };
	}

	public static String[] getMAPKeys() {
		return new String[] { "map.enterCodeInBrowserLabel", "map.openInBrowserButton", "map.cancelButton" };
	}

	public static String[] getODKeys() {
		return new String[] { "od.title" };
	}

	public static String[] getOPKeys() {
		return new String[] { "op.versionGroupBox", "op.languageGroupBox", "op.openFolderButton",
				"op.saveOptionsButton" };
	}

	public static String[] getVGBKeys() {
		return new String[] { "vgb.title", "vgb.showVersionsCheckBox", "vgb.useVersionLabel" };
	}

	public static String[] getYAPKeys() {
		return new String[] { "yap.microsoftSigninButton", "yap.microsoftSigninButton.signing_in", "yap.usernameLabel",
				"yap.passwordLabel", "yap.optionsButton", "yap.rememberPasswordCheckBox", "yap.linkLabel",
				"yap.linkLabel.outdated", "yap.signinButton", "yap.signinButton.signing_in" };
	}
}
