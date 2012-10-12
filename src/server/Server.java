/** @Pulkit 
 * Hosts the game and enables players to connect.
 * Each connecting player gets assigned a separate SessionManager to henceforth manage requests.
 */
package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import server.model.Model;
import components.Debug;
import components.grid.Cell;
import components.grid.Coordinates;
import components.grid.GameMap;
import components.model.User;
import components.network.ClientInterface;
import components.network.ServerInterface;

public class Server extends UnicastRemoteObject implements ServerInterface{

	/* Components and Game Parameters */
	private static Model model; // for storing user info
	protected static GameMap serverMap; // must always use with synchronized
	private int playerCount; // total number of players to wait for before starting game
	protected static int activePlayers; // track how many players are active
	private ServerGUI serverGUI;
	private int port;
	private Thread monster = null;

	private static HashMap<Integer, ClientInterface> players;
	private int playerId, connectedPlayers;
	ArrayList<Coordinates> initialPlayerPositions = new ArrayList<Coordinates>();

	/* Game States */
	protected static int gameState = 0; // must always use with sync lock
	public static int CONNECTING = 1;
	public static int STARTED = 2;
	public static int ENDED = 3;
	/* Sync locks */
	protected static Object gameStateLock = new Object(); // must always use with synchronized

	public Server(int port, int testMapType) throws RemoteException{
		/* Initialize components */
		this.model = new Model();
		this.port = port;

		/* Initialize the game parameters */
		serverMap = new GameMap(GameMap.TEST, testMapType);

		/* Register for RMI */
		System.setProperty("java.rmi.server.codebase", Server.class.getProtectionDomain().getCodeSource().getLocation().toString());

		System.setSecurityManager(new RMISecurityManager());

		try{
			Naming.rebind("rmi://localhost:" + port + "/Server", this);
			Naming.rebind("rmi://10.130.55.243:" + port + "/Server", this);
		}catch(RemoteException e){
			// TODO Auto-generated catch block
			System.err.println("Could not make a remote connection: " + e.toString());
		}catch(MalformedURLException e){
			// TODO Auto-generated catch block
			System.err.println("Invalid url supplied for RMI: " + e.toString());
		}

		Debug.log("Server", "bound to RMI");

		/* Show gui */
		serverGUI = new ServerGUI();

		/* And we're set to run the game! */
		prepareGame();
	}

	private void prepareGame() throws RemoteException{
		Debug.log("MainMenu", "starting run");
		serverGUI.showGameSetupMenu();

		while(!serverGUI.isStarted()){
			// wait for it to be started
			Debug.log("MainMenu", "gui not started");
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){
				// TODO Auto-generated catch block
				System.err.println(e.toString());
			}
		}
		Debug.log("MainMenu", "gui started!");

		try{
			playerCount = serverGUI.getPlayerCount() + 1;
		}catch(NumberFormatException e){
			System.err.println("Invalid number entered for player count!");
			return;
		}
		Debug.log("MainMenu", "player count: " + playerCount);

		players = new HashMap<Integer, ClientInterface>(playerCount);

		serverGUI.showGameRunning(port);
		Debug.log("MainMenu", "gui shows running");

		/* Get all the players connected */
		setGameState(Server.CONNECTING);

		/* Start monster too */
		monster = new Thread(){
			public void run(){
				try{
					new Monster("localhost", port);
				}catch(RemoteException e){
					// TODO Auto-generated catch block
					System.err.println("Monster thread threw remote exception " + e.toString());
				}
			}
		};
		monster.start();

		/* Connect players */
		try{
			Server.activePlayers = 0; // reset active player count to 0
			connectedPlayers = 0;
			playerId = 1;

			/* Initial player positions possible */
			initialPlayerPositions.add(serverMap.CORNER_NW);
			initialPlayerPositions.add(serverMap.CORNER_NE);
			initialPlayerPositions.add(serverMap.CORNER_SE);
			initialPlayerPositions.add(serverMap.CORNER_SW);
			initialPlayerPositions.add(serverMap.CENTRE);

		}catch(IllegalArgumentException e){
			// TODO Auto-generated catch block
			System.err.println("Server encountered exception while initializing GameMap: " + e.toString());
		}

		while(true){
			synchronized(Server.gameStateLock){
				if(connectedPlayers >= playerCount) break;
			}
		}

