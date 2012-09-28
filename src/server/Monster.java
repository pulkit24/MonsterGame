/** @Cetin
 * Runs and controls the monster. 
 * Monster simply connects to the server just like any other player.
 * Preferably assign it an id of 0 on the server!
 * Usage:
 * Initialize, then use start() method to run as separate thread.
 */
package server;

import client.GUI;
import components.Debug;
import components.grid.Coordinates;
import components.grid.GameMap;
import components.network.CommunicationManager;
import components.packets.JoinSuccessPacket;
import components.packets.MovePacket;
import components.packets.NotificationPacket;
import components.packets.Packet;
import components.packets.SuccessPacket;
import components.packets.UpdatePacket;

public class Monster extends Thread{
	private CommunicationManager comm; // Abstracts away all the socket communication neatly
	private String host;
	private int port;
	private int playerId;
	private String playerName;
	protected GameMap playerMap;
	protected GUI gui;
	private Coordinates currentPosition;
	private int resetsLeft = 3; // Only allow 3 resets during the game

	/* Control the movement speed in milliseconds */
	private int moveTime = 230; // Time taken to make a move in ms
	private static int moveTimeSlow = 750;

	/* Determine turns than to slow down motion */
	private int moveDirection = 0;
	private static int alongX = 1;
	private static int alongY = 2;

