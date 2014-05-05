package games.hebele.football.objects.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import games.hebele.football.objects.Player;

public class Gnu extends EnemyPatrolling {

	public Gnu(World world, Stage stage, Player player, float posX, float posY) {
		super(world, stage, player, posX, posY);

		enemyAnimation = new Animation(0.1f, textureAtlas.findRegions("gnu"));
		enemyAnimation.setPlayMode(PlayMode.LOOP);
		setRegion(enemyAnimation.getKeyFrame(0.2f));
		
		setSize(getWidth(), getHeight()*1.3f);
		
		textAlternatives.add("GNU");
		textAlternatives.add("At abinin kýllý göðsüne!");
	}

}
