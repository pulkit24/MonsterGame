/** @Pulkit
 * Handles player's requests - launch one SessionManager for each player.
 * Launched on the server side.
 */
package server;

import java.net.Socket;
import components.Debug;
import components.grid.Cell;
import components.grid.Coordinates;
import components.grid.GameMap;
import components.network.CommunicationManager;
import components.packets.MovePacket;
import components.packets.NotificationPacket;
import components.packets.Packet;
import components.packets.SuccessPacket;
import components.packets.UpdatePacket;

public class SessionManager extends Thread{
	private CommunicationManager comm;
	private int playerId;
	private Coordinates playerPosition;
	private Boolean isTerminated = false;
	private Boolean isMonster = false;

	public SessionManager(){
	}

	public SessionManager(Socket socket){
		comm = new CommunicationManager(socket);
	}

	public SessionManager(Socket socket, int playerId){
		comm = new CommunicationManager(socket);
		this.playerId = playerId;
	}

	public SessionManager(Socket socket, Boolean isMonster){
		comm = new CommunicationManager(socket);
		this.isMonster = isMonster;
	}

	public SessionManager(Socket socket, int playerId, Boolean isMonster){
		comm = new CommunicationManager(socket);
		this.playerId = playerId;
		this.isMonster = isMonster;
	}

	/* Main function */
	public void run(){
		sendStartSignal();

		while(!isTerminated){
			handlePlayerRequest(getPlayerRequest());
		}
	}

	private void sendStartSignal(){
		/* Start Game Protocol */

		/* 0. Wait until the server sets the game as started */
		synchronized(Server.gameStateLock){
			while(Server.gameState != Server.STARTED)
				try{
					Server.gameStateLock.wait();
				}catch(InterruptedException e){
					// TODO Auto-generated catch block
					System.err.println(e.toString());
				}
		}

		/* 1. Send start game packet */
		NotificationPacket notificationPacket = new NotificationPacket(NotificationPacket.STARTGAME);
		comm.sendPacket(notificationPacket);
		Debug.log("Session handler for player " + playerId, "start message sent to player");
	}

	private Packet getPlayerRequest(){
		/* Get a packet from player */
		Debug.log("Session handler for player " + playerId, "awaiting any packet");
		return comm.receivePacket();
	}

	private void handlePlayerRequest(Packet requestPacket){
		/* What type is the packet? */
		int requestType = requestPacket.getType();

		if(requestType == Packet.MOVEPACKET){
			/* Move Protocol */
			performMove((MovePacket)requestPacket);
		}else if(requestPacket.getType() == Packet.NOTIFICATIONPACKET){
			/* Update Protocol */
			handleNotice((NotificationPacket)requestPacket);
		}else{
			/* Unrecognized packet */
			Debug.log("Session handler for player " + playerId, "unexpected request packet received");
		}
	}

	private void performMove(MovePacket movePacket){
		/* Move Protocol */
		Debug.log("Session handler for player " + playerId, "move packet received");

		/* 1. Execute the move */
		Boolean moveSuccess = false;
		Coordinates moveCoords = movePacket.getNewCoords();
		synchronized(GameMap.gameMapLock){
			/* Check if the move is valid and doesn't involve the death of a player in case of monster */
			if(Server.serverMap.isValidMove(playerPosition, moveCoords)){
				/* Execute the move */
				Server.serverMap.makeMove(playerId, playerPosition, moveCoords);
				/* Update player's current position */
				playerPosition = moveCoords;
				moveSuccess = true;
				Debug.log("Session handler for player " + playerId, "moved player to " + moveCoords);
			}else if(isMonster && Server.serverMap.getCell(moveCoords).getStatus() == Cell.OCCUPIED){
				/* Else, if this is a kill move, and you're a monster, kill the player */
				Server.serverMap.getCell(moveCoords).setStatus(Cell.EMPTY); // player wiped from map!
				Server.serverMap.makeMove(playerId, playerPosition, moveCoords); // monster moved to the player's position

				/* Update player's current position */
				playerPosition = moveCoords;
				moveSuccess = true;
				Debug.log("Session handler for player " + playerId, "moved player to " + moveCoords);
			}
		}

		/* 2. Send move success to the player */
		SuccessPacket moveSuccessPacket = new SuccessPacket(moveSuccess);
		Debug.log("Session handler for player " + playerId, "sending move success");
		comm.sendPacket(moveSuccessPacket);
		Debug.log("Session handler for player " + playerId, "sent");
	}

	private void handleNotice(NotificationPacket noticePacket){
		/* Get notice type */
		int noticeType = noticePacket.getNoticeType();

		if(noticeType == NotificationPacket.REFRESHREQUEST){
			/* Update Protocol */
			sendMapUpdate();
		}else if(noticeType == NotificationPacket.TERMINATED){
			/* Quit Protocol */
			terminateSession();
		}
	}

	private void sendMapUpdate(){
		/* Update Protocol */
		Debug.log("Session handler for player " + playerId, "refresh request received");

		/* -1. Are you the last remaining player? */
		Boolean lastPlayer = false;
		synchronized(Server.gameStateLock){
			if(Server.activePlayers <= 1) lastPlayer = true;
		}

		boolean isDead = false;
		
		synchronized(GameMap.gameMapLock){
			if(lastPlayer){
				/* 0 Send a game won update */
				UpdatePacket victoryPacket = new UpdatePacket(Server.serverMap, true);
				Debug.log("Session handler for player " + playerId, "sending game summary packet");
				comm.sendPacket(victoryPacket);
				Debug.log("Session handler for player " + playerId, "sent");

				/* End */
				terminateSession();
			}else{
				/* 0. Was the map changed since the last request? */
				if(!Server.serverMap.isLastMoveBy(playerId)){
					/* 0.1 Are you still on the map, or were you killed off by the monster? */
					Cell playerCell = Server.serverMap.getCell(playerPosition);
					if(!(playerCell.getStatus() == Cell.OCCUPIED && playerCell.getOccupant() == playerId)){
						/* Dude, you just died! Send this last update and then kill yourself */
						isDead = true;
					}

					/* 1.1 Send a map update */
					UpdatePacket updatePacket = new UpdatePacket(Server.serverMap);
					Debug.log("Session handler for player " + playerId, "sending update packet");
					comm.sendPacket(updatePacket);
					Debug.log("Session handler for player " + playerId, "sent");

				}else{
					/* 1.2 Send an empty packet - because the client expects some reply! */
					Debug.log("Session handler for player " + playerId, "Nothing to update");
					Packet blankPacket = new Packet();
					Debug.log("Session handler for player " + playerId, "sending blank packet");
					comm.sendPacket(blankPacket);
					Debug.log("Session handler for player " + playerId, "sent");
				}
			}
		}
		
		if(isDead) terminateSession();
	}

	private void terminateSession(){
		Debug.log("Session handler for player " + playerId, "has been asked to terminate");
		isTerminated = true;

		/* Remove yourself from the active player count */
		synchronized(Server.gameStateLock){
			Server.activePlayers--;
		}
	}

	public Socket getSocket(){
		return comm.getCommunicationSocket();
	}

	public int getPlayerId(){
		return playerId;
	}

	public Coordinates getPlayerPosition(){
		return playerPosition;
	}

	public synchronized GameMap getServerMap(){
		return Server.serverMap;
	}

	public void setPlayerPosition(Coordinates playerPosition){
		this.playerPosition = playerPosition;
	}

	public void setPlayerId(int playerId){
		this.playerId = playerId;
	}
}
