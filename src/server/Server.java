/** @Pulkit 
 * Hosts the game and enables players to connect.
 * Each connecting player gets assigned a separate SessionManager to henceforth manage requests.
 */
package server;

import java.util.ArrayList;
import components.Debug;
import components.grid.Coordinates;
import components.grid.GameMap;
import components.network.CommunicationManager;
import components.packets.JoinSuccessPacket;
import components.packets.NotificationPacket;

public class Server{
	/* Components and Game Parameters */
	private CommunicationManager comm; // abstracts away all the socket communication neatly
	protected static GameMap serverMap; // must always use with synchronized
	private int playerCount; // total number of players to wait for before starting game
	protected static int activePlayers; // track how many players are active
	private ServerGUI serverGUI;
	private int port;

	/* Game States */
	protected static int gameState = 0; // must always use with sync lock
	public static int CONNECTING = 1;
	public static int STARTED = 2;
	public static int ENDED = 3;
	/* Sync locks */
	protected static Object gameStateLock = new Object(); // must always use with synchronized

	public Server(int port, int testMapType){
		/* Initialize components */
		this.port = port;
		comm = new CommunicationManager(port);

		/* Initialize the game parameters */
		serverMap = new GameMap(GameMap.TEST, testMapType);

		/* Show gui */
		serverGUI = new ServerGUI();

		/* And we're set to run the game! */
		runGame();
	}

	private void runGame(){
		while(true){
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
				continue;
			}
			Debug.log("MainMenu", "player count: "+playerCount);

			serverGUI.showGameRunning(port);
			Debug.log("MainMenu", "gui shows running");

			/* Get all the players connected */
			setGameState(Server.CONNECTING);
			
			/* Start monster too */
			Monster monster = new Monster("localhost", port);
			monster.start();
			
			connectPlayers();
			Debug.log("MainMenu", "players connected");

			/* Start the game! */
			setGameState(Server.STARTED); // all session handlers should now begin their activities
			Debug.log("MainMenu", "game started");
			
			while(Server.activePlayers>0){
				// wait for players to die
				Debug.log("MainMenu", "waiting for players to die");
				try{
					Thread.sleep(1500);
				}catch(InterruptedException e){
					// TODO Auto-generated catch block
					System.err.println(e.toString());
				}
			}
			Debug.log("MainMenu", "all players dead");
			
			Debug.log("MainMenu", "monster closed");
		}
	}

	/* Connect all the players */
	private void connectPlayers(){
		try{
			Server.activePlayers = 0;	// reset active player count to 0
			int connectedPlayers = 0;
			int playerId = 1;

			/* Initial player positions possible */
			ArrayList<Coordinates> initialPlayerPositions = new ArrayList<Coordinates>();
			initialPlayerPositions.add(serverMap.CORNER_NW);
			initialPlayerPositions.add(serverMap.CORNER_NE);
			initialPlayerPositions.add(serverMap.CORNER_SE);
			initialPlayerPositions.add(serverMap.CORNER_SW);
			initialPlayerPositions.add(serverMap.CENTRE);

			/* Join Game Protocol */
			while(connectedPlayers < playerCount && !serverGUI.isClosed()){
				/* Accept all players */

				/* 0. Wait for a player to connect */
				Debug.log("Server", "Waiting for connection");
				comm.waitForConnection();
				Debug.log("Server", "Connected to a player");

				/* 1. Get NotificationPacket from player */
				Debug.log("Server", "Awaiting packet");
				NotificationPacket noticePacket = (NotificationPacket)comm.receivePacket();
				Debug.log("Server", "Notification packet received");

				if(noticePacket.getNoticeType() == NotificationPacket.JOINGAME){
					/* Player wants to join - create session handler */
					Debug.log("Server", "Player wants to join");
					SessionManager playerHandler = new SessionManager(comm.getCommunicationSocket(), noticePacket.isMonster());

					/* Set session handler parameters */
					int id;
					if(noticePacket.isMonster()) id = 0;
					else id = playerId;

					playerHandler.setPlayerId(id);

					/* Set player position in session handler as well as the game map */
					Coordinates thisPlayersPosition = initialPlayerPositions.get(noticePacket.getPreferredPosition());
					if(noticePacket.isMonster()) thisPlayersPosition = initialPlayerPositions.get(4);

					playerHandler.setPlayerPosition(thisPlayersPosition);

					synchronized(GameMap.gameMapLock){
						Server.serverMap.getCell(thisPlayersPosition).setOccupant(id);
						Server.serverMap.setLastMoveBy(id);
					}

					playerHandler.start(); // Fork into a new thread
					Debug.log("Server", "Player session handler started");
					if(!noticePacket.isMonster()){
						synchronized(Server.gameStateLock){
							Server.activePlayers++; // count this player as active
						}
					}

					/* 2. Reply with JoinSuccessPacket to player */
					JoinSuccessPacket joinSuccessPacket;
					synchronized(GameMap.gameMapLock){
						joinSuccessPacket = new JoinSuccessPacket(Server.serverMap, id, thisPlayersPosition, serverMap.CENTRE);
					}
					comm.sendPacket(joinSuccessPacket);
					Debug.log("Server", "Reply sent to player");

					/* Increment player id for the next player, only if this one wasn't a monster */
					if(!noticePacket.isMonster()) playerId++;

					/* Increment the count of connected players */
					connectedPlayers++;
				}
			}
		}catch(IllegalArgumentException e){
			// TODO Auto-generated catch block
			System.err.println("Server encountered exception while initializing GameMap: " + e.toString());
		}
		Debug.log("Server", "All players connected");
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
			return Server.gameState==Server.CONNECTING;
		}
	}

	/* Standalone executable */
//	public static void main(String args[]){
//		Debug.MODE = false;
//		if(args.length > 0) new Server(Integer.parseInt(args[0]), Integer.parseInt(args[2]));
//		else new Server(56413, GameMap.TEST_1);
//	}
}
