package game;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Flower extends Block {

	/**
	 * Points to image of a flower
	 */
	private BufferedImage image;
	
	public Flower(Player player) {
		super(player);
		this.color = Color.pink;
		this.height = 50;
		this.width = 50;
		image = player.getImages()[2];
	}

	@Override
	public void collideWithPlayer(Player p) {
		p.allowSabotage();
		p.raiseScore(75);
	}

	@Override
	protected BufferedImage getImage() {
		return image;
	}

}
