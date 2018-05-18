package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import server.InputMessage;
import server.OutputMessage;

@SuppressWarnings("serial")
public class Comp extends GameDriverV3 implements KeyListener {
	
	private byte ID;
	private ClientOutput cout;
	
	//Renders and controls state 0
	private IntroScreen intro;
	
	//Renders and controls state 1
	private NameScreen nameScreen;
	
	//Handles listening for input to place blocks
	private BlockPlacer placer;
	
	//0 - Logging in; 1 - Sending name; 2 - Waiting for others to join; 3 - Playing
	private int gameState = 0;
	
	//0 - Normal playing; 1 - immobilized; 2 - dead
	private int playingState = 0;
	
	/*
	 * Constants holding gameState to allow for uniformity
	 */
	private static final int STATE_LOGGING_IN = 0;
	private static final int STATE_SENDING_NAME = 1;
	private static final int STATE_WAITING_FOR_GAME = 2;
	private static final int STATE_PLAYING = 3;
	
	/*
	 * Constants that determine the player's condition while playing
	 */
	private static final int PLAYING_STATE_NORMAL = 0;
	private static final int PLAYING_STATE_IMMOBILIZED = 1;
	private static final int PLAYING_STATE_DEAD = 2;
	
	public Comp() {
		intro = new IntroScreen(this);
		nameScreen = new NameScreen(this);
		
		this.addKeyListener(this);
		this.addKeyListener(intro.getHostField());
		this.addKeyListener(intro.getPortField());
	}

	/**
	 * Handles all of the rendering
	 */
	@Override
	public void draw(Graphics2D win) {
		if(gameState == STATE_LOGGING_IN) {				//Draws connect screen
			intro.draw(win);
			
		} else if(gameState == STATE_SENDING_NAME) {			//Draws getName screen
			nameScreen.draw(win);
			
		} else if(gameState == STATE_WAITING_FOR_GAME) {
			win.setColor(Color.BLACK);
			win.fillRect(0, 0, 800, 600);
			
			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.setFont(new Font("Yu Gothic", Font.BOLD, 100));
			win.setColor(Color.WHITE);
			win.drawString("WAITING", 150, 200);
			
		} else if(gameState == STATE_PLAYING) {
			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.setFont(new Font("Yu Gothic", Font.BOLD, 100));
			
			if(playingState == Comp.PLAYING_STATE_NORMAL) {
				win.setColor(Color.BLACK);
				win.fillRect(0, 0, 800, 600);
				
				win.setColor(Color.WHITE);
				win.drawString("PLAYING", 150, 200);
				
			} else if(playingState == Comp.PLAYING_STATE_IMMOBILIZED){
				win.setColor(Color.GREEN);
				win.fillRect(0, 0, 800, 600);
				
				win.setColor(Color.WHITE);
				win.drawString("IMMOBILIZED", 75, 200);
				
			} else if(playingState == Comp.PLAYING_STATE_DEAD) {
				win.setColor(Color.BLACK);
				win.fillRect(0, 0, 800, 600);
				
				win.setColor(Color.WHITE);
				win.setFont(new Font("Yu Gothic", Font.BOLD, 50));
				win.drawString("You have died :(", 175, 200);
				win.drawString("Wait until the next round", 75, 350);
			}
			
			placer.draw(win);
			
		}
	}
	
	/**
	 * Processes incoming messages from server
	 * @param message
	 */
	public synchronized void process(byte message) {
		switch(message) {
		case OutputMessage.START_GAME:
			setGameState(Comp.STATE_PLAYING);
			break;
			
		case OutputMessage.CAN_PLACE:
			placer.startTimer();
			break;
			
		case OutputMessage.IMMOBILIZED:
			if(playingState != Comp.PLAYING_STATE_DEAD) {
				playingState = Comp.PLAYING_STATE_IMMOBILIZED;
			}
			break;
			
		case OutputMessage.FREE:
			if(playingState != Comp.PLAYING_STATE_DEAD) {
				playingState = Comp.PLAYING_STATE_NORMAL;
			}
			break;
		
		case OutputMessage.DIE:
			playingState = Comp.PLAYING_STATE_DEAD;
			break;
			
		default:
			System.out.println("Unknown message: " + message);
		}
	}
	
	/**
	 * Sends a string to server
	 * @param message
	 */
	public void transmitString(String message) {
		cout.transmit(message);
	}
	
	/**
	 * Sends a byte command to server
	 * @param inputMessage
	 */
	public void transmitMessage(InputMessage message) {
		cout.transmit(message);
	}
	
	/*
	 * Sends a message to the server when a key is pressed
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(gameState == STATE_PLAYING) {
			int keyCode = e.getKeyCode();
			
			if(keyCode == KeyEvent.VK_UP) {
				cout.transmit(new InputMessage(ID, InputMessage.JUMP));
			}
			if(keyCode == KeyEvent.VK_1) {
				cout.transmit(new InputMessage((byte) 1, InputMessage.SABOTAGE));
			}
			if(keyCode == KeyEvent.VK_2) {
				cout.transmit(new InputMessage((byte) 2, InputMessage.SABOTAGE));
			}
			if(keyCode == KeyEvent.VK_3) {
				cout.transmit(new InputMessage((byte) 3, InputMessage.SABOTAGE));
			}
			if(keyCode == KeyEvent.VK_4) {
				cout.transmit(new InputMessage((byte) 4, InputMessage.SABOTAGE));
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * GETTERS AND SETTERS:
	 */
	
	public void setID(byte ID) {
		this.ID = ID;
	}
	
	public int getID() {
		return ID;
	}
	
	public int getPort() {
		return intro.getPort();
	}
	
	public String getHost() {
		return intro.getHost();
	}
	
	public synchronized int getGameState() {
		return gameState;
	}
	
	public void setGameState(int gameState) {
		if(this.gameState != gameState) {			//If gameState is changing out of current state, change keyListeners
			if(gameState == 1) {
				this.removeKeyListener(intro.getHostField());
				this.removeKeyListener(intro.getPortField());
				this.addKeyListener(nameScreen.getNameField());
				this.addKeyListener(nameScreen);
				
			} else if(gameState == STATE_PLAYING) {
				this.removeKeyListener(nameScreen.getNameField());
				this.removeKeyListener(nameScreen);
				
				placer = new BlockPlacer(this);
				this.addKeyListener(placer);
			}
			
		}
		this.gameState = gameState;
	}
	
	public void setOutputSocket(ClientOutput cout) {
		this.cout = cout;
	}
	
}
