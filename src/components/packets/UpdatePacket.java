/** @Pulkit
 * Used to send regular map updates to the players
 * Usage:
 * 1. Use constructor to initialize
 * 2. Serialize and send
 * 3. Use getNewGameMap() to get the new map
 */
package components.packets;

import components.grid.GameMap;

public class UpdatePacket extends Packet{
	private static final long serialVersionUID = -1090831391049369122L;

	private GameMap newGameMap;
	private Boolean won = false;

	public UpdatePacket(GameMap newGameMap){
		super(Packet.UPDATEPACKET);
		this.newGameMap = newGameMap;
		won = false;
	}
	
	public UpdatePacket(GameMap newGameMap, Boolean won){
		super(Packet.UPDATEPACKET);
		this.newGameMap = newGameMap;
		this.won = won;
	}
	
	public GameMap getNewGameMap(){
		return newGameMap;
	}
	
	public Boolean won(){
		return won;
	}
}
