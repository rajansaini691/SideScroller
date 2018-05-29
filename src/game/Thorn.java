package game;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Thorn extends Block {

	public Thorn(Player player) {
		super(player);
		this.color = new Color(140, 115, 42);
	}

	@Override
	public void collideWithPlayer(Player p) {
		p.poison();
		p.raiseScore(150);
	}

	@Override
	protected BufferedImage getImage() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
