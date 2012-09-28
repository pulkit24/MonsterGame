package monsterRun.client.model.entities;

import java.awt.Image;

import monsterRun.client.model.GameBoard;
import monsterRun.common.model.Position;
import monsterRun.common.model.enums.Direction;
import monsterRun.common.model.janimationframework.controllers.sprites.ImageSprite;

/**
 *
 * The base class of all entities that manages the {@link Position},
 * {@link Direction} and the {@link ImageSprite}
 */
public abstract class AbstractEntity {

	private ImageSprite sprite;

	protected Position position;
	protected GameBoard gameBoard;
	protected Direction direction;

	public AbstractEntity(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
		this.direction = Direction.RIGHT;
	}

	public Image getCalculatedSprite() {
		return sprite.getCalculatedSprite();
	}

	public Image getSprite(int frame) {
		return sprite.getFrame(frame);
	}

	public void setSprite(ImageSprite sprite) {
		this.sprite = sprite;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public abstract void move(Direction direction);
}
