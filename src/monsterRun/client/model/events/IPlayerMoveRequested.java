package monsterRun.client.model.events;

import monsterRun.common.model.enums.Direction;

public interface IPlayerMoveRequested {
	void moveRequested(Direction direction);
}
