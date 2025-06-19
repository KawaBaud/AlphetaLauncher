
package io.github.KawaBaud.launcher.util;

import io.github.KawaBaud.launcher.ELanguage;
import io.github.KawaBaud.launcher.LauncherConfig;
import io.github.KawaBaud.launcher.LauncherLanguage;
import io.github.KawaBaud.launcher.impl.UTF8ResourceBundle;
import io.github.KawaBaud.launcher.ui.MicrosoftAuthPanel;
import io.github.KawaBaud.launcher.ui.YggdrasilAuthPanel;
import io.github.KawaBaud.launcher.ui.options.LanguageGroupBox;
import io.github.KawaBaud.launcher.ui.options.OptionsDialog;
import io.github.KawaBaud.launcher.ui.options.OptionsPanel;
import io.github.KawaBaud.launcher.ui.options.VersionGroupBox;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LauncherOptionsUtils {

	private static final Logger LOGGER;
	private static Map<String, String> versionMap;
	private static Map<String, String> languageMap;

	static {
		LOGGER = LoggerFactory.getLogger(LauncherOptionsUtils.class);
	}

	private LauncherOptionsUtils() {
	}

	public static void updateLanguageComboBox(LanguageGroupBox lgb) {
		languageMap = new HashMap<>();

		DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<>();

		Arrays.stream(ELanguage.values()).sorted(Comparator.comparing(ELanguage::getLanguageName))
				.forEachOrdered(language -> {
					defaultComboBoxModel.addElement(language.getLanguageName());
					languageMap.put(language.getLanguageName(), language.toString().toLowerCase());
				});

		Object selectedLanguage = LauncherConfig.get(0);
		languageMap.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), selectedLanguage)).findFirst()
				.ifPresent(entry -> defaultComboBoxModel.setSelectedItem(entry.getKey()));

		lgb.getLanguageComboBox().setModel(defaultComboBoxModel);
	}

	public static void updateVersionComboBox(VersionGroupBox vgb) {
		versionMap = new HashMap<>();

		DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<>();

		String fileName = "assets/versions.json";
		URL fileUrl = LauncherOptionsUtils.class.getClassLoader().getResource(fileName);

		InputStream is = Optional.ofNullable(LauncherOptionsUtils.class.getClassLoader().getResourceAsStream(fileName))
				.orElseThrow(() -> new NullPointerException("is cannot be null"));
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			JSONObject json = new JSONObject(br.lines().collect(Collectors.joining()));
			List<String> oldLegacy = Collections
					.unmodifiableList(Arrays.asList("legacy_beta", "legacy_alpha", "legacy_infdev"));
			List<String> legacy = Collections.singletonList("legacy_release");
			legacy.forEach(version -> {
				JSONArray versionArray = json.getJSONArray(version);
				IntStream.range(0, versionArray.length()).mapToObj(versionArray::getJSONObject).sorted((o1, o2) -> {
					String v1 = o1.getString("versionId");
					String v2 = o2.getString("versionId");
					return -compareVersionIds(v1, v2);
				}).collect(Collectors.toList()).forEach(o -> {
					String versionId = o.getString("versionId");
					String versionName = o.getString("versionName");

					versionMap.put(versionName, versionId);
					defaultComboBoxModel.addElement(versionName);
				});
			});
			oldLegacy.forEach(version -> {
				JSONArray versionArray = json.getJSONArray(version);
				IntStream.range(0, versionArray.length()).mapToObj(versionArray::getJSONObject).sorted((o1, o2) -> {
					String v1 = o1.getString("versionId");
					String v2 = o2.getString("versionId");
					return -compareVersionIds(v1, v2);
				}).collect(Collectors.toList()).forEach(o -> {
					boolean showBetaVersionsSelected = Boolean.parseBoolean(LauncherConfig.get(1).toString())
							&& Objects.equals(version, oldLegacy.get(0));
					boolean showAlphaVersionsSelected = Boolean.parseBoolean(LauncherConfig.get(2).toString())
							&& Objects.equals(version, oldLegacy.get(1));
					boolean showInfdevVersionsSelected = Boolean.parseBoolean(LauncherConfig.get(3).toString())
							&& Objects.equals(version, oldLegacy.get(2));
					if (showBetaVersionsSelected || showAlphaVersionsSelected || showInfdevVersionsSelected) {
						String versionId = o.getString("versionId");
						String versionName = o.getString("versionName");

						versionMap.put(versionName, versionId);
						defaultComboBoxModel.addElement(versionName);
					}
				});
			});
		} catch (IOException ioe) {
			LOGGER.error("Cannot read {}", fileUrl, ioe);
		}

		Object selectedVersion = LauncherConfig.get(4);
		versionMap.entrySet().stream().filter(entry -> entry.getValue().equals(selectedVersion)).findFirst()
				.ifPresent(entry -> defaultComboBoxModel.setSelectedItem(entry.getKey()));

		vgb.getVersionComboBox().setModel(defaultComboBoxModel);
	}

	public static void updateSelectedVersion(VersionGroupBox vgb) {
		String selectedItem = (String) vgb.getVersionComboBox().getSelectedItem();
		selectedItem = versionMap.get(selectedItem);

		Object selectedVersion = LauncherConfig.get(4);
		boolean versionChanged = !Objects.equals(selectedItem, selectedVersion);
		if (versionChanged) {
			LauncherConfig.set(4, selectedItem);
			LauncherConfig.saveConfig();
		}
	}

	public static void updateSelectedLanguage(LanguageGroupBox lgb) {
		String selectedItem = (String) lgb.getLanguageComboBox().getSelectedItem();
		selectedItem = languageMap.get(selectedItem);

		Object selectedLanguage = LauncherConfig.get(0);
		boolean languageChanged = !Objects.equals(selectedItem, selectedLanguage);
		if (languageChanged) {
			LauncherConfig.set(0, selectedItem);
			LauncherConfig.saveConfig();

			String finalSelectedItem = selectedItem;

			SwingUtilities.invokeLater(() -> {
				UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(finalSelectedItem);
				if (Objects.nonNull(bundle)) {
					LanguageGroupBox.getInstance().updateComponentTexts(bundle);
					OptionsDialog.getInstance().updateContainerTitles(bundle);
					OptionsPanel.getInstance().updateComponentTexts(bundle);
					VersionGroupBox.getInstance().updateComponentTexts(bundle);

					if (Objects.nonNull(MicrosoftAuthPanel.getInstance())) {
						MicrosoftAuthPanel.getInstance().updateComponentTexts(bundle);
					}
					YggdrasilAuthPanel.getInstance().updateComponentTexts(bundle);
				}

				OptionsDialog.getInstance().pack();
			});
		}
	}

	private static int compareVersionIds(String v1, String v2) {
		String[] v1Split = v1.replaceAll("[^\\d._]", "").split("[._]");
		String[] v2Split = v2.replaceAll("[^\\d._]", "").split("[._]");
		int v1SplitLength = v1Split.length;
		int v2SplitLength = v2Split.length;
		int vSplitLength = Math.max(v1SplitLength, v2SplitLength);
		for (int i = 0; i < vSplitLength; i++) {
			int v1SplitValue = i < v1SplitLength ? Integer.parseInt(v1Split[i]) : 0;
			int v2SplitValue = i < v2SplitLength ? Integer.parseInt(v2Split[i]) : 0;
			if (v1SplitValue != v2SplitValue) {
				return Integer.compare(v1SplitValue, v2SplitValue);
			}
		}
		return 0;
	}
}
