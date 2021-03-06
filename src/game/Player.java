package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import server.InputMessage;
import server.OutputMessage;
import server.PlayerCollection;
import server.Server;

/**
 * The Player object corresponds to a player in real life. It contains all of
 * the game elements, such as a runner and obstacles
 * 
 * @author rajans1900
 *
 */
public class Player {

	/**
	 * Unique identifier assigned to player; helps determine where on the screen the
	 * player is. Number from 0 to 4
	 */
	private byte ID;

	/**
	 * Has access to PlayerCollection to interface with networking layer
	 */
	private PlayerCollection players;

	/**
	 * Name of player given by client All caps
	 */
	private String name;

	/**
	 * Used to determine the amount of screen space allocated to this player
	 */
	private int numPlayers, gameHeight;

	/**
	 * Determines the field dimensions
	 */
	private int fieldTop, fieldBottom;

	/**
	 * Main Obscurer overlay
	 */
	private Obscurer obscure;

	/**
	 * Main poison overlay
	 */
	private Poison poison;

	/**
	 * Main runner that moves around
	 */
	private Runner runner;

	/**
	 * Controls whether the player is sabotaged & what sabotage the player is
	 * currently experiencing
	 */
	private int sabotageState = 0;
	
	/**
	 * Holds the player's score
	 */
	private int score = 0;

	/**
	 * Determines whether player is alive, dead, or disconnected
	 */
	private int state = 0;

	/**
	 * Controls all of the blocks
	 */
	private BlockManager blockManager;

	/**
	 * Holds reference to already loaded images
	 */
	private BufferedImage[] images;

	/**
	 * Controls how high the floor is
	 */
	public static final int FLOOR_HEIGHT = 10;

	/**
	 * Maps different player states to integers for easy lookup
	 */
	private static final int STATE_ALIVE = 0;
	private static final int STATE_DISCONNECTED = 1;
	private static final int STATE_DEAD = 2;

	/**
	 * Maps different sabotage states to integers
	 */
	private static final int SABOTAGE_NONE = 0;
	private static final int SABOTAGE_WARNED = 1;
	private static final int SABOTAGE_DELAY = 2;
	private static final int SABOTAGE_OBSCURE = 3;
	private static final int SABOTAGE_REVERSE = 4;

	/**
	 * Creates a new Player object with given ID and reference to other players
	 * 
	 * @param ID
	 * @param players
	 */
	public Player(byte ID, PlayerCollection players, int score) {
		this.ID = ID;
		this.players = players;
		this.score = score;
	}

	public void draw(Graphics2D win) {
		if (state == STATE_ALIVE) {
			// Draws background
			win.setColor(Color.WHITE);
			win.fillRect(0, fieldTop, Server.SCREEN_WIDTH, gameHeight);

			// Draws player name
			win.setColor(Color.GRAY);
			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.setFont(new Font("Century Gothic", Font.PLAIN, 30));
			win.drawString("Client " + ID + ": " + name, 30, fieldTop + 30);

			// Draws player's score
			win.setColor(Color.BLACK);
			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.setFont(new Font("Century Gothic", Font.ROMAN_BASELINE, 30));
			win.drawString("" + score, Server.SCREEN_WIDTH - 140, fieldTop + 30);
			
			// Draws runner
			runner.draw(win);

			// Draws floor
			win.setColor(new Color(0, 48, 4));
			win.fillRect(0, fieldBottom - FLOOR_HEIGHT, Server.SCREEN_WIDTH, FLOOR_HEIGHT);

			// Draws blocks
			blockManager.draw(win);
			
			// Draws pipe
			win.drawImage(images[5], Server.SCREEN_WIDTH - 75, fieldTop, null);
			// Draws poison overlay
			poison.draw(win);

			// Draws obscurer overlay
			if (sabotageState == SABOTAGE_OBSCURE) {
				obscure.draw(win);
			}

		} else if (state == STATE_DISCONNECTED) {
			// Draws background
			win.setColor(Color.WHITE);
			win.fillRect(0, fieldTop, Server.SCREEN_WIDTH, gameHeight);

			// Draws "I'M DISCONNECTED! :("
			win.setColor(Color.GRAY);
			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.setFont(new Font("Century Gothic", Font.PLAIN, 50));
			win.drawString("I'M DISCONNECTED! :(", 70, fieldTop + 30);

		} else if (state == STATE_DEAD) {
			// Draws background
			win.setColor(Color.BLACK);
			win.fillRect(0, fieldTop, Server.SCREEN_WIDTH, gameHeight);
			
			// Draws "PLAYER ended with x points"
			win.setColor(Color.GRAY);
			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.setFont(new Font("Century Gothic", Font.PLAIN, 35));
			win.drawString(name + " ended with " + score + " points", 70, fieldTop + 70);
			

		}

	}

