package game;

/**
 * Block used for testing; will not be used in final game
 * @author Rajan Saini
 *
 */
public class TestBlock extends Block {

	public TestBlock(Player player) {
		super(player);
		rect.setSize(50, 30);
	}

	@Override
	public void collideWithPlayer(Player p) {
		//Do nothing when you collide; this is just to test
		
	}

}
