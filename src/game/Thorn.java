package game;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Thorn extends Block {

	/**
	 * Image to be added
	 */
	BufferedImage image;
	
	/**
	 * Creates a new thorn object
	 * @param player
	 */
	public Thorn(Player player) {
		super(player);
		this.color = new Color(140, 115, 42);
		this.image = player.getImages()[3];
		this.height = image.getHeight();
		this.width = image.getWidth();
		this.SPEED += 3;
	}

	@Override
	public void collideWithPlayer(Player p) {
		p.poison();
		p.raiseScore(150);
	}

	@Override
	protected BufferedImage getImage() {
		return image;
	}
	
}
