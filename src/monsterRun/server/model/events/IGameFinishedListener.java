package monsterRun.server.model.events;

/**
 * 
 * Interface that defines the methods for a listener listening to the event of
 * the game finishing
 * 
 */
public interface IGameFinishedListener {
	public void gameFinished(String winner);
}
