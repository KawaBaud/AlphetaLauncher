
package io.github.KawaBaud.launcher.ui.options;

import io.github.KawaBaud.launcher.LauncherConfig;
import io.github.KawaBaud.launcher.LauncherLanguage;
import io.github.KawaBaud.launcher.impl.UTF8ResourceBundle;
import io.github.KawaBaud.launcher.util.LauncherLanguageUtils;
import io.github.KawaBaud.launcher.util.LauncherUtils;
import java.awt.Window;
import java.util.Objects;
import javax.swing.JDialog;

public class OptionsDialog extends JDialog {

	public static final long serialVersionUID = 1L;
	private static OptionsDialog instance;

	public OptionsDialog(Window owner) {
		super(owner, ModalityType.MODELESS);

		setInstance(this);

		this.setContentPane(new OptionsPanel());

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.pack();

		this.setLocation(this.getOwner().getLocation());
		this.setResizable(false);

		String selectedLanguage = (String) LauncherConfig.get(0);
		UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
		this.updateContainerTitles(Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());
	}

	public static OptionsDialog getInstance() {
		return instance;
	}

	private static void setInstance(OptionsDialog od) {
		instance = od;
	}

	public void updateContainerTitles(UTF8ResourceBundle bundle) {
		LauncherUtils.setContainerTitle(bundle, this, LauncherLanguageUtils.getODKeys()[0]);
	}

	@Override
	public String getTitle() {
		return LauncherLanguageUtils.getODKeys()[0];
	}
}
