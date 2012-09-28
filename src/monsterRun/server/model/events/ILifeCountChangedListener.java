package monsterRun.server.model.events;

import monsterRun.server.model.entities.Player;

/**
 * 
 * Interface that defines the methods for a listener listening to the event of
 * catching a message to be output to the consoe
 * 
 */
public interface ILifeCountChangedListener {
	public void lifeCountChanged(Player p, int life);
}
