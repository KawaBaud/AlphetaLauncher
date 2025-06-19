
package io.github.KawaBaud.launcher.ui.options;

import static io.github.KawaBaud.launcher.util.LauncherUtils.WORKING_DIRECTORY_PATH;

import io.github.KawaBaud.launcher.LauncherConfig;
import io.github.KawaBaud.launcher.LauncherLanguage;
import io.github.KawaBaud.launcher.impl.UTF8ResourceBundle;
import io.github.KawaBaud.launcher.util.LauncherLanguageUtils;
import io.github.KawaBaud.launcher.util.LauncherOptionsUtils;
import io.github.KawaBaud.launcher.util.LauncherUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import lombok.Getter;

public class OptionsPanel extends JPanel implements ActionListener {

	public static final long serialVersionUID = 1L;
	private static OptionsPanel instance;
	private final LanguageGroupBox languageGroupBox;
	private final VersionGroupBox versionGroupBox;
	private final JLabel implVersionLabel;
	private final JButton openFolderButton;
	@Getter
	private final JButton saveOptionsButton;

	public OptionsPanel() {
		super(true);

		setInstance(this);

		this.languageGroupBox = new LanguageGroupBox();
		this.versionGroupBox = new VersionGroupBox();
		this.implVersionLabel = new JLabel(this.getClass().getPackage().getImplementationVersion(),
				SwingConstants.CENTER);
		this.openFolderButton = new JButton(LauncherLanguageUtils.getOPKeys()[2]);
		this.saveOptionsButton = new JButton(LauncherLanguageUtils.getOPKeys()[3]);

		this.setLayout(this.getGroupLayout());

		this.implVersionLabel.setEnabled(false);
		this.saveOptionsButton.setEnabled(false);

		this.openFolderButton.addActionListener(this);
		this.saveOptionsButton.addActionListener(this);

		String selectedLanguage = (String) LauncherConfig.get(0);
		UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
		this.updateComponentTexts(Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());
	}

	public static OptionsPanel getInstance() {
		return instance;
	}

	private static void setInstance(OptionsPanel op) {
		instance = op;
	}

	public void updateComponentTexts(UTF8ResourceBundle bundle) {
		LauncherUtils.setComponentText(bundle, this.languageGroupBox,
				this.languageGroupBox.setTitledBorder(LauncherLanguageUtils.getOPKeys()[1]));
		LauncherUtils.setComponentText(bundle, this.versionGroupBox,
				this.versionGroupBox.setTitledBorder(LauncherLanguageUtils.getOPKeys()[0]));
		LauncherUtils.setComponentText(bundle, this.openFolderButton, LauncherLanguageUtils.getOPKeys()[2]);
		LauncherUtils.setComponentText(bundle, this.saveOptionsButton, LauncherLanguageUtils.getOPKeys()[3]);
	}

	private LayoutManager getGroupLayout() {
		GroupLayout gl = new GroupLayout(this);
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		gl.setHorizontalGroup(gl.createSequentialGroup().addGroup(gl.createParallelGroup()
				.addComponent(this.languageGroupBox).addComponent(this.versionGroupBox)
				.addGroup(gl.createSequentialGroup()
						.addComponent(this.implVersionLabel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(this.openFolderButton, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(this.saveOptionsButton, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))));
		gl.setVerticalGroup(
				gl.createSequentialGroup().addComponent(this.languageGroupBox).addComponent(this.versionGroupBox)
						.addGroup(gl.createParallelGroup(Alignment.CENTER).addComponent(this.implVersionLabel)
								.addComponent(this.openFolderButton).addComponent(this.saveOptionsButton)));
		return gl;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (Objects.equals(source, this.openFolderButton)) {
			LauncherUtils.openDesktop(WORKING_DIRECTORY_PATH);
		}
		if (Objects.equals(source, this.saveOptionsButton)) {
			LauncherOptionsUtils.updateSelectedLanguage(this.languageGroupBox);
			LauncherOptionsUtils.updateSelectedVersion(this.versionGroupBox);

			this.saveOptionsButton.setEnabled(false);
		}
	}
}
