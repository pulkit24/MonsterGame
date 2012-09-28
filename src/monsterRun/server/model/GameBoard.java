package monsterRun.server.model;

import java.util.ArrayList;
import java.util.HashMap;

import monsterRun.common.model.Constants;
import monsterRun.common.model.Position;
import monsterRun.common.model.enums.Direction;
import monsterRun.common.model.enums.GameMode;
import monsterRun.common.model.jevents.JEvent;
import monsterRun.server.model.entities.Monster;
import monsterRun.server.model.entities.Player;
import monsterRun.server.model.events.IGameFinishedListener;
import monsterRun.server.model.events.ILifeCountChangedListener;
import monsterRun.server.model.events.IPlayerDeadListener;

/**
 * 
 * Class representing the game board
 * 
 */
public class GameBoard extends Thread implements ILifeCountChangedListener {

	private int rows;
	private int columns;
	/**
	 * String array to represent the positions of the player and the monster
	 * internally and another string array for tracking bonus lives internally
	 * and Object array that contains objects to lock for synchronizing
	 * positions for player move
	 */
	private String[][] grid;
	private Object[][] locks;
	/** String constants for internal representation of entities */
	private final String playerString = "P";
	private final String monsterString = "X";
	private final String blockString = "B";
	private final String spaceString = " ";
	/** Other instance variables */
	private Monster monster;
	private final int numMovesBetweenSpeedChange = 12;
	private final double ninetyPercent = 0.9;
	private final int easyMonsterSleepTime = 750;
	private final int mediumMonsterSleepTime = 500;
	private final int hardMonsterSleepTime = 250;
	private ArrayList<Player> players;
	private GameMode mode;
	private long monsterSleepTime;
	private ArrayList<String> ids;

	/** Events fired when certain things change */
	public final JEvent<IPlayerDeadListener> playerDead = JEvent
			.create(IPlayerDeadListener.class);

	public final JEvent<IGameFinishedListener> gameFinished = JEvent
			.create(IGameFinishedListener.class);

	public final JEvent<ILifeCountChangedListener> lifeCountChanged = JEvent
			.create(ILifeCountChangedListener.class);

