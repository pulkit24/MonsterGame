package monsterRun.common.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import monsterRun.client.view.renderer.RendererPanel;
import monsterRun.common.controller.MSizeAnimationController;
import monsterRun.common.model.ImageStore;

/**
 *
 * A reusable side bar panel that has hide/show toggle animations
 */
public class LeftSidebar extends JPanel implements KeyEventDispatcher {
	private static final long serialVersionUID = 1278348005409645277L;

	private JPanel togglePnl;

	private boolean pressed;
	private MSizeAnimationController sizeAnimController;

	public LeftSidebar(RendererPanel renderer, Color fill, Color border) {
		this(renderer, fill, border, 5, 36);
	}

	public LeftSidebar(RendererPanel renderer, Color fill, Color border,
			int minWidth, int maxWidth) {

		setOpaque(true);
		setBackground(fill);

		setPreferredSize(new Dimension(maxWidth, getHeight()));
		setLayout(new BorderLayout(0, 0));

		togglePnl = new JPanel();
		FlowLayout flowLayout = (FlowLayout) togglePnl.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		togglePnl.setBorder(null);
		togglePnl.setAlignmentY(0.0f);
		togglePnl.setAlignmentX(0.0f);
		togglePnl.setOpaque(true);
		togglePnl.setPreferredSize(new Dimension(5, 0));
		togglePnl.setBackground(border);
		add(togglePnl, BorderLayout.EAST);

		JLabel lblIcon = new JLabel("");
		lblIcon.setAlignmentY(0.0f);
		lblIcon.setIconTextGap(0);
		lblIcon.setPreferredSize(new Dimension(5, 20));
		ImageIcon icon = ImageStore.get().getResizedIcon("dots2.png", 5, 17);
		lblIcon.setIcon(icon);
		togglePnl.add(lblIcon);
		togglePnl
				.setToolTipText("<html>Show/Hide the toolbar<br/>Ctrl+Shift+S</html>");

		sizeAnimController = new MSizeAnimationController(renderer, this, 350,
				5, getHeight(), 165, this.getHeight(), true, false);

		togglePnl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				sizeAnimController.toggle();
			}
		});

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (!pressed && e.isControlDown() && e.isShiftDown()
				&& e.getKeyCode() == KeyEvent.VK_S) {

			if (e.getID() == KeyEvent.KEY_PRESSED) {
				pressed = true;
			}

			sizeAnimController.toggle();

			return true;
		}

		if (e.getID() == KeyEvent.KEY_RELEASED) {
			pressed = false;
		}

		return false;
	}
}
