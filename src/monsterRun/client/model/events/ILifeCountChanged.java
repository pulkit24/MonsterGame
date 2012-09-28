package monsterRun.client.model.events;

import monsterRun.client.model.entities.PlayerEntity;

public interface ILifeCountChanged {
	void lifeCountChanged(PlayerEntity player, int count);
}
