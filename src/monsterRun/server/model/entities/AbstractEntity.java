package monsterRun.server.model.entities;

import monsterRun.common.model.Position;
import monsterRun.server.model.GameBoard;

/**
 * 
 * Class representing entities on board, entities are player and monster
 * 
 */
public abstract class AbstractEntity {
	private Position pos;
	private GameBoard board;

	public AbstractEntity(Position position, GameBoard board) {
		this.pos = position;
		this.board = board;
	}

	public Position getPosition() {
		return pos;
	}

	public void setPos(Position pos) {
		this.pos = pos;
	}

	public GameBoard getBoard() {
		return board;
	}

	public void setBoard(GameBoard board) {
		this.board = board;
	}
}
