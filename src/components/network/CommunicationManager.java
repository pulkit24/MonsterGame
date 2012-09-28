/** @Pulkit
 * Manages the network communication for you, relieving you of the hassles of 
 * socket creation and exception handling
 * Usage:
 * 	If you are a server:
 * 		1. Use CommunicationManager(int myPort) constructor to initialize with your port no.
 * 		2. Use waitForConnections() to get a connection from the client
 * 		3. Once connection is established, you can use sendMessage, sendPacket, receiveMessage and receivePacket functions for communication
 * 	If you are a client:
 * 		1. Use CommunicationManager() or CommunicationManager(String theirIP, int theirPort) to initialize with the server's details
 * 		2. Use connect() to connect to the server (or use connect(String theirIP, int theirPort) if you haven't supplied the details above)
 * 		3. Once connection is established, you can use sendMessage, sendPacket, receiveMessage and receivePacket functions for communication
 */
package components.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import components.packets.Packet;

public class CommunicationManager{
	private int myPort; // your own port no.
	private String theirIP; // ip address of the client/server you are contacting
	private int theirPort; // port no. of the client/server you are contacting
	private Socket communicationSocket = null; // used to send/receive messages
	private ServerSocket connectionSocket = null; // used to establish connections

	public CommunicationManager(int myPort){
		/* Useful for servers */
		this.myPort = myPort;
	}

	public CommunicationManager(String theirIP, int theirPort){
		/* Useful for clients */
		this.theirIP = theirIP;
		this.theirPort = theirPort;
	}

	public CommunicationManager(){
		/* Empty constructor as a possibility for clients */
	}

	public CommunicationManager(int myPort, String theirIP, int theirPort){
		this.myPort = myPort;
		this.theirIP = theirIP;
		this.theirPort = theirPort;
	}

	public CommunicationManager(Socket communicationSocket){
		/* Pickup an existing socket */
		this.communicationSocket = communicationSocket;
	}

	public Boolean waitForConnection(){
		/* Useful for servers */
		try{
			/* Reuse existing socket if any */
			if(connectionSocket == null || !connectionSocket.isBound()) connectionSocket = new ServerSocket(myPort);
			communicationSocket = connectionSocket.accept();
			communicationSocket.setTcpNoDelay(true);
			return true;
		}catch(IOException e){
			// TODO Auto-generated catch block
			System.err.println("Network error: could not create a server socket on port" + myPort + ": " + e.toString());
		}
		return false;
	}

	public Boolean connect(){
		/* Useful for clients */
		return connect(theirIP, theirPort);
	}

	public Boolean connect(String theirIP, int theirPort){
		/* Useful for clients - usually to override the default server ip/port */
		try{
			communicationSocket = new Socket(theirIP, theirPort);
			communicationSocket.setTcpNoDelay(true);
			return true;
		}catch(UnknownHostException e){
			// TODO Auto-generated catch block
			System.err.println("Network error: could not identify " + theirIP + ": " + e.toString());
		}catch(IOException e){
			// TODO Auto-generated catch block
			System.err.println("Network error: could not create a client socket" + ": " + e.toString());
		}
		return false;
	}

	public Boolean sendMessage(String data){
		/* Send something to the other end */
		try{
			new BufferedWriter(new OutputStreamWriter(communicationSocket.getOutputStream())).write(data);
			return true;
		}catch(IOException e){
			// TODO Auto-generated catch block
			System.err.println("Network error: could not send data" + e.toString());
		}
		return false;
	}

	public Boolean sendPacket(Packet packet){
		/* Send something to the other end */
		try{
			new ObjectOutputStream(communicationSocket.getOutputStream()).writeObject(packet);
			return true;
		}catch(IOException e){
			// TODO Auto-generated catch block
			System.err.println("Network error: could not send data: " + e.toString());
		}
		return false;
	}

	public String receiveMessage(){
		/* Receive something from the other end */
		try{
			return new BufferedReader(new InputStreamReader(communicationSocket.getInputStream())).readLine();
		}catch(IOException e){
			// TODO Auto-generated catch block
			System.err.println("Network error: could not receive data: " + e.toString());
		}
		return "";
	}

	public Packet receivePacket(){
		/* Receive something from the other end */
		try{
			return (Packet)new ObjectInputStream(communicationSocket.getInputStream()).readObject();
		}catch(IOException e){
			// TODO Auto-generated catch block
			System.err.println("Network error: could not receive data: " + e.toString());
		}catch(ClassNotFoundException e){
			// TODO Auto-generated catch block
			System.err.println("Network error: unidentified class received: " + e.toString());
		}
		return null;
	}

	public void close(){
		try{
			communicationSocket.close();
		}catch(IOException e){
			// TODO Auto-generated catch block
			System.err.println("Network error: could not close client socket: " + e.toString());
		}
		try{
			connectionSocket.close();
		}catch(IOException e){
			// TODO Auto-generated catch block
			System.err.println("Network error: could not close server socket: " + e.toString());
		}
	}

	public Socket getCommunicationSocket(){
		/* Get the communication socket */
		return communicationSocket;
	}
}
