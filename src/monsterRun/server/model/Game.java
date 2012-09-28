package monsterRun.server.model;

import java.util.ArrayList;

import monsterRun.common.model.GameGridPattern;
import monsterRun.common.model.Position;
import monsterRun.common.model.enums.Direction;
import monsterRun.common.model.enums.GameBoardType;
import monsterRun.common.model.enums.GameMode;
import monsterRun.common.model.jevents.JEvent;
import monsterRun.server.model.entities.Player;
import monsterRun.server.model.events.IGameFinishedListener;
import monsterRun.server.model.events.ILifeCountChangedListener;
import monsterRun.server.model.events.IMonsterMoveListener;
import monsterRun.server.model.events.IPlayerDeadListener;

/**
 * 
 * Class that represents the game being played
 * 
 */
public class Game implements IMonsterMoveListener, IPlayerDeadListener,
		IGameFinishedListener, ILifeCountChangedListener {
	/** Instance variables for customized game */
	private GameMode mode;
	private GameBoard board;
	private GameBoardType boardStyle;
	/** Other instance variables */
	private boolean started;
	private boolean finished;
	private int monsterCount;
	private int playersCount;
	private int requestedNumPlayers;
	private ArrayList<Integer> availablePositions;
	private ArrayList<Position> playerStartPositions;
	private ArrayList<Direction> playerStartDirections;
	/**
	 * Events that are fired when the states of certain objects change
	 */
	public final JEvent<ILifeCountChangedListener> lifeCountChanged = JEvent
			.create(ILifeCountChangedListener.class);

	public final JEvent<IMonsterMoveListener> monsterMoved = JEvent
			.create(IMonsterMoveListener.class);

	public final JEvent<IPlayerDeadListener> playerDead = JEvent
			.create(IPlayerDeadListener.class);

	public final JEvent<IGameFinishedListener> gameFinished = JEvent
			.create(IGameFinishedListener.class);

	/**
	 * Constructor
	 */
	public Game() {
		started = false;
		finished = false;

		monsterCount = 1;
		playersCount = 0;
		requestedNumPlayers = 0;

		availablePositions = new ArrayList<Integer>();
		playerStartPositions = new ArrayList<Position>();
		playerStartDirections = new ArrayList<Direction>();
	}

	public void setGameMode(GameMode mode) {
		this.mode = mode;
	}

	public GameMode getGameMode() {
		return mode;
	}

	/**
	 * Set the board style based on client's selection
	 * 
	 * @param boardStyle
	 *            the enum that represents the type of board the client has
	 *            selected
	 */
	public void setBoardStyle(GameBoardType boardStyle) {
		this.boardStyle = boardStyle;
		boolean[][] grid = GameGridPattern.parse(boardStyle);
		int lastRow = grid.length - 1;
		int lastColumn = grid[0].length - 1;
		board = new GameBoard(grid, mode);
		// Add positions that are available for players
		for (int i = 0; i < 4; i++) {
			availablePositions.add(i);
		}
		// Adding the player start positions and directions
		playerStartPositions.add(new Position(0, 0));
		playerStartDirections.add(Direction.RIGHT);
		playerStartPositions.add(new Position(0, lastColumn));
		playerStartDirections.add(Direction.LEFT);
		playerStartPositions.add(new Position(lastRow, 0));
		playerStartDirections.add(Direction.RIGHT);
		playerStartPositions.add(new Position(lastRow, lastColumn));
		playerStartDirections.add(Direction.LEFT);
		// Attach itself as listener to events
		board.playerDead.addListener(this);
		board.gameFinished.addListener(this);
		board.lifeCountChanged.addListener(this);
		board.getMonster().monsterMoved.addListener(this);
	}

	public GameBoardType getBoardStyle() {
		return boardStyle;
	}

	public boolean hasFinished() {
		return finished;
	}

	public GameBoard getBoard() {
		return board;
	}

	public int getRequestedNumPlayers() {
		return requestedNumPlayers;
	}

	public void setNumberOfPlayers(int requestedNumPlayers) {
		this.requestedNumPlayers = requestedNumPlayers;
	}

	public boolean hasStarted() {
		return started;
	}

	public int getPlayersCount() {
		return playersCount;
	}

	public void setPlayersCount(int playersCount) {
		this.playersCount = playersCount;
	}

	public int getMonsterCount() {
		return monsterCount;
	}

	public void finishGame() {
		finished = true;
		started = false;
	}

	public void setMonsterCount(int monsterCount) {
		this.monsterCount = monsterCount;
	}

	/**
	 * Add all players to the game
	 * 
	 * @param players
	 *            the array list of all players to be added to the game
	 */
	public void addPlayers(ArrayList<Player> players) {
		for (Player player : players) {
			board.addPlayer(player);
		}
	}

	/**
	 * Remove position from list of positions available
	 * 
	 * @param posIndex
	 *            the index if the position that is not available for players
	 */
	private void removeFromAvailablePos(int posIndex) {
		for (int i = 0; i < availablePositions.size(); i++) {
			if (availablePositions.get(i) == posIndex) {
				availablePositions.remove(i);
				break;
			}
		}
	}

	public ArrayList<Integer> getAvailablePositions() {
		synchronized (availablePositions) {
			return availablePositions;
		}
	}

	/**
	 * Get start position of player
	 * 
	 * @param index
	 *            the index of the start position of the player
	 * @return Position which is the start position of the player
	 */
	public Position getPlayerStartPosition(int index) {
		synchronized (availablePositions) {
			if (availablePositions.contains(index)
					&& index < playerStartPositions.size()) {
				removeFromAvailablePos(index);
				return playerStartPositions.get(index);
			}

			return null;
		}
	}

	/**
	 * Get player start direction, it is used for animating on the client side
	 * 
	 * @param index
	 *            the index of the start direction of the player
	 * @return Directions which is an enum representing the direction of the
	 *         player
	 */
	public Direction getPlayerStartDirection(int index) {
		synchronized (availablePositions) {
			if (index < playerStartDirections.size()) {
				return playerStartDirections.get(index);
			}

			return null;
		}
	}

	/**
	 * Start the game
	 */
	public void start() {
		started = true;
		finished = false;
		board.start();
	}

	/**
	 * Deal with monster movement, fires an event when the monster movement from
	 * the board is detected
	 * 
	 * @param pos
	 *            the new position of the monster
	 */
	@Override
	public void monsterMoved(Position pos) {
		monsterMoved.get().monsterMoved(pos);
	}

	/**
	 * Deal with player dead event, fires an event when a player dead event from
	 * the board is detected
	 * 
	 * @param id
	 *            the unique id of the player who died
	 * @param pos
	 *            the last position of the player
	 */
	@Override
	public void playerDead(String id, Position pos) {
		playerDead.get().playerDead(id, pos);
	}

	/**
	 * Deal with game finished event, fires an event when the game finished
	 * event from the board is detected
	 * 
	 * @param winner
	 *            the winner of the game
	 * @param names
	 *            the list of names of players in the game
	 * @param scores
	 *            the list of scores of players in the game
	 */
	@Override
	public void gameFinished(String winner) {
		finished = true;
		gameFinished.get().gameFinished(winner);
	}

	/**
	 * Deal with life count change for player, fires an event when the life
	 * count of a player changes on the board and that event is detected
	 * 
	 * @param player
	 *            the player whose life count has changed
	 * @param life
	 *            is the new number of lives for the player
	 * 
	 */
	@Override
	public void lifeCountChanged(Player player, int life) {
		if (player != null) {
			lifeCountChanged.get().lifeCountChanged(player, life);
		}
	}

}
