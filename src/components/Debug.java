/** @Pulkit
 * Replacement for the tiring "System.out.println". 
 * When publishing, simply hide these messages by setting MODE to false.
 * (No need to remove all the Debug.log statements all through your code!)
 * Usage:
 * 1. Use Debug.log(String message) to print debug messages
 * 2. Set Debug.MODE to true in your main function to show messages
 * 
 */
package components;

public class Debug{
	public static Boolean MODE = false;
	public static String onlyAllow = null;

//	public static void log(String message){
//		if(MODE) System.out.println("Debug message: " + message);
//	}

	public static void log(String caller, String message){
		if(MODE && (onlyAllow == null || caller.equals(onlyAllow))) System.out
				.println("Debug message: " + caller + " - " + message);
	}
}
