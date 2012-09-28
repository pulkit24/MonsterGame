/** @Pulkit 
 * Generic packet for sending/receiving data across the network 
 */
package components.packets;

import java.io.Serializable;

public class Packet implements Serializable{
	private static final long serialVersionUID = 1683346984947872606L;

	/* Packet type for identification */
	private int type;
	public static int PACKET = 0;
	public static int NOTIFICATIONPACKET = 1;
	public static int SUCCESSPACKET = 3;
	public static int MOVEPACKET = 5;
	public static int UPDATEPACKET = 6;

	public Packet(){
		this.type = Packet.PACKET;
	}
	public Packet(int type){
		this.type = type;
	}
	public int getType(){
		return type;
	}
}
