package monsterRun.server.model.events;

import monsterRun.common.model.Position;

/**
 * 
 * Interface that defines the methods for a listener listening to the event of a
 * player dying in the game
 * 
 */
public interface IPlayerDeadListener {
	public void playerDead(String id, Position position);
}
