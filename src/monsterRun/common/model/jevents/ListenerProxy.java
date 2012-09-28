package monsterRun.common.model.jevents;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *
 * The Proxy class that is used by the {@link JEvent} class instead of the
 * actual listener interface
 * 
 * @param <T>
 *            The listener interface
 */
public final class ListenerProxy<T> implements InvocationHandler {

	private ArrayList<T> listenersList;

	public ListenerProxy(ArrayList<T> listenersList) {
		this.listenersList = listenersList;
	}

	/**
	 * The method that java invokes when any method of this proxy is invoked.
	 */
	@Override
	public Object invoke(Object proxy, Method m, Object[] args)
			throws Throwable {

		for (int i = listenersList.size() - 1; i >= 0; i--) {
			try {
				m.invoke(listenersList.get(i), args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}
}