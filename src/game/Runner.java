package game;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import server.OutputMessage;

/**
 * The runner, like its name suggests, is the object that does the running. Its
 * main jobs are to render itself, play a running animation, and jump.
 * 
 * @author rajans1900
 *
 */
public class Runner {

	/**
	 * The initial jumping speed of the runner (v0)
	 */
	private static final int JUMP_SPEED = 10;

	/**
	 * Stores the initial locations of the runner. This way, it always ends up where
	 * it jumps.
	 */
	private int x0, y0;

	/**
	 * Stores the current location of the runner
	 */
	private int x, y;

	/**
	 * Stores the dimensions of the runner
	 */
	private int height, width;

	/**
	 * Determines whether the player is jumping. This way, the player can only
	 * continue jumping after it hits the ground
	 */
	private boolean jumping = false;

	/**
	 * When the runner is immobilized, it cannot jump
	 */
	private boolean immobilized = false;
	private int immobilizedCounter = 0;

	/**
	 * Current velocities of the runner (dx will usually be 0) We are assuming that
	 * up is the positive direction
	 */
	private double dx = 0;
	private double dy = 0;

	/**
	 * Controls the strength of gravity when the runner jumps
	 */
	private static final double GRAVITY = 0.55;

	/**
	 * Rectangle used for collision detection as an invisible hitbox
	 */
	private Rectangle rect;

	/**
	 * Needs access to player object to send message to client
	 */
	private Player player;

	/**
	 * Variables needed for animation
	 */
	private static int counter;
	private static final int frameTime = 30;
	private int currentFrame = 0;
	private BufferedImage runnerFrames[] = new BufferedImage[4];

	/**
	 * Creates runner object
	 * 
	 * @param x
	 *            Initial x coordinate
	 * @param y
	 *            Initial y coordinate
	 */
	public Runner(int x, int y, Player p, BufferedImage runningSprite) {

		// Cuts up sprite into frames and puts it in runnerFrames array so it can be
		// rendered
		int imageWidth = 256 / 4;
		int imageHeight = 96;

		for (int i = 0; i < runnerFrames.length; i++) {
			runnerFrames[i] = runningSprite.getSubimage(i * imageWidth, 0, imageWidth, imageHeight);
		}

		/* Sets initial positional and dimensional variables */

		// Sets height of hitbox
		this.height = imageHeight;
		this.width = imageWidth;

		// Sets & stores initial location
		this.x0 = x;
		this.y0 = y - height;

		// Sets current location equal to initial location
		this.x = x0;
		this.y = y0;

		// Initializes hitbox
		rect = new Rectangle(x + 6, y - height + 10, width / 2 - 10, height - 10);

		// Stores reference to player object owning the runner
		this.player = p;

	}

	/**
	 * Draws the player
	 * 
	 * @param win
	 */
	public void draw(Graphics2D win) {
		// Draws and animates runner
		counter++;

		if (counter > frameTime) {
			counter = 0;
			currentFrame++;
			currentFrame %= runnerFrames.length;
		}

		win.drawImage(runnerFrames[currentFrame], x, y, null);

		// Handles jumping
		if (jumping) {
			dy -= GRAVITY;

			// Stop jumping when you hit the ground
			if (y >= y0 && dy < 0) {
				jumping = false;
				dy = 0;
				y = y0;
			}
		}

		// Moves the player
		y -= dy;
		x += dx;
		rect.setLocation(x + 6, y + 10);

		// Keeps the player immobilized until counter reaches 0
		if (immobilized) {
			immobilizedCounter--;

			// When no longer immobilized, tell the client that they are free and turn off
			// the immobilized variable
			if (immobilizedCounter < 1) {
				immobilized = false;
				player.sendMessage(new OutputMessage(player.getID(), OutputMessage.FREE));
			}
		}
	}

	public void jump() {
		// If the player is immobilized or currently jumping, don't jump
		if (immobilized || jumping) {
			return;
		}

		// Tells the player to jump
		System.out.println("Jumping");
		jumping = true;
		dy = JUMP_SPEED;

	}

	/**
	 * Immobilizes the player for a specified number of frames
	 * 
	 * @param frames
	 */
	public void immobilize(int frames) {
		immobilized = true;
		immobilizedCounter = frames;

		player.sendMessage(new OutputMessage(player.getID(), OutputMessage.IMMOBILIZED));
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean intersects(Rectangle r) {
		return rect.intersects(r);
	}
	
	/**
	 * Returns whether the runner is immobilized or not
	 * @return Returns true if the runner is currently immobilized
	 */
	public boolean immobilized() {
		return immobilized;
	}

}
