package monsterRun.client.model;

import java.awt.KeyboardFocusManager;
import java.io.IOException;
import java.net.UnknownHostException;

import monsterRun.client.controller.GameKeyListener;
import monsterRun.client.model.events.IThemeChanged;
import monsterRun.client.view.renderer.themes.AbstractBoardTheme;
import monsterRun.client.view.renderer.themes.DefaultBoardTheme;
import monsterRun.common.model.Constants;
import monsterRun.common.model.enums.GameBoardType;
import monsterRun.common.model.jevents.JEvent;
import monsterRun.common.model.network.Communicator;

public class Facade {

	private GameBoard gameBoard;
	private GameKeyListener keyListener;
	private Communicator communicator;

	private AbstractBoardTheme theme;

	public final JEvent<IThemeChanged> themeChanged = JEvent
			.create(IThemeChanged.class);

	public Facade() {

		theme = new DefaultBoardTheme();

		// Connection
		communicator = new Communicator(Constants.HOST, Constants.PORT, true);

		// Game Board
		gameBoard = new GameBoard(GameBoardType.DEFAULT, getCommunicator(), this);

		// Key Listener
		keyListener = new GameKeyListener(Constants.KEY_DELAY);

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(keyListener);

		enableKeyListener(false);
	}

	public GameBoard getGameBoard() {
		return gameBoard;
	}

	public GameKeyListener getKeyListener() {
		return keyListener;
	}

	public Communicator getCommunicator() {
		return communicator;
	}

	public AbstractBoardTheme getTheme() {
		return theme;
	}

	public void setTheme(AbstractBoardTheme theme) {
		this.theme = theme;
		themeChanged.get().themeChanged(theme);
	}

	public void enableKeyListener(boolean enable) {
		keyListener.setEnabled(enable);
	}

	public void initializeGameBoard(GameBoardType board) {
		gameBoard.reInitialize(board);
	}

	public void connectToServer() throws UnknownHostException, IOException {
		communicator.connect();
	}

	public void disconnectFromServer() throws IOException {
		communicator.close();
	}
}
