package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import server.InputMessage;
import server.OutputMessage;

public class Comp extends GameDriverV3 implements KeyListener {

	/**
	 * Serial version ID for Comp
	 */
	private static final long serialVersionUID = 1;

	private byte ID;
	private ClientOutput cout;

	// Renders and controls state 0
	private IntroScreen intro;

	// Renders and controls state 1
	private NameScreen nameScreen;

	// Handles listening for input to place blocks
	private BlockPlacer placer;

	// 0 - Logging in; 1 - Sending name; 2 - Waiting for others to join; 3 - Playing
	private int gameState = 0;

	// The mode the client is in while playing
	private int playingState = 0;

	// Determines whether the player is immobilized; separate from the playing state
	private boolean immobilized = false;

	/*
	 * Constants holding gameState to allow for uniformity
	 */
	private static final int STATE_LOGGING_IN = 0;
	private static final int STATE_SENDING_NAME = 1;
	private static final int STATE_WAITING_FOR_GAME = 2;
	private static final int STATE_PLAYING = 3;
	private static final int STATE_WAITING_FOR_RESTART = 4;

	/*
	 * Constants that determine the player's condition while playing
	 */
	private static final int PLAYING_STATE_NORMAL = 0;
	private static final int PLAYING_STATE_DEAD = 2;
	private static final int PLAYING_STATE_CAN_SABOTAGE = 3;
	private static final int AWAITING_SABOTAGE_REVERSE = 4;
	private static final int RECEIVED_SABOTAGE_REVERSE = 5;
	private static final int AWAITING_SABOTAGE_OBSCURE = 6;
	private static final int RECEIVED_SABOTAGE_OBSCURE = 7;
	private static final int AWAITING_SABOTAGE_DELAY = 8;
	private static final int RECEIVED_SABOTAGE_DELAY = 9;
	
	/**
	 * Holds reference to SoundDriver, which stores and plays sounds
	 */
	SoundDriver driver;

	public Comp() {
		intro = new IntroScreen(this);
		nameScreen = new NameScreen(this);

		this.addKeyListener(this);
		this.addKeyListener(intro.getHostField());
		this.addKeyListener(intro.getPortField());

		String[] soundFiles = {"activate", "can_place", "can_sabotage",
				"death", "immobile", "jump", "place_block", "sabotaged", 
				"timer_1", "timer_2", "timer_3", "timer_4", "timer_5"};
		
		driver = new SoundDriver(soundFiles);
		
	}

