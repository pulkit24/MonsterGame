package monsterRun.client.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;

import monsterRun.client.model.Facade;
import monsterRun.client.view.renderer.RendererPanel;
import monsterRun.client.view.renderer.themes.AbstractBoardTheme;
import monsterRun.client.view.renderer.themes.BlueBoardTheme;
import monsterRun.client.view.renderer.themes.DarkBoardTheme;
import monsterRun.client.view.renderer.themes.DefaultBoardTheme;
import monsterRun.client.view.renderer.themes.GreenBoardTheme;
import monsterRun.client.view.renderer.themes.LightBoardTheme;
import monsterRun.client.view.renderer.themes.RedBoardTheme;
import monsterRun.common.model.ImageStore;
import monsterRun.common.model.MessageFactory;
import monsterRun.common.model.enums.MessageID;
import monsterRun.common.model.network.NetworkMessage;
import monsterRun.common.model.network.events.IMessageReceivedListener;
import monsterRun.common.view.MToolbar;
import monsterRun.common.view.Preference;

public class Toolbar extends MToolbar implements IMessageReceivedListener {
	private static final long serialVersionUID = 6512066561214373329L;

	private JButton newGameButton;
	private JComboBox themeSwitcher;
	private JButton fullScreenButton;

	private AbstractBoardTheme themes[];

	public Toolbar(final MonsterFrame frame, final Facade facade,
			final RendererPanel renderer) {

		super(renderer, Preference.HEADER_BACKGROUND,
				Preference.HEADER_BACKGROUND_DARK);

		// New Game
		newGameButton = new JButton("Join Game");
		newGameButton.setOpaque(false);
		newGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newGameButton.setEnabled(false);

				frame.showWaitingPanel();
				facade.getCommunicator().sendMessage(
						MessageFactory.createJoinGameRequest());
			}
		});

		add(newGameButton);

		// Theme switcher
		themes = new AbstractBoardTheme[] { new DefaultBoardTheme(),
				new BlueBoardTheme(), new DarkBoardTheme(),
				new LightBoardTheme(), new GreenBoardTheme(),
				new RedBoardTheme() };

		themeSwitcher = new JComboBox(themes);
		themeSwitcher.setOpaque(false);
		themeSwitcher.setFocusable(false);
		themeSwitcher.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				facade.setTheme((AbstractBoardTheme) themeSwitcher
						.getSelectedItem());
			}
		});

		add(themeSwitcher);

		// Full screen
		fullScreenButton = new JButton(ImageStore.get().getIcon(
				"fullscreen.png"));
		fullScreenButton.setOpaque(false);
		fullScreenButton.setToolTipText("Enter/Exit Fullscreen mode");
		fullScreenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Preference.setFullscreen(!Preference.isFullscreen());
			}
		});

		add(fullScreenButton);

		facade.getCommunicator().messageReceived.addListener(this);
	}

	@Override
	public void messageReceived(String senderId, NetworkMessage mess) {

		NetworkMessage message = mess.clone();
		MessageID id = message.getId(MessageID.class);

		switch (id) {
		case GAME_STARTED:
			// newGameButton.setEnabled(false);
			break;
		case GAME_FINISHED:
			// newGameButton.setEnabled(true);
			break;
		default:
			break;
		}
	}
}