		/* All players connected */
		Debug.log("Server", "All players connected");
		runGame();
	}

	/* Connect all the players */
	public boolean connect(ClientInterface player, String theirHost, int theirPort, int preferredPosition, boolean isMonster,
			String firstname, String surname, String address, String phone, String username, String password) throws RemoteException{
		Debug.log("Server", "Join request received");

		if(player == null || serverGUI.isClosed()) return false;

		/* Player wants to join */
		Debug.log("Server", "Player wants to join");

		/* Set player parameters */
		int id;
		if(isMonster) id = 0;
		else id = playerId;

		/* Check player validity - is this a request to register? */
		if(!isMonster){
			User user = new User(firstname, surname, address, phone, username, password);
			if(user.isFullRegistration()){
				Debug.log("User", "full registration requested with firstname: "+firstname);
				boolean success = model.add(user);
				if(!success) return false; // duplicate
			}
			else if(!model.exists(user)) return false; // no, it's a login
		}

		synchronized(Server.players){
			Debug.log("Server", "added this player to list with original size " + Server.players.size());
			Server.players.put(id, player);
		}

		Coordinates thisPlayersPosition = initialPlayerPositions.get(preferredPosition);
		if(isMonster) thisPlayersPosition = initialPlayerPositions.get(4);

		synchronized(GameMap.gameMapLock){
			Server.serverMap.getCell(thisPlayersPosition).setOccupant(id);
			Server.serverMap.setLastMoveBy(id);
		}

		if(!isMonster){
			synchronized(Server.gameStateLock){
				Server.activePlayers++; // count this player as active
			}
		}

		/* 2. Reply with JoinSuccess to player */
		player.joinSuccess(id, thisPlayersPosition, serverMap.CENTRE);
		Debug.log("Server", "Join success sent to player");

		synchronized(Server.gameStateLock){
			/* Increment player id for the next player, only if this one wasn't a monster */
			if(!isMonster) playerId++;

			/* Increment the count of connected players */
			connectedPlayers++;
		}

		return true;
	}

	private void runGame() throws RemoteException{
		Debug.log("MainMenu", "players connected");

		/* Start the game! */
		setGameState(Server.STARTED);
		Debug.log("MainMenu", "game started");

		/* Signal all players */
		GameMap startMap;
		synchronized(GameMap.gameMapLock){
			startMap = Server.serverMap;
		}

		synchronized(Server.players){
			for(ClientInterface client:Server.players.values()){
				(new BatchStarter(client, startMap)).start();
			}
		}

		/* Is game over? */
		while(true){
			synchronized(Server.gameStateLock){
				Debug.log("Server", "Active players: " + Server.activePlayers);
				if(Server.activePlayers <= 1) break;
			}
		}

		Debug.log("Server", "Only 1 player left");

		synchronized(Server.players){
			/* Bring monster back into the den! */
			Server.players.get(0).deathNotice(0); // inform monster to die
			Server.players.remove(0); // remove monster

			Debug.log("Server", "Confirming the player count is " + Server.players.size());

			for(Object aliveClientId:Server.players.keySet().toArray()){
				declareWinner(Server.players.get(aliveClientId));
			}
		}

		/* Start a new game! */
		prepareGame();
	}

	/* From former Session Handler */
	public boolean makeMove(ClientInterface player, Coordinates playerPosition, Coordinates moveCoords, int playerId, boolean isMonster)
			throws RemoteException{
		boolean moveSuccess = false;
		GameMap endMap;
		synchronized(GameMap.gameMapLock){

			/* Check if the move is valid and doesn't involve the death of a player in case of monster */
			if(Server.serverMap.isValidMove(playerPosition, moveCoords)){

				/* Execute the move */
				Server.serverMap.makeMove(playerId, playerPosition, moveCoords);

				moveSuccess = true;
				Debug.log("Server", "player " + playerId + "moved player to " + moveCoords);

			}else if(isMonster && Server.serverMap.getCell(moveCoords).getStatus() == Cell.OCCUPIED){
				/* Else, if this is a kill move, and you're a monster, kill the player */
				int playerKilledId = Server.serverMap.getCell(moveCoords).getOccupant();
				Server.serverMap.getCell(moveCoords).setStatus(Cell.EMPTY); // player wiped from map!
				Server.serverMap.makeMove(playerId, playerPosition, moveCoords); // monster moved to the player's position

				moveSuccess = true;
				Debug.log("Server", "monster " + playerId + "moved player to " + moveCoords);

				/* Kill that player! */
				synchronized(Server.players){
					killPlayer(Server.players.get(playerKilledId), true);
				}
			}

			endMap = Server.serverMap;
		}

		/* Send everyone a map update */
		synchronized(Server.players){
			for(ClientInterface client:Server.players.values())
				(new BatchUpdater(client, endMap)).start();
		}

		return moveSuccess;
	}

	public void quitGame(ClientInterface player, boolean isMonster) throws RemoteException{
		if(isMonster){
			try{
				monster.join();
			}catch(InterruptedException e){
				// TODO Auto-generated catch block
				System.err.println("Monster thread was dying when it was interrupted " + e.toString());
			}
		}else killPlayer(player, false);
	}

	private void killPlayer(ClientInterface player, boolean sendNotice) throws RemoteException{
		Debug.log("Server", "Killing player " + player.getUserName());

		synchronized(Server.players){
			for(Object playerId:Server.players.keySet().toArray()){
				if(Server.players.get(playerId).equals(player)) Server.players.remove(playerId);
			}
		}

		int scoreGained = 0;
		synchronized(Server.gameStateLock){
			scoreGained = playerCount - Server.activePlayers - 1;
			Server.activePlayers--;
		}

		/* Did the player deserve a notice? i.e., was it a kill or a quit? */
		if(sendNotice){
			int scorePrevious = model.getScore(player.getUserName());

			int scoreTotal = scoreGained + scorePrevious;

			model.updateScore(player.getUserName(), scoreTotal);

			/* Send death notice to the player */
			(new Notifier(player, Notifier.LOSS, scoreTotal)).start();
		}
	}

	private void declareWinner(ClientInterface player) throws RemoteException{
		Debug.log("Server", "Winning player is " + player.getUserName());

		/* Compute score */
		int scoreGained = 0;
		synchronized(Server.gameStateLock){
			scoreGained = playerCount - Server.activePlayers - 1;
		}

		int scorePrevious = model.getScore(player.getUserName());

		int scoreTotal = scoreGained + scorePrevious;

		model.updateScore(player.getUserName(), scoreTotal);

		/* Let player know */
		(new Notifier(player, Notifier.WIN, scoreTotal)).start();
	}

	private void setGameState(int gameState){
		synchronized(Server.gameStateLock){
			Server.gameState = gameState;
			Debug.log("Server", "Game state is now " + Server.gameState);
			Server.gameStateLock.notifyAll(); // wake up session handlers!
		}
	}

	public Boolean isLive(){
		/* Test for other classes to know when the server is live and ready of connections */
		synchronized(Server.gameStateLock){
			return Server.gameState == Server.CONNECTING;
		}
	}

	public void register(String firstname, String surname, String address, String phone, String username, String password){
		User user = new User(firstname, surname, address, phone, username, password);
		model.add(user);
	}
}

