
package io.github.KawaBaud.launcher.ui.options;

import io.github.KawaBaud.launcher.LauncherConfig;
import io.github.KawaBaud.launcher.LauncherLanguage;
import io.github.KawaBaud.launcher.impl.UTF8ResourceBundle;
import io.github.KawaBaud.launcher.impl.swing.JGroupBox;
import io.github.KawaBaud.launcher.util.LauncherLanguageUtils;
import io.github.KawaBaud.launcher.util.LauncherOptionsUtils;
import io.github.KawaBaud.launcher.util.LauncherUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.Getter;

public class VersionGroupBox extends JGroupBox implements ActionListener {

	public static final long serialVersionUID = 1L;
	private static VersionGroupBox instance;
	private final JCheckBox showBetaVersionsCheckBox;
	private final JCheckBox showAlphaVersionsCheckBox;
	private final JCheckBox showInfdevVersionsCheckBox;
	private final JLabel useVersionLabel;
	@Getter
	private final JComboBox<String> versionComboBox;

	public VersionGroupBox() {
		super(LauncherLanguageUtils.getVGBKeys()[0], true);

		setInstance(this);

		this.showBetaVersionsCheckBox = new JCheckBox(LauncherLanguageUtils.getVGBKeys()[1]);
		this.showAlphaVersionsCheckBox = new JCheckBox(LauncherLanguageUtils.getVGBKeys()[1]);
		this.showInfdevVersionsCheckBox = new JCheckBox(LauncherLanguageUtils.getVGBKeys()[1]);
		this.useVersionLabel = new JLabel(LauncherLanguageUtils.getVGBKeys()[2], SwingConstants.RIGHT);
		this.versionComboBox = new JComboBox<>();

		this.setLayout(this.getGroupLayout());

		this.showBetaVersionsCheckBox.setSelected(Boolean.parseBoolean(LauncherConfig.get(1).toString()));
		this.showAlphaVersionsCheckBox.setSelected(Boolean.parseBoolean(LauncherConfig.get(2).toString()));
		this.showInfdevVersionsCheckBox.setSelected(Boolean.parseBoolean(LauncherConfig.get(3).toString()));

		this.showBetaVersionsCheckBox.addActionListener(this);
		this.showAlphaVersionsCheckBox.addActionListener(this);
		this.showInfdevVersionsCheckBox.addActionListener(this);
		this.versionComboBox.addActionListener(this);

		String selectedLanguage = (String) LauncherConfig.get(0);
		UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
		this.updateComponentTexts(Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());

		LauncherOptionsUtils.updateVersionComboBox(this);
	}

	public static VersionGroupBox getInstance() {
		return instance;
	}

	public static void setInstance(VersionGroupBox vgb) {
		VersionGroupBox.instance = vgb;
	}

	public void updateComponentTexts(UTF8ResourceBundle bundle) {
		List<String> versions = Collections.unmodifiableList(Arrays.asList("Beta", "Alpha", "Infdev"));
		List<String> releaseDateRange = Collections.unmodifiableList(
				Arrays.asList("2010-12-20 -> 2011-09-15", "2010-06-30 -> 2010-12-03", "2010-02-27 -> 2010-06-30"));

		LauncherUtils.setComponentText(bundle, this.showBetaVersionsCheckBox, LauncherLanguageUtils.getVGBKeys()[1],
				versions.get(0), releaseDateRange.get(0));
		LauncherUtils.setComponentText(bundle, this.showAlphaVersionsCheckBox, LauncherLanguageUtils.getVGBKeys()[1],
				versions.get(1), releaseDateRange.get(1));
		LauncherUtils.setComponentText(bundle, this.showInfdevVersionsCheckBox, LauncherLanguageUtils.getVGBKeys()[1],
				versions.get(2), releaseDateRange.get(2));
		LauncherUtils.setComponentText(bundle, this.useVersionLabel, LauncherLanguageUtils.getVGBKeys()[2]);
	}

	private LayoutManager getGroupLayout() {
		GroupLayout gl = new GroupLayout(this);
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		gl.setHorizontalGroup(gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(Alignment.LEADING).addComponent(this.showBetaVersionsCheckBox)
						.addComponent(this.showAlphaVersionsCheckBox).addComponent(this.showInfdevVersionsCheckBox)
						.addGroup(gl.createSequentialGroup().addComponent(this.useVersionLabel)
								.addComponent(this.versionComboBox))));
		gl.setVerticalGroup(gl.createSequentialGroup().addComponent(this.showBetaVersionsCheckBox)
				.addComponent(this.showAlphaVersionsCheckBox).addComponent(this.showInfdevVersionsCheckBox)
				.addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(this.useVersionLabel)
						.addComponent(this.versionComboBox)));
		return gl;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		JCheckBox[] showVersionCheckBoxes = new JCheckBox[] { this.showBetaVersionsCheckBox,
				this.showAlphaVersionsCheckBox, this.showInfdevVersionsCheckBox };
		for (JCheckBox checkBox : showVersionCheckBoxes) {
			if (Objects.equals(source, checkBox)) {
				LauncherConfig.set(1, this.showBetaVersionsCheckBox.isSelected());
				LauncherConfig.set(2, this.showAlphaVersionsCheckBox.isSelected());
				LauncherConfig.set(3, this.showInfdevVersionsCheckBox.isSelected());

				boolean showVersionsSelected = Stream.of(Boolean.parseBoolean(LauncherConfig.get(1).toString()),
						Boolean.parseBoolean(LauncherConfig.get(2).toString()),
						Boolean.parseBoolean(LauncherConfig.get(3).toString())).anyMatch(Boolean::booleanValue);
				OptionsPanel.getInstance().getSaveOptionsButton().setEnabled(showVersionsSelected);

				LauncherOptionsUtils.updateVersionComboBox(this);
				break;
			}
		}
		if (Objects.equals(source, this.versionComboBox)) {
			Object selectedVersion = LauncherConfig.get(4);
			Object selectedItem = this.versionComboBox.getSelectedItem();
			boolean selectedVersionEqual = Objects.equals(selectedVersion, selectedItem);

			OptionsPanel.getInstance().getSaveOptionsButton().setEnabled(!selectedVersionEqual);
		}
	}
}
