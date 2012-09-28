package monsterRun.client.model.entities;

import java.io.IOException;

import monsterRun.client.model.GameBoard;
import monsterRun.common.model.ImageStore;
import monsterRun.common.model.Position;
import monsterRun.common.model.enums.Direction;
import monsterRun.common.model.janimationframework.controllers.sprites.ImageSprite;

public class MonsterEntity extends AbstractEntity {

	public MonsterEntity(GameBoard gameBoard) {
		super(gameBoard);

		ImageSprite sp = new ImageSprite(10);

		try {
			// Loads the monster animated GIF
			sp.loadFromGif(ImageStore.get().getImageStream("monster4.gif"));
		} catch (IOException e) {
		}

		setSprite(sp);
		setPosition(new Position(0, 0));
	}

	@Override
	public void move(Direction direction) {
	}
}
