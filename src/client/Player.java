/** @Cetin
 * Client side function for the player functionality.
 * Usage:
 * Just initialize. It will automatically launch.
 * Launches GUI automatically, gets connection details from the player,
 * Tries to connect, then runs the game if it's started.
 */
package client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import components.Debug;
import components.grid.Coordinates;
import components.grid.GameMap;
import components.model.User;
import components.network.ClientInterface;
import components.network.ServerInterface;
import components.packets.MovePacket;

public class Player extends UnicastRemoteObject implements ClientInterface{
	private int playerId;
	private User playerDetails; // complete mode of player 
	protected GameMap playerMap;
	protected GUI gui;
	private Coordinates currentPosition;
	private int resetsLeft = 3; // only allow 3 resets during the game
	private int preferredPosition; // asked at the start of player
	private String myHost = "";
	private int myPort = 0;

	private int moveTime = 250; // time taken to make a move in milliseconds

	private ServerInterface server = null; // for RMI calls!

	public Player(String host, int port) throws RemoteException{
		/* Initialize GUI */
		gui = new GUI();
		gui.setResetsLeft(resetsLeft);
		Debug.log("Player", "GUI initiated");

		myHost = host;
		myPort = port;

		/* Get details of the host server from player */
		String hostDetails[] = gui.getHostDetails(false, host, port + "");
		if(hostDetails[0] == null || hostDetails[1] == null) quitGame(false); // quit if player declined/cancelled to enter details

		/* Connect - first attempt! */
		boolean connected = connect(hostDetails[0], Integer.parseInt(hostDetails[1]));
		while(!connected && !gui.isClosed()){
			/* Get details of the host server showing the failed previous attempt, if any */
			hostDetails = gui.getHostDetails(true, hostDetails[0], hostDetails[1]);
			if(hostDetails[0] == null || hostDetails[1] == null) System.exit(0); // quit if player declined/cancelled to enter details

			/* Another attempt to connect */
			connected = connect(hostDetails[0], Integer.parseInt(hostDetails[1]));
		}

		/* Login/register into the game - first attempt! */
		boolean joined = joinGame();
		while(!joined && !gui.isClosed()){
			/* Another attempt to connect */
			joined = joinGame();
		}
		
		Debug.log("Player", "Connected!");
		while(true){
			synchronized(GameMap.gameMapLock){
				if(gui.isClosed()) break;
			}
			// runGame(); // play!
		}

		/* GUI closed - send quit message if connected */
		quitGame(connected);
	}

	/* Connect to the server */
	private boolean connect(String host, int port){
		/* Connect to server */
		Debug.log("Player", "Connecting on host ...");

		/* Get the Registry! */
		try{
			Registry reg = LocateRegistry.getRegistry(host, port);
			server = (ServerInterface)reg.lookup("Server");

			/* Reflect in GUI */
			gui.showConnectionProgress();
			
			Debug.log("Player", "Connected!");

			return true;
		}catch(AccessException e){
			// TODO Auto-generated catch block
			System.err.println("Player could not access server registry " + e.toString());
		}catch(RemoteException e){
			// TODO Auto-generated catch block
			System.err.println("Player could not find the RMI register on the server" + e.toString());
		}catch(NotBoundException e){
			// TODO Auto-generated catch block
			System.err.println("Player could not bind to the provided RMI address " + e.toString());
		}

		return false;
	}

	private boolean joinGame() throws RemoteException{
		/* Register/Login user */
		/* Get player details the player */
		gui.showLoginWindow(playerDetails);
		
		while(!gui.isUserDataAvailable()){
			// wait for user to enter details
			Debug.log("MainMenu", "waiting for player details");
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){
				// TODO Auto-generated catch block
				System.err.println(e.toString());
			}
		}
		playerDetails = gui.getPlayerDetails();
		Debug.log("MainMenu", "player details received!");
		
		if(playerDetails == null) quitGame(false); // quit if player declined/cancelled to enter details
	
		/* Get preferred position */
		preferredPosition = gui.getPreferredPosition();
	
