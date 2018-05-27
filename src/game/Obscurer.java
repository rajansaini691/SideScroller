package game;

import java.awt.Color;
import java.awt.Graphics2D;

import server.Server;

/**
 * The job of the obscurer is to draw some sort of obscuring overlay, such that
 * whenever the player gets sabotaged, they will have trouble seeing their screen
 * @author Rajan Saini
 *
 */
public class Obscurer {
	
	/**
	 * Dimensions
	 */
	private int x, y, width, height;
	
	/**
	 * Controls how opaque the obscurer is, so that it can vary and be more distracting
	 */
	private float opacity = 0f;
	
	/**
	 * Counts the number of frames passed since the last reset, so that the opacity can
	 * vary with respect to time while remaining in this thread
	 */
	private int counter = 0;
	 
	/**
	 * Amount of frames per obscuring cycle
	 */
	private static final int counterMax = 30;
	
	/**
	 * We don't want the opacity to start at 0 like the poison; it should always obscure
	 */
	private static final float startingOpacity = 0.5f;
	
	/**
	 * We don't want to fully block out the screen like the poison; otherwise the player will die.
	 * The obscuring is meant to be a nuisance, not an overpowered killer
	 */
	private static final float endingOpacity = 0.9f;
	
	/**
	 * Creates a new Obscurer object. The obscurer object is meant to 
	 * obscure the playing screen if the player gets sabotaged
	 * @param x The leftmost x-coordinate of the area
	 * @param y The topmost y-coordinate of the area
	 * @param width The width of the area
	 * @param height The height of the area
	 */
	public Obscurer(Player p) {
		this.x = 0;
		this.y = p.getFieldTop();
		this.width = Server.SCREEN_WIDTH;
		this.height = p.getGameHeight();
	}
	
	public void draw(Graphics2D win) {		
		counter += 1;
		if(counter > counterMax) counter = 0;
		
		opacity = (float) ((endingOpacity - startingOpacity) / 2.0f * Math.sin(counter * Math.PI / counterMax) + (startingOpacity + endingOpacity)/2f);
		
		win.setColor(new Color((float) 0, (float) 0, (float) 0, opacity));
		win.fillRect(x, y, width, height);
		
	}

}
