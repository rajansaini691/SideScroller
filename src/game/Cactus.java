package game;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Cactus extends Block {
	
	/**
	 * Picture of a cactus (png)
	 */
	private BufferedImage image;
	
	public Cactus(Player player) {
		super(player);
		this.color = new Color(14, 84, 0);
		rect.setSize(42, 75);
		
		image = player.getImages()[1];
		
	}

	@Override
	public void collideWithPlayer(Player p) {
		p.die();
		
	}

	@Override
	protected BufferedImage getImage() {
		return image;
	}

}
