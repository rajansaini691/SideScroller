package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

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
	protected static final int SPEED = 4;
	
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
	 * Creates a block in the player's field
	 * @param player
	 */
	public Block(Player player) {
		height = 50;
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
		
		rect.setLocation(x, y);
		win.setColor(color);
		win.fill(rect);
		
		if(runner.intersects(rect)) {
			collideWithPlayer(player);
		}
		
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
}
