package server;

public class NetworkBridge {
	
	private SocketCollection sockets;
	private PlayerCollection players;
	
	public NetworkBridge() {
		
	}
	
	public void init(SocketCollection sockets, PlayerCollection players) {
		this.sockets = sockets;
		this.players = players;
	}
	
	/**
	 * Sends a message to the client
	 * @param player
	 * @param message
	 */
	public void messageOut(OutputMessage message) {
		sockets.transmitToPlayer(message);
	}
	
	/**
	 * Processes a message from the client
	 */
	public void messageIn(InputMessage message) {
		players.process(message);
	}
	
	/**
	 * Passes name to players
	 * @param ID
	 * @param name
	 */
	public void setName(byte ID, String name) {
		players.setName(ID, name);
	}
	
	public void addPlayer(byte key) {
		players.addPlayer(key);
	}
	
	public void removePlayer(byte key) {
		players.removePlayer(key);
	}
	
}
