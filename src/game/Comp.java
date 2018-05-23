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
import server.PlayerCollection;

public class Comp extends GameDriverV3 {
	
	private int gameState = 0; 			//0 - Waiting screen; 1 - Game
	private PlayerCollection playerCollection;
	private Hashtable<Byte, Player> players;
	private BufferedImage[] images;
	
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
			playerCollection.draw(win);
		}
	}
	
	public void startGame() {		
		for(byte i : players.keySet()) {
			players.get(i).startGame(images);
		}
		
		gameState = 1;
	}
	
}
