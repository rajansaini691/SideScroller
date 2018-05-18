package game;

import java.awt.Graphics2D;
import java.util.ArrayList;

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
	 * Adds a new block object to the list of blocks
	 * @param block
	 */
	public synchronized void addBlock(Block block) {
		blocks.add(block);
	}
	
}
