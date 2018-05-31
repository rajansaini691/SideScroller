package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class IntroScreen {

	//Contains two text fields detailing the port and host
	private InputText inputPort, inputHost;
	
	//Holds networking variables
	private String host;
	private int port;
	
	//Stores the Comp object so that it can change its game state
	private Comp comp;
	
	public IntroScreen(Comp comp) {
		inputPort = new InputText(130, 500, 200, 50, comp);
		inputPort.select();
		inputHost = new InputText(510, 500, 200, 50, comp);
		
		this.comp = comp;
		
	}
	
	public void draw(Graphics2D win) {
		//Draws background
		win.setColor(Color.BLACK);
		win.fillRect(0, 0, 800, 600);
		
		win.setColor(Color.WHITE);
		win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//Draws Welcome!
		win.setFont(new Font("Yu Gothic", Font.BOLD, 80));
		win.drawString("Welcome!", 200, 100);
		
		//Draws "Please enter in the port and host. Press enter to move on to the next field"
		win.setFont(new Font("Century Gothic", Font.PLAIN, 35));
		win.drawString("Please type in the port and host.", 100, 400);
		win.drawString("Press ENTER to move on to the next field.", 40, 450);
		
		win.setFont(new Font("Lucida Handwriting", Font.ITALIC, 20));
		win.setColor(new Color(114, 181, 101));
		win.drawString("Plants are deceptive. You see them there looking as if", 90, 180);
		win.drawString("once rooted they know their places; not like animals, like us", 50, 230);
		win.drawString("always running around, leaving traces.", 150, 280);
		
		//Initializes drawing text
		win.setFont(new Font("Yu Gothic", Font.PLAIN, 20));
		win.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		win.setColor(Color.WHITE);
		
		//Draws text fields and labels
		win.drawString("Port: ", 50, 533);
		inputPort.draw(win);
		
		win.drawString("Host: ", 420, 533);
		inputHost.draw(win);
		
		//Switches to modifying IP address
		if(inputPort.isFinished()) {
			inputPort.deselect();
			inputHost.select();
		}
		
		//Enters waiting state and sets port and host
		if(inputHost.isFinished()) {
			inputHost.deselect();
			try {
				port = Integer.parseInt(inputPort.getMessage());
			} catch(NumberFormatException e) {
				port = 0;
			}
			host = inputHost.getMessage();
			
			comp.setGameState(1);
			
		}

	}
	
	
	public InputText getPortField() {
		return inputPort;
	}
	
	public InputText getHostField() {
		return inputHost;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
}
