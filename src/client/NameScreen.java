package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class NameScreen implements KeyListener {
	
	private Comp comp;
	private InputText nameField;
	
	public NameScreen(Comp comp) {
		this.comp = comp;
		
		nameField = new InputText(20, 20, 400, 50, comp);
		nameField.select();
	}
	
	public void draw(Graphics2D win) {
		win.setColor(Color.BLACK);
		win.fillRect(0, 0, 800, 600);
		
		nameField.draw(win);
		
		win.setColor(Color.WHITE);
		win.setFont(new Font("Century Gothic", Font.BOLD, 60));
		win.drawString("How to play: ", 30, 140);
		
		win.setFont(new Font("Century Gothic", Font.PLAIN, 30));
		win.drawString("When your timer starts counting down, press", 30, 200);
		win.drawString("SPACEBAR. This pushes a plant from every player's", 30, 240);
		win.drawString("pipe. If it goes to zero, you die!", 30, 280);
		win.drawString("Press UP to jump.", 30, 350);
		win.drawString("Cacti kill, thorns poison, and grasses immobilize.", 30, 410);
		win.drawString("Flowers let you sabotage a player (by client #).", 30, 470);
		win.drawString("Getting sabotaged gives you points!", 30, 530);
		
		win.drawString("<--- Type name &", 430, 50);
		win.drawString("press ENTER", 490, 90);
	}
	
	public InputText getNameField() {
		return nameField;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			String name = nameField.getMessage();
			
			if(name.length() == 0) name = " ";
			
			comp.transmitString(name);
			comp.setGameState(2);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
}
