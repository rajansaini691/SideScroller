package client;

import java.awt.Color;
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
	}
	
	public InputText getNameField() {
		return nameField;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			String name = nameField.getMessage();
			
			if(name.length() == 0) name += " ";
			
			comp.transmitString(nameField.getMessage());
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
