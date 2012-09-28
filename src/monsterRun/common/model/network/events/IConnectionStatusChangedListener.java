package monsterRun.common.model.network.events;

import java.util.EventListener;

public interface IConnectionStatusChangedListener extends EventListener {
	public void connectionLost(String clientId);
}
