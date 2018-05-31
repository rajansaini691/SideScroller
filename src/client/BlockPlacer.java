package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import server.InputMessage;

/**
 * The BlockPlacer listens to the server to see if it can place a block, and places one when it can
 * @author rajans1900
 *
 */
public class BlockPlacer implements KeyListener {
	
	/**
	 * References comp to be able to handle I/O
	 */
	private Comp comp;
	
	/**
	 * Controls whether the player can place a block
	 */
	private boolean canPlace = false;
	private boolean space = false;
	
	/**
	 * Counts down when the player is allowed to place
	 */
	private Timer timer;
	
	/**
	 * The time the player has left to place
	 */
	private int counter;
	
	/**
	 * The initial time the player has
	 */
	private int initialCount = 5;
	
	/**
	 * Instantiates a BlockPlacer object
	 * @param comp
	 */
	public BlockPlacer(Comp comp) {
		this.comp = comp;
		
		timer = new Timer();
		
		//Player 1 gets to place the first block; after that it's anyone's game
		if(comp.getID() == 1) {
			System.out.println("I'll start placing");
			startTimer();
		}
	}
	
	/**
	 * Draws the count down timer
	 * @param win
	 */
	public void draw(Graphics2D win) {
		if(canPlace) {
			win.setColor(Color.WHITE);
			win.setFont(new Font("Yu gothic", Font.BOLD, 200));
			win.drawString("" + counter, 330, 500);
		}
	}
	
	/**
	 * Lets the player start placing and starts the timer
	 */
	public void startTimer() {
		canPlace = true;
		
		counter = initialCount;
		
		//Resets timer, since a timer must be reset after cancellation
		timer.cancel();
		timer = new Timer();
		
		//Start decrementing the counter. If it hits zero, tell the player to die
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				counter--;
				
				if(counter == 0) {
					comp.transmitMessage(new InputMessage((byte) comp.getID(), InputMessage.DIE));
					comp.transmitMessage(new InputMessage((byte) (comp.getID() + 1), InputMessage.CAN_PLACE));
					timer.cancel();
				}
				
				comp.playSound(12 - initialCount + counter);
				
				//Manually refreshes window since the number is being drawn
				comp.repaint();
			}
			
		}, 700, 700);
	}
	
	public void decrementTime() {
		initialCount--;
	}
	
	public void cancelTimer() {
		timer.cancel();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		/*
		 * As long as it's the client's turn, tell everyone to place a block when the space bar 
		 * gets pressed. Then stop the timer. Finally, tell the next player that it's his/her turn
		 * to place a block.
		 */
		if(canPlace && !space && e.getKeyCode() == KeyEvent.VK_SPACE) {
			space = true;
			canPlace = false;
			comp.transmitMessage(new InputMessage(InputMessage.PLAYER_ALL, InputMessage.ADD_BLOCK));
			comp.transmitMessage(new InputMessage((byte) (comp.getID() + 1), InputMessage.CAN_PLACE));
			timer.cancel();
			
			// Plays place_block.wav
			comp.playSound(6);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			space = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
}
