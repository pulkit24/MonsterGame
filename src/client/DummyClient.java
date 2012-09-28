/** @Pulkit
 * Outdated executable client with basic send receive executable 
 */
package client;

import components.Debug;
import components.network.CommunicationManager;
import components.packets.JoinSuccessPacket;
import components.packets.NotificationPacket;

public class DummyClient{
	public static void main(String args[]){
		Debug.MODE = true;

		/* Follow the Join Game Protocol */
		/* 0. Connect to server */
		CommunicationManager comm = new CommunicationManager("localhost", 56413);
		Debug.log("DummyClient", "Connecting...");
		comm.connect();
		Debug.log("DummyClient", "Connected!");

		/* 1. Send join notice */
		NotificationPacket joinNoticePacket = new NotificationPacket(NotificationPacket.JOINGAME);
		Debug.log("DummyClient", "Sending join request");
		comm.sendPacket(joinNoticePacket);
		Debug.log("DummyClient", "Sent");

		/* 2. Wait for reply */
		Debug.log("DummyClient", "Waiting for reply");
		JoinSuccessPacket replyPacket = (JoinSuccessPacket)comm.receivePacket();
		Debug.log("DummyClient", "Reply received, result is: " + replyPacket.getSuccess());
	}
}
