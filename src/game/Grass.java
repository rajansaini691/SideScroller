package game;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Grass extends Block {

	private BufferedImage image;
	
	public Grass(Player player) {
		super(player);
		this.color = Color.GREEN;
		this.image = player.getImages()[4];
		this.height = image.getHeight();
		this.width = image.getWidth();
		this.SPEED -= 3;
	}

	@Override
	public void collideWithPlayer(Player p) {
		p.getRunner().immobilize(100);
		p.raiseScore(150);
	}

	@Override
	protected BufferedImage getImage() {
		return image;
	}

}
