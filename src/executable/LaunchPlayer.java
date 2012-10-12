/** Executable
 * Launches the player with good defaults.
 */
package executable;

import java.rmi.RemoteException;
import client.Player;
import components.Debug;

public class LaunchPlayer{
	public static void main(String args[]){
		Debug.MODE = true;
		try{
			new Player("10.130.39.10", 56413);
		}catch(RemoteException e){
			// TODO Auto-generated catch block
			System.err.println("LaunchPlayer caught a remote exception"+ e.toString());
		}
	}
}