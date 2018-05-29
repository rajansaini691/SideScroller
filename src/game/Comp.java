package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import server.GameDriverV3;
import server.OutputMessage;
import server.PlayerCollection;

public class Comp extends GameDriverV3 {
	
	private int gameState = 0; 			//0 - Waiting screen; 1 - Game; 2 - Leaderboard
	private PlayerCollection playerCollection;
	private Hashtable<Byte, Player> players;
	private BufferedImage[] images;
	
	/**
	 * Stores the number of players so that it can reset the game.
	 * Defaults to 100 at first so that the game doesn't accidentally reset
	 */
	private static int numPlayers = 100;
	
	public Comp(PlayerCollection playerCollection) {
		this.playerCollection = playerCollection;
		players = playerCollection.getPlayers();
		
		//Initializes resources: 0 - Robot
		images = new BufferedImage[1];
		
		try {
			images[0] = ImageIO.read(this.getClass().getResourceAsStream("/Robot.png"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	
	// TODO Draw IP address to screen OR create a mapping to letters (last priority)
	@Override
	public void draw(Graphics2D win) {
		if(gameState == 0) {				//Draws waiting screen
			//Draws background
			win.setColor(Color.BLACK);
			win.fillRect(0, 0, 800, 600);
			
			//Draws the title
			win.setColor(Color.WHITE);
			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.setFont(new Font("Century Gothic", Font.PLAIN, 65));
			win.drawString("Enjoy the jazz!", 150, 100);
			
			//Draws the players' names
			win.setFont(new Font("Century Gothic", Font.PLAIN, 40));
			win.drawString("Players: ", 100, 200);
			
			win.setFont(new Font("Century Gothic", Font.PLAIN, 30));
			
			for(byte i : players.keySet()) {
				String name = players.get(i).getName();
				
				if(name != null) {
					win.drawString(players.get(i).getName(), 150, 50 * i + 220);
				}
			}
			
			
		} else if(gameState == 1) {
			//Draw players
			try {
				playerCollection.draw(win);
			} catch (NullPointerException e){
				System.exit(0);
			}
			
			//Controls death
			if(numPlayers < 1) {
				System.out.println("Restarting game");
				restartGame();
				gameState = 2;
			}
		} else if (gameState == 2) {
			//TODO Draw leaderboard
			win.setColor(new Color(189, 255, 140));
			win.fillRect(0, 0, 800, 600);
			
			
			playerCollection.getLeaderboard().draw(win);
			
			if(playerCollection.getLeaderboard().readyToStart()) {
				gameState = 0;
				playerCollection.broadcastMessage(OutputMessage.START_GAME);
				startGame();
			}
		}
	}
	
	/**
	 * Lowers the number of players by 1 whenever a player dies
	 */
	public static void decrementNumPlayers() {
		numPlayers--;
	}
	
	/**
	 * Reinstantiates all players and notifies client
	 */
	public synchronized void restartGame() {
		// Reinstantiates all players
		playerCollection.reset();
		
		playerCollection.broadcastMessage(OutputMessage.RESET);
	}
	
	public void startGame() {		
		for(byte i : players.keySet()) {
			players.get(i).startGame(images);
		}
		
		numPlayers = players.size();
		
		gameState = 1;
	}
	
}
