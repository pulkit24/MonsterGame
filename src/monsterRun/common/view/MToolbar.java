package monsterRun.common.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import monsterRun.client.view.renderer.RendererPanel;
import monsterRun.common.controller.MSizeAnimationController;
import monsterRun.common.model.ImageStore;

/**
 * 
 * A reusable {@link JToolBar} implementation that has customizable colors and
 * show/hide animations
 */
public class MToolbar extends JToolBar implements KeyEventDispatcher {

	private static final long serialVersionUID = 6512066561214373329L;

	private JButton toggle;
	private JPanel container;

	private Color fill;
	private Color border;

	private boolean pressed;
	private MSizeAnimationController sizeAnimController;

	public MToolbar(RendererPanel renderer, Color fill, Color border) {
		this(renderer, fill, border, 5, 36);
	}

	public MToolbar(RendererPanel renderer, Color fill, Color border,
			int minHeight, int maxHeight) {

		this.fill = fill;
		this.border = border;

		setPreferredSize(new Dimension(this.getWidth(), maxHeight));

		setOpaque(true);
		setFloatable(false);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Color.BLACK));

		container = new JPanel();
		container.setOpaque(false);
		container.setLayout(new FlowLayout(FlowLayout.LEFT));
		add(container, BorderLayout.CENTER);

		toggle = new ImageButton(ImageStore.get().getResizedIcon("dots.png",
				17, 5));
		toggle.setToolTipText("<html>Show/Hide the toolbar<br/>Ctrl+Shift+T</html>");
		toggle.setFocusable(false);
		add(toggle, BorderLayout.EAST);

		sizeAnimController = new MSizeAnimationController(renderer, this, 250,
				this.getWidth(), minHeight, this.getHeight(), maxHeight, false,
				true);

		toggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sizeAnimController.toggle();
			}
		});

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);
	}

	public void setContainerLayout(LayoutManager mgr) {
		container.setLayout(mgr);
	}

	@Override
	public Component add(Component comp) {
		return container.add(comp);
	}

	@Override
	public void remove(Component comp) {
		container.remove(comp);
	}

	@Override
	protected void paintBorder(Graphics g) {
		super.paintBorder(g);

		g.setColor(border);
		g.fillRect(0, 0, 5, this.getHeight());
	}

	@Override
	protected void paintComponent(Graphics g2) {
		super.paintComponent(g2);

		Graphics2D g = G2dUtilities.turnOnAntialiasing((Graphics2D) g2);

		g.setColor(fill);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {

		if (!pressed && e.isControlDown() && e.isShiftDown()
				&& e.getKeyCode() == KeyEvent.VK_T) {

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
