package monsterRun.server.model.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import monsterRun.common.model.Constants;
import monsterRun.common.model.Position;
import monsterRun.common.model.enums.Direction;
import monsterRun.common.model.enums.GameMode;
import monsterRun.common.model.jevents.JEvent;
import monsterRun.server.model.GameBoard;
import monsterRun.server.model.events.IMonsterMoveListener;
import monsterRun.server.model.tree.Tree;
import monsterRun.server.model.utilities.Utility;

/**
 * 
 * Class representing the monster on the board
 * 
 */
public class Monster extends AbstractEntity {
	/** Event that gets fired when the monster moves */
	public final JEvent<IMonsterMoveListener> monsterMoved = new JEvent<IMonsterMoveListener>(
			IMonsterMoveListener.class);
	private Direction currentDirection;
	private Direction oldDirection;
	private GameMode mode;
	private long monsterMoveSleepTime;

	/**
	 * Constructor
	 * 
	 * @param monsterPos
	 *            the starting position of the monster
	 * @param board
	 *            the board that is being played on
	 */
	public Monster(Position monsterPos, GameBoard board) {
		super(monsterPos, board);
		monsterMoved.get().monsterMoved(this.getPosition());
		currentDirection = Direction.LEFT;
		oldDirection = null;
	}

	/**
	 * Move the monster
	 */
	public void move() {
		// Build a tree using the breadth-first technique
		Tree pathTree = new Tree(getPosition().toString());
		ArrayList<String> path = addChildrenToPath(pathTree, getPosition());
		// Handle if there is no path
		if (path == null) {
			System.err.println("Path finding failed");
			return;
		}
		if (path.size() > 0) {
			if (Constants.IS_DEBUG) {
				System.out.println("Current Position is : " + getPosition());
				System.out.println("Next Position is : " + path.get(0));
				System.out.println("The path is: ");
				System.out.println(path);
			}
			// Calculate the time to slow down monster for turning
			long millis = 0;
			if (oldDirection != null) {
				millis = (long) (monsterMoveSleepTime * 2.0);
			}
			oldDirection = currentDirection;
			// Move the monster to new position if possible
			Position newPos = Utility.getPositionFromString(path.get(0));
			boolean possible = getBoard().updateMonsterPosition(newPos);
			if (possible) {
				// Slow down monster for turning
				currentDirection = getBoard().getCurrentDirection(
						getPosition(), newPos);
				if (oldDirection != null && currentDirection != null
						&& oldDirection != currentDirection) {
					if (Constants.IS_DEBUG) {
						System.out.println("Monster turning");
					}
					try {
						Thread.sleep(millis);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				setPos(newPos);
				// Fire event indicating that the monster has moved
				monsterMoved.get().monsterMoved(this.getPosition());
			}
		}
	}

	/**
	 * Make the monster wait after a player has been killed
	 */
	public void digest() {
		long sleepTime = 2000;
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			System.err.println("Monster could not digest the player");
			e.printStackTrace();
		}
	}

	/**
	 * Build the tree for monster's path finding and return the path
	 * 
	 * @param pathTree
	 *            the tree used to find the path
	 * @param pos
	 *            the current position of the monster
	 * @return ArrayList containing the path of the monster
	 */
	private ArrayList<String> addChildrenToPath(Tree pathTree, Position pos) {
		LinkedList<Position> nodes = new LinkedList<Position>();
		nodes.add(pos);
		while (!nodes.isEmpty()) {
			// Inspect each node to see if a player is there and add its
			// children to the tree if they are
			// not already there
			Position node = nodes.removeFirst();
			if (getBoard().isPlayerInPosition(node)) {
				ArrayList<String> path = pathTree
						.getPathToRoot(node.toString());
				path = Utility.reverseArrayList(path);
				return path;
			}
			HashMap<String, Position> neighbours = getBoard()
					.getNeighBoursOfPosition(node);
			Object[] directions = neighbours.keySet().toArray();
			for (Object direction : directions) {
				Position neighbour = neighbours.get(direction);
				if (getBoard().isReachable(neighbour)) {
					if (!pathTree.exists(neighbour.toString())) {
						try {
							nodes.add(neighbour);
							pathTree.addChild(node.toString(),
									neighbour.toString());
						} catch (Exception e) {
							System.err
									.println("Path finding of monster failed");
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;
	}

	public GameMode getMode() {
		return mode;
	}

	public void setMode(GameMode mode) {
		this.mode = mode;
	}

	public void setMonsterMoveSleepingTime(long sleepTime) {
		this.monsterMoveSleepTime = sleepTime;
	}
}
