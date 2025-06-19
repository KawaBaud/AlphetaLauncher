
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
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.Getter;

public class LanguageGroupBox extends JGroupBox implements ActionListener {

	public static final long serialVersionUID = 1L;
	private static LanguageGroupBox instance;
	private final JLabel setLanguageLabel;
	@Getter
	private final JComboBox<String> languageComboBox;

	public LanguageGroupBox() {
		super(LauncherLanguageUtils.getLGBKeys()[0], true);

		setInstance(this);

		this.setLanguageLabel = new JLabel(LauncherLanguageUtils.getLGBKeys()[1], SwingConstants.RIGHT);
		this.languageComboBox = new JComboBox<>();

		this.setLayout(this.getGroupLayout());

		this.languageComboBox.addActionListener(this);

		String selectedLanguage = (String) LauncherConfig.get(0);
		UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
		this.updateComponentTexts(Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());

		LauncherOptionsUtils.updateLanguageComboBox(this);
	}

	public static LanguageGroupBox getInstance() {
		return instance;
	}

	private static void setInstance(LanguageGroupBox lgb) {
		instance = lgb;
	}

	public void updateComponentTexts(UTF8ResourceBundle bundle) {
		LauncherUtils.setComponentText(bundle, this.setLanguageLabel, LauncherLanguageUtils.getLGBKeys()[1]);
	}

	private LayoutManager getGroupLayout() {
		GroupLayout gl = new GroupLayout(this);
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		gl.setHorizontalGroup(
				gl.createSequentialGroup().addComponent(this.setLanguageLabel).addComponent(this.languageComboBox));
		gl.setVerticalGroup(gl.createSequentialGroup().addGroup(gl.createParallelGroup(Alignment.BASELINE)
				.addComponent(this.setLanguageLabel).addComponent(this.languageComboBox)));
		return gl;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (Objects.equals(source, this.languageComboBox)) {
			boolean selectedLanguageEqual = Objects.equals(LauncherConfig.get(0),
					this.languageComboBox.getSelectedItem());
			OptionsPanel.getInstance().getSaveOptionsButton().setEnabled(!selectedLanguageEqual);
		}
	}
}
