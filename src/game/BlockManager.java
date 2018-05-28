package game;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * The BlockManager creates, maintains, draws, and destroys blocks
 * @author rajans1900
 *
 */
public class BlockManager {
	
	/**
	 * Where the blocks being maintained are stored
	 */
	public ArrayList<Block> blocks;
	
	/**
	 * List of blocks to be removed
	 */
	public ArrayList<Block> removeQueue;
	
	/**
	 * Creates a new BlockManager object
	 */
	public BlockManager() {
		blocks = new ArrayList<Block>();
		removeQueue = new ArrayList<Block>();
	}
	
	/**
	 * Draws the blocks to the screen
	 * @param win
	 */
	public synchronized void draw(Graphics2D win) {
		for(Block block : blocks) {
			block.draw(win);
			
			//Puts offending block in list of blocks to be removed
			if(block.getX() + block.getWidth() < 0) {
				removeQueue.add(block);
			}
		}
		
		//Goes through blocks to be removed & removes them
		for(Block block : removeQueue) {
			blocks.remove(block);
		}
		removeQueue.clear();
	}
	
	/**
	 * Generates a randomly chosen block
	 * @param block
	 */
	public synchronized void addBlock(Player p) {
		// Block to be added
		Block block;
		
		double random = Math.random();
		
		/*
		 * The block will have a:
		 * 		50% chance of becoming a cactus
		 * 		20% chance of becoming a thorn
		 * 		20% chance of becoming grass
		 * 		10% chance of becoming a flower
		 */
		if(random < 0.5) {
			block = new Cactus(p);
		} else if(random < 0.7) {
			block = new Thorn(p);
		} else if(random < 0.9) {
			block = new Grass(p);
		} else {
			block = new Flower(p);
		}
		
		// Adds the block to list of blocks so that it can be drawn and accessed globally
		// (obviously without breaking encapsulation).
		blocks.add(block);
	}
	
}
