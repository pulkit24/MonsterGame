package monsterRun.client.view.popuppanels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

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

public class PositionPickPanel extends JPanel implements ActionListener,
		IMessageReceivedListener {

	private static final long serialVersionUID = -1302940248939858735L;

	private JButton btnSpecify;

	private JButton btnTopLeft;
	private JButton btnTopRight;
	private JButton btnBottomLeft;
	private JButton btnBottomRight;

	private String selectedCommand;
	private JLabel lblSelctedPosition;

	private ArrayList<JButton> posButtons;

	private int animateDistance;
	private AnimationManager animationManager;
	private PositionAnimationSequence seq1;

	private Communicator comm;
	private MonsterFrame frame;

	public PositionPickPanel(final Communicator communicator, MonsterFrame frame) {
		this.frame = frame;
		this.comm = communicator;

		this.animateDistance = 150;

		setPreferredSize(new Dimension(355, 333));

		setLayout(new BorderLayout(0, 0));

		posButtons = new ArrayList<JButton>();

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Preference.FOOTER_BACKGROUND);
		buttonsPanel.setForeground(Preference.FOOTER_FOREGROUND);
		buttonsPanel.setPreferredSize(new Dimension(0, 45));
		add(buttonsPanel, BorderLayout.SOUTH);
		buttonsPanel.setLayout(null);

		btnSpecify = new JButton("Specify");
		btnSpecify.addActionListener(this);

		btnSpecify.setOpaque(false);
		btnSpecify.setBounds(250 - animateDistance, 5, 100, 35);
		btnSpecify.setFont(Preference.FORMS_FONT);
		buttonsPanel.add(btnSpecify);

		JPanel centrePanel = new JPanel();
		centrePanel.setBorder(new EmptyBorder(5, 75, 5, 75));
		add(centrePanel, BorderLayout.CENTER);
		centrePanel.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		centrePanel.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(3, 3, 0, 0));

		btnTopLeft = new JButton("TL");
		btnTopLeft.setOpaque(false);
		btnTopLeft.setEnabled(false);
		btnTopLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedCommand = ((JButton) e.getSource()).getText();
				lblSelctedPosition.setText("Selected Position: Top Left");
			}
		});

		posButtons.add(btnTopLeft);
		panel.add(btnTopLeft);

		JLabel lblDivider = new JLabel("");
		panel.add(lblDivider);

		btnTopRight = new JButton("TR");
		btnTopRight.setOpaque(false);
		btnTopRight.setEnabled(false);
		btnTopRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedCommand = ((JButton) e.getSource()).getText();
				lblSelctedPosition.setText("Selected Position: Top Right");
			}
		});

		posButtons.add(btnTopRight);
		panel.add(btnTopRight);

		JLabel lblDivider_1 = new JLabel("");
		panel.add(lblDivider_1);

		JLabel lblDivider_2 = new JLabel("");
		panel.add(lblDivider_2);

		JLabel lblDivider_3 = new JLabel("");
		panel.add(lblDivider_3);

		btnBottomLeft = new JButton("BL");
		btnBottomLeft.setOpaque(false);
		btnBottomLeft.setEnabled(false);
		btnBottomLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedCommand = ((JButton) e.getSource()).getText();
				lblSelctedPosition.setText("Selected Position: Bottom Left");
			}
		});

		posButtons.add(btnBottomLeft);
		panel.add(btnBottomLeft);

		JLabel lblDivider_4 = new JLabel("");
		panel.add(lblDivider_4);

		btnBottomRight = new JButton("BR");
		btnBottomRight.setOpaque(false);
		btnBottomRight.setEnabled(false);
		btnBottomRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedCommand = ((JButton) e.getSource()).getText();
				lblSelctedPosition.setText("Selected Position: Bottom Right");
			}
		});

		posButtons.add(btnBottomRight);
		panel.add(btnBottomRight);

		lblSelctedPosition = new JLabel("Selcted Position: None");
		lblSelctedPosition.setHorizontalAlignment(SwingConstants.CENTER);
		lblSelctedPosition.setHorizontalTextPosition(SwingConstants.CENTER);
		lblSelctedPosition.setBorder(new EmptyBorder(0, 0, 5, 0));
		centrePanel.add(lblSelctedPosition, BorderLayout.NORTH);

		JLabel lblSpcfyPos = new JLabel("Specify start position:");
		lblSpcfyPos.setHorizontalAlignment(SwingConstants.CENTER);
		lblSpcfyPos.setOpaque(true);
		lblSpcfyPos.setFont(Preference.FORMS_FONT);
		lblSpcfyPos.setBackground(Preference.HEADER_BACKGROUND);
		lblSpcfyPos.setForeground(Preference.HEADER_FOREGROUND);
		lblSpcfyPos.setPreferredSize(new Dimension(0, 45));
		add(lblSpcfyPos, BorderLayout.NORTH);

		communicator.messageReceived.addListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = selectedCommand;

		if (command != null) {
			int index = 0;

			if (command.equalsIgnoreCase("TL")) {
				index = 0;
			} else if (command.equalsIgnoreCase("TR")) {
				index = 1;
			} else if (command.equalsIgnoreCase("BL")) {
				index = 2;
			} else if (command.equalsIgnoreCase("BR")) {
				index = 3;
			}

			frame.showWaitingPanel();
			comm.sendMessage(MessageFactory.createStartPosResponse(index));
		}
	}

	@Override
	public synchronized void setVisible(final boolean aFlag) {
		super.setVisible(aFlag);

		if (aFlag) {
			Preference.requestDefaultButtonFocus(btnSpecify);

			if (animationManager == null) {
				animationManager = new AnimationManager();

				seq1 = new PositionAnimationSequence(btnSpecify, 1000,
						(250 - animateDistance) + animateDistance, 0, true,
						false, new CubicCalculator());

				animationManager.add(seq1);
			}

			animationManager.start();
		} else {
			animationManager.stop();

			btnSpecify.setBounds(250 - animateDistance, 5, 100, 35);
		}
	}

	@Override
	public void messageReceived(String senderId, NetworkMessage mess) {
		NetworkMessage message = mess.clone();
		MessageID id = message.getId(MessageID.class);

		// Disables all 4 position picking buttons
		for (int i = 0; i < posButtons.size(); i++) {
			posButtons.get(i).setEnabled(false);
		}

		switch (id) {
		case START_POS_REQUEST:
			ArrayList<Integer> availablePos = message.readObject();

			for (int i = 0; i < availablePos.size(); i++) {
				posButtons.get(availablePos.get(i)).setEnabled(true);
			}

			break;
		default:
			break;
		}
	}
}
