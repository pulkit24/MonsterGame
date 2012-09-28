package monsterRun.server.model.events;

import monsterRun.common.model.Position;

/**
 * @author Wriddhi
 * Interface that defines the methods for a listener listening to the event of a
 * bonus life appearing or disappearing from the board
 * 
 */
public interface IBonusLifeModifiedListener {
	public void bonusLifeCreated(Position pos);

	public void bonusLifeDestroyed(Position pos);
}
