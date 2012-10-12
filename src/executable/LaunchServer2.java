/** Executable
 * Launches the server with good defaults and the alternate map,
 */
package executable;

import java.rmi.RemoteException;
import server.Server;
import components.Debug;
import components.grid.GameMap;

public class LaunchServer2{
	public static void main(String args[]){
		Debug.MODE = true;
		// Debug.onlyAllow = "Monster";

		try{
			new Server(56413, GameMap.TEST_2);
		}catch(RemoteException e){
			// TODO Auto-generated catch block
			System.err.println("LaunchServer2 threw remote exception " + e.toString());
		}
	}
}