	/**
	 * Constructor
	 * 
	 * @param cells
	 *            the boolean array that represents the board, false means a
	 *            blocked cell
	 * @param mode
	 *            the mode of the game
	 */
	public GameBoard(boolean[][] cells, GameMode mode) {
		setMode(mode);
		rows = cells.length;
		columns = cells[0].length;
		// Set monster delay based on game difficulty
		monsterSleepTime = easyMonsterSleepTime;
		if (mode == GameMode.MEDIUM) {
			monsterSleepTime = mediumMonsterSleepTime;
		} else if (mode == GameMode.HARD) {
			monsterSleepTime = hardMonsterSleepTime;
		}
		ids = new ArrayList<String>();
		players = new ArrayList<Player>();
		// Set up the monster
		Position monsterPos = getCentralPosition();
		monster = new Monster(monsterPos, this);
		monster.setMode(mode);
		monster.setMonsterMoveSleepingTime(monsterSleepTime);
		// Set up all the arrays and the lock objects
		grid = new String[rows][columns];
		locks = new Object[rows][columns];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				locks[i][j] = new Object();
				if (cells[i][j] == false) {
					grid[i][j] = blockString;
				} else {
					grid[i][j] = spaceString;
				}
			}
		}
		grid[monsterPos.getRow()][monsterPos.getColumn()] = monsterString;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public Monster getMonster() {
		return monster;
	}

	/**
	 * Add player to board
	 * 
	 * @param player
	 *            the player to be added to the board
	 */
	public void addPlayer(Player player) {
		Position pos = player.getPosition();
		int rowNum = pos.getRow();
		int colNum = pos.getColumn();
		grid[rowNum][colNum] = playerString;
		players.add(player);
		// Attach itself as listener to the event fires when the life of the
		// player changes
		player.lifeCountChanged.addListener(this);
	}

	public void setMonster(Monster monster) {
		this.monster = monster;
	}

	public String[][] getGrid() {
		return grid;
	}

	public void setGrid(String[][] grid) {
		this.grid = grid;
	}

	/**
	 * Run the thread
	 */
	@Override
	public void run() {
		super.run();
		long sleepTime = monsterSleepTime;
		int counter = 0;
		while (players.size() > 1) {
			if (counter > 0 && counter % numMovesBetweenSpeedChange == 0) {
				sleepTime = (long) (ninetyPercent * sleepTime);
			}
			if (Constants.IS_DEBUG) {
				System.out.println(sleepTime);
			}
			// Move the monster and see if a player has been killed and sleep
			// between moves
			monster.move();
			checkForKill();
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			counter++;
		}
		try {
			// Finish game by finalizing and calculating everything
			finalizeGame();
		} catch (Exception e) {

		}

	}

	/**
	 * Finalize the game by deciding winner and preparing score board and firing
	 * game finished event
	 */
	private void finalizeGame() {
		ids.add(players.get(0).getId());
		// Fire game finished event
		gameFinished.get().gameFinished(players.get(0).getId());
	}

	/**
	 * Check if the monster killed a player
	 */
	public void checkForKill() {
		boolean killed = false;
		synchronized (players) {
			for (Player player : players) {
				// If player is dead, calculate player's score, kill the player
				// and remove the player from the board
				if (monster.getPosition().equals(player.getPosition())) {
					ids.add(player.getId());
					player.kill();
					removePlayerFromBoard(player);
					killed = true;
					break;
				}
			}
		}
		// Check if game has finished after the kill and make the monster wait
		// before moving again after killing
		if (killed) {
			if (players.size() == 1) {
				finalizeGame();
			}
			monster.digest();
		}
	}

	public long getMonsterSleepTime() {
		return monsterSleepTime;
	}

	public void setMonsterSleepTime(long monsterSleepTime) {
		this.monsterSleepTime = monsterSleepTime;
	}

	/**
	 * Remove dead or dropped out player from board
	 * 
	 * @param player
	 *            the player who is dead or has dropped out
	 */
	public void removePlayerFromBoard(Player player) {
		Position pos = player.getPosition();
		synchronized (locks[pos.getRow()][pos.getColumn()]) {
			grid[pos.getRow()][pos.getColumn()] = spaceString;
		}
		players.remove(player);
		// Fire event of the player's death
		playerDead.get().playerDead(player.getId(), player.getPosition());
		// Fire event of player's life count change
		player.lifeCountChanged.removeListener(this);
	}

	public boolean isPlayerAlive(String playerId) {
		for (Player player : players) {
			if (player.getId().equals(playerId)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Check to see if updating the position of the monster is possible for its
	 * current move and move the monster internally if it is possible
	 * 
	 * @param newPos
	 *            the position that the monster is trying to move to
	 * @return boolean value which indicates if the move is possible
	 */
	public boolean updateMonsterPosition(Position newPos) {
		// Synchronize the monster's move, the blocked cells are not checked for
		// here because the monster move algorithm takes them into account
		// already
		synchronized (locks[newPos.getRow()][newPos.getColumn()]) {
			Position monsterPos = getMonster().getPosition();
			synchronized (locks[monsterPos.getRow()][monsterPos.getColumn()]) {
				grid[monsterPos.getRow()][monsterPos.getColumn()] = spaceString;
				grid[newPos.getRow()][newPos.getColumn()] = monsterString;
			}
		}
		printGrid();
		return true;
	}

	/**
	 * Check to see if updating the position of the player is possible for its
	 * current move and move the player internally if the move is possible
	 * 
	 * @param oldPos
	 *            the old position of the player from where it's moving
	 * @param newPos
	 *            the new position where the player wants to move
	 * @param player
	 *            the player tying to move
	 * @return boolean value that suggests if the move is possible
	 */
	public boolean updatePlayerPosition(Position oldPos, Position newPos,
			Player player) {
		synchronized (locks[newPos.getRow()][newPos.getColumn()]) {
			if (Constants.IS_DEBUG) {
				System.out.println("In move");
				System.out.println("In monster pos " + monster.getPosition());
			}
			// If there is another player at the new position or the position is
			// outside of the board or the position is blocked, then move is not
			// possible
			if (isPlayerInPosition(newPos) || isBlocked(newPos)
					|| !isPositionValid(newPos)) {
				return false;
			}
			synchronized (locks[oldPos.getRow()][oldPos.getColumn()]) {
				if (Constants.IS_DEBUG) {
					System.out.println("In sync zone");
				}
				// Obtain bonus life while moving to new position if bonus life
				// is available at that position
				grid[oldPos.getRow()][oldPos.getColumn()] = spaceString;
				grid[newPos.getRow()][newPos.getColumn()] = playerString;
				return true;
			}
		}
	}

	public HashMap<String, Position> getNeighBoursOfPosition(Position pos) {
		HashMap<String, Position> neighbourPos = new HashMap<String, Position>();
		Position leftPosition = getPositionTowardsLeft(pos);
		if (leftPosition != null) {
			neighbourPos.put("LEFT", leftPosition);
		}
		Position rightPosition = getPositionTowardsRight(pos);
		if (rightPosition != null) {
			neighbourPos.put("RIGHT", rightPosition);
		}
		Position topPosition = getPositionTowardsTop(pos);
		if (topPosition != null) {
			neighbourPos.put("TOP", topPosition);
		}
		Position bottomPosition = getPositionTowardsBottom(pos);
		if (bottomPosition != null) {
			neighbourPos.put("BOTTOM", bottomPosition);
		}
		return neighbourPos;
	}

	public Position getPositionTowardsLeft(Position pos) {
		Position leftPosition = new Position(pos.getRow(), pos.getColumn() - 1);
		if (isPositionValid(leftPosition)) {
			return leftPosition;
		}
		return null;
	}

	/**
	 * Get the center of the board
	 * 
	 * @return Position object representing the center of the board
	 */
	public Position getCentralPosition() {
		return new Position(rows / 2, columns / 2);
	}

	public Position getPositionTowardsRight(Position pos) {
		Position rightPosition = new Position(pos.getRow(), pos.getColumn() + 1);
		if (isPositionValid(rightPosition)) {
			return rightPosition;
		}
		return null;
	}

	public Position getPositionTowardsTop(Position pos) {
		Position topPosition = new Position(pos.getRow() - 1, pos.getColumn());
		if (isPositionValid(topPosition)) {
			return topPosition;
		}
		return null;
	}

	public Position getPositionTowardsBottom(Position pos) {
		Position bottomPosition = new Position(pos.getRow() + 1,
				pos.getColumn());
		if (isPositionValid(bottomPosition)) {
			return bottomPosition;
		}
		return null;
	}

	public boolean isBlocked(Position pos) {
		return grid[pos.getRow()][pos.getColumn()]
				.equalsIgnoreCase(blockString);
	}

	/**
	 * Check if a cell is blocked or has a monster in it
	 * 
	 * @param pos
	 *            the position to be checked
	 * @return boolean value indicating if there is a monster or a blocked cell
	 *         at the position
	 */
	public boolean isReachable(Position pos) {
		return grid[pos.getRow()][pos.getColumn()]
				.equalsIgnoreCase(blockString) == false
				&& grid[pos.getRow()][pos.getColumn()]
						.equalsIgnoreCase(monsterString) == false;
	}

	public boolean isPlayerInPosition(Position pos) {
		return grid[pos.getRow()][pos.getColumn()]
				.equalsIgnoreCase(playerString) == true;
	}

	public boolean isMonsterInPosition(Position pos) {
		synchronized (locks[pos.getRow()][pos.getColumn()]) {
			return grid[pos.getRow()][pos.getColumn()]
					.equalsIgnoreCase(monsterString) == true;
		}
	}

	public void printGrid() {
		if (Constants.IS_DEBUG) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					System.out.print("|");
					System.out.print(grid[i][j]);
				}
				System.out.println("|");
			}
		}
	}

	/**
	 * Check if a position is within the boundaries of the board
	 * 
	 * @param pos
	 *            the position to be checked
	 * @return boolean value indicating if a position is within the boundaries
	 *         of the board
	 */
	public boolean isPositionValid(Position pos) {
		return ((pos.getRow() >= 0 && pos.getRow() < rows) && (pos.getColumn() >= 0 && pos
				.getColumn() < columns));
	}

	/**
	 * Get the current direction of movement based on the old position and the
	 * new position
	 * 
	 * @param oldPos
	 *            the old position
	 * @param newPos
	 *            the new position
	 * @return Directions enum suggesting what direction the entity id moving in
	 */
	public Direction getCurrentDirection(Position oldPos, Position newPos) {
		if (this.getPositionTowardsLeft(oldPos) != null
				&& this.getPositionTowardsLeft(oldPos).equals(newPos)) {
			return Direction.LEFT;
		} else if (this.getPositionTowardsRight(oldPos) != null
				&& this.getPositionTowardsRight(oldPos).equals(newPos)) {
			return Direction.RIGHT;
		} else if (this.getPositionTowardsTop(oldPos) != null
				&& this.getPositionTowardsTop(oldPos).equals(newPos)) {
			return Direction.UP;
		} else if (this.getPositionTowardsBottom(oldPos) != null
				&& this.getPositionTowardsBottom(oldPos).equals(newPos)) {
			return Direction.DOWN;
		}
		return null;
	}

	public GameMode getMode() {
		return mode;
	}

	public void setMode(GameMode mode) {
		this.mode = mode;
	}

	/**
	 * Handle the event when the life count of a player changes
	 * 
	 * @param player
	 *            the player whose life count has changed
	 * @param life
	 *            the new number of lives of the player
	 */
	@Override
	public void lifeCountChanged(Player player, int life) {
		lifeCountChanged.get().lifeCountChanged(player, life);
	}

}
