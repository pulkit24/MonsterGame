package monsterRun.server.model.events;

import monsterRun.common.model.Position;

/**
 * 
 * Interface that defines the methods for a listener listening to the event of
 * the monster moving in the game
 * 
 */
public interface IMonsterMoveListener {
	public void monsterMoved(Position pos);
}
