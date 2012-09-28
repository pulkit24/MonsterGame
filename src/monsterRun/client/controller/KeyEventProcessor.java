package monsterRun.client.controller;

import monsterRun.client.model.GameBoard;
import monsterRun.client.model.entities.AbstractEntity;
import monsterRun.client.model.events.IPlayerMoveRequested;
import monsterRun.common.model.enums.Direction;

public class KeyEventProcessor implements IPlayerMoveRequested {

	private GameBoard gameBoard;

	public KeyEventProcessor(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
	}

	@Override
	public void moveRequested(Direction direction) {
		AbstractEntity p = gameBoard.getCurrentPlayer();

		if (p != null && direction != null) {
			p.move(direction);
		}
	}
}
