package monsterRun.client.model.events;

import monsterRun.client.model.entities.AbstractEntity;
import monsterRun.client.model.entities.PlayerEntity;

public interface IGameStateChanged {

	void playerAdded(AbstractEntity entity);

	void playerRemoved(AbstractEntity entity);

	void currentPlayerAdded(PlayerEntity player);

	void currentPlayerLoggedIn(PlayerEntity player);

	void currentPlayerDied();

	void gameStarted();
}
