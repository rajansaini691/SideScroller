package game;

import java.util.Comparator;

/**
 * Individual element of leaderboard; sole purpose is to store name & score of player. This provides for
 * more efficient memory allocation when resetting the game
 * @author Rajan Saini
 *
 */
public class LeaderboardPlayer implements Comparator<LeaderboardPlayer> {

	private String name; 
	private int score;
	private boolean ready = false;
	
	public LeaderboardPlayer(String name, int score) {
		this.name = name;
		this.score = score;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}

	
	/**
	 * Used for sorting
	 */
	@Override
	public int compare(LeaderboardPlayer p1, LeaderboardPlayer p2) {
		return p2.getScore() - p1.getScore();
	}
	
	/**
	 * Sets "ready" to true, signifying that the player is ready to start the game
	 */
	public void flagAsReady() {
		ready = true;
	}
	
	/**
	 * Decides whether the player is ready or not
	 * @return Returns true if the player is ready and false if the player is not ready
	 */
	public boolean ready() {
		return ready;
	}

	

}
