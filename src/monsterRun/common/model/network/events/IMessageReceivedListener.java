package monsterRun.common.model.network.events;

import java.util.EventListener;

import monsterRun.common.model.network.NetworkMessage;

public interface IMessageReceivedListener extends EventListener {
	public void messageReceived(String senderId, NetworkMessage message);
}
