package monsterRun.server.model.events;

/**
 * 
 * Interface that defines the methods for a listener listening to the event of
 * catching a message to be output to the console
 * 
 */
public interface ISysoutMessageCatched {
	public void sysoutCatched(String message);
}