	/**
	 * Handles all of the rendering
	 */
	@Override
	public void draw(Graphics2D win) {
		if (gameState == STATE_LOGGING_IN) { // Draws connect screen
			intro.draw(win);

		} else if (gameState == STATE_SENDING_NAME) { // Draws getName screen
			nameScreen.draw(win);

		} else if (gameState == STATE_WAITING_FOR_GAME) {
			win.setColor(Color.BLACK);
			win.fillRect(0, 0, 800, 600);

			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.setFont(new Font("Yu Gothic", Font.BOLD, 100));
			win.setColor(Color.WHITE);
			win.drawString("WAITING", 150, 200);

		} else if (gameState == STATE_PLAYING) {
			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.setFont(new Font("Yu Gothic", Font.BOLD, 100));

			if (playingState == Comp.PLAYING_STATE_NORMAL) {
				win.setColor(Color.BLACK);
				win.fillRect(0, 0, 800, 600);

				win.setColor(Color.WHITE);
				win.drawString("PLAYING", 150, 200);

			} else if (playingState == Comp.PLAYING_STATE_DEAD) {
				win.setColor(Color.BLACK);
				win.fillRect(0, 0, 800, 600);

				win.setColor(Color.WHITE);
				win.setFont(new Font("Yu Gothic", Font.BOLD, 50));
				win.drawString("You have died :(", 175, 200);
				win.drawString("Wait until the next round", 75, 350);

			} else if (playingState == Comp.PLAYING_STATE_CAN_SABOTAGE) {
				win.setColor(new Color(0, 58, 76));
				win.fillRect(0, 0, 800, 600);

				win.setColor(Color.WHITE);
				win.setFont(new Font("Yu Gothic", Font.BOLD, 77));
				win.drawString("You can sabotage!", 40, 170);

				win.setFont(new Font("Yu Gothic", Font.BOLD, 30));
				win.drawString("Press the number of the player you want to sabotage.", 0, 220);
				win.drawString("Make sure it exists and isn't yours!", 130, 270);

			} else if (playingState == Comp.AWAITING_SABOTAGE_DELAY) {
				win.setColor(new Color(60, 0, 0));
				win.fillRect(0, 0, 800, 600);

				win.setColor(Color.WHITE);
				win.setFont(new Font("Yu Gothic", Font.BOLD, 58));
				win.drawString("You have been sabotaged!", 20, 200);

				win.setFont(new Font("Yu Gothic", Font.BOLD, 30));
				win.drawString("Your jumps are about to get delayed by 150 ms", 45, 300);

			} else if (playingState == Comp.AWAITING_SABOTAGE_OBSCURE) {
				win.setColor(new Color(0, 60, 0));
				win.fillRect(0, 0, 800, 600);

				win.setColor(Color.WHITE);
				win.setFont(new Font("Yu Gothic", Font.BOLD, 58));
				win.drawString("You have been sabotaged!", 20, 200);

				win.setFont(new Font("Yu Gothic", Font.BOLD, 30));
				win.drawString("Your screen is about to get obscured", 100, 300);

			} else if (playingState == Comp.AWAITING_SABOTAGE_REVERSE) {
				win.setColor(new Color(0, 0, 60));
				win.fillRect(0, 0, 800, 600);

				win.setColor(Color.WHITE);
				win.setFont(new Font("Yu Gothic", Font.BOLD, 58));
				win.drawString("You have been sabotaged!", 20, 200);

				win.setFont(new Font("Yu Gothic", Font.BOLD, 30));
				win.drawString("Your controls are about to get inverted", 100, 250);
				win.drawString("(down = jump)", 275, 300);

			} else if (playingState == Comp.RECEIVED_SABOTAGE_DELAY) {
				win.setColor(new Color(150, 0, 0));
				win.fillRect(0, 0, 800, 600);

				win.setColor(Color.WHITE);
				win.setFont(new Font("Yu Gothic", Font.BOLD, 58));
				win.drawString("You have been sabotaged!", 20, 200);

				win.setFont(new Font("Yu Gothic", Font.BOLD, 30));
				win.drawString("Your jumps are now delayed by 150 ms", 90, 300);

			} else if (playingState == Comp.RECEIVED_SABOTAGE_OBSCURE) {
				win.setColor(new Color(0, 150, 0));
				win.fillRect(0, 0, 800, 600);

				win.setColor(Color.WHITE);
				win.setFont(new Font("Yu Gothic", Font.BOLD, 58));
				win.drawString("You have been sabotaged!", 20, 200);

				win.setFont(new Font("Yu Gothic", Font.BOLD, 30));
				win.drawString("Your screen is now obscured", 165, 300);

			} else if (playingState == Comp.RECEIVED_SABOTAGE_REVERSE) {
				win.setColor(new Color(61, 64, 158));
				win.fillRect(0, 0, 800, 600);

				win.setColor(Color.WHITE);
				win.setFont(new Font("Yu Gothic", Font.BOLD, 58));
				win.drawString("You have been sabotaged!", 20, 200);

				win.setFont(new Font("Yu Gothic", Font.BOLD, 30));
				win.drawString("Your controls are now inverted!", 155, 250);
				win.drawString("(down = jump)", 277, 300);
			}

			// Only draw the placing counter while the client is alive
			if (playingState != Comp.PLAYING_STATE_DEAD) {
				placer.draw(win);
			}

			if (immobilized) {
				// TODO Draw some sort of immobilized flag over whatever state the client is in

			}

		} else if (gameState == STATE_WAITING_FOR_RESTART) {
			win.setColor(new Color(51, 2, 46));
			win.fillRect(0, 0, 800, 600);

			win.setColor(Color.WHITE);
			win.setColor(Color.WHITE);
			win.setFont(new Font("Yu Gothic", Font.BOLD, 100));
			win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			win.drawString("Game over!", 100, 200);

			win.setFont(new Font("Yu Gothic", Font.BOLD, 50));
			win.drawString("Press ENTER when you are", 55, 340);
			win.drawString("ready to restart", 200, 410);
		}
	}

