package components.network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import components.grid.Coordinates;

public interface ServerInterface extends Remote{
	public boolean connect(ClientInterface client, String myHost, int myPort, int preferredPosition, boolean isMonster, String firstname,
			String surname, String address, String phone, String username, String password) throws RemoteException;

	public boolean makeMove(ClientInterface client, Coordinates from, Coordinates to, int playerId, boolean isMonster)
			throws RemoteException;

	public void quitGame(ClientInterface client, boolean isMonster) throws RemoteException;

	/* User account controls */
	public void register(String firstname, String surname, String address, String phone, String username, String password)
			throws RemoteException;
}
