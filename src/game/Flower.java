package game;

import java.awt.Color;

public class Flower extends Block {

	public Flower(Player player) {
		super(player);
		this.color = Color.pink;
	}

	@Override
	public void collideWithPlayer(Player p) {
		p.allowSabotage();
	}

}
