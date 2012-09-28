package monsterRun.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import monsterRun.client.model.entities.AbstractEntity;
import monsterRun.client.model.entities.MonsterEntity;
import monsterRun.client.model.entities.PlayerEntity;
import monsterRun.client.model.events.IGameStateChanged;
import monsterRun.common.model.GameGridPattern;
import monsterRun.common.model.Position;
import monsterRun.common.model.enums.Direction;
import monsterRun.common.model.enums.GameBoardType;
import monsterRun.common.model.enums.MessageID;
import monsterRun.common.model.jevents.JEvent;
import monsterRun.common.model.network.Communicator;
import monsterRun.common.model.network.NetworkMessage;
import monsterRun.common.model.network.events.IMessageReceivedListener;

public class GameBoard implements IMessageReceivedListener {

	private int rows;
	private int columns;
	private boolean[][] cells;// The grid pattern

	private PlayerEntity player;// The current player

	private MonsterEntity monster;// The monster entity

	private HashMap<String, PlayerEntity> allEntities;

	private Facade facade;
	private Communicator comm;

	public final JEvent<IGameStateChanged> gameStateChanged = JEvent
			.create(IGameStateChanged.class);

	public GameBoard(GameBoardType gameBoard, Communicator communicator,
			Facade facade) {
		this.facade = facade;
		this.comm = communicator;
		communicator.messageReceived.addListener(this);

		reInitialize(gameBoard);
	}

	public void reInitialize(GameBoardType board) {
		cells = GameGridPattern.parse(board);

		rows = cells.length;
		columns = cells[0].length;

		player = null;
		allEntities = new HashMap<String, PlayerEntity>();
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public boolean isValidCell(int row, int column) {
		if (row >= 0 && row < rows && column >= 0 && column < columns) {
			return cells[row][column];
		}

		return false;
	}

	/**
	 * Returns a list of all the entities. This include the monster and the
	 * players
	 * 
	 * @return
	 */
	public List<AbstractEntity> getAllEntities() {
		List<AbstractEntity> entities = new ArrayList<AbstractEntity>();
		entities.addAll(allEntities.values());
		entities.add(monster);

		return entities;
	}

	public PlayerEntity getCurrentPlayer() {
		return player;
	}

	/**
	 * Listens to the messages from the server
	 */
	@Override
	public void messageReceived(String senderId, NetworkMessage mess) {
		NetworkMessage message = mess.clone();
		MessageID id = message.getId(MessageID.class);

		switch (id) {

		case GAME_STARTED:
			gameStateChanged.get().gameStarted();
			break;

		case PLAYER_NUM_LIVES_LEFT:
			String clientId = message.readString();
			int lives = message.readInt();

			PlayerEntity lp = null;

			if (comm.getClientID().equalsIgnoreCase(clientId)) {
				if (player != null) {
					lp = player;
				}
			} else {
				if (allEntities.containsKey(clientId)) {
					lp = allEntities.get(clientId);
				}
			}

			if (lp != null) {
				lp.setLives(lives);
			}

			break;

		case CLIENT_CONNNECTED:

			// Reads the details of the connected client
			clientId = message.readString();
			Position position = message.readObject();
			Direction direction = message.readEnum(Direction.class);
			int num = message.readInt();

			// Checks to see if the connected client is self. If yes initialize
			// the current player entity
			if (comm.getClientID().equalsIgnoreCase(clientId)) {
				if (player == null) {
					player = new PlayerEntity(this, comm, clientId, num);
					gameStateChanged.get().currentPlayerAdded(player);
				}

				player.setPosition(position);
				player.setDirection(direction);
			}
			// If another person connects, add it to a separate list
			else {
				if (!allEntities.containsKey(clientId)) {
					PlayerEntity p = new PlayerEntity(this, comm, clientId, num);
					allEntities.put(clientId, p);
					gameStateChanged.get().playerAdded(p);
				}

				allEntities.get(clientId).setPosition(position);
				allEntities.get(clientId).setDirection(direction);
			}

			break;

		case CLIENT_MOVED:
			clientId = message.readString();

			int r = message.readInt();
			int c = message.readInt();
			position = new Position(r, c);

			direction = message.readEnum(Direction.class);

			if (comm.getClientID().equalsIgnoreCase(clientId)) {
				if (player != null) {
					player.setPosition(position);
					player.setDirection(direction);
				}
			} else {
				if (allEntities.containsKey(clientId)) {
					AbstractEntity entity = allEntities.get(clientId);
					entity.setPosition(position);
					entity.setDirection(direction);
				}
			}

			break;

		case MONSTER_MOVED:
			r = message.readInt();
			c = message.readInt();
			position = new Position(r, c);

			if (monster == null) {
				monster = new MonsterEntity(this);
			}

			Position lastPos = monster.getPosition();

			// Calculates the direction of the monster for rendering help
			if (position.getColumn() > lastPos.getColumn()) {
				monster.setDirection(Direction.RIGHT);
			} else if (position.getColumn() < lastPos.getColumn()) {
				monster.setDirection(Direction.LEFT);
			}

			monster.setPosition(position);

			break;

		case PLAYER_DEAD:
			clientId = message.readString();

			if (comm.getClientID().equalsIgnoreCase(clientId)) {
				facade.enableKeyListener(false);// Disable input when game ends
				player = null;
				gameStateChanged.get().currentPlayerDied();
			} else if (allEntities.containsKey(clientId)) {
				gameStateChanged.get().playerRemoved(allEntities.get(clientId));
				allEntities.remove(clientId);
			}

			break;

		default:
			break;
		}
	}
}
