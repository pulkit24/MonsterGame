package monsterRun.server.model.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import monsterRun.common.model.Position;

/**
 * 
 * Class that has a collection of utility functions used by the server
 * 
 */
public class Utility {

	/**
	 * Convert a string to a position object
	 * 
	 * @param pos
	 *            the string to be converted
	 * @return Position object which has been generated from the string
	 */
	public static Position getPositionFromString(String pos) {
		String[] tokens = pos.split(",");
		return new Position(Integer.parseInt(tokens[0]),
				Integer.parseInt(tokens[1]));
	}

	/**
	 * Get the key from the map which is associated with the lowest value
	 * 
	 * @param map
	 *            the map that has the key value pairs
	 * @return Object which is the key with the least value associated with it
	 */
	public static <T> Object getKeyWithMinValue(HashMap<T, Integer> map) {
		int min = -1;
		Object key = null;
		Object[] keys = map.keySet().toArray();
		for (int n = 0; n < keys.length; n++) {
			if (min < 0 || map.get(keys[n]) < min
					|| (min == map.get(keys[n]) && Math.random() < 0.5)) {
				min = map.get(keys[n]);
				key = keys[n];
			}
		}
		return key;
	}

	/**
	 * Reverse an array list
	 * 
	 * @param list
	 *            the list to be reversed
	 * @return ArrayList which is the reversed list of values
	 */
	public static <T> ArrayList<T> reverseArrayList(ArrayList<T> list) {
		ArrayList<T> reverseList = new ArrayList<T>();
		for (int n = list.size() - 1; n >= 0; n--) {
			reverseList.add(list.get(n));
		}
		return reverseList;
	}

	/**
	 * Get the key associated with the specified value
	 * 
	 * @param map
	 *            the map storing the key value pairs
	 * @param value
	 *            the value whose key needs to be found
	 * @return Object which is the key associated with the value passed as a
	 *         parameter
	 */
	public static <T> Object getKeyWithValue(HashMap<T, Integer> map,
			Integer value) {
		if (map.containsValue(value)) {
			Object[] keys = map.keySet().toArray();
			for (int n = 0; n < keys.length; n++) {
				if (map.get(keys[n]).equals(value)) {
					return keys[n];
				}
			}

		}
		return null;
	}
}
