package games.hebele.football.objects.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import games.hebele.football.objects.Player;

public class Kit extends EnemyStanding {

	public Kit(World world, Stage stage, Player player, float posX, float posY) {
		super(world, stage, player, posX, posY);
		
		enemyAnimation = new Animation(0.1f, textureAtlas.findRegions("kit"));
		enemyAnimation.setPlayMode(PlayMode.LOOP);
		setRegion(enemyAnimation.getKeyFrame(0.2f));
		
		setSize(getWidth(), getHeight()*1.3f);
		
		
		textAlternatives.add("KÝT");
		textAlternatives.add("Oðlum oynamasanýza burada!");
		textAlternatives.add("Keserim topunu!");
	}

}
