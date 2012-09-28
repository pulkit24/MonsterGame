package monsterRun.common.model;

import java.util.ArrayList;

import monsterRun.common.model.enums.Direction;
import monsterRun.common.model.enums.GameBoardType;
import monsterRun.common.model.enums.GameMode;
import monsterRun.common.model.enums.MessageID;
import monsterRun.common.model.network.NetworkMessage;

/**
 * Used by both the client and the server to manage the send/received
 * {@link NetworkMessage}
 * 
 */
public class MessageFactory {

	// ////////////////////////////// CLIENT //////////////////////////////////

	public static NetworkMessage createPlayerMoveMessage(Direction direction) {
		NetworkMessage m = new NetworkMessage(MessageID.CLIENT_MOVE_REQUEST);
		m.writeInt(direction.ordinal());

		return m;
	}
	

	public static NetworkMessage createGameDetails(int number,
			GameBoardType board, GameMode mode) {

		NetworkMessage m = new NetworkMessage(MessageID.GAME_DETAILS);
		m.writeInt(number);
		m.writeInt(board.ordinal());
		m.writeInt(mode.ordinal());

		return m;
	}

	public static NetworkMessage createDetailsResponse() {
		NetworkMessage m = new NetworkMessage(MessageID.CLIENT_DETAILS);

		return m;
	}

	public static NetworkMessage createJoinGameRequest() {
		NetworkMessage m = new NetworkMessage(MessageID.GAME_JOIN_REQUEST);

		return m;
	}

	public static NetworkMessage createStartPosResponse(int index) {
		NetworkMessage m = new NetworkMessage(MessageID.START_POS_RESPONSE);
		m.writeInt(index);

		return m;
	}

	// ////////////////////////////// SERVER //////////////////////////////////

	public static NetworkMessage createServerFull() {
		NetworkMessage m = new NetworkMessage(MessageID.SERVER_FULL);
		m.writeString("Game has already started and the server is full. Try again later");

		return m;
	}

	public static NetworkMessage createInvalidInput(String message) {
		NetworkMessage m = new NetworkMessage(MessageID.INVALID_INPUT);
		m.writeString(message);

		return m;
	}

	public static NetworkMessage createGameDetailsRequest() {
		NetworkMessage m = new NetworkMessage(MessageID.SPECIFY_GAME_DETAILS);

		return m;
	}

	public static NetworkMessage createNumPlayerSuccess() {
		NetworkMessage m = new NetworkMessage(MessageID.SPECIFY_PLAYERS_DONE);

		return m;
	}

	public static NetworkMessage createBoardSuccess() {
		NetworkMessage m = new NetworkMessage(MessageID.SPECIFY_BOARD_DONE);

		return m;
	}

	public static NetworkMessage createPlayerConnect(String id, 
			Position position, Direction direction, int num) {

		NetworkMessage m = new NetworkMessage(MessageID.CLIENT_CONNNECTED);
		m.writeString(id);
		m.writeObject(position);
		m.writeInt(direction.ordinal());
		m.writeInt(num);

		return m;
	}

	public static NetworkMessage createPlayerMove(String id, Position position,
			int direction) {

		NetworkMessage m = new NetworkMessage(MessageID.CLIENT_MOVED);
		m.writeString(id);
		m.writeInt(position.getRow());
		m.writeInt(position.getColumn());
		m.writeInt(direction);

		return m;
	}

	public static NetworkMessage createMonsterMove(Position position) {
		NetworkMessage m = new NetworkMessage(MessageID.MONSTER_MOVED);
		m.writeInt(position.getRow());
		m.writeInt(position.getColumn());

		return m;
	}

	public static NetworkMessage createGameStarted(GameBoardType boardStyle) {
		NetworkMessage m = new NetworkMessage(MessageID.GAME_STARTED);
		m.writeInt(boardStyle.ordinal());

		return m;
	}

	public static NetworkMessage createDetailsRequest() {
		NetworkMessage m = new NetworkMessage(MessageID.CLIENT_DETAILS);

		return m;
	}

	public static NetworkMessage createPlayerDeathMessage(String id) {
		NetworkMessage m = new NetworkMessage(MessageID.PLAYER_DEAD);
		m.writeString(id);

		return m;
	}

	public static NetworkMessage createNumLivesLeft(String playerId,
			int numLives) {

		NetworkMessage m = new NetworkMessage(MessageID.PLAYER_NUM_LIVES_LEFT);
		m.writeString(playerId);
		m.writeInt(numLives);

		return m;
	}

	public static NetworkMessage createGameFinished(String winner) {
		NetworkMessage m = new NetworkMessage(MessageID.GAME_FINISHED);
		m.writeString(winner);

		return m;
	}

	public static NetworkMessage createStartPosRequest(
			ArrayList<Integer> availablePos) {

		NetworkMessage m = new NetworkMessage(MessageID.START_POS_REQUEST);
		m.writeObject(availablePos);
		return m;
	}
}