	/**
	 * Executes the command received
	 * 
	 * @param command
	 */
	public synchronized void execute(byte command) {
		// Don't execute any player commands until the game has started
		if (numPlayers == 0)
			return;

		System.out.println(name + " received command " + command);
		
		// If the player is alive, it will execute commands related to playing
		if (state == STATE_ALIVE) {
			switch (command) {
			case InputMessage.JUMP:
				runner.jump();
				break;

			case InputMessage.ADD_BLOCK:
				blockManager.addBlock(this);
				break;

			case InputMessage.CAN_PLACE:
				sendMessage(new OutputMessage(ID, OutputMessage.CAN_PLACE));
				break;

			// Received when the blockPlace count goes to 0
			case InputMessage.DIE:
				die();
				break;

			case InputMessage.SABOTAGE:
				System.out.println("Player " + ID + " has been sabotaged");

				if (this.state != STATE_DEAD && this.sabotageState == SABOTAGE_NONE) {
					activateSabotage();
				}

				break;

			default:
				System.out.println("Received unknown command: " + command);
			}
		} else {
			// Commands to be executed when the player is dead
			switch(command) {
			
			//Tell client to pass their placing responsibility to the next player
			case InputMessage.CAN_PLACE:
				sendMessage(new OutputMessage(ID, OutputMessage.DONT_PLACE));
			}
		}
	}

	/**
	 * Sends a message to the server
	 * 
	 * @param message
	 */
	public void sendMessage(OutputMessage message) {
		players.sendMessage(message);
	}

	/*
	 * Handles all inits when the game actually starts
	 */
	public void startGame(BufferedImage[] images) {
		// Calculates height of playing area from number of players when starting the
		// game
		numPlayers = players.getPlayers().size();
		gameHeight = Server.SCREEN_HEIGHT / numPlayers;

		// Holds reference to preloaded images
		this.images = images;

		// Creates a runner object
		runner = new Runner(30, ID * gameHeight - FLOOR_HEIGHT, this, images[0]);

		// Calculates dimensions of playing area from number of players when starting
		fieldTop = (ID - 1) * gameHeight;
		fieldBottom = ID * gameHeight;

		// Instantiates game-related objects
		blockManager = new BlockManager();
		poison = new Poison(this);
		obscure = new Obscurer(this);
	}

	/**
	 * Notifies the client that the player is now allowed to sabotage
	 */
	public void allowSabotage() {
		if (!runner.immobilized() && this.state == STATE_ALIVE && this.sabotageState == SABOTAGE_NONE) {
			sendMessage(new OutputMessage(ID, OutputMessage.CAN_SABOTAGE));
		}
	}

