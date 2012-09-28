package monsterRun.common.model.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class NetworkMessage {

	private int messageId;
	private int readerIndex;
	private ArrayList<Byte> dataBytes;

	public NetworkMessage(Enum<?> e) {
		this(e.ordinal());
	}

	/**
	 * Initializes a new network message that can be send over a network
	 * 
	 * @param messageId
	 *            The id of the message.
	 */
	public NetworkMessage(int messageId) {
		this.messageId = messageId;
		this.dataBytes = new ArrayList<Byte>();

		// Adds the id to the front of the message
		writeInt(messageId);

		resetReaderIndex();
	}

	/**
	 * Reinitializes a {@link NetworkMessage} from a byte array
	 * 
	 * @param bytes
	 */
	public NetworkMessage(byte[] bytes) {

		byte[] idBytes = new byte[4];
		for (int i = 0; i < 4; i++) {
			idBytes[i] = bytes[i];
		}

		this.messageId = this.byteArrayToInt(idBytes);

		this.dataBytes = new ArrayList<Byte>();
		for (byte b : bytes) {
			dataBytes.add(b);
		}

		resetReaderIndex();
	}

	public NetworkMessage(NetworkMessage message) {
		this(message.getDataBytes());
	}

	/**
	 * Clones a {@link NetworkMessage} bye getting the data bytes array from the
	 * passed in {@link NetworkMessage}
	 */
	@Override
	public NetworkMessage clone() {
		return new NetworkMessage(getDataBytes());
	}

	public int getId() {
		return messageId;
	}

	public <E extends Enum<?>> E getId(Class<E> cls) {
		int idInt = getId();
		E[] enums = cls.getEnumConstants();

		if (idInt < enums.length) {
			return enums[idInt];
		}

		return null;
	}

	public void resetReaderIndex() {
		readerIndex = 4;
	}

	public byte[] getDataBytes() {
		byte[] bytes = new byte[dataBytes.size()];

		for (int i = 0; i < dataBytes.size(); i++) {
			bytes[i] = dataBytes.get(i);
		}

		return bytes;
	}

	// //////////////////// WRITE START //////////////////////

	public NetworkMessage writeEnum(Enum<?> e) {
		writeInt(e.ordinal());

		return this;
	}

	public NetworkMessage writeString(String s) {
		byte[] bytes = s.getBytes();
		writeBytes(bytes);

		return this;
	}

	public NetworkMessage writeBytes(byte[] bytes) {
		writeInt(bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			dataBytes.add(bytes[i]);
		}

		return this;
	}

	public NetworkMessage writeBoolean(boolean v) {
		int bool = v ? 1 : 0;
		writeInt(bool);

		return this;
	}

	public NetworkMessage writeByte(byte b) {
		dataBytes.add(b);

		return this;
	}

	public NetworkMessage writeChar(char c) {
		dataBytes.add((byte) c);

		return this;
	}

	/**
	 * Serializes and adds the serialized byte array to the message
	 * 
	 * @param o
	 * @return
	 */
	public NetworkMessage writeObject(Serializable o) {

		byte[] yourBytes = null;

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);

			out.writeObject(o);
			yourBytes = bos.toByteArray();

			out.close();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (yourBytes != null) {
			writeBytes(yourBytes);
		}

		return this;
	}

	public NetworkMessage writeInt(int in) {

		byte[] bytes = this.intToByteArray(in);

		for (int i = 0; i < 4; i++) {
			dataBytes.add(bytes[i]);
		}

		return this;
	}

	public NetworkMessage writeLong(long l) {
		writeString(Long.toString(l));

		return this;
	}

	public NetworkMessage writeFloat(float f) {
		writeString(Float.toString(f));

		return this;
	}

	public NetworkMessage writeDouble(double d) {
		writeString(Double.toString(d));

		return this;
	}

	public NetworkMessage writeNetworkMessage(NetworkMessage m) {
		writeBytes(m.getDataBytes());

		return this;
	}

	// ///////////////////// WRITE END ///////////////////////

	// //////////////////// READ START //////////////////////

	public <E extends Enum<?>> E readEnum(Class<E> cls) {
		int idInt = readInt();
		E[] enums = cls.getEnumConstants();

		if (idInt < enums.length) {
			return enums[idInt];
		}

		return null;
	}

	public NetworkMessage readNetworkMessage() {
		return new NetworkMessage(readBytes());
	}

	public String readString() {
		return new String(readBytes());
	}

	public boolean readBoolean() {
		return readInt() == 1 ? true : false;
	}

	public byte readByte() {
		return dataBytes.get(readerIndex++);
	}

	public char readChar() {
		return (char) readByte();
	}

	/**
	 * Reads the next chunk of bytes and de-serializes it to the actual object
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T readObject() {

		byte[] yourBytes = readBytes();
		Object o = null;

		if (yourBytes != null) {
			try {
				ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
				ObjectInput in = new ObjectInputStream(bis);

				o = in.readObject();

				bis.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (o != null) {
			return (T) o;
		}

		return null;
	}

	public int readInt() {
		int length = 4;

		byte[] bytes = new byte[length];

		int index = 0;
		for (int i = readerIndex; i < readerIndex + length; i++) {
			bytes[index++] = dataBytes.get(i);
		}

		int integer = this.byteArrayToInt(bytes);

		readerIndex += length;

		return integer;
	}

	public long readLong() {
		return Long.parseLong(readString());
	}

	public float readFloat() {
		return Float.parseFloat(readString());
	}

	public double readDouble() {
		return Double.parseDouble(readString());
	}

	public byte[] readBytes() {

		int length = readInt();

		byte[] bytes = new byte[length];

		int index = 0;
		for (int i = readerIndex; i < readerIndex + length; i++) {
			bytes[index++] = dataBytes.get(i);
		}

		readerIndex += length;

		return bytes;
	}

	// ///////////////////// READ END ///////////////////////

	private byte[] intToByteArray(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}

	private int byteArrayToInt(byte[] b) {
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}

	public void clear() {
		dataBytes.clear();
	}
}