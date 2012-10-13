/** @Cetin
 * Runs and controls the monster. 
 * Monster simply connects to the server just like any other player.
 * Preferably assign it an id of 0 on the server!
 * Usage:
 * Initialize, then use start() method to run as separate thread.
 */
package server.controller;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import client.view.GUI;
import components.Debug;
import components.grid.Coordinates;
import components.grid.GameMap;
import components.network.ClientInterface;
import components.network.ServerInterface;

public class Monster extends UnicastRemoteObject implements ClientInterface{
	private static final long serialVersionUID = -132504913954318600L;
	
	private int playerId;
	private String playerName;
	protected GameMap playerMap;
	protected GUI gui;
	private Coordinates currentPosition;
	private int resetsLeft = 3; // Only allow 3 resets during the game

	/* Control the movement speed in milliseconds */
	private int moveTime = 230; // Time taken to make a move in ms
	private static int moveTimeSlow = 750;

	private ServerInterface server = null; // for RMI calls!

	/* Determine turns than to slow down motion */
	private int moveDirection = 0;
	private static int alongX = 1;
	private static int alongY = 2;

	public Monster(String host, int port) throws RemoteException{

		/* Initialize GUI */
		gui = new GUI(true);
		gui.setResetsLeft(resetsLeft);
		Debug.log("Monster", "GUI initiated");

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

		Debug.log("Monster", "Connected!");
		while(!gui.isClosed()){

			// runGame(); // play!
		}

		/* GUI closed - send quit message if connected */
		quitGame(connected);
	}

	private boolean connect(String host, int port){
		/* Connect to server */
		Debug.log("Monster", "Connecting...");

		/* Reflect in GUI */
		/* Get the Registry! */
		try{
			Registry reg = LocateRegistry.getRegistry(host, port);
			server = (ServerInterface)reg.lookup("Server");

			// server = (ServerInterface)Naming.lookup("rmi://"+host+":"+port+"/Server");

			/* Reflect in GUI */
			// gui.showConnectionProgress();

			/* Register this client as Callback */
			server.connect(this, host, port, 0, true, "", "", "", "", "", "");
			return true;
		}catch(AccessException e){
			// TODO Auto-generated catch block
			System.err.println("Monster could not access server registry " + e.toString());
		}catch(RemoteException e){
			// TODO Auto-generated catch block
			System.err.println("Monster could not find the RMI register on the server" + e.toString());
		}catch(NotBoundException e){
			// TODO Auto-generated catch block
			System.err.println("Monster could not bind to the provided RMI address " + e.toString());
			// }catch(MalformedURLException e){
			// // TODO Auto-generated catch block
			// System.err.println("Monster supplied a bad RMI url "+e.toString());
		}

		return false;
	}

	public void joinSuccess(int playerId, Coordinates playerPosition, Coordinates monsterPosition){
		this.playerId = playerId;
		this.currentPosition = playerPosition;

		if(gui.isClosed()) return; // if gui closed, did the impatient user close the window?

		/* Joined! Show users the state */
		// gui.showConnectionConfirmation(); // "waiting for other players..."
	}

	public void runGame(GameMap startMap) throws RemoteException{
		this.playerMap = startMap;

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
			boolean isTurn = false;
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
			Debug.log("Monster", "New coords: " + moveCoords);
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

	private void sendMove(Coordinates moveCoords, Boolean reset) throws RemoteException{
		/* Move or Reset Protocol */

		/* 1. Send move */
		Debug.log("Monster " + playerId, "Sending move packet: " + moveCoords.toString());
		boolean isMoveSuccess = server.makeMove(this, currentPosition, moveCoords, playerId, true);
		Debug.log("Monster " + playerId, "Sent");

		/* 2. Wait for reply */
		Debug.log("Monster " + playerId, "Waiting for reply");
		Debug.log("Monster " + playerId, "Reply received, result is: " + isMoveSuccess);

		/* Reflect in GUI */
		if(isMoveSuccess){
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

	public void quitGame(Boolean tellServer) throws RemoteException{
		/* Quit Game Protocol */

		/* 1. Send termination notice */
		if(tellServer){
			Debug.log("Monster " + playerId, "sending quit notice");
			server.quitGame(this, true);
			Debug.log("Monster " + playerId, "Sent");
		}

	}

	public void deathNotice(int score){
		// gui.showDefeatMessage();
		gui.forceClose();
	}

	public void gameWonNotice(int score){
		// gui.showVictoryMessage();
	}

	public void updateMap(GameMap gameMap){
		// synchronized(GameMap.gameMapLock){
		playerMap = gameMap;
		gui.setGameMap(playerMap);
		gui.refresh();
		// }
	}

	public String getUserName(){
		return null;
	}

}
