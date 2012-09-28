/** Executable
 * Launches the player with good defaults.
 */
package executable;

import client.Player;
import components.Debug;

public class LaunchPlayer{
	public static void main(String args[]){
		Debug.MODE = false;
		new Player("localhost", 56413);
	}
}
