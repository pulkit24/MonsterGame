/** Executable
 * Launches the server with good defaults.
 */
package executable;

import java.rmi.RemoteException;
import server.Server;
import components.Debug;
import components.grid.GameMap;

public class LaunchServer{
	public static void main(String args[]){
		Debug.MODE = true;
		Debug.dontAllow = "Map, Monster";

		try{
			new Server(56413, GameMap.TEST_1);
		}catch(RemoteException e){
			// TODO Auto-generated catch block
			System.err.println("LaunchServer threw remote exception " + e.toString());
		}
	}
}
