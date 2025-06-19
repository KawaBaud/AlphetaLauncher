
package io.github.KawaBaud.launcher.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.swing.JPanel;

public class LauncherPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static LauncherPanel instance;
	private final transient Image lightDirtBgImg;
	private transient BufferedImage bImg;

	public LauncherPanel() {
		super(new GridBagLayout(), true);

		setInstance(this);

		URL lightDirtBgImgUrl = Optional
				.ofNullable(this.getClass().getClassLoader().getResource("assets/ui/light_dirt_background.png"))
				.orElseThrow(() -> new NullPointerException("lightDirtBgImgUrl cannot be null"));
		this.lightDirtBgImg = this.getToolkit().getImage(lightDirtBgImgUrl);

		this.setBackground(Color.BLACK);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		this.add(new YggdrasilAuthPanel(), gbc);
	}

	public static LauncherPanel getInstance() {
		return instance;
	}

	private static void setInstance(LauncherPanel lp) {
		instance = lp;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int panelWidth = this.getWidth();
		int panelHeight = this.getHeight();
		int lightDirtBgImgWidth = this.lightDirtBgImg.getWidth(this) << 1;
		int lightDirtBgImgHeight = this.lightDirtBgImg.getHeight(this) << 1;

		Graphics2D g2d = (Graphics2D) g;
		GraphicsConfiguration configuration = g2d.getDeviceConfiguration();

		if ((this.bImg == null)
				|| (this.bImg.getWidth(this) != panelWidth || this.bImg.getHeight(this) != panelHeight)) {
			this.bImg = configuration.createCompatibleImage(panelWidth >> 1, panelHeight >> 1, Transparency.OPAQUE);
		}

		Graphics2D g2dBuffered = this.bImg.createGraphics();
		g2dBuffered.setComposite(AlphaComposite.Clear);
		g2dBuffered.fillRect(0, 0, panelWidth, panelHeight);
		g2dBuffered.setComposite(AlphaComposite.SrcOver);

		try {
			int gridWidth = (panelWidth + lightDirtBgImgWidth) >> 5;
			int gridHeight = (panelHeight + lightDirtBgImgHeight) >> 5;
			IntStream.range(0, (gridWidth * gridHeight)).forEach(i -> {
				int gridX = (i % gridWidth) << 5;
				int gridY = (i / gridWidth) << 5;
				g2dBuffered.drawImage(this.lightDirtBgImg, gridX, gridY, lightDirtBgImgWidth, lightDirtBgImgHeight,
						this);
			});

			this.drawTitleString("Alpheta Launcher", panelWidth, panelHeight, g2dBuffered);
		} finally {
			g2dBuffered.dispose();
		}

		g2d.drawImage(bImg, 0, 0, panelWidth, panelHeight, this);
	}

	private void drawTitleString(String s, int width, int height, Graphics2D g2d) {
		g2d.setFont(this.getFont().deriveFont(Font.BOLD, 20f));
		g2d.setColor(Color.LIGHT_GRAY);

		FontMetrics fm = g2d.getFontMetrics();
		int titleWidth = fm.stringWidth(s);
		int titleHeight = fm.getHeight();
		int titleX = (width >> 1 >> 1) - (titleWidth >> 1);
		int titleY = (height >> 1 >> 1) - (titleHeight << 1);
		g2d.drawString(s, titleX, titleY);
	}
}
