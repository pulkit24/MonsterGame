/** @Pulkit 
 * Send a move/reset request to the server
 * Usage:
 * 1. Use constructor to initialize:
 * 		For regular move, use MoveType.REGULAR and the target coordinates
 * 		For reset move, use MoveType.RESET and GameMap.CENTRE for coordinates
 * 2. Serialize and send
 * 3. Use getters to get data
 */
package components.packets;

import components.grid.Coordinates;

public class MovePacket extends Packet{
	private static final long serialVersionUID = 5263546275183232863L;

	private int moveType;
	private Coordinates newCoords;

	/* Move types */
	public static final int REGULAR = 0;
	public static final int RESET = 1;

	public MovePacket(int moveType, Coordinates newCoords){
		super(Packet.MOVEPACKET);
		this.moveType = moveType;
		this.newCoords = newCoords;
	}

	public int getMoveType(){
		return moveType;
	}
	public Coordinates getNewCoords(){
		return newCoords;
	}
}
