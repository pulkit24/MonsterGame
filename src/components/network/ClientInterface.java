package components.network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import components.grid.Coordinates;
import components.grid.GameMap;

public interface ClientInterface extends Remote{
	public void joinSuccess(int playerId, Coordinates playerPosition, Coordinates monsterPosition) throws RemoteException;
	public void runGame(GameMap gameMap) throws RemoteException;
	public void updateMap(GameMap gameMap) throws RemoteException;
	
	public void deathNotice(int score) throws RemoteException;
	public void gameWonNotice(int score) throws RemoteException;
	
	public String getUserName() throws RemoteException;
}
