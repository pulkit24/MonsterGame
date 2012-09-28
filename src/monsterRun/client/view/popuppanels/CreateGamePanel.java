package monsterRun.client.view.popuppanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import monsterRun.common.model.Constants;
import monsterRun.common.model.MessageFactory;
import monsterRun.common.model.enums.GameBoardType;
import monsterRun.common.model.enums.GameMode;
import monsterRun.common.model.enums.MessageID;
import monsterRun.common.model.janimationframework.algorithms.CubicCalculator;
import monsterRun.common.model.janimationframework.controllers.PositionAnimationSequence;
import monsterRun.common.model.janimationframework.implementation.AnimationManager;
import monsterRun.common.model.network.Communicator;
import monsterRun.common.model.network.NetworkMessage;
import monsterRun.common.model.network.events.IMessageReceivedListener;
import monsterRun.common.view.Preference;

public class CreateGamePanel extends JPanel implements ActionListener,
		IMessageReceivedListener {
	private static final long serialVersionUID = -1302940248939858735L;

	private JButton btnOk;
	private JLabel lblError;
	private JSpinner spinner;

	private Communicator communicator;

	private int animateDistance;
	private AnimationManager animationManager;
	private PositionAnimationSequence seq2;

	private ButtonGroup modeBtnGroup;
	private ButtonGroup boardBtnGroup;

	public CreateGamePanel(Communicator communicator) {
		this.communicator = communicator;
		this.animateDistance = 180;

		setPreferredSize(new Dimension(355, 337));

		setLayout(new BorderLayout(0, 0));

		boardBtnGroup = new ButtonGroup();
		modeBtnGroup = new ButtonGroup();

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Preference.FOOTER_BACKGROUND);
		buttonsPanel.setForeground(Preference.FOOTER_FOREGROUND);
		buttonsPanel.setPreferredSize(new Dimension(0, 45));
		add(buttonsPanel, BorderLayout.SOUTH);
		buttonsPanel.setLayout(null);

		btnOk = new JButton("OK");
		btnOk.setOpaque(false);
		btnOk.setBounds(250 - animateDistance, 5, 100, 35);
		btnOk.setFont(Preference.FORMS_FONT);
		btnOk.addActionListener(this);

		buttonsPanel.add(btnOk);

		JPanel centrePanel = new JPanel();
		add(centrePanel, BorderLayout.CENTER);
		GridBagLayout gbl_centrePanel = new GridBagLayout();
		gbl_centrePanel.columnWidths = new int[] { 20, 151, 87, 0, 20, 0 };
		gbl_centrePanel.rowHeights = new int[] { 20, 0, 0, 37, 0, 0 };
		gbl_centrePanel.columnWeights = new double[] { 1.0, 0.0, 1.0, 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_centrePanel.rowWeights = new double[] { 0.0, 1.0, 1.0, 0.0, 0.0,
				Double.MIN_VALUE };
		centrePanel.setLayout(gbl_centrePanel);

		lblError = new JLabel("Error Panel");
		lblError.setVisible(false);
		lblError.setBorder(new EmptyBorder(2, 2, 2, 2));
		lblError.setForeground(Color.RED);
		GridBagConstraints gbc_lblError = new GridBagConstraints();
		gbc_lblError.gridwidth = 3;
		gbc_lblError.insets = new Insets(0, 0, 5, 5);
		gbc_lblError.gridx = 1;
		gbc_lblError.gridy = 0;
		centrePanel.add(lblError, gbc_lblError);

		JLabel lblGameBoard = new JLabel("Game Board: ");
		lblGameBoard.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		GridBagConstraints gbc_lblGameBoard = new GridBagConstraints();
		gbc_lblGameBoard.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblGameBoard.insets = new Insets(0, 0, 5, 5);
		gbc_lblGameBoard.gridx = 1;
		gbc_lblGameBoard.gridy = 1;
		centrePanel.add(lblGameBoard, gbc_lblGameBoard);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 1;
		centrePanel.add(panel, gbc_panel);
		panel.setLayout(new GridLayout(3, 1, 0, 0));

		// Add Board Types Radio buttons
		for (GameBoardType m : GameBoardType.values()) {
			JRadioButton rdbtnType = new JRadioButton(m.toString()
					.toLowerCase());
			rdbtnType.setActionCommand(m.ordinal() + "");
			boardBtnGroup.add(rdbtnType);
			panel.add(rdbtnType);
			rdbtnType.setSelected(true);
		}

		JLabel lblGameMode = new JLabel("Game Mode: ");
		lblGameMode.setBorder(null);
		lblGameMode.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		GridBagConstraints gbc_lblGameMode = new GridBagConstraints();
		gbc_lblGameMode.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblGameMode.insets = new Insets(0, 0, 5, 5);
		gbc_lblGameMode.gridx = 1;
		gbc_lblGameMode.gridy = 2;
		centrePanel.add(lblGameMode, gbc_lblGameMode);

		JPanel gameModesPanel = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.anchor = GridBagConstraints.NORTH;
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.gridx = 2;
		gbc_panel_1.gridy = 2;
		centrePanel.add(gameModesPanel, gbc_panel_1);
		gameModesPanel.setLayout(new GridLayout(2, 1, 0, 0));

		// Add Game Modes Radio buttons
		for (GameMode m : GameMode.values()) {
			JRadioButton rdbtnMode = new JRadioButton(m.toString()
					.toLowerCase());
			modeBtnGroup.add(rdbtnMode);
			rdbtnMode.setActionCommand(m.ordinal() + "");
			gameModesPanel.add(rdbtnMode);

			rdbtnMode.setSelected(true);
		}

		JLabel lblPlayers = new JLabel("Number of Players: ");
		GridBagConstraints gbc_lblPlayers = new GridBagConstraints();
		gbc_lblPlayers.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblPlayers.insets = new Insets(0, 0, 5, 5);
		gbc_lblPlayers.gridx = 1;
		gbc_lblPlayers.gridy = 3;
		centrePanel.add(lblPlayers, gbc_lblPlayers);
		lblPlayers.setFont(Preference.FORMS_FONT);

		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(Constants.MIN_PLAYERS,
				Constants.MIN_PLAYERS, Constants.MAX_PLAYERS, 1));
		spinner.setMinimumSize(new Dimension(40, 25));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.fill = GridBagConstraints.BOTH;
		gbc_spinner.insets = new Insets(0, 0, 5, 5);
		gbc_spinner.gridx = 2;
		gbc_spinner.gridy = 3;
		centrePanel.add(spinner, gbc_spinner);

		JLabel lblNewLabel = new JLabel(" ");
		lblNewLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 3;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 4;
		centrePanel.add(lblNewLabel, gbc_lblNewLabel);

		JLabel lblLogin = new JLabel("Create Game:");
		lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogin.setOpaque(true);
		lblLogin.setFont(Preference.FORMS_FONT);
		lblLogin.setBackground(Preference.HEADER_BACKGROUND);
		lblLogin.setForeground(Preference.HEADER_FOREGROUND);
		lblLogin.setPreferredSize(new Dimension(0, 45));
		add(lblLogin, BorderLayout.NORTH);

		communicator.messageReceived.addListener(this);
	}

	@Override
	public synchronized void setVisible(final boolean aFlag) {
		super.setVisible(aFlag);

		lblError.setVisible(false);

		if (aFlag) {
			Preference.requestDefaultButtonFocus(btnOk);

			if (animationManager == null) {
				animationManager = new AnimationManager();

				seq2 = new PositionAnimationSequence(btnOk, 800,
						(250 - animateDistance) + animateDistance, 0, true,
						false, new CubicCalculator());

				animationManager.add(seq2);
			}

			animationManager.start();
		} else {
			animationManager.stop();

			btnOk.setBounds(250 - animateDistance, 5, 100, 35);
		}
	}

	public void setError(final String err) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				lblError.setText(err);
				lblError.setVisible(true);
				lblError.repaint();
				repaint();
			}
		});

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		int num = (Integer) spinner.getValue();

		// Parses the board type from the game creation panel
		String boardString = boardBtnGroup.getSelection().getActionCommand();
		int boardInt = Integer.parseInt(boardString);
		GameBoardType board = GameBoardType.values()[boardInt];

		// Parses the mode type from the game creation panel
		String modeString = modeBtnGroup.getSelection().getActionCommand();
		int modeInt = Integer.parseInt(modeString);
		GameMode mode = GameMode.values()[modeInt];

		// Sends the game details to the server
		NetworkMessage m = MessageFactory.createGameDetails(num, board, mode);
		communicator.sendMessage(m);
	}

	@Override
	public void messageReceived(String senderId, NetworkMessage mess) {
		NetworkMessage message = mess.clone();
		MessageID id = message.getId(MessageID.class);

		switch (id) {
		case INVALID_INPUT:
			this.setError(message.readString());
			break;
		default:
			break;
		}
	}
}
