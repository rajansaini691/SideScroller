package game;

import java.awt.Color;

public class Grass extends Block {

	public Grass(Player player) {
		super(player);
		this.color = Color.GREEN;
		
	}

	@Override
	public void collideWithPlayer(Player p) {
		p.getRunner().immobilize(100);
		p.raiseScore(150);
	}

}