/* Utility threads for asynchronously reply to client (via callbacks) */

class BatchStarter extends Thread{
	private ClientInterface client;
	private GameMap gameMap;

	public BatchStarter(ClientInterface client, GameMap gameMap){
		this.client = client;
		this.gameMap = gameMap;
	}

	public void run(){
		try{
			client.runGame(gameMap);
		}catch(RemoteException e){
			// TODO Auto-generated catch block
			System.err.println("One of the batch starter threads remotely excepted " + e.toString());
		}
	}
}

/* Used to asynchronously send a map update to all players */
class BatchUpdater extends Thread{
	private ClientInterface client;
	private GameMap gameMap;

	public BatchUpdater(ClientInterface client, GameMap gameMap){
		this.client = client;
		this.gameMap = gameMap;
	}

	public void run(){
		try{
			client.updateMap(gameMap);
		}catch(RemoteException e){
			// TODO Auto-generated catch block
			System.err.println("One of the batch updater threads remotely excepted " + e.toString());
		}
	}
}

/* Used to asyncly send a win/loss notice */
class Notifier extends Thread{
	private ClientInterface client;
	private int score;

	private int noticeType = 0;
	public static int WIN = 1;
	public static int LOSS = 2;

	public Notifier(ClientInterface client, int noticeType, int score){
		this.client = client;
		this.noticeType = noticeType;
		this.score = score;
	}

	public void run(){
		try{
			if(noticeType == WIN) client.gameWonNotice(score);
			else if(noticeType == LOSS) client.deathNotice(score);
		}catch(RemoteException e){
			// TODO Auto-generated catch block
			System.err.println("Notifier thread remotely excepted " + e.toString());
		}
	}
}