	/**
	 * Processes incoming messages from server
	 * 
	 * @param message
	 */
	public synchronized void process(byte message) {

		switch (message) {
		case OutputMessage.START_GAME:
			setGameState(Comp.STATE_PLAYING);
			break;

		case OutputMessage.CAN_PLACE:
			placer.startTimer();
			
			driver.play(1);
			break;

		case OutputMessage.IMMOBILIZED:
			if (playingState != Comp.PLAYING_STATE_DEAD) {
				immobilized = true;
				
				//Plays "immobilized.wav"
				driver.play(4);
			}
			break;

		case OutputMessage.FREE:
			if (playingState != Comp.PLAYING_STATE_DEAD) {
				immobilized = false;
			}
			break;

		case OutputMessage.DIE:
			playingState = Comp.PLAYING_STATE_DEAD;
			
			//Playes "die.wav"
			driver.play(3);
			break;

		case OutputMessage.CAN_SABOTAGE:
			playingState = Comp.PLAYING_STATE_CAN_SABOTAGE;
			
			driver.play(2);
			break;

		case OutputMessage.WARNING_DELAY_JUMP:
			playingState = Comp.AWAITING_SABOTAGE_DELAY;
			
			driver.play(7);
			break;

		case OutputMessage.WARNING_OBSCURE:
			playingState = Comp.AWAITING_SABOTAGE_OBSCURE;
			
			driver.play(7);
			break;

		case OutputMessage.WARNING_REVERSE:
			playingState = Comp.AWAITING_SABOTAGE_REVERSE;
			
			driver.play(7);
			break;

		case OutputMessage.DELAY_JUMP:
			playingState = Comp.RECEIVED_SABOTAGE_DELAY;
			
			driver.play(0);
			break;

		case OutputMessage.OBSCURE:
			playingState = Comp.RECEIVED_SABOTAGE_OBSCURE;
			
			driver.play(0);
			break;

		case OutputMessage.REVERSE:
			playingState = Comp.RECEIVED_SABOTAGE_REVERSE;
			
			driver.play(0);
			break;

		case OutputMessage.RELEASE:
			playingState = Comp.PLAYING_STATE_NORMAL;
			break;

		case OutputMessage.DONT_PLACE:
			transmitMessage(new InputMessage((byte) (ID + 1), InputMessage.CAN_PLACE));
			break;

		case OutputMessage.RESET:
			this.gameState = STATE_WAITING_FOR_RESTART;
			break;

		default:
			System.out.println("Unknown message: " + message);
		}

		// Manually refreshes window as soon as an update comes from the server
		repaint();
	}

	/**
	 * Sends a string to server
	 * 
	 * @param message
	 */
	public void transmitString(String message) {
		cout.transmit(message);
	}

	/**
	 * Sends a byte command to server
	 * 
	 * @param inputMessage
	 */
	public void transmitMessage(InputMessage message) {
		cout.transmit(message);
	}

	/*
	 * Sends a message to the server when a key is pressed (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// Stores e.getKeyCode() ahead of time as a micro-optimization
		int keyCode = e.getKeyCode();

		if (gameState == STATE_PLAYING) {

			// When the keys are reversed, the player needs to press down instead of up to
			// jump
			int jumpCode = (playingState == Comp.RECEIVED_SABOTAGE_REVERSE) ? KeyEvent.VK_DOWN : KeyEvent.VK_UP;

			if (keyCode == jumpCode) {
				// Adds delay to the transmit if the delay is activated
				if (playingState == Comp.RECEIVED_SABOTAGE_DELAY) {

					Timer timer = new Timer();

					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							cout.transmit(new InputMessage(ID, InputMessage.JUMP));
							
							if(!immobilized) {
								//Plays "jump" sound effect
								driver.play(5);
							}
						}

					}, 150);

				} else {
					// Transmits the jump message as normal
					cout.transmit(new InputMessage(ID, InputMessage.JUMP));
					
					if(!immobilized) {
						//Plays "jump" sound effect
						driver.play(5);
					}
				}
			}

			if (playingState == Comp.PLAYING_STATE_CAN_SABOTAGE) {

				/*
				 * If the player presses something other than a number, a NumberFormatException
				 * will be thrown. When that happens, do nothing.
				 */

				try {
					byte sabotageID = Byte.parseByte(KeyEvent.getKeyText(keyCode));

					// Transmit as long as the client does not sabotage itself
					if (sabotageID != this.ID) {
						cout.transmit(new InputMessage(sabotageID, InputMessage.SABOTAGE));
						playingState = Comp.PLAYING_STATE_NORMAL;
					}
				} catch (NumberFormatException e1) {

				}

			}
		} else if (gameState == Comp.STATE_WAITING_FOR_RESTART) {
			
			if(keyCode == KeyEvent.VK_ENTER) {
				cout.transmit(new InputMessage(ID, InputMessage.READY_FOR_RESTART));
				gameState = Comp.STATE_WAITING_FOR_GAME;
				repaint();
			}
			
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {

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
	
	/**
	 * Plays a sound at the given index, specified by the input string at the beginning
	 * @param index
	 */
	public void playSound(int index) {
		driver.play(index);
	}

	public void setGameState(int gameState) {
		if (this.gameState != gameState) { // If gameState is changing out of current state, change keyListeners
			if (gameState == 1) {
				this.removeKeyListener(intro.getHostField());
				this.removeKeyListener(intro.getPortField());
				this.addKeyListener(nameScreen.getNameField());
				this.addKeyListener(nameScreen);

			} else if (gameState == STATE_PLAYING) {
				this.removeKeyListener(nameScreen.getNameField());
				this.removeKeyListener(nameScreen);

				placer = new BlockPlacer(this);
				this.addKeyListener(placer);
				this.playingState = Comp.PLAYING_STATE_NORMAL;
				this.immobilized = false;
			}

			// Refreshes window to reflect change in state
			repaint();
		}
		this.gameState = gameState;
	}

	public void setOutputSocket(ClientOutput cout) {
		this.cout = cout;
	}

}
