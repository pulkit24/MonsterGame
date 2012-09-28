package monsterRun.server.model.entities;

import monsterRun.common.model.Constants;
import monsterRun.common.model.Position;
import monsterRun.common.model.enums.Direction;
import monsterRun.common.model.jevents.JEvent;
import monsterRun.common.model.network.Communicator;
import monsterRun.server.model.GameBoard;
import monsterRun.server.model.events.ILifeCountChangedListener;

/**
 * 
 * Class representing the players in the game
 * 
 */
public class Player extends AbstractEntity {
	/** Instance variables */
	private String id;
	private int numLivesLeft;
	private Communicator communicator;
	private Direction direction;
	/** Event that gets fired when the life count of the player changes */
	public final JEvent<ILifeCountChangedListener> lifeCountChanged = JEvent
			.create(ILifeCountChangedListener.class);

	/**
	 * Constructor
	 * 
	 * @param id
	 *            the unique id of the player
	 * @param playerPos
	 *            the position of the player
	 * @param board
	 *            the board that is being played on
	 * @param communicator
	 *            the communicator responsible for communication between this
	 *            client and the server
	 * @param details
	 *            the details of the player
	 */
	public Player(String id, Position playerPos, GameBoard board,
			Communicator communicator) {
		super(playerPos, board);
		setId(id);
		setNumLivesLeft(Constants.MAX_LIVES);
		this.communicator = communicator;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getNumLivesLeft() {
		return numLivesLeft;
	}

	public void setNumLivesLeft(int numLivesLeft) {
		this.numLivesLeft = numLivesLeft;
		// Fire event on life count changed
		lifeCountChanged.get().lifeCountChanged(this, numLivesLeft);
	}

	public Communicator getCommunicator() {
		return communicator;
	}

	/**
	 * Move the player
	 * 
	 * @param direction
	 *            the direction the player is moving in
	 * @return boolean value indicating if the move was possible
	 */
	public boolean move(Direction direction) {
		Position newPos = null;
		// Calculate the new position based on the direction
		if (direction == Direction.LEFT) {
			Position left = getBoard().getPositionTowardsLeft(getPosition());
			if (left != null) {
				newPos = left;
			}
		}
		if (direction == Direction.RIGHT) {
			Position right = getBoard().getPositionTowardsRight(getPosition());
			if (right != null) {
				newPos = right;
			}
		}
		if (direction == Direction.UP) {
			Position up = getBoard().getPositionTowardsTop(getPosition());
			if (up != null) {
				newPos = up;
			}
		}
		if (direction == Direction.DOWN) {
			Position down = getBoard().getPositionTowardsBottom(getPosition());
			if (down != null) {
				newPos = down;
			}
		}
		if (direction == Direction.CENTER) {
			if (numLivesLeft > 0) {
				Position center = getBoard().getCentralPosition();
				if (center != null) {
					newPos = center;
				}
			}
		}
		// Check if the move is possible
		if (newPos != null) {
			boolean possible = getBoard().updatePlayerPosition(getPosition(),
					newPos, this);
			if (possible) {
				// Handle moving to center
				if (direction == Direction.CENTER) {
					int numLives = numLivesLeft - 1;
					setNumLivesLeft(numLives);
				}
				setPos(newPos);
				// Check if the player can be killed after the move
				getBoard().checkForKill();
				return true;
			}
		}
		return false;
	}

	/**
	 * Kill the player
	 */
	public void kill() {
		setNumLivesLeft(0);
	}

}
