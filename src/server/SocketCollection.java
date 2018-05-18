package server;

import java.net.Socket;
import java.util.Hashtable;

/**
 * Composed of socket threads; works between them and the NetworkBridge
 * @author rajans1900
 *
 */
public class SocketCollection {
	
	private Hashtable<Byte, SocketThread> threads;
	private NetworkBridge bridge;
	
	public SocketCollection(NetworkBridge bridge) {
		threads = new Hashtable<Byte, SocketThread>();
		this.bridge = bridge;
	}
	
	public void addSocket(byte ID, Socket socket) {
		bridge.addPlayer(ID);
		SocketThread thread = new SocketThread(socket, ID, this);
		threads.put(ID, thread);
		thread.start();
	}
	
	/*
	 * Removes a player with given key
	 */
	public void removePlayer(byte key) {
		bridge.removePlayer(key);
		if(threads.remove(key) == null) {
			System.out.println("Unsuccessfully removed socket");
			throw new NullPointerException();
		}
	}
	
	/*
	 * Passes outgoing message from bridge to socket
	 */
	public synchronized void transmitToPlayer(OutputMessage message) {
		SocketThread thread = threads.get(message.getID());
		thread.transmit(message.getMessage());
	}
	
	/**
	 * Passes incoming message from socket to bridge
	 * @param message
	 */
	synchronized void accept(InputMessage message) {
		bridge.messageIn(message);
	}
	
	/**
	 * Passes name to bridge
	 * @param ID
	 * @param name
	 */
	synchronized void setName(byte ID, String name) {
		bridge.setName(ID, name);
	}
	
	/**
	 * Sends a message to every client
	 */
	public void broadcast(byte message) {
		for(Byte ID : threads.keySet()) {
			threads.get(ID).transmit(message);
		}
	}
	
}
