package monsterRun.client.view.sidebar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import monsterRun.client.model.GameBoard;
import monsterRun.client.model.entities.AbstractEntity;
import monsterRun.client.model.entities.PlayerEntity;
import monsterRun.client.model.events.IGameStateChanged;
import monsterRun.client.model.events.ILifeCountChanged;
import monsterRun.client.view.renderer.RendererPanel;
import monsterRun.common.model.Constants;
import monsterRun.common.model.ImageStore;
import monsterRun.common.view.LeftSidebar;
import monsterRun.common.view.Preference;

public class GameDetailsPanel extends LeftSidebar implements IGameStateChanged,
		ILifeCountChanged {

	private static final long serialVersionUID = 1278348005409645277L;

	private GameBoard board;

	private JLabel lblMe;
	private JLabel lblLives;
	private JLabel lblMeicon;
	private JPanel otherPlayersPanel;

	public GameDetailsPanel(GameBoard board, RendererPanel renderer) {
		super(renderer, Preference.FOOTER_BACKGROUND_DARK,
				Preference.FOOTER_BACKGROUND, 5, 150);

		this.board = board;

		setForeground(Preference.FOOTER_FOREGROUND);

		setPreferredSize(new Dimension(150, 325));

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panel_1.setPreferredSize(new Dimension(10, 60));
		panel.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BorderLayout(0, 0));

		lblMe = new JLabel("Me");
		lblMe.setOpaque(true);
		lblMe.setBackground(Preference.FOOTER_BACKGROUND);
		lblMe.setForeground(Preference.HEADER_FOREGROUND);
		panel_1.add(lblMe, BorderLayout.NORTH);

		JPanel panel_4 = new JPanel();
		panel_4.setPreferredSize(new Dimension(10, 50));
		panel_4.setOpaque(false);
		panel_1.add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new BorderLayout(0, 0));

		lblMeicon = new JLabel("");
		lblMeicon.setBorder(new EmptyBorder(5, 5, 5, 5));
		lblMeicon.setPreferredSize(new Dimension(50, 50));
		panel_4.add(lblMeicon, BorderLayout.WEST);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new MatteBorder(0, 1, 0, 0,
				Preference.FOOTER_BACKGROUND));
		panel_3.setOpaque(false);
		panel_4.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new GridLayout(3, 0, 0, 0));

		lblLives = new JLabel("Lives");
		lblLives.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		lblLives.setForeground(Preference.FOOTER_FOREGROUND);
		panel_3.add(new JLabel());
		panel_3.add(lblLives);

		JPanel panel_2 = new JPanel();
		panel_2.setOpaque(false);
		panel.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		JLabel lblOthers = new JLabel("Others");
		lblOthers.setOpaque(true);
		lblOthers.setBackground(Preference.FOOTER_BACKGROUND);
		lblOthers.setForeground(Preference.HEADER_FOREGROUND);
		panel_2.add(lblOthers, BorderLayout.NORTH);

		otherPlayersPanel = new JPanel();
		otherPlayersPanel.setOpaque(false);
		panel_2.add(otherPlayersPanel, BorderLayout.CENTER);
		otherPlayersPanel.setLayout(new BoxLayout(otherPlayersPanel,
				BoxLayout.Y_AXIS));

		board.gameStateChanged.addListener(this);
	}

	@Override
	public void playerAdded(AbstractEntity entity) {
		otherPlayersPanel.removeAll();

		List<AbstractEntity> es = board.getAllEntities();
		Dimension dim = new Dimension((int) otherPlayersPanel
				.getPreferredSize().getWidth(), 20);

		for (AbstractEntity e : es) {
			if (e instanceof PlayerEntity) {
				OtherPlayerLabel opl = new OtherPlayerLabel();
				opl.setPreferredSize(dim);
				opl.initialize((PlayerEntity) e);

				otherPlayersPanel.add(opl);
			}
		}

		otherPlayersPanel.repaint();
		otherPlayersPanel.validate();
	}

	@Override
	public void playerRemoved(AbstractEntity entity) {
		for (int i = 0; i < otherPlayersPanel.getComponentCount(); i++) {
			OtherPlayerLabel opl = (OtherPlayerLabel) otherPlayersPanel
					.getComponent(i);

			if (opl.getPlayerEntity() == entity) {
				opl.kill();
			}
		}
	}

	@Override
	public void lifeCountChanged(PlayerEntity player, int count) {
		lblLives.setText(" x " + count);
	}

	@Override
	public void currentPlayerDied() {
		lblLives.setText(" [Died]");
	}

	@Override
	public void currentPlayerAdded(PlayerEntity player) {

		Dimension dim = lblMeicon.getPreferredSize();

		Image playerIcon = ImageStore.get().resizeImage(player.getSprite(4),
				dim.width - 10, dim.height - 10);

		lblMe.setText("Me: " + player.getName());
		lblMeicon.setIcon(new ImageIcon(playerIcon));

		ImageIcon heart = ImageStore.get().getResizedIcon("heart.png", 20, 20);
		lblLives.setIcon(heart);

		lblLives.setText(" x " + Constants.MAX_LIVES);

		player.lifeCountChanged.addListener(this);
	}

	@Override
	public void currentPlayerLoggedIn(PlayerEntity player) {
		currentPlayerAdded(player);
	}

	@Override
	public void gameStarted() {
	}
}
