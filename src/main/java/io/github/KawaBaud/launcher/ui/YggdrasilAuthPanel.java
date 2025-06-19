package io.github.KawaBaud.launcher.ui;

import io.github.KawaBaud.launcher.Launcher;
import io.github.KawaBaud.launcher.LauncherConfig;
import io.github.KawaBaud.launcher.LauncherLanguage;
import io.github.KawaBaud.launcher.impl.UTF8ResourceBundle;
import io.github.KawaBaud.launcher.impl.swing.JHyperlink;
import io.github.KawaBaud.launcher.impl.swing.JNotchPanel;
import io.github.KawaBaud.launcher.impl.swing.TransparentJButton;
import io.github.KawaBaud.launcher.impl.swing.TransparentJCheckBox;
import io.github.KawaBaud.launcher.ui.options.OptionsDialog;
import io.github.KawaBaud.launcher.util.LauncherLanguageUtils;
import io.github.KawaBaud.launcher.util.LauncherUtils;
import io.github.KawaBaud.launcher.util.MicrosoftAuthUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class YggdrasilAuthPanel extends JNotchPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static YggdrasilAuthPanel instance;
	private final TransparentJButton microsoftSigninButton;
	private final JLabel usernameLabel;
	private final JLabel passwordLabel;
	private final JTextField usernameField;
	private final JPasswordField passwordField;
	private final TransparentJButton optionsButton;
	private final TransparentJCheckBox rememberPasswordCheckBox;
	private final JHyperlink linkLabel;
	private final TransparentJButton signinButton;

	public YggdrasilAuthPanel() {
		super(true);

		setInstance(this);

		this.microsoftSigninButton = new TransparentJButton(LauncherLanguageUtils.getYAPKeys()[0]);
		this.usernameLabel = new JLabel(LauncherLanguageUtils.getYAPKeys()[2], SwingConstants.RIGHT);
		this.passwordLabel = new JLabel(LauncherLanguageUtils.getYAPKeys()[3], SwingConstants.RIGHT);
		this.usernameField = new JTextField(20);
		this.usernameField.setEnabled(false); // RIP authserver.mojang.com
		this.passwordField = new JPasswordField(20);
		this.passwordField.setEnabled(false); // RIP authserver.mojang.com
		this.optionsButton = new TransparentJButton(LauncherLanguageUtils.getYAPKeys()[4]);
		this.rememberPasswordCheckBox = new TransparentJCheckBox(LauncherLanguageUtils.getYAPKeys()[5]);
		this.rememberPasswordCheckBox.setEnabled(false); // RIP authserver.mojang.com
		this.linkLabel = new JHyperlink(
				Objects.nonNull(LauncherUtils.getOutdated()) && Boolean.TRUE.equals(LauncherUtils.getOutdated())
						? LauncherLanguageUtils.getYAPKeys()[7]
						: LauncherLanguageUtils.getYAPKeys()[6],
				SwingConstants.LEFT);
		this.signinButton = new TransparentJButton(LauncherLanguageUtils.getYAPKeys()[8]);
		this.signinButton.setEnabled(false); // RIP authserver.mojang.com

		this.setLayout(this.getGroupLayout());

		this.microsoftSigninButton.addActionListener(this);
		this.optionsButton.addActionListener(this);
		this.linkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				LauncherUtils.openBrowser(
						Objects.nonNull(LauncherUtils.getOutdated()) && Boolean.TRUE.equals(LauncherUtils.getOutdated())
								? String.valueOf(LauncherUtils.getGenericUrls()[2])
								: String.valueOf(LauncherUtils.getGenericUrls()[0]));
			}
		});

		String selectedLanguage = (String) LauncherConfig.get(0);
		UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
		this.updateComponentTexts(Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());
	}

	public static YggdrasilAuthPanel getInstance() {
		return instance;
	}

	private static void setInstance(YggdrasilAuthPanel yap) {
		YggdrasilAuthPanel.instance = yap;
	}

	public void updateComponentTexts(UTF8ResourceBundle bundle) {
		LauncherUtils.setComponentText(bundle, this.microsoftSigninButton, LauncherLanguageUtils.getYAPKeys()[0]);
		LauncherUtils.setComponentText(bundle, this.usernameLabel, LauncherLanguageUtils.getYAPKeys()[2]);
		LauncherUtils.setComponentText(bundle, this.passwordLabel, LauncherLanguageUtils.getYAPKeys()[3]);
		LauncherUtils.setComponentText(bundle, this.optionsButton, LauncherLanguageUtils.getYAPKeys()[4]);
		LauncherUtils.setComponentText(bundle, this.rememberPasswordCheckBox, LauncherLanguageUtils.getYAPKeys()[5]);
		LauncherUtils.setComponentText(bundle, this.linkLabel,
				Objects.nonNull(LauncherUtils.getOutdated()) && Boolean.TRUE.equals(LauncherUtils.getOutdated())
						? LauncherLanguageUtils.getYAPKeys()[7]
						: LauncherLanguageUtils.getYAPKeys()[6]);
		LauncherUtils.setComponentText(bundle, this.signinButton, LauncherLanguageUtils.getYAPKeys()[8]);
	}

	private LayoutManager getGroupLayout() {
		GroupLayout gl = new GroupLayout(this);
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		gl.setHorizontalGroup(
				gl.createParallelGroup(Alignment.CENTER).addComponent(this.microsoftSigninButton, 0, 0, Short.MAX_VALUE)
						.addGroup(gl.createSequentialGroup()
								.addGroup(gl.createParallelGroup(Alignment.LEADING)
										.addComponent(this.usernameLabel, Alignment.TRAILING)
										.addComponent(this.passwordLabel, Alignment.TRAILING)
										.addComponent(this.optionsButton, Alignment.TRAILING))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl.createParallelGroup(Alignment.LEADING).addComponent(this.usernameField)
										.addComponent(this.passwordField).addComponent(this.rememberPasswordCheckBox)))
						.addGroup(gl.createSequentialGroup().addComponent(this.linkLabel)
								.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(this.signinButton)));
		gl.setVerticalGroup(gl.createSequentialGroup().addComponent(this.microsoftSigninButton)
				.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(this.usernameLabel)
						.addComponent(this.usernameField))
				.addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(this.passwordLabel)
						.addComponent(this.passwordField))
				.addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(this.optionsButton)
						.addComponent(this.rememberPasswordCheckBox))
				.addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(this.linkLabel)
						.addComponent(this.signinButton)));
		return gl;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String selectedLanguage = (String) LauncherConfig.get(0);
		UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);

		Object source = event.getSource();
		if (Objects.equals(source, this.microsoftSigninButton)) {
			Arrays.stream(this.getComponents()).forEachOrdered(component -> component.setEnabled(false));
			this.microsoftSigninButton.setText(bundle.getString(LauncherLanguageUtils.getYAPKeys()[1]));

			if (LauncherUtils.isOutdated()) {
				LauncherUtils.swapContainers(this.getParent(),
						new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[2]));
				return;
			}

			this.signInWithMicrosoft();
		}
		if (Objects.equals(source, this.optionsButton)) {
			SwingUtilities
					.invokeLater(() -> new OptionsDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true));
		}
	}

	private void signInWithMicrosoft() {
		String microsoftProfileName = (String) LauncherConfig.get(6);
		String microsoftProfileId = (String) LauncherConfig.get(5);
		String microsoftAccessToken = (String) LauncherConfig.get(7);
		boolean accessTokenMatched = Objects.nonNull(microsoftAccessToken)
				&& LauncherUtils.JWT_PATTERN.matcher(microsoftAccessToken).matches();
		if (!accessTokenMatched) {
			MicrosoftAuthUtils.executeMicrosoftAuthWorker(MicrosoftAuthUtils.AZURE_CLIENT_ID);
			return;
		}

		boolean microsoftCredentialsEmpty = microsoftProfileName.isEmpty() || microsoftAccessToken.isEmpty()
				|| microsoftProfileId.isEmpty();

		Launcher.launchMinecraft(microsoftProfileName, microsoftAccessToken, microsoftProfileId,
				microsoftCredentialsEmpty);
	}
}
