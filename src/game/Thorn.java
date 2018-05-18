package game;

import java.awt.Color;

public class Thorn extends Block {

	public Thorn(Player player) {
		super(player);
		this.color = new Color(140, 115, 42);
	}

	@Override
	public void collideWithPlayer(Player p) {
		p.poison();
	}
	
}
