package game;

import java.awt.Color;
import java.awt.Graphics2D;

import server.Server;

/**
 * Draws the poison overlay when the player gets poisoned and deactivates it when the poison runs out
 * @author rajans1900
 *
 */
public class Poison {
	
	/**
	 * The color of the overlay (should be some shade of purple)
	 */
	private Color poisonColor = new Color(145, 0, 186);
	
	/**
	 * Only activates the effect when the player actually gets poisoned
	 */
	private boolean poisoned = false;
	
	/**
	 * Controls when the poison stops
	 */
	private int counter;
	
	/**
	 * Stores the number of frames the poison lasts for to control rendering
	 */
	private int poisonMax;
	
	/**
	 * Dimensions of poison
	 */
	private int x, y, width, height;
	
	/**
	 * Creates new Poison object
	 */
	public Poison(Player p) {
		this.x = 0;
		this.y = p.getFieldTop();
		this.width = Server.SCREEN_WIDTH;
		this.height = p.getGameHeight();
	}
	
	/**
	 * Draws the poison overlay
	 * @param win
	 */
	public void draw(Graphics2D win) {
		if(poisoned) {
			//TODO - Get the poisonColor to change with each frame, dependent on counter
			poisonColor = new Color((float) (145.0/255.0),(float) 0, (float) (186.0/255.0), (float) Math.sin(counter * Math.PI / poisonMax));
			
			win.setColor(poisonColor);
			win.fillRect(x, y, width, height);
			
			counter--;
			
			if(counter <= 0) {
				poisoned = false;
				poisonMax = 0;
			}
		}
	}
	
	/**
	 * Activates the poison overlay
	 * @param duration Number of frames to be poisoned
	 */
	public void startPoison(int duration) {
		//If the player is already being poisoned, don't screw him/her over even more
		if(poisoned) return;
		
		counter = duration;
		poisonMax = duration;
		poisoned = true;
	}
	
}
