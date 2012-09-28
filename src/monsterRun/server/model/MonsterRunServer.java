package monsterRun.server.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBException;

import monsterRun.common.model.Constants;
import monsterRun.common.model.MessageFactory;
import monsterRun.common.model.Position;
import monsterRun.common.model.enums.Direction;
import monsterRun.common.model.jevents.JEvent;
import monsterRun.common.model.network.Communicator;
import monsterRun.common.model.network.NetworkMessage;
import monsterRun.server.controller.MessageListener;
import monsterRun.server.model.entities.Player;
import monsterRun.server.model.events.IGameFinishedListener;
import monsterRun.server.model.events.ILifeCountChangedListener;
import monsterRun.server.model.events.IMonsterMoveListener;
import monsterRun.server.model.events.IPlayerDeadListener;
import monsterRun.server.model.events.IServerStatusChangedListener;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

/**
 * Class representing the server running the game
 * 
 */
public class MonsterRunServer extends Thread implements IMonsterMoveListener,
		IPlayerDeadListener, IGameFinishedListener, ILifeCountChangedListener {
	private int port;
	private ServerSocket serverSocket;
	private Game game;
	/** Mutex for synchronization */
	private Mutex mutex;
	private ArrayList<Player> authenticatedPlayers;
	/** Event fired when the server state changes */
	public final JEvent<IServerStatusChangedListener> serverStatusChanged = JEvent
			.create(IServerStatusChangedListener.class);

	/**
	 * Constructor
	 * 
	 * @param port
	 *            the port the server is running on
	 * @throws JAXBException
	 * @throws IOException
	 */
	public MonsterRunServer(int port) throws JAXBException, IOException {
		this.port = port;
		mutex = new Mutex();
		// Accounts manager to handle accounts data
		restartGame();
	}

	public boolean hasGameStarted() {
		return game.hasStarted();
	}

	public void releaseMutex() {
		mutex.release();
	}

	public void acquireMutex() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
		}
	}

	public int getClientCount() {
		synchronized (authenticatedPlayers) {
			return authenticatedPlayers.size();
		}
	}

	/**
	 * Remove client from list of authenticated clients
	 * 
	 * @param email
	 *            the email of the client to be removed
	 */
	public void removeClient(String userID) {
		synchronized (authenticatedPlayers) {
			for (Player player : authenticatedPlayers) {
				if (player.getId().equals(userID)) {
					authenticatedPlayers.remove(player);
					break;
				}
			}
		}
	}

	public boolean isPlayerAlive(String playerId) {
		GameBoard board = game.getBoard();
		if (board == null) {
			return false;
		} else {
			return board.isPlayerAlive(playerId);
		}
	}

	public synchronized boolean isFirstClient() {
		synchronized (authenticatedPlayers) {
			return authenticatedPlayers.size() == 0;
		}
	}

	public synchronized boolean isServerFull() {
		return game.hasStarted();
	}

	public synchronized boolean isLastPlayer() {
		return getClientCount() == game.getRequestedNumPlayers();
	}

	/**
	 * Get the player with the given unique id
	 * 
	 * @param id
	 *            the unique id being searched for
	 * @return Player object which is the player with the specified unique id
	 */
	public Player getPlayerWithId(String id) {
		synchronized (authenticatedPlayers) {
			for (Player player : authenticatedPlayers) {
				if (player.getId().equals(id)) {
					return player;
				}
			}
			return null;
		}
	}

	public ArrayList<Player> getPlayers() {
		synchronized (authenticatedPlayers) {
			return authenticatedPlayers;
		}
	}

	public void setPlayers(ArrayList<Player> players) {
		synchronized (players) {
			authenticatedPlayers = players;
		}
	}

	/**
	 * Send message to connecting client that the server is full, if the server
	 * is full
	 * 
	 * @param comm
	 *            the communicator between the client and the server
	 * @return boolean value to indicate if the sending was successful
	 */
	public boolean trySendServerFull(Communicator comm) {
		if (isServerFull()) {
			comm.sendMessage(MessageFactory.createServerFull());
			comm.close();
			if (Constants.IS_DEBUG) {
				System.out.println("Server full");
				System.out.println("Exited: " + getClientCount());
			}
			return true;
		}
		return false;
	}

	/**
	 * Add player or client to the list of authenticated players
	 * 
	 * @param comm
	 *            the communicator object between the player and the server
	 * @param id
	 *            the unique id of the player
	 * @param posIndex
	 *            the index of the position selected by the player
	 * @return boolean value indicating if the addition was successful
	 */
	public boolean addPlayer(Communicator comm, String id, int posIndex) {
		// Synchronize list of players
		synchronized (authenticatedPlayers) {
			// Handle the situation if server is full
			if (trySendServerFull(comm)) {
				releaseMutex();
				return false;
			}
			Position pos = game.getPlayerStartPosition(posIndex);
			Direction dir = game.getPlayerStartDirection(posIndex);
			if (pos == null || dir == null) {
				return false;
			}
			// Add player
			Player player = new Player(id, pos, game.getBoard(), comm);
			player.setDirection(dir);
			authenticatedPlayers.add(player);
			return true;
		}
	}

	public Game getGame() {
		return this.game;
	}

	/**
	 * Broadcast message which contains the details of all players in the game
	 * when the game starts
	 */
	public void broadcastConnectionUpdate() {
		// Synchronize list of players
		synchronized (authenticatedPlayers) {
			for (int i = 0; i < authenticatedPlayers.size(); i++) {
				NetworkMessage pm = MessageFactory.createPlayerConnect(
						authenticatedPlayers.get(i).getId(),
						authenticatedPlayers.get(i).getPosition(),
						authenticatedPlayers.get(i).getDirection(), i);
				broadcastMessage(pm);
			}
		}
	}

	/**
	 * Broadcast message to all clients
	 * 
	 * @param message
	 *            the network message object to be sent over the network
	 */
	public void broadcastMessage(NetworkMessage message) {
		synchronized (authenticatedPlayers) {
			for (Player player : authenticatedPlayers) {
				player.getCommunicator().sendMessage(message);
			}
		}
	}

	public synchronized void startGame() {
		synchronized (authenticatedPlayers) {
			broadcastMessage(MessageFactory.createGameStarted(game
					.getBoardStyle()));
			broadcastConnectionUpdate();
			game.addPlayers(authenticatedPlayers);
			game.start();
		}
	}

	/**
	 * Start the server
	 * 
	 * @throws IOException
	 */
	public synchronized void startServer() throws IOException {
		if (isAlive()) {
			return;
		}
		serverSocket = new ServerSocket(port);
		start();
		// Fire event for server started event
		serverStatusChanged.get().serverStarted();
	}

	/**
	 * Stop server
	 * 
	 * @throws IOException
	 */
	public synchronized final void stopServer() throws IOException {
		if (!isAlive()) {
			return;
		}
		interrupt();
		if (serverSocket != null) {
			serverSocket.close();
		}
		// Fire event for server stopped event
		serverStatusChanged.get().serverStopped();
	}

	/**
	 * Run the thread
	 */
	public void run() {
		super.run();
		System.out.println("-------------------------------------------------");
		System.out.println("Server started at: " + new Date());
		System.out.println("Host: " + Constants.HOST);
		System.out.println("Port: " + Constants.PORT);
		System.out.println("-------------------------------------------------");
		System.out.println();

		// Accept and handle client connections
		while (!isInterrupted()) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("Player connected");
				Communicator comm = new Communicator(socket, false);
				MessageListener listener = new MessageListener(comm, this);
				comm.messageReceived.addListener(listener);
				comm.sendMessage(MessageFactory.createDetailsRequest());
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	/**
	 * Handle the event fired when the monster moves
	 * 
	 * @param pos
	 *            the position of the monster
	 */
	@Override
	public void monsterMoved(Position pos) {
		if (Constants.IS_DEBUG) {
			System.out.println("Broadcasting monster move");
		}
		NetworkMessage pm = MessageFactory.createMonsterMove(pos);
		broadcastMessage(pm);
	}

	/**
	 * Handle the event fired when a player dies
	 * 
	 * @param id
	 *            the unique id of the player who is dead
	 * @param playerPos
	 *            the position of the player who is dead
	 */
	@Override
	public void playerDead(String id, Position playerPos) {
		System.out.println("Broadcasting player dead: " + id);
		NetworkMessage playerMove = MessageFactory.createPlayerMove(id,
				playerPos, Direction.LEFT.ordinal());
		broadcastMessage(playerMove);
		NetworkMessage pm = MessageFactory.createPlayerDeathMessage(id);
		broadcastMessage(pm);
	}

	/**
	 * Handle the event fired when the game is finished
	 * 
	 * @param winner
	 *            the winner of the game
	 * @param names
	 *            the names of all the players in order of highest score to
	 *            lowest score
	 * @param scores
	 *            the scores of all the players for the current game in order of
	 *            highest score to lowest score
	 */
	@Override
	public void gameFinished(String winner) {
		NetworkMessage pm = MessageFactory.createGameFinished(winner);
		broadcastMessage(pm);
		restartGame();
	}

	/**
	 * Create a new game
	 */
	public void restartGame() {
		// Detach the current game from the events that it was listening to
		if (game != null) {
			game.playerDead.removeListener(this);
			game.monsterMoved.removeListener(this);
			game.gameFinished.removeListener(this);
			game.lifeCountChanged.removeListener(this);
		}
		// Create a new game
		game = new Game();
		authenticatedPlayers = new ArrayList<Player>();
		// Attach the new game to the events that the game has to listen to
		game.playerDead.addListener(this);
		game.monsterMoved.addListener(this);
		game.gameFinished.addListener(this);
		game.lifeCountChanged.addListener(this);
	}

	/**
	 * Handle the event fired when the life count of a player changes
	 */
	@Override
	public void lifeCountChanged(Player p, int life) {
		broadcastMessage(MessageFactory.createNumLivesLeft(p.getId(),
				p.getNumLivesLeft()));
	}
}
