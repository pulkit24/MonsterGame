package monsterRun.client.view.popuppanels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import monsterRun.client.view.MonsterFrame;
import monsterRun.common.model.MessageFactory;
import monsterRun.common.model.enums.MessageID;
import monsterRun.common.model.janimationframework.algorithms.CubicCalculator;
import monsterRun.common.model.janimationframework.controllers.PositionAnimationSequence;
import monsterRun.common.model.janimationframework.implementation.AnimationManager;
import monsterRun.common.model.network.Communicator;
import monsterRun.common.model.network.NetworkMessage;
import monsterRun.common.model.network.events.IMessageReceivedListener;
import monsterRun.common.view.Preference;

public class ScoreBoardPanel extends JPanel implements IMessageReceivedListener {
	private static final long serialVersionUID = -1302940248939858735L;

	private JPanel tp;

	private JButton btnPlay;
	private JButton btnExit;
	private JLabel winnerLabel;

	private int animateDistance;
	private AnimationManager animationManager;
	private PositionAnimationSequence seq1, seq2;

	public ScoreBoardPanel(final MonsterFrame frame,
			final Communicator communicator) {

		this.animateDistance = 100;

		setPreferredSize(new Dimension(355, 197));

		setLayout(new BorderLayout(0, 0));

		winnerLabel = new JLabel();
		winnerLabel.setFont(Preference.GLOBAL_FONT);
		winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Preference.FOOTER_BACKGROUND);
		buttonsPanel.setForeground(Preference.FOOTER_FOREGROUND);
		buttonsPanel.setPreferredSize(new Dimension(0, 45));
		add(buttonsPanel, BorderLayout.SOUTH);
		buttonsPanel.setLayout(null);

		btnExit = new JButton("Close Game");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.close();
			}
		});

		btnExit.setOpaque(false);
		btnExit.setBounds(3, 5, 127, 35);
		btnExit.setFont(Preference.FORMS_FONT);
		buttonsPanel.add(btnExit);

		btnPlay = new JButton("Play Again");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.showWaitingPanel();
				communicator.sendMessage(MessageFactory.createJoinGameRequest());
			}
		});

		btnPlay.setOpaque(false);
		btnPlay.setBounds(133, 5, 115, 35);
		btnPlay.setFont(Preference.FORMS_FONT);
		buttonsPanel.add(btnPlay);

		JPanel centrePanel = new JPanel();
		add(centrePanel, BorderLayout.CENTER);
		centrePanel.setLayout(new BorderLayout(0, 0));

		tp = new JPanel();
		tp.setBorder(null);
		centrePanel.add(tp, BorderLayout.CENTER);
		tp.setLayout(new BorderLayout(0, 0));

		tp.add(winnerLabel, BorderLayout.CENTER);

		communicator.messageReceived.addListener(this);

		JLabel lblGameFin = new JLabel("Game Finished:");
		lblGameFin.setHorizontalAlignment(SwingConstants.CENTER);
		lblGameFin.setOpaque(true);
		lblGameFin.setFont(Preference.FORMS_FONT);
		lblGameFin.setBackground(Preference.HEADER_BACKGROUND);
		lblGameFin.setForeground(Preference.HEADER_FOREGROUND);
		lblGameFin.setPreferredSize(new Dimension(0, 45));
		add(lblGameFin, BorderLayout.NORTH);
	}

	@Override
	public synchronized void setVisible(final boolean aFlag) {
		super.setVisible(aFlag);

		if (aFlag) {
			Preference.requestDefaultButtonFocus(btnPlay);

			if (animationManager == null) {
				animationManager = new AnimationManager();

				seq1 = new PositionAnimationSequence(btnPlay, 1000, 233, 0,
						true, false, new CubicCalculator());
				seq2 = new PositionAnimationSequence(btnExit, 800, 103, 0,
						true, false, new CubicCalculator());

				animationManager.add(seq1);
				animationManager.add(seq2, 50);
			}

			animationManager.start();
		} else {
			animationManager.stop();

			btnPlay.setBounds(233 - animateDistance, 5, 115, 35);
			btnExit.setBounds(103 - animateDistance, 5, 127, 35);
		}
	}

	@Override
	public void messageReceived(String senderId, NetworkMessage mess) {
		NetworkMessage message = mess.clone();
		MessageID id = message.getId(MessageID.class);

		switch (id) {
		case GAME_FINISHED:

			// Reads the score board variables
			String winnerId = message.readString();

			winnerLabel.setText("Winner: " + winnerId);

			repaint();
			validate();

			break;
		default:
			break;
		}
	}
}
