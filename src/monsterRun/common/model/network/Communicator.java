package monsterRun.common.model.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.server.UID;

import monsterRun.common.model.jevents.JEvent;
import monsterRun.common.model.network.events.IConnectionStatusChangedListener;
import monsterRun.common.model.network.events.IMessageReceivedListener;

/**
 * Deals with sending and receiving messages to and from another client
 */
public class Communicator extends Thread {

	private int port;
	private String host;

	private String clientId;

	private Socket socket;
	private DataInputStream reader;
	private DataOutputStream writer;

	private Object lock;

	/**
	 * Gets fired when a new message is received
	 */
	public final JEvent<IMessageReceivedListener> messageReceived = JEvent
			.create(IMessageReceivedListener.class);

	/**
	 * Gets fired when the connection with the other client is lost
	 */
	public final JEvent<IConnectionStatusChangedListener> connectionStatusChanged = JEvent
			.create(IConnectionStatusChangedListener.class);

	private Communicator(boolean isClient) {
		clientId = isClient ? new UID().toString() : "server";
	}

	public Communicator(String host, int port, boolean isClient) {
		this(isClient);

		this.host = host;
		this.port = port;
	}

	public Communicator(Socket socket, boolean isClient) throws IOException {
		this(isClient);

		connect(socket);
	}

	public void connect() throws UnknownHostException, IOException {
		if (isAlive() || host == null) {
			return;
		}

		connect(new Socket(host, port));
	}

	private void connect(Socket socket) throws IOException {
		this.socket = socket;

		writer = new DataOutputStream(socket.getOutputStream());
		reader = new DataInputStream(socket.getInputStream());

		lock = new Object();

		// Turns off TCP delay to prevent packets
		// from queuing up on the client
		socket.setTcpNoDelay(true);

		start();
	}

	public String getClientID() {
		return clientId;
	}

	@Override
	public void run() {
		super.run();

		try {
			// Reads bytes form the reader until the thread is interrupted.
			while (!isInterrupted()) {

				String senderId = reader.readUTF();
				int messageBytesLength = reader.readInt();
				byte[] messageBytes = new byte[messageBytesLength];
				reader.readFully(messageBytes);

				// Converts the byte array to a Network Message
				NetworkMessage message = new NetworkMessage(messageBytes);

				messageReceived.get().messageReceived(senderId, message);
			}
		} catch (Exception e) {
			reader = null;
			writer = null;
			connectionStatusChanged.get().connectionLost(clientId);

			System.err.println(e.getLocalizedMessage());
		}
	}

	/**
	 * The synchronized method that converts a {@link NetworkMessage} to byte
	 * array and sends it through the writer
	 * 
	 * @param message
	 *            The {@link NetworkMessage} to send
	 */
	public void sendMessage(NetworkMessage message) {
		if (writer == null) {
			return;
		}

		synchronized (this.lock) {
			try {
				byte[] messageBytes = message.getDataBytes();

				writer.writeUTF(clientId);// The client id
				writer.writeInt(messageBytes.length);// Message byte length
				writer.write(messageBytes);// Message bytes

				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {

		if (!isAlive()) {
			return;
		}

		interrupt();

		try {
			writer.flush();
			writer.close();
			reader.close();
			socket.close();
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
		}
	}

}
