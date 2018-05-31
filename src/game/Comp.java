package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import server.GameDriverV3;
import server.OutputMessage;
import server.PlayerCollection;

public class Comp extends GameDriverV3 {
	
	private int gameState = 0; 			//0 - Waiting screen; 1 - Game; 2 - Leaderboard
	private PlayerCollection playerCollection;
	private Hashtable<Byte, Player> players;
	private BufferedImage[] images;
	
	/**
	 * Sound-related variables
	 */
	private MediaPlayer jazzPlayer;
	private MediaPlayer syncopatedPlayer;
	private MediaPlayer fastPlayer;
	
	/**
	 * Networking variables that get drawn to the screen
	 */
	private String IP = " ";
	private int port = 0;
	
	/**
	 * Stores the number of players so that it can reset the game.
	 * Defaults to 100 at first so that the game doesn't accidentally reset
	 */
	private static int numPlayers = 100;
	
	public Comp(PlayerCollection playerCollection) {
		this.playerCollection = playerCollection;
		players = playerCollection.getPlayers();
		
		//Initializes resources: 0 - Robot, 1 - Cactus, 2 - flower, 3 - thorn, 4 - grass, 5 - pipe
		images = new BufferedImage[6];
		
		try {
			images[0] = ImageIO.read(this.getClass().getResourceAsStream("/Robot.png"));
			images[1] = ImageIO.read(this.getClass().getResourceAsStream("/cactus.png"));
			images[2] = ImageIO.read(this.getClass().getResourceAsStream("/flower.png"));
			images[3] = ImageIO.read(this.getClass().getResourceAsStream("/thorn.png"));
			images[4] = ImageIO.read(this.getClass().getResourceAsStream("/grass.png"));
			images[5] = ImageIO.read(this.getClass().getResourceAsStream("/pipe.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		new javafx.embed.swing.JFXPanel(); // forces JavaFX init
		
		Media jazzFile = new Media(this.getClass().getResource("/jazz.mp3").toString());
		jazzPlayer = new MediaPlayer(jazzFile);
		jazzPlayer.play();
		
		Media fastFile = new Media(this.getClass().getResource("/fast.mp3").toString());
		fastPlayer = new MediaPlayer(fastFile);
		
		Media syncFile = new Media(this.getClass().getResource("/syncopated.mp3").toString());
		syncopatedPlayer = new MediaPlayer(syncFile);
		
	}

	@Override
	public void draw(Graphics2D win) {
		if(gameState == 0) {				//Draws waiting screen
			//Draws background
			win.setColor(Color.BLACK);
			win.fillRect(0, 0, 800, 600);
			
			//Draws the title
			win.setColor(new Color(194, 252, 208));
			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.setFont(new Font("Century Gothic", Font.BOLD, 100));
			win.drawString("PLANTS", 220, 100);
			
			//Draws the subtitle
			win.setColor(new Color(114, 181, 101));
			win.setFont(new Font("Lucida Handwriting", Font.ITALIC, 30));
			win.drawString("By Rajan Saini", 250, 140);
			
			//Draws the players' names
			win.setColor(Color.WHITE);
			win.setFont(new Font("Century Gothic", Font.PLAIN, 40));
			win.drawString("Players: ", 100, 200);
			
			win.setFont(new Font("Century Gothic", Font.PLAIN, 30));
			
			for(byte i : players.keySet()) {
				String name = players.get(i).getName();
				
				if(name != null) {
					win.drawString(players.get(i).getName(), 150, 50 * i + 220);
				}
			}
			
			// Draws the host and port to be connected to
			win.setColor(new Color(196, 196, 196));
			win.drawString("Listening on port " + port + " and address " + IP, 30, 470);
			
			//Draws "Enjoy the jazz!"
			win.setColor(new Color(230, 230, 230));
			win.setFont(new Font("Century Gothic", Font.ITALIC, 30));
			win.drawString("(Enjoy the jazz!)", 280, 520);
			
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
				
				fastPlayer.stop();
				syncopatedPlayer.play();
			}
		} else if (gameState == 2) {
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
	 * Displays given port and host to the screen
	 * @param port server's port
	 * @param host server's IP address or hostname
	 */
	public void displayHost(int port, String host) {
		this.IP = host;
		this.port = port;
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
		
		// Stops the jazz and syncopated players, and starts the fast player on an infinite loop
		syncopatedPlayer.stop();
		jazzPlayer.stop();
		fastPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		fastPlayer.play();
	}
	
}
