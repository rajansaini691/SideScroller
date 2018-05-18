package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import server.InputMessage;
import server.OutputMessage;
import server.PlayerCollection;
import server.Server;

/**
 * The Player object corresponds to a player in real life. It contains all of the game elements, such
 * as a runner and obstacles
 * @author rajans1900
 *
 */
public class Player {
	
	/**
	 * Unique identifier assigned to player; helps determine where on the screen the player is.
	 * Number from 0 to 4
	 */
	private byte ID;
	
	/**
	 * Has access to PlayerCollection to interface with networking layer
	 */
	private PlayerCollection players;
	
	/**
	 * Name of player given by client
	 * All caps
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
	 * Main poison overlay
	 */
	private Poison poison;
	
	/**
	 * Main runner that moves around
	 */
	private Runner runner;
	
	/**
	 * Determines whether player is alive, dead, or disconnected
	 */
	private int state = 0;
	
	/**
	 * Controls all of the blocks
	 */
	private BlockManager blockManager;
	
	/*
	 * Controls how high the floor is
	 */
	public static final int FLOOR_HEIGHT = 10;
	
	/**
	 * Maps different player states to integers for easy lookup
	 */
	private static final int STATE_ALIVE = 0;
	private static final int STATE_DISCONNECTED = 1;
	@SuppressWarnings("unused")
	private static final int STATE_DEAD = 2;
	
	public Player(byte ID, PlayerCollection players) {
		this.ID = ID;
		this.players = players;
	}
	
	public void draw(Graphics2D win) {
		if(state == STATE_ALIVE) {
			//Draws background
			win.setColor(Color.WHITE);
			win.fillRect(0, fieldTop, Server.SCREEN_WIDTH, gameHeight);
			
			//Draws player name
			win.setColor(Color.GRAY);
			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.setFont(new Font("Century Gothic", Font.PLAIN, 30));
			win.drawString(name, 30, fieldTop + 30);
			
			//Draws runner
			runner.draw(win);
			
			//Draws floor
			win.setColor(Color.BLACK);
			win.fillRect(0, fieldBottom - FLOOR_HEIGHT, Server.SCREEN_WIDTH, FLOOR_HEIGHT);
			
			//Draws blocks
			blockManager.draw(win);
			
			//Draws poison overlay
			poison.draw(win);
			
		} else if(state == STATE_DISCONNECTED) {
			//Draws background
			win.setColor(Color.WHITE);
			win.fillRect(0, fieldTop, Server.SCREEN_WIDTH, gameHeight);
			
			//Draws "I'M DISCONNECTED! :("
			win.setColor(Color.GRAY);
			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.setFont(new Font("Century Gothic", Font.PLAIN, 50));
			win.drawString("I'M DISCONNECTED! :(", 70, fieldTop + 30);
			
		}
		
	}
	
	/**
	 * Executes the command received
	 * @param command
	 */
	public synchronized void execute(byte command) {
		System.out.println("Received command: " + command);
		
		switch(command) {
		case InputMessage.JUMP:
			runner.jump();
			break;
			
		case InputMessage.ADD_BLOCK:
			//blockManager.addBlock(new TestBlock(this));
			blockManager.addBlock(new Thorn(this));
			break;
		
		case InputMessage.CAN_PLACE:
			sendMessage(new OutputMessage(ID, OutputMessage.CAN_PLACE));
			break;
		
		//Received when the blockPlace count goes to 0
		case InputMessage.DIE:
			die();
			break;
			
		default:
			System.out.println("Received unknown command: " + command);
		}
	}
	
	/**
	 * Sends a message to the server
	 * @param message
	 */
	public void sendMessage(OutputMessage message) {
		players.sendMessage(message);
	}
	
	/*
	 * Handles all inits when the game actually starts
	 */
	public void startGame() {
		numPlayers = players.getPlayers().size();
		gameHeight = Server.SCREEN_HEIGHT / numPlayers;
		
		runner = new Runner(30, ID * gameHeight - FLOOR_HEIGHT, this);
		
		fieldTop = (ID - 1) * gameHeight;
		fieldBottom = ID * gameHeight;
		
		blockManager = new BlockManager();
		
		poison = new Poison(this);		
	}
	
	/**
	 * Shuts down all functions and tells client that the player is dead
	 */
	public void die() {
		System.out.println(name + " died");
		sendMessage(new OutputMessage(ID, OutputMessage.DIE));
	}
	
	/**
	 * Turns on poison overlay
	 */
	public void poison() {
		poison.startPoison(100);
	}
	
	/**
	 * Called when the client disconnects
	 */
	public void disconnect() {
		state = STATE_DISCONNECTED;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/*
	 * Returns name, or null if no name is yet assigned
	 */
	public String getName() {
		return (name == null || name.length() < 1)? null : name;
	}
	
	/**
	 * Returns the top coordinate of the player's field
	 * @return top of field
	 */
	public int getFieldTop() {
		return fieldTop;
	}
	
	/**
	 * Returns the bottom coordinate of the player's field
	 * @return
	 */
	public int getFieldBottom() {
		return fieldBottom;
	}
	
	/**
	 * Returns player's runner object
	 * @return
	 */
	public Runner getRunner() {
		return runner;
	}
	
	public byte getID() {
		return this.ID;
	}
	
	/**
	 * Returns height of playing area
	 * @return
	 */
	public int getGameHeight() {
		return gameHeight;
	}
}
