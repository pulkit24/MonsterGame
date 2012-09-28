package monsterRun.server.controller;

import monsterRun.common.model.Constants;
import monsterRun.common.model.MessageFactory;
import monsterRun.common.model.enums.Direction;
import monsterRun.common.model.enums.GameBoardType;
import monsterRun.common.model.enums.GameMode;
import monsterRun.common.model.enums.MessageID;
import monsterRun.common.model.network.Communicator;
import monsterRun.common.model.network.NetworkMessage;
import monsterRun.common.model.network.events.IConnectionStatusChangedListener;
import monsterRun.common.model.network.events.IMessageReceivedListener;
import monsterRun.server.model.MonsterRunServer;
import monsterRun.server.model.entities.Player;

/**
 * Class that is responsible for listening to the communication with each client
 * on the server side, this class listens to the messages sent by the clients to
 * the server
 * 
 */
public class MessageListener implements IMessageReceivedListener,
		IConnectionStatusChangedListener {

	/**
	 * Instance variables for each individual client, one instance of this class
	 * is created for the communication between the server and one client
	 */
	private Communicator comm;
	private MonsterRunServer server;

	/**
	 * Details about the client for this individual message listener
	 */
	private String userID;
	private int startPos;

	/**
	 * Constructor
	 * 
	 * @param comm
	 *            the communicator object that is responsible for the
	 *            communication between the server and the client
	 * @param srvr
	 *            the server that is running the game
	 * @param acntMgr
	 *            the manager class that handles account operations for clients
	 */
	public MessageListener(Communicator comm, MonsterRunServer srvr) {

		this.comm = comm;
		this.server = srvr;
		// Listen to the event that gets fired when the connection status
		// between client and server changes
		comm.connectionStatusChanged.addListener(this);
	}

	public String getUserID() {
		return userID;
	}

	/**
	 * Receive message and perform operations based on the message received
	 * 
	 * @param senderId
	 *            the unique ID of the client who has sent a message to the
	 *            server
	 * @param mess
	 *            the NetworkMessage object that has been sent by the client to
	 *            the server
	 */
	@Override
	public void messageReceived(String senderId, NetworkMessage mess) {
		NetworkMessage message = mess.clone();
		MessageID messageType = message.getId(MessageID.class);

		// Determine what the message is by checking the ID of the message which
		// is an enum
		switch (messageType) {
		// Get the unique generated client ID
		case CLIENT_DETAILS:
			userID = senderId;
			System.out.println("Received user ID: " + userID);

			break;
		// Handle game join request
		case GAME_JOIN_REQUEST:

			handleJoinGameRequest();
			break;
			
		// Handle selection of starting position by client
		case START_POS_RESPONSE:
			startPos = message.readInt();

			handleStartPositionMessage(startPos);
			break;
		// Handle game details selected by the player
		case GAME_DETAILS:
			int numPlayers = message.readInt();
			GameBoardType board = GameBoardType.values()[message.readInt()];
			GameMode mode = GameMode.values()[message.readInt()];

			createGameBoard(numPlayers, board, mode);
			break;

		// Handle client move request
		case CLIENT_MOVE_REQUEST:
			// Get the client ID and the direction to move
			String playerId = senderId;
			Direction direction = Direction.values()[message.readInt()];

			handleClientMoveRequest(playerId, direction);
			break;

		default:
			break;
		}
	}

	private void handleJoinGameRequest() {

		System.out.println(userID + " ~ WAITING FOR JOIN GAME LOCK ~");

		// Synchronize the game joining and creating
		server.acquireMutex();

		System.out.println(userID + " + JOIN GAME LOCK ACQUIRED +");

		// Check if first client for the server
			if (server.isFirstClient()) {
				// Ask for input for the game creation such as board, mode,
				// etc.
				comm.sendMessage(MessageFactory.createGameDetailsRequest());
			} else {
				// Check if server is full, if so send a server full message
				if (server.trySendServerFull(comm)) {
					server.releaseMutex();
					return;
				}
				// Ask for start position of client
				comm.sendMessage(MessageFactory.createStartPosRequest(server
						.getGame().getAvailablePositions()));
			}
		}

	private void handleStartPositionMessage(int startPos2) {

		// Validate the input
		if (startPos >= Constants.MAX_PLAYERS) {
			comm.sendMessage(MessageFactory.createStartPosRequest(server
					.getGame().getAvailablePositions()));
			return;
		}

		// Add player to the game if position was valid
		if (server.addPlayer(comm, userID, startPos)) {
			// Start game if required number of players have joined in
			if (server.isLastPlayer()) {
				if (Constants.IS_DEBUG) {
					System.out.println(server.getPlayers());
				}
				server.startGame();
			}
		}

		System.out.println(userID + " - JOIN GAME LOCK RELEASED-");
		server.releaseMutex();
	}

	private void createGameBoard(int numPlayers, GameBoardType board,
			GameMode mode) {

		// Validate input
		if ((numPlayers < Constants.MIN_PLAYERS || numPlayers > Constants.MAX_PLAYERS)) {
			String error = "Please select a number between 2 and 4";
			comm.sendMessage(MessageFactory.createInvalidInput(error));
		}
		// Send message confirming successful game creation
		else {

			System.out.println("Board is: " + board);
			System.out.println("Game mode is: " + mode);
			System.out.println("Number of players is: " + numPlayers);

			server.getGame().setGameMode(mode);
			server.getGame().setBoardStyle(board);
			server.getGame().setNumberOfPlayers(numPlayers);
			comm.sendMessage(MessageFactory.createNumPlayerSuccess());
			comm.sendMessage(MessageFactory.createStartPosRequest(server
					.getGame().getAvailablePositions()));

			System.out.println(userID + "- GAME CREATED LOCK RELEASED -");
		}
	}

	private void handleClientMoveRequest(String playerId, Direction direction) {

		// Get the player associated with the client and move it
		Player player = server.getPlayerWithId(playerId);
		if (player != null) {
			player.move(direction);
		} else {
			return;
		}
		// Send message confirming successful move if the player is still
		// alive
		if (server.isPlayerAlive(playerId)) {
			server.broadcastMessage(MessageFactory.createPlayerMove(playerId,
					player.getPosition(), direction.ordinal()));
		}
	}

	/**
	 * Deal with the scenario when the connection is lost between a client and
	 * the server
	 * 
	 * @param clientId
	 *            the client whose connection with the server has been lost
	 */
	@Override
	public void connectionLost(String clientId) {
		if (userID == null) {
			return;
		}
		// Get player object, remove it from the board and log the client out if
		// connection has dropped
		Player player = server.getPlayerWithId(userID);
		if (player != null) {
			try {
				server.getGame().getBoard().removePlayerFromBoard(player);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Remove client from the list of active clients
		server.removeClient(userID);
		server.releaseMutex();
	}
}
