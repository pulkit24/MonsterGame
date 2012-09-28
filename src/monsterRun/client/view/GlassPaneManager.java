package monsterRun.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Paint;
import java.awt.event.MouseAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;

import monsterRun.client.view.renderer.RendererPanel;
import monsterRun.common.view.G2dUtilities;
import monsterRun.common.view.Preference;

/**
 * Manages the glass panel of the {@link JFrame} to allow popup components to be
 * added/removed easily
 */
public class GlassPaneManager {

	private Paint p;

	private Color shade;
	private Color shade1;
	private Color shaddow;

	private boolean drawShade;

	private JPanel glassPane;
	private JPanel shadePanel;
	private RendererPanel renderer;

	private GridBagConstraints gbc;
	private Component popupComponent;

	public GlassPaneManager(JPanel glassPane, RendererPanel renderer) {
		this.renderer = renderer;
		this.glassPane = glassPane;

		this.glassPane.setLayout(new BorderLayout());

		// White Shade
		this.shade = new Color(255, 255, 255, 210);
		this.shade1 = new Color(255, 255, 255, 170);
		this.shaddow = new Color(0, 0, 0, 120);

		// The overlay panel with shade
		this.shadePanel = new JPanel(new GridBagLayout()) {
			private static final long serialVersionUID = 1898209537162949900L;

			@Override
			public void paintComponent(Graphics g2) {
				super.paintComponent(g2);
				Graphics2D g = G2dUtilities.turnOnAntialiasing((Graphics2D) g2);

				p = new GradientPaint(0, 0, shade1, 0, this.getHeight() / 2,
						shade, true);
				g.setPaint(p);

				g.fillRect(0, 0, this.getWidth(), this.getHeight());

				if (popupComponent != null && drawShade) {
					g.setColor(shaddow);
					g.fillRect(popupComponent.getX() - 5,
							popupComponent.getY() - 5,
							popupComponent.getWidth() + 10,
							popupComponent.getHeight() + 10);
				}
			}
		};

		this.gbc = new GridBagConstraints();
		this.gbc.anchor = GridBagConstraints.CENTER;
		this.gbc.weighty = 1;

		this.shadePanel.setOpaque(false);
		this.glassPane.add(shadePanel);

		// Hack to fix click through
		this.shadePanel.addMouseListener(new MouseAdapter() {
		});
	}

	public void setPopupComponent(Component component) {
		synchronized (this) {
			removePopupComponent();

			popupComponent = component;
			shadePanel.add(component, gbc);

			setDrawShade(true);
			popupComponent.setVisible(true);
			shadePanel.repaint();

			setVisible(true);
		}
	}

	public void removePopupComponent() {
		synchronized (this) {
			if (popupComponent != null) {
				popupComponent.setVisible(false);
				shadePanel.remove(popupComponent);
				popupComponent = null;
			}

			setVisible(false);
		}
	}

	public void setDrawShade(boolean draw) {
		drawShade = draw;
	}

	public void setVisible(boolean isVisible) {
		setVisible(isVisible, true);
	}

	public void setVisible(boolean isVisible, boolean propogate) {
		glassPane.setVisible(isVisible);
		renderer.pauseRendering(isVisible);

		if (propogate) {
			if (popupComponent != null) {
				popupComponent.setVisible(isVisible);
			}
		}

		Preference.repaintFrame();
		Preference.validateFrame();
	}
}
