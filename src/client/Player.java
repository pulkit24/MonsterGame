/** @Cetin
 * Client side function for the player functionality.
 * Usage:
 * Just initialize. It will automatically launch.
 * Launches GUI automatically, gets connection details from the player,
 * Tries to connect, then runs the game if it's started.
 */
package client;

import components.Debug;
import components.grid.Cell;
import components.grid.Coordinates;
import components.grid.GameMap;
import components.network.CommunicationManager;
import components.packets.JoinSuccessPacket;
import components.packets.MovePacket;
import components.packets.NotificationPacket;
import components.packets.Packet;
import components.packets.SuccessPacket;
import components.packets.UpdatePacket;

public class Player{
	private CommunicationManager comm; // abstracts away all the socket communication neatly
	private int playerId;
	private String playerName;
	protected GameMap playerMap;
	protected GUI gui;
	private Coordinates currentPosition;
	private int resetsLeft = 3; // only allow 3 resets during the game
	private int preferredPosition;	// asked at the start of player 

	private int moveTime = 250; // time taken to make a move in milliseconds

	public Player(String host, int port){
		/* Initialize GUI */
		gui = new GUI();
		gui.setResetsLeft(resetsLeft);
		Debug.log("Player", "GUI initiated");

		/* Get player details the player */
		playerName = gui.getPlayerDetails();
		if(playerName == null) quitGame(false); // quit if player declined/cancelled to enter details

		/* Get details of the host server from player */
		String hostDetails[] = gui.getHostDetails(false, host, port + "");
		if(hostDetails[0] == null || hostDetails[1] == null) quitGame(false); // quit if player declined/cancelled to enter details

		/* Connect - first attempt! */
		Boolean connected = connect(hostDetails[0], Integer.parseInt(hostDetails[1]));
		while(!connected && !gui.isClosed()){
			/* Get details of the host server showing the failed previous attempt, if any */
			hostDetails = gui.getHostDetails(true, hostDetails[0], hostDetails[1]);
			if(hostDetails[0] == null || hostDetails[1] == null) System.exit(0); // quit if player declined/cancelled to enter details

			/* Another attempt to connect */
			connected = connect(hostDetails[0], Integer.parseInt(hostDetails[1]));
		}
		
		/* Get preferred position */
		preferredPosition = gui.getPreferredPosition();

		if(!gui.isClosed()){
			Debug.log("Player", "Connected!");
			runGame(); // play!
		}

		/* GUI closed - send quit message if connected */
		quitGame(connected);
	}

	/* Connect to the server */
	private Boolean connect(String host, int port){
		/* Connect to server */
		comm = new CommunicationManager(host, port);
		Debug.log("Player", "Connecting...");

		/* Reflect in GUI */
		gui.showConnectionProgress();

		return comm.connect();
	}

	/* Main game logic */
	private void runGame(){
		/* Try to join a new game */
		joinGame(); /* player id received here */
		/* Joined! Show users the state */
		gui.showConnectionConfirmation(); // "waiting for other players..."
		/* Await game start */
		awaitGameStart();

		if(gui.isClosed()) return; //  if gui closed, did the impatient user close the window?

		/* Update GUI */
		synchronized(GameMap.gameMapLock){
			gui.setPlayerDetails(playerId, playerName);
			gui.setGameMap(playerMap);
			gui.refresh();
		}

		/* Get the move from the user OR get the latest game map */
		while(!gui.isClosed()){ // as long as the user didn't close the window

			/* Wait a while for fairness */
			delay(moveTime);

			/* Request a map refresh instead */
			requestMapUpdate();

			/* Did I die? */
			if(playerMap.getCell(currentPosition).getStatus() != Cell.OCCUPIED
					|| playerMap.getCell(currentPosition).getOccupant() != playerId){
				/* Yep,  sadly  you dead*/
				gui.showDefeatMessage();
				return;
			}

			/* Find something to do */
			synchronized(GameMap.gameMapLock){
				/* Is there any valid move? */
				int moveDirection = gui.getUserInput();
				if(GameMap.isValidDirection(moveDirection)){ // just confirm if the movement keys are valid
					/* Movement */
					Debug.log("Player", "User input detected!");
					Coordinates moveCoords = playerMap.getMoveCoords(currentPosition, moveDirection);
					Debug.log("Player", "New coords: " + moveCoords);
					/* Send the move to the server */
					sendMove(moveCoords, false);
				}else if(moveDirection == GameMap.RESET){
					/* Reset */
					Debug.log("Player", "User asked for reset");
					/* Do we have any resets left? */
					if(resetsLeft <= 0){
						Debug.log("Player " + playerId, "Resets exhausted");
					}else{
						/* Send the move to the server */
						sendMove(playerMap.CENTRE, true);
					}
				}
			}
		}
	}

	private void delay(int delayTime){
		/* Make a move every few milliseconds */
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
		NotificationPacket joinNoticePacket = new NotificationPacket(NotificationPacket.JOINGAME, preferredPosition);
		Debug.log("Player", "Sending join request");
		comm.sendPacket(joinNoticePacket);
		Debug.log("Player", "Sent");

		/* 2. Wait for reply */
		Debug.log("Player", "Waiting for reply");
		JoinSuccessPacket replyPacket = (JoinSuccessPacket)comm.receivePacket();
		Debug.log("Player", "Reply received, result is: " + replyPacket.getSuccess());

		/* Get all useful data out of this packet */
		playerId = replyPacket.getPlayerId();
		synchronized(GameMap.gameMapLock){
			playerMap = replyPacket.getGameMap();
		}
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
			/* Update player's current position  sync..*/
			currentPosition = moveCoords;
			synchronized(GameMap.gameMapLock){
				gui.setGameMap(playerMap);
				if(reset){
					resetsLeft--;
					gui.setResetsLeft(resetsLeft); // update heads up display (HUD) elements if needed
				}
				gui.refresh();
			}
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
			synchronized(GameMap.gameMapLock){
				playerMap = updatePacket.getNewGameMap();
				gui.setGameMap(playerMap);
				gui.refresh();
			}

			/* Did you win? */
			if(updatePacket.won()){
				gui.showVictoryMessage();
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
		System.exit(0);
	}

	/* Standalone executable */
//	public static void main(String args[]){
//		Debug.MODE = false;
//		if(args.length > 0) new Player(args[0], Integer.parseInt(args[1]));
//		else new Player("localhost", 56413);
//	}
}
