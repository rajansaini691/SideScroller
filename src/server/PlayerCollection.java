package server;

import java.awt.Graphics2D;
import java.util.Hashtable;
import java.util.Iterator;

import game.Leaderboard;
import game.Player;

/**
 * Houses all Player objects and interfaces with sockets
 * @author rajans1900
 *
 */
public class PlayerCollection {
	/**
	 * Stores all players in a hashable form, so that players can be looked up
	 */
	private Hashtable<Byte, Player> players;
	
	/**
	 * Stores leaderboard object so it can be filled upon reset
	 */
	private Leaderboard leaderboard;
	
	/**
	 * Bridges between main game state and sockets
	 */
	private NetworkBridge bridge;
	
	public PlayerCollection(NetworkBridge bridge) {
		this.players = new Hashtable<Byte, Player>();
		this.bridge = bridge;
	}
	
	/**
	 * Looks up player based on ID and calls that player's execute message.
	 * If player does not exist throw an exception
	 * @param message
	 */
	public synchronized void process(InputMessage message) {
		byte command = message.getMessage();
		byte messageID = message.getID();
		
		
		if(messageID == InputMessage.PLAYER_ALL) {
			//Transmit command to all players
			for(Iterator<Byte> itr = players.keySet().iterator(); itr.hasNext(); ) {
				byte i = itr.next();
				players.get(i).execute(command);
				
			}
			
		} else if(messageID > players.size() + 1) {
			System.out.println("Player with ID " + messageID + " does not exist");
			
		} else if(command == InputMessage.READY_FOR_RESTART) {
			leaderboard.ready(messageID);
			
		} else {
			/*
			 * The conditional exists so that if the ID is over the maximum capacity, 
			 * we can loop back to player 1. This way the client can send a message
			 * to the next player without having knowledge of them
			 */
			players.get((messageID > players.size())? 1 : messageID).execute(command);
			
		}
	}
	
	/**
	 * Sends a message to client
	 * @param message
	 */
	public void sendMessage(OutputMessage message) {
		bridge.messageOut(message);
	}
	
	/**
	 * Adds a player to the collection and maps it to given ID
	 * @param ID
	 */
	public synchronized void addPlayer(byte ID) {
		players.put(ID, new Player(ID, this));
	}
	
	/**
	 * Removes player with given ID
	 * @param ID
	 */
	public synchronized void removePlayer(byte ID) {
		players.get(ID).disconnect();
		players.remove(ID);
		System.out.println("Player " + ID + " disconnected");
	}
	
	/**
	 * Returns the key of the specified player
	 * @param p
	 * @return
	 */
	public synchronized Byte getKeyFromPlayer(Player p) {
		for(Byte i : players.keySet()) {
			if(players.get(i).equals(p)) {
				return i;
			}
		}
		return null;
	}
	
	/**
	 * Resets each player's state, but does not start the game
	 */
	public synchronized void reset() {
		leaderboard = new Leaderboard(players.size());
		
		for(Byte i : players.keySet()) {
			Player previousPlayer = players.get(i);
			players.put(i, new Player(previousPlayer.getID(), this));
			players.get(i).setName(previousPlayer.getName());
			
			leaderboard.addPlayer(previousPlayer.getID(), previousPlayer.getName(), previousPlayer.getScore());
		}
		
		leaderboard.sortPlayers();
	}
	
	/**
	 * Returns the player when given a key
	 * @param key
	 * @return
	 */
	public Player getPlayerFromKey(Byte key) {
		return players.get(key);
	}

	/**
	 * Sets name of player with given ID
	 * @param ID
	 * @param name
	 */
	public void setName(byte ID, String name) {
		players.get(ID).setName(name);
	}
	
	/**
	 * Used for rendering; Iterator ensures it is thread-safe
	 * @param win
	 */
	public synchronized void draw(Graphics2D win) {		
		for(Iterator<Byte> itr = players.keySet().iterator(); itr.hasNext(); ) {
			byte i = itr.next();
			players.get(i).draw(win);
		}
	}
	
	/**
	 * Sends a message to all clients
	 * @param message Message to be broadcast
	 */
	public void broadcastMessage(byte message) {
		for(Byte i : players.keySet()) {
			bridge.messageOut(new OutputMessage(i, message));
		}
	}
	
	public Hashtable<Byte, Player> getPlayers() {
		return players;
	}
	
	/**
	 * Gives other classes access to the leaderboard object so that it can be drawn
	 * @return
	 */
	public Leaderboard getLeaderboard() {
		return leaderboard;
	}
	
}
