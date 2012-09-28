package monsterRun.client.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import monsterRun.client.controller.KeyEventProcessor;
import monsterRun.client.controller.MonsterFrameMessageListener;
import monsterRun.client.model.Facade;
import monsterRun.client.model.events.IThemeChanged;
import monsterRun.client.view.popuppanels.CreateGamePanel;
import monsterRun.client.view.popuppanels.InitialLoadPanel;
import monsterRun.client.view.popuppanels.PositionPickPanel;
import monsterRun.client.view.popuppanels.ScoreBoardPanel;
import monsterRun.client.view.renderer.RendererPanel;
import monsterRun.client.view.renderer.themes.AbstractBoardTheme;
import monsterRun.client.view.sidebar.GameDetailsPanel;
import monsterRun.common.model.network.NetworkMessage;
import monsterRun.common.view.Preference;

public class MonsterFrame extends JFrame implements IThemeChanged {

	private static final long serialVersionUID = -7205566574493657595L;

	private Facade facade;

	private Toolbar toolbar;
	private JPanel container;
	private RendererPanel canvas;

	private GlassPaneManager glassPane;

	private InitialLoadPanel waitingPanel;
	private ScoreBoardPanel scorebrdPanel;
	private CreateGamePanel createGamePanel;
	private GameDetailsPanel gameDetailsPanel;
	private PositionPickPanel positionPickPanel;

	private KeyEventProcessor keyEventProcessor;
	private MonsterFrameMessageListener messageProcessor;

	public MonsterFrame() throws UnknownHostException, IOException {
		Preference.initialize(this);

		facade = new Facade();

		// Container
		container = (JPanel) this.getContentPane();
		container.setLayout(new BorderLayout());
		container.setBackground(facade.getTheme().getInvalidCellColor());

		// Canvas
		canvas = new RendererPanel(facade);
		container.add(canvas, BorderLayout.CENTER);

		// Glass Pane
		glassPane = new GlassPaneManager((JPanel) this.getGlassPane(), canvas);

		// Toolbar
		toolbar = new Toolbar(this, facade, canvas);
		container.add(toolbar, BorderLayout.NORTH);

		// Game details panel
		gameDetailsPanel = new GameDetailsPanel(facade.getGameBoard(), canvas);
		container.add(gameDetailsPanel, BorderLayout.WEST);

		// Key Listener
		keyEventProcessor = new KeyEventProcessor(facade.getGameBoard());

		// Message processor
		messageProcessor = new MonsterFrameMessageListener(this, canvas, facade);

		// Listeners
		addWindowListener(messageProcessor);

		facade.themeChanged.addListener(this);

		facade.getCommunicator().connectionStatusChanged
				.addListener(messageProcessor);
		facade.getCommunicator().messageReceived.addListener(messageProcessor);
		facade.getKeyListener().moveRequested.addListener(keyEventProcessor);

		// Connect
		facade.connectToServer();
	}

	public void close() {
		try {
			facade.disconnectFromServer();
		} catch (IOException e) {
		}

		System.exit(0);
	}

	@Override
	public void themeChanged(AbstractBoardTheme theme) {
		container.setBackground(theme.getInvalidCellColor());
	}

	public void hidePopups() {
		glassPane.removePopupComponent();
	}

	// Initial Load Panel
	public void showWaitingPanel() {
		if (waitingPanel == null) {
			waitingPanel = new InitialLoadPanel();
		}

		showPopup(waitingPanel);
		glassPane.setDrawShade(false);
	}

	// Create Game
	public void showCreateGamePanel() {
		if (createGamePanel == null) {
			createGamePanel = new CreateGamePanel(facade.getCommunicator());
		}

		showPopup(createGamePanel);
	}

	// Specify Position
	public void showPositionPanel(String senderId, NetworkMessage mess) {
		if (positionPickPanel == null) {
			positionPickPanel = new PositionPickPanel(facade.getCommunicator(),
					this);
			positionPickPanel.messageReceived(senderId, mess);
		}

		showPopup(positionPickPanel);
	}

	// Score board Panel
	public void showScoreBoardPanel(String senderId, NetworkMessage mess) {

		if (scorebrdPanel == null) {
			scorebrdPanel = new ScoreBoardPanel(this, facade.getCommunicator());
			scorebrdPanel.messageReceived(senderId, mess);
		}

		showPopup(scorebrdPanel);
	}

	public void showPopup(Component comp) {
		glassPane.setPopupComponent(comp);
		glassPane.setVisible(true);
	}
}