	public Monster(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public void run(){
		/* Initialize GUI */
		gui = new GUI(true);
		gui.setResetsLeft(resetsLeft);
		Debug.log("Player", "GUI initiated");

		/* Get details from the player */
		playerName = "Monster"; // gui.getPlayerDetails();
		if(playerName == null) quitGame(false); // quit if player declined/cancelled to enter details

		/* Get details of the host server */
		String hostDetails[] = {host, port + ""}; // gui.getHostDetails(false, host, port + "");
		if(hostDetails[0] == null || hostDetails[1] == null) quitGame(false); // quit if player declined/cancelled to enter details

		/* Connect - first attempt! */
		Debug.log("Monster", "Connecting...");
		Boolean connected = connect(hostDetails[0], Integer.parseInt(hostDetails[1]));
		while(!connected && !gui.isClosed()){
			Debug.log("Monster", "failed");
			/* Wait a moment */
			try{
				Thread.sleep(5000);// sleep 
			}catch(InterruptedException e){
				// TODO Auto-generated catch block
				System.err.println(e.toString());
			}
			Debug.log("Monster", "Reconnecting...");
			
			/* Another attempt to connect */
			connected = connect(hostDetails[0], Integer.parseInt(hostDetails[1]));
		}

		if(!gui.isClosed()){
			Debug.log("Monster", "Connected!");
			runGame(); // play!
		}

		/* GUI closed - send quit message if connected */
		quitGame(connected);
	}

	private Boolean connect(String host, int port){
		/* Connect to server */
		comm = new CommunicationManager(host, port);
		Debug.log("Player", "Connecting...");

		/* Reflect in GUI */
		// gui.showConnectionProgress();

		return comm.connect();
	}

	private void runGame(){
		/* Try to join a new game */
		joinGame(); // player id received here
		/* Joined! Show users the state */
		// gui.showConnectionConfirmation(); // "waiting for other players..."
		/* Await game start */
		awaitGameStart();

		if(gui.isClosed()) return; // did the impatient user close the window?

		/* Update GUI */
		// synchronized(GameMap.gameMapLock){
		gui.setPlayerDetails(playerId, playerName);
		gui.setGameMap(playerMap);
		gui.refresh();
		// }

		Boolean firstRun = true; // breather period in the first run before the monster gets active

		/* Get the move from the user OR get the latest game map */
		while(!gui.isClosed()){ // as long as the user didn't close the window

			/* Make a move every few ms */
			if(firstRun) delay(5000);
			else delay(moveTime);
			firstRun = false;

			/* Request a map and refresh as well */
			requestMapUpdate();

			/* Find something to do */
			// synchronized(GameMap.gameMapLock){

			/* Is there any valid move? */
			Coordinates spottedPlayerLocation = playerMap.getNearestPlayerCoordinates(playerId, currentPosition);
			Debug.log("Monster " + playerId, "Closest player is at " + spottedPlayerLocation);

			Coordinates moveCoords;

			/* Is this player adjacent to you? */
			if(currentPosition.getDistanceManhattan(spottedPlayerLocation) == 1){
				/* If so, Move over and kill them! */
				moveCoords = spottedPlayerLocation;
			}else{
				/* Else, compute the next coords we need to move to */
				moveCoords = playerMap.getDirection(currentPosition, spottedPlayerLocation);
			}

			/* Track move direction */
			Boolean isTurn = false;
			if(moveCoords.getX() == currentPosition.getX()){
				if(moveDirection == alongY) isTurn = true; // it changed move direction! it turned!
				moveDirection = alongX;
			}else if(moveCoords.getY() == currentPosition.getY()){
				if(moveDirection == alongX) isTurn = true; // it changed move direction! it turned!
				moveDirection = alongY;
			}
			/* Control speed if making a turn */
			if(isTurn) delay(moveTimeSlow);

			/* Movement */
			Debug.log("Monster", "Move decided");
			Debug.log("Player", "New coords: " + moveCoords);
			/* Send the move to the server */
			sendMove(moveCoords, false);

			// }
		}
	}

	private void delay(int delayTime){
		/* Make a move every few ms */
		try{
			Thread.sleep(delayTime);
		}catch(InterruptedException e){
			// TODO Auto-generated catch block
			System.err.println(e.toString());
		}
	}

	private void joinGame(){
		/* Follow the Join Game Protocol */

		/* 1. Send join notice */
		NotificationPacket joinNoticePacket = new NotificationPacket(NotificationPacket.JOINGAME, true);
		Debug.log("Player", "Sending join request");
		comm.sendPacket(joinNoticePacket);
		Debug.log("Player", "Sent");

		/* 2. Wait for reply */
		Debug.log("Player", "Waiting for reply");
		JoinSuccessPacket replyPacket = (JoinSuccessPacket)comm.receivePacket();
		Debug.log("Player", "Reply received, result is: " + replyPacket.getSuccess());

		/* Get all useful data out of this packet */
		playerId = replyPacket.getPlayerId();
		// synchronized(GameMap.gameMapLock){
		playerMap = replyPacket.getGameMap();
		// }
		currentPosition = replyPacket.getInitialPosition();
	}

	private void awaitGameStart(){
		/* Start Game Protocol */

		/* 1. Wait for start game notice */
		Debug.log("Player " + playerId, "Waiting for start game notice");
		NotificationPacket notificationReceived = (NotificationPacket)comm.receivePacket();
		if(notificationReceived.getNoticeType() == NotificationPacket.STARTGAME) Debug.log("Player " + playerId,
				"Received start game notice");
		else Debug.log("Player " + playerId, "Received some stray notification packet");
	}

	private void sendMove(Coordinates moveCoords, Boolean reset){
		/* Move or Reset Protocol */

		/* 1. Send move */
		int type = reset ? MovePacket.RESET : MovePacket.REGULAR;
		MovePacket movePacket = new MovePacket(type, moveCoords);
		Debug.log("Player " + playerId, "Sending move packet: " + movePacket.getNewCoords().toString());
		comm.sendPacket(movePacket);
		Debug.log("Player " + playerId, "Sent");

		/* 2. Wait for reply */
		Debug.log("Player " + playerId, "Waiting for reply");
		SuccessPacket successPacket = (SuccessPacket)comm.receivePacket();
		Debug.log("Player " + playerId, "Reply received, result is: " + successPacket.getSuccess());

		/* Reflect in GUI */
		if(successPacket.getSuccess()){
			/* Execute the move */
			playerMap.makeMove(playerId, currentPosition, moveCoords);
			/* Update player's current position */
			currentPosition = moveCoords;
			// synchronized(GameMap.gameMapLock){
			gui.setGameMap(playerMap);
			if(reset){
				resetsLeft--;
				gui.setResetsLeft(resetsLeft); // update Heads Up Display elements if needed
			}
			gui.refresh();
			// }
		}
	}

	private void requestMapUpdate(){
		/* Request a normal map refresh */
		NotificationPacket refreshRequestPacket = new NotificationPacket(NotificationPacket.REFRESHREQUEST);
		Debug.log("Player " + playerId, "Sending request for map refresh");
		comm.sendPacket(refreshRequestPacket);
		Debug.log("Player " + playerId, "Sent");

		/* 2. Wait for reply */
		Debug.log("Player " + playerId, "Waiting for reply");
		Packet replyPacket = comm.receivePacket();
		Debug.log("Player " + playerId, "Update received");
		/* Could be blank! */
		if(replyPacket.getType() == Packet.UPDATEPACKET){
			UpdatePacket updatePacket = (UpdatePacket)replyPacket;

			/* Get all useful data out of this packet */
			// synchronized(GameMap.gameMapLock){
			playerMap = updatePacket.getNewGameMap();
			gui.setGameMap(playerMap);
			gui.refresh();
			// }

			/* Did you win? */
			if(updatePacket.won()){
				// gui.showVictoryMessage();
				quitGame(false);
			}
		}
	}

	public void quitGame(Boolean tellServer){
		/* Quit Game Protocol */

		/* 1. Send termination notice */
		if(tellServer){
			NotificationPacket quitPacket = new NotificationPacket(NotificationPacket.TERMINATED);
			Debug.log("Player " + playerId, "sending quit notice");
			comm.sendPacket(quitPacket);
			Debug.log("Player " + playerId, "Sent");
		}

		/* Shut down application */
		try{
			this.join();
		}catch(InterruptedException e){
			// TODO Auto-generated catch block
			System.err.println(e.toString());
		}
	}

	/* Standalone executable */
//	public static void main(String args[]){
//		Debug.MODE = false;
//		if(args.length > 0) new Monster(args[0], Integer.parseInt(args[1]));
//		else new Monster("localhost", 56413);
//	}
}
