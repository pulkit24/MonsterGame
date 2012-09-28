/** Executable
 * Launches the server with good defaults.
 */
package executable;

import server.Server;
import components.Debug;
import components.grid.GameMap;

public class LaunchServer2{
	public static void main(String args[]){
		Debug.MODE = true;
		Debug.onlyAllow = "Monster";
		
		new Server(56413, GameMap.TEST_2);
	}
}
