/** @Pulkit 
 * Used as a reply packet for joining players 
 * Reports successful joining, with a set of initializing data, including:
 * game map, player's id and position, and the position of the monster
 * Usage:
 * 1. Use constructor JoinSuccessPacket(GameMap gameMap, int playerId, Coordinates initialPosition, Coordinates monsterPosition) 
 *    to initialize (do not use the other constructor!)
 * 2. Serialize and send
 * 3. Use getSuccess to confirm joining success
 * 4. Use getters to get all the data
 */
package components.packets;

import components.grid.Coordinates;
import components.grid.GameMap;

public class JoinSuccessPacket extends SuccessPacket{
	private static final long serialVersionUID = 271610704800157226L;

	private GameMap gameMap;
	private int playerId;
	private Coordinates initialPosition;
	private Coordinates monsterPosition;

	public JoinSuccessPacket(GameMap gameMap, int playerId, Coordinates initialPosition, Coordinates monsterPosition){
		super(true);
		this.gameMap = gameMap;
		this.playerId = playerId;
		this.initialPosition = initialPosition;
		this.monsterPosition = monsterPosition;
	}

	public JoinSuccessPacket(Boolean success){
		/* Don't use this constructor! */
		super(success);
		this.gameMap = new GameMap(GameMap.TEST_1);
		this.playerId = 1;
		this.initialPosition = Coordinates.ZERO;
		this.monsterPosition = Coordinates.ZERO;
	}

	public GameMap getGameMap(){
		return gameMap;
	}
	public int getPlayerId(){
		return playerId;
	}
	public Coordinates getInitialPosition(){
		return initialPosition;
	}
	public Coordinates getMonsterPosition(){
		return monsterPosition;
	}
}
