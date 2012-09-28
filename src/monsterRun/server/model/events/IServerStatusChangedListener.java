package monsterRun.server.model.events;

/**
 * 
 * Interface that defines the methods for a listener listening to the event of
 * the server state being changed
 * 
 */
public interface IServerStatusChangedListener {
	public void serverStarted();

	public void serverStopped();
}
