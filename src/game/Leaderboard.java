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
	 * Basically, the players' scores will get drawn one at a time, from last to first. currentScore
	 * holds the animated score of the player whose score is getting drawn
	 */
	private int currentScore = 0;
	
	/**
	 * Index of the player whose score is getting drawn
	 */
	private int currentIndex;
	
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
		this.currentIndex = numPlayers - 1;
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
			
			win.drawString(player.getName(), 100, 80 * i + 200);
			//win.drawString("" + player.getScore(), 600, 80 * i + 200);
		}
		
		//If currentIndex = 4, don't draw anything. Draw already animated players
		for(int i = leaderboard.size() - 1; i > currentIndex; i--) {
			if(leaderboard.get(i).ready()) {
				win.setColor(Color.GRAY);
			} else {
				win.setColor(Color.BLACK);
			}
			win.drawString("" + leaderboard.get(i).getScore(), 600, 80 * i + 200);
		}
		
		// Draw animation
		if(currentIndex >= 0) {
			LeaderboardPlayer currentPlayer = leaderboard.get(currentIndex);
			
			currentScore += 1;
			
			if(currentScore > currentPlayer.getScore()) {
				currentIndex--;
				currentScore = 0;
			}
			
			win.drawString("" + currentScore, 600, 80 * currentIndex + 200);
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
	 * Checks if all of the players are ready to start, and that the scores are drawn.
	 *  If so, it returns true
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
