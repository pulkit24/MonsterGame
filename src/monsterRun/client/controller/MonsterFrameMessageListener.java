package monsterRun.client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import monsterRun.client.model.Facade;
import monsterRun.client.view.MonsterFrame;
import monsterRun.client.view.renderer.RendererPanel;
import monsterRun.common.model.MessageFactory;
import monsterRun.common.model.enums.GameBoardType;
import monsterRun.common.model.enums.MessageID;
import monsterRun.common.model.network.Communicator;
import monsterRun.common.model.network.NetworkMessage;
import monsterRun.common.model.network.events.IConnectionStatusChangedListener;
import monsterRun.common.model.network.events.IMessageReceivedListener;

/**
 *  Listens to {@link NetworkMessage}s from the server
 */
public class MonsterFrameMessageListener extends WindowAdapter implements
		IMessageReceivedListener, IConnectionStatusChangedListener {

	private Facade facade;
	private MonsterFrame frame;
	private RendererPanel renderer;

	private boolean serverFull;

	public MonsterFrameMessageListener(MonsterFrame frame,
			RendererPanel renderer, Facade facade) {

		this.frame = frame;
		this.facade = facade;
		this.renderer = renderer;
	}

	@Override
	public void messageReceived(String senderId, NetworkMessage mess) {
		NetworkMessage message = mess.clone();
		MessageID id = message.getId(MessageID.class);

		switch (id) {
		case GAME_FINISHED:
			frame.showScoreBoardPanel(senderId, message);
			break;
		case SPECIFY_GAME_DETAILS:
			frame.showCreateGamePanel();
			break;
		case START_POS_REQUEST:
			frame.showPositionPanel(senderId, message);
			break;
		case SPECIFY_PLAYERS_DONE:
			frame.hidePopups();
			frame.showWaitingPanel();
			break;
		case GAME_STARTED:

			GameBoardType boardType = message.readEnum(GameBoardType.class);

			facade.initializeGameBoard(boardType);
			renderer.redrawBoard();
			frame.hidePopups();
			facade.enableKeyListener(true);
			break;
		case CLIENT_DETAILS:

			Communicator communicator = facade.getCommunicator();
			NetworkMessage m = MessageFactory.createDetailsResponse();
			communicator.sendMessage(m);

			break;
		case SERVER_FULL:

			serverFull = true;

			JOptionPane op = new JOptionPane(
					"The Server is Full.\nPress OK to close the Application.",
					JOptionPane.WARNING_MESSAGE);

			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});

			op.setOptions(new JButton[] { okButton });

			frame.showPopup(op);

			break;
		default:
			break;
		}
	}

	@Override
	public void connectionLost(String clientId) {

		if (serverFull) {
			return;
		}

		JOptionPane op = new JOptionPane(
				"The Server exited.\nPress OK to close the Application.",
				JOptionPane.WARNING_MESSAGE);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		op.setOptions(new JButton[] { okButton });

		frame.showPopup(op);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		frame.close();
	}
}
