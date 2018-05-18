package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Creates a text field usable in swing
 * @author rajans1900
 *
 */
public class InputText implements KeyListener {
	
	/**
	 * Formatting variables
	 */
	private int x, y, width, height;
	private int fontSize = 20;
	private Font font = new Font("Yu Gothic", Font.PLAIN, fontSize);
	private Color textColor;
	private Color backgroundSelectedColor;
	private Color backgroundColor;
	private Color borderColor;
	
	/**
	 * Stores the message typed into the field
	 */
	private String message = "";
	
	/*
	 * True whenever the enter key is pressed
	 */
	private boolean entered = false;
	
	/*
	 * Only outputs to screen when the user actually selects it
	 */
	private boolean selected = false;
	
	/**
	 * Makes a new input text field with given width and height
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public InputText(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		textColor = Color.WHITE;
		backgroundSelectedColor = Color.GRAY;
		backgroundColor = Color.BLACK;
		borderColor = Color.WHITE;
	}
	
	/**
	 * Renders the text field
	 * @param win
	 */
	public void draw(Graphics2D win) {
		//Draws fill
		win.setColor((selected)? backgroundSelectedColor : backgroundColor);
		win.fillRect(x, y, width, height);
		
		//Draws border
		win.setColor(borderColor);
		win.drawRect(x, y, width, height);
		
		win.setColor(textColor);
		win.setFont(font);
		win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		win.drawString(message, x + 20, y + height/2 + fontSize / 2);
	}
	
	/*
	 * Waits for the user to finish typing the message and then returns it
	 * when the user is finished
	 */
	public String getMessage() {
		return message;
	}
	
	/*
	 * Once the user presses enter, this becomes true, as they are finished passing input
	 */
	public boolean isFinished() {
		return selected && entered;
	}
	
	/*
	 * Allows the user to type text
	 */
	public void select() {
		selected = true;
	}
	
	/*
	 * Stops the user from entering text
	 */
	public void deselect() {
		selected = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		if(keyCode == KeyEvent.VK_ENTER && !entered && selected) {
			entered = true;
		}
		
		if(!entered && selected) {
			
			//Records numbers
			if(keyCode - 48 >= 0 && keyCode - 48 <= 10) {
				message += keyCode - 48;
			}
			
			//Records the period (needed for IP addresses)
			if(keyCode == '.') {
				message += ".";
			}
			
			//Records letters
			if(keyCode > 64 && keyCode < 91) {
				message += Character.toString((char) keyCode);
			}
			
			//Lets the user delete the last character when backspacing
			if(keyCode == KeyEvent.VK_BACK_SPACE && message.length() > 0) {
				message = message.substring(0, message.length() - 1);
			}
			
			//Auto fills - TODO Get rid of these when finalizing
			if(keyCode == '[') {
				message = "2222";
			}
			if(keyCode == ']') {
				message = "10.30.141.34";			//My LAN IP at Beckman
			}
			if(keyCode == '\\') {
				message = "192.168.1.232"; 			//My LAN IP at home
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			entered = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
