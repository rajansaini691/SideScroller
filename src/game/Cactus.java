package game;

import java.awt.Color;

public class Cactus extends Block {

	public Cactus(Player player) {
		super(player);
		this.color = new Color(14, 84, 0);
		rect.setSize(30, 50);
	}

	@Override
	public void collideWithPlayer(Player p) {
		p.die();
		
	}

}
