package game;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Flower extends Block {

	public Flower(Player player) {
		super(player);
		this.color = Color.pink;
	}

	@Override
	public void collideWithPlayer(Player p) {
		p.allowSabotage();
		p.raiseScore(75);
	}

	@Override
	protected BufferedImage getImage() {
		// TODO Auto-generated method stub
		return null;
	}

}
