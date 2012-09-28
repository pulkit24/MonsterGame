package monsterRun.client.model.entities;

import monsterRun.client.model.GameBoard;
import monsterRun.client.model.events.ILifeCountChanged;
import monsterRun.common.model.Constants;
import monsterRun.common.model.ImageStore;
import monsterRun.common.model.MessageFactory;
import monsterRun.common.model.Position;
import monsterRun.common.model.enums.Direction;
import monsterRun.common.model.enums.MessageID;
import monsterRun.common.model.janimationframework.controllers.sprites.ImageSprite;
import monsterRun.common.model.jevents.JEvent;
import monsterRun.common.model.network.Communicator;
import monsterRun.common.model.network.NetworkMessage;
import monsterRun.common.model.network.events.IMessageReceivedListener;

public class PlayerEntity extends AbstractEntity implements
		IMessageReceivedListener {

	private int lives;
	private String name;

	private boolean gotReply;
	private Communicator communicator;

	public final JEvent<ILifeCountChanged> lifeCountChanged = JEvent
			.create(ILifeCountChanged.class);

	private String[] folders = new String[] { "yellow", "green", "pink", "blue" };

	public PlayerEntity(GameBoard gameBoard, Communicator communicator,
			String name, int num) {
		super(gameBoard);

		this.name = name;
		this.communicator = communicator;

		lives = Constants.MAX_LIVES;

		communicator.messageReceived.addListener(this);
		gotReply = true;

		// Loads the images from the file and constructs an animated sprite
		ImageSprite sp = new ImageSprite(40);

		int numFolders = folders.length;
		String folder = folders[num % numFolders];

		for (int i = 1; i <= 9; i++) {
			sp.addSprite(ImageStore.get()
					.getIcon(folder + "/image00" + i + ".png").getImage());
		}

		for (int i = 8; i >= 1; i--) {
			sp.addSprite(ImageStore.get()
					.getIcon(folder + "/image00" + i + ".png").getImage());
		}

		super.setSprite(sp);
		super.setPosition(new Position(0, 0));
	}

	public void setLives(int lives) {
		this.lives = lives;
		lifeCountChanged.get().lifeCountChanged(this, lives);
	}

	public int getLives() {
		return lives;
	}

	public String getName() {
		return name;
	}

	@Override
	public void move(Direction direction) {

		// If the reply form the server is not received yet for the last move
		// request, wait
		if (!gotReply) {
			return;
		}

		Position next = null;

		switch (direction) {
		case LEFT:
			next = new Position(position.getRow(), position.getColumn() - 1);
			break;
		case RIGHT:
			next = new Position(position.getRow(), position.getColumn() + 1);
			break;
		case UP:
			next = new Position(position.getRow() - 1, position.getColumn());
			break;
		case DOWN:
			next = new Position(position.getRow() + 1, position.getColumn());
			break;
		default:
			next = position;
			break;
		}

		if (gameBoard.isValidCell(next.getRow(), next.getColumn())) {
			gotReply = false;
			communicator.sendMessage(MessageFactory
					.createPlayerMoveMessage(direction));
		}
	}

	@Override
	public void messageReceived(String senderId, NetworkMessage mess) {
		NetworkMessage message = mess.clone();
		MessageID id = message.getId(MessageID.class);

		switch (id) {
		case CLIENT_MOVED:
			String clientId = message.readString();

			// Reset change flag when reply for last message is received
			if (communicator.getClientID().equalsIgnoreCase(clientId)) {
				gotReply = true;
			}
			break;
		default:
			break;
		}

	}
}
