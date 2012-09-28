package monsterRun.common.model.jevents;

import java.lang.reflect.Proxy;
import java.util.ArrayList;

import sun.reflect.Reflection;

/**
 * 
 * JEvent-Provides an event framework similar to the C# event framework
 * 
 * @param <T>
 *            The interface type of the event that should be fired
 */
public final class JEvent<T> {

	private T listener;
	private Class<?> permissionClass;
	private ArrayList<T> listenersList;

	private JEvent() {
	}

	public static JEvent<IDefaultListener> create() {
		return new JEvent<IDefaultListener>(IDefaultListener.class,
				Reflection.getCallerClass(2));
	}

	public static <I> JEvent<I> create(Class<I> classType) {
		return new JEvent<I>(classType, Reflection.getCallerClass(2));
	}

	public static <I> JEvent<I> create(Class<I> classType,
			Class<?> permissionClass) {
		return new JEvent<I>(classType, permissionClass);
	}

	/**
	 * Initializes a JEvent instance for the provided interface type. Uses
	 * reflection to get the class that initialized the event to check for
	 * permission. Only the initialized class can invoke the event.
	 * 
	 * @param classType
	 *            The interface type of the event that should be fired
	 */
	public JEvent(Class<T> classType) {
		this(classType, Reflection.getCallerClass(2));
	}

	/**
	 * Initializes a JEvent instance for the provided interface type. Uses the
	 * provided permission class to check for permission. Only the class with
	 * permission can invoke the event.
	 * 
	 * @param classType
	 *            The interface type of the event that should be fired
	 * @param permissionClass
	 *            The class that has permission to invoke the event
	 */
	@SuppressWarnings("unchecked")
	public JEvent(Class<T> classType, Class<?> permissionClass) {
		this();

		this.permissionClass = permissionClass;
		this.listenersList = new ArrayList<T>();

		listener = (T) Proxy.newProxyInstance(
				ListenerProxy.class.getClassLoader(),
				new Class[] { classType }, new ListenerProxy<T>(listenersList));
	}

	/**
	 * @return The Proxy to the specified class type if the invoked class has
	 *         permission. Otherwise returns null.
	 */
	public synchronized T get() {
		if (hasPermisson()) {
			return listener;
		}

		return null;
	}

	/**
	 * Register a class for getting notifications.
	 * 
	 * @param listener
	 */
	public synchronized void addListener(T listener) {
		if (!listenersList.contains(listener)) {
			listenersList.add(listener);
		}
	}

	/**
	 * Remove a class from the list of classes that gets notified.
	 * 
	 * @param listener
	 */
	public synchronized void removeListener(T listener) {
		if (listenersList.contains(listener)) {
			listenersList.remove(listener);
		}
	}

	public synchronized boolean clear() {
		if (hasPermisson()) {
			listenersList.clear();
			return true;
		}

		return false;
	}

	private boolean hasPermisson() {
		if (Reflection.getCallerClass(3).equals(permissionClass)) {
			return true;
		}

		return false;
	}
}