		/* Register this client as Callback */
		return server.connect(this, myHost, myPort, preferredPosition, false, 
				playerDetails.getFirstName(),
				playerDetails.getSurname(),
				playerDetails.getAddress(),
				playerDetails.getPhone(),
				playerDetails.getUsername(),
				playerDetails.getPassword());
	}

	public void joinSuccess(int playerId, Coordinates playerPosition, Coordinates monsterPosition){
		this.playerId = playerId;
		this.currentPosition = playerPosition;

		if(gui.isClosed()) return; // if gui closed, did the impatient user close the window?

		/* Joined! Show users the state */
		gui.showConnectionConfirmation(); // "waiting for other players..."
	}

	/* Main game logic */
	public void runGame(GameMap startMap) throws RemoteException{
		/* Try to join a new game */
		// joinGame(); /* player id received here */
		/* Await game start */
		// awaitGameStart();
		Debug.log("Player", "game start signal received");
		this.playerMap = startMap;

		if(gui.isClosed()) return; // if gui closed, did the impatient user close the window?

		/* Update GUI */
		synchronized(GameMap.gameMapLock){
			gui.setPlayerDetails(playerId, playerDetails.getUsername());
			gui.setGameMap(playerMap);
			gui.refresh();
		}

		/* Get the move from the user OR get the latest game map */
		while(!gui.isClosed()){ // as long as the user didn't close the window

			/* Wait a while for fairness */
			delay(moveTime);

			/* Request a map refresh instead */
			// requestMapUpdate();

			/* Did I die? */
			// if(playerMap.getCell(currentPosition).getStatus() != Cell.OCCUPIED
			// || playerMap.getCell(currentPosition).getOccupant() != playerId){
			// /* Yep, sadly you dead*/
			// gui.showDefeatMessage();
			// return;
			// }

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

	// private void joinGame(){
	// /* Follow the Join Game Protocol */
	//
	// /* 1. Send join notice */
	// Debug.log("Player", "Sending join request");
	// try{
	// server.joinGame(preferredPosition, InetAddress.getLocalHost().getHostAddress(), comm.getMyPort());
	// }catch(UnknownHostException e){
	// // TODO Auto-generated catch block
	// System.err.println("Couldn't find my own address! " + e.toString());
	// }
	// Debug.log("Player", "Sent");
	//
	// /* 2. Wait for reply */
	// Debug.log("Player", "Waiting for reply");
	// JoinSuccessPacket replyPacket = (JoinSuccessPacket)comm.receiveData();
	// Debug.log("Player", "Reply received, result is: " + replyPacket.getSuccess());
	//
	// /* Get all useful data out of this packet */
	// playerId = replyPacket.getPlayerId();
	// synchronized(GameMap.gameMapLock){
	// playerMap = replyPacket.getGameMap();
	// }
	// currentPosition = replyPacket.getInitialPosition();
	// }

	// private void awaitGameStart(){
	// /* Start Game Protocol */
	//
	// /* 1. Wait for start game notice */
	// Debug.log("Player " + playerId, "Waiting for start game notice");
	// NotificationPacket notificationReceived = (NotificationPacket)comm.receiveData();
	// if(notificationReceived.getNoticeType() == NotificationPacket.STARTGAME) Debug.log("Player " + playerId,
	// "Received start game notice");
	// else Debug.log("Player " + playerId, "Received some stray notification packet");
	// }

	private void sendMove(Coordinates moveCoords, Boolean reset) throws RemoteException{
		/* Move or Reset Protocol */

		/* 1. Send move */
		int type = reset ? MovePacket.RESET : MovePacket.REGULAR;
		Debug.log("Player " + playerId, "Sending move: " + moveCoords.toString());
		boolean isMoveSuccess = server.makeMove(this, currentPosition, moveCoords, playerId, false);
		Debug.log("Player " + playerId, "Sent");

		/* 2. Wait for reply */
		Debug.log("Player " + playerId, "Waiting for reply");
		Debug.log("Player " + playerId, "Reply received, result is: " + isMoveSuccess);

		/* Reflect in GUI */
		if(isMoveSuccess){
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

	// private void requestMapUpdate(){
	// /* Request a normal map refresh */
	// Debug.log("Player " + playerId, "Sending request for map refresh");
	// server.requestUpdate();
	// Debug.log("Player " + playerId, "Sent");
	//
	// /* 2. Wait for reply */
	// Debug.log("Player " + playerId, "Waiting for reply");
	// Packet replyPacket = comm.receiveData();
	// Debug.log("Player " + playerId, "Update received");
	// /* Could be blank! */
	// if(replyPacket.getType() == Packet.UPDATEPACKET){
	// UpdatePacket updatePacket = (UpdatePacket)replyPacket;
	//
	// /* Get all useful data out of this packet */
	// synchronized(GameMap.gameMapLock){
	// playerMap = updatePacket.getNewGameMap();
	// gui.setGameMap(playerMap);
	// gui.refresh();
	// }
	//
	// /* Did you win? */
	// if(updatePacket.won()){
	// gui.showVictoryMessage();
	// quitGame(false);
	// }
	// }
	// }

	public void quitGame(Boolean tellServer) throws RemoteException{
		/* Quit Game Protocol */

		/* 1. Send termination notice */
		if(tellServer){
			Debug.log("Player " + playerId, "sending quit notice");
			server.quitGame(this, false);
			Debug.log("Player " + playerId, "Sent");
		}

		/* Shut down application */
		System.exit(0);
	}

	/* Standalone executable */
	// public static void main(String args[]){
	// Debug.MODE = false;
	// if(args.length > 0) new Player(args[0], Integer.parseInt(args[1]));
	// else new Player("localhost", 56413);
	// }

	public void deathNotice(int score){
		gui.showDefeatMessage(score);
		System.exit(0);
	}

	public void gameWonNotice(int score){
		gui.showVictoryMessage(score);
		System.exit(0);
	}

	public void updateMap(GameMap gameMap){
		synchronized(GameMap.gameMapLock){
			playerMap = gameMap;
			gui.setGameMap(playerMap);
			gui.refresh();
		}
	}

	public String getUserName(){
		return playerDetails.getUsername();
	}
}