	/**
	 * Warns its client of a randomly chosen sabotage, then activates it
	 */
	public void activateSabotage() {
		// Picking random sabotage
		Random random = new Random();

		int sabotageType = random.nextInt(3); // 0 - Invert, 1 - Obscure, 2 - Delay

		// Warning client
		switch (sabotageType) {
		case 0:
			sendMessage(new OutputMessage(ID, OutputMessage.WARNING_REVERSE));
			break;
		case 1:
			sendMessage(new OutputMessage(ID, OutputMessage.WARNING_OBSCURE));
			break;
		case 2:
			sendMessage(new OutputMessage(ID, OutputMessage.WARNING_DELAY_JUMP));
			break;
		default:
			System.out.println("PLAYER " + ID + " #### UNKNOWN SABOTAGE TYPE: " + sabotageType);
		}

		// Changes state to a warned state
		this.sabotageState = Player.SABOTAGE_WARNED;

		// Turn the sabotage on after waiting 5 seconds
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				switch (sabotageType) {
				case 0:
					// Debug sabotage activation
					System.out.println("Inverting Player " + ID + "'s controls");

					// Notify client of sabotage decision (reverse)
					sendMessage(new OutputMessage(ID, OutputMessage.REVERSE));

					// Change state to an active REVERSED state
					sabotageState = Player.SABOTAGE_REVERSE;
					
					//Raise the score by 250 pts
					raiseScore(250);

					break;

				case 1:
					// Debug sabotage activation
					System.out.println("Obscuring Player " + ID + "'s screen");

					// Notify client of sabotage decision (obscure)
					sendMessage(new OutputMessage(ID, OutputMessage.OBSCURE));

					// Change state to an active OBSCURED state
					sabotageState = Player.SABOTAGE_OBSCURE;
					
					//Raise score by 100 pts
					raiseScore(100);
					break;

				case 2:
					// Debug sabotage activation
					System.out.println("Delaying Player " + ID + "'s jumping");

					// Notify client of sabotage decision (Delay)
					sendMessage(new OutputMessage(ID, OutputMessage.DELAY_JUMP));

					// Change state to an active OBSCURED state
					sabotageState = Player.SABOTAGE_DELAY;
					
					//Raise score by 200 pts
					raiseScore(200);
					break;

				default:
					System.out.println("Player " + ID + ": Unknown sabotage state " + sabotageState);
				}

			}

		}, 5000);

		// Release the sabotage after 15 seconds
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				releaseSabotage();
			}

		}, 15000);

	}

	/**
	 * Shuts down all functions and tells client that the player is dead
	 */
	public synchronized void die() {
		System.out.println(name + " died");
		state = STATE_DEAD;
		sendMessage(new OutputMessage(ID, OutputMessage.DIE));
		
		players.broadcastMessage(OutputMessage.LOWER_TIME);
		
		Comp.decrementNumPlayers();
		
		raiseScore((numPlayers - Comp.getNumPlayers()) * 200);
		System.out.println(name + "'s score was raised by " + (numPlayers - Comp.getNumPlayers()) * 200);
	}
	
	/**
	 * Determines whether the player is dead
	 * @return Returns true if the player is dead, and false if not
	 */
	public boolean isDead() {
		return state == STATE_DEAD;
	}

	/**
	 * Called when the client disconnects
	 */
	public void disconnect() {
		state = STATE_DISCONNECTED;
	}

	/**
	 * Turns on poison overlay
	 */
	public void poison() {
		poison.startPoison(100);
	}

	/**
	 * Raises the client's score by the specified amount
	 * @param score the amount to be raised
	 */
	public void raiseScore(int score) {
		this.score += score;
	}
	
	/**
	 * Frees the player from any activated sabotages and notifies client
	 */
	public void releaseSabotage() {
		sabotageState = SABOTAGE_NONE;
		sendMessage(new OutputMessage(ID, OutputMessage.RELEASE));
		System.out.println("Releasing " + name + "'s sabotage");
	}

	/**
	 * Returns player's human-readable name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns name, or null if no name is yet assigned
	 */
	public String getName() {
		return (name == null || name.length() < 1) ? null : name;
	}

	/**
	 * Returns the top coordinate of the player's field
	 * 
	 * @return top of field
	 */
	public int getFieldTop() {
		return fieldTop;
	}

	/**
	 * Returns the bottom coordinate of the player's field
	 * 
	 * @return
	 */
	public int getFieldBottom() {
		return fieldBottom;
	}

	/**
	 * Returns player's runner object
	 * 
	 * @return
	 */
	public Runner getRunner() {
		return runner;
	}

	public byte getID() {
		return this.ID;
	}
	
	/**
	 * Lets the other nodes reference the player images so that they can draw them
	 * @return image objects
	 */
	public BufferedImage[] getImages() {
		return images;
	}
	
	/**
	 * Returns score of player
	 * @return
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Returns height of playing area
	 * 
	 * @return
	 */
	public int getGameHeight() {
		return gameHeight;
	}
}
