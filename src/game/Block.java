package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import server.Server;

/**
 * A block is an object that falls on the right of the screen
 * and then moves toward the player on the left. 
 * @author rajans1900
 */
public abstract class Block {
	
	/**
	 * Stores the block's position
	 */
	protected int x, y;
	
	/**
	 * Stores the block's dimensions
	 */
	protected int height, width;
	
	/**
	 * Stores the block's falling speed
	 */
	private double dy;
	
	/**
	 * All blocks move to the left at the same rate
	 */
	protected static final int SPEED = 6;
	
	/**
	 * Runner whose zone the block is in
	 */
	private Runner runner;
	
	/**
	 * Bottom of playing field
	 */
	protected int fieldBottom;
	
	/**
	 * Controls appearance of block
	 */
	protected Color color;
	
	/**
	 * Rectangle used for rendering & collision detection
	 */
	protected Rectangle rect;
	
	/**
	 * Player object housing the block
	 */
	protected Player player;
	
	/**
	 * When the block is obsolete, it will be flagged for deletion
	 */
	private boolean obsolete = false;
	
	/**
	 * Creates a block in the player's field
	 * @param player
	 */
	public Block(Player player) {
		height = 30;
		width = 30;
		
		x = Server.SCREEN_WIDTH - width - 10;
		y = player.getFieldTop() + 10;
		
		fieldBottom = player.getFieldBottom();
		
		color = Color.GRAY;
		
		dy = 0;
		
		runner = player.getRunner();
		this.player = player;
		
		rect = new Rectangle(x, y, width, height);
		
	}
	
	/**
	 * Draws the block: First it falls, then it scrolls
	 * @param win
	 */
	public void draw(Graphics2D win) {
		
		if((y + rect.getHeight() + dy) < (fieldBottom - Player.FLOOR_HEIGHT)) {
			//Brick falls
			dy += 0.3;
			y += dy;
			
		} else {
			//Brick scrolls left
			dy = 0;
			x -= SPEED;
			y = (int) (fieldBottom - Player.FLOOR_HEIGHT - rect.getHeight());
			
		}
		
		//Draws block in new location
		rect.setLocation(x, y);
		
		//Draws image if it exists
		BufferedImage image = getImage();
		
		if(image != null) {
			win.drawImage(image, x, (int) (y + rect.getHeight() - image.getHeight()), null);
		} else {
			win.setColor(color);
			win.fill(rect);
		}
		
		//Conditions for block deletion
		if(this.x + this.width < 0) {
			obsolete = true;
		}
		
		if(runner.intersects(rect)) {
			collideWithPlayer(player);
			obsolete = true;
		}
		
	}
	
	/**
	 * If the block is obsolete, they should be marked for deletion off of the screen
	 * @return Returns whether the block is obsolete
	 */
	public boolean isObsolete() {
		return obsolete;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	/**
	 * Controls the block's effect on the player when they collide
	 * @param p
	 */
	public abstract void collideWithPlayer(Player p);

	protected abstract BufferedImage getImage();
}
