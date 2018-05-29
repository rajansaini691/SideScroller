package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The leaderboard holds the scores and names of the client. It then ranks them and
 * displays them to the screen. 
 * @author Rajan Saini
 */
public class Leaderboard {

	/**
	 * Ranked list of players
	 */
	private ArrayList<LeaderboardPlayer> leaderboard = new ArrayList<LeaderboardPlayer>();
	
	/**
	 * Maps between ID and player
	 */
	private HashMap<Byte, LeaderboardPlayer> playerMap = new HashMap<Byte, LeaderboardPlayer>();
	
	/**
	 * Stores the number of players
	 */
	private int numPlayers;
	
	/**
	 * Creates a new Leaderboard object, determined by the number of players
	 * @param numPlayers
	 */
	public Leaderboard(int numPlayers) {
		this.numPlayers = numPlayers;
	}
	
	/**
	 * Draws the leaderboard to the screen
	 * @param win
	 */
	public void draw(Graphics2D win) {
		// Draws "Here are the rankings: " as a heading
		win.setColor(Color.BLACK);
		win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		win.setFont(new Font("Century Gothic", Font.BOLD, 60));
		win.drawString("Here are the rankings: ", 50, 100);
		
		
		// Iterates through the list of players and draws them
		win.setFont(new Font("Century Gothic", Font.ITALIC, 40));
		
		for(int i = 0; i < leaderboard.size(); i++) {
			LeaderboardPlayer player = leaderboard.get(i);
			
			// Grays out ready players to prepare for next round
			if(player.ready()) {
				win.setColor(Color.GRAY);
			} else {
				win.setColor(Color.BLACK);
			}
			
			try {
				win.drawString(player.getName(), 100, 80 * i + 200);
			} catch (Exception e) {
				System.out.println("Null name: " + player.getName() + " at i = " + i);
				System.exit(0);
			}
			win.drawString("" + player.getScore(), 600, 80 * i + 200);
		}
		
	}
	
	/**
	 * Adds a new player to the leaderboard with given name, score, and ID
	 * @param ID
	 * @param name
	 * @param score
	 */
	public void addPlayer(byte ID, String name, int score) {
		// New player to be added
		LeaderboardPlayer player = new LeaderboardPlayer(name, score);
		
		// Adds player to ordered rankings & unordered map
		leaderboard.add(player);
		playerMap.put(ID, player);
	}
	
	/**
	 * Sorts the players by their score
	 */
	public void sortPlayers() {
		// Sorts the leaderboard array, using the first object as a comparator
		leaderboard.sort(leaderboard.get(0));
	}
	
	/**
	 * Flags the player with the given ID as ready
	 * @param ID
	 */
	public void ready(byte ID) {
		playerMap.get(ID).flagAsReady();
	}
	
	/**
	 * Checks if all of the players are ready to start. If so, it returns true
	 * @return Returns true if all are ready to start, and false otherwise
	 */
	public boolean readyToStart() {
		for(LeaderboardPlayer player : leaderboard) {
			if(!player.ready()) {
				return false;
			}
		}
		return true;
	}

}
