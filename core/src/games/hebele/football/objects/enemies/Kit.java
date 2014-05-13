package games.hebele.football.objects.enemies;

import games.hebele.football.objects.Player;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Kit extends Enemy {

	public Kit(World world, Stage stage, Player player, float posX, float posY) {
		super(world, stage, player, posX, posY);

		// SET ENEMY ANIMATION (FROM TEXTURE ATLAS)
		setEnemyAnimation("kit");

		// CHANGES THE IMAGE SIZE (NOT THE BOX2D OBJECT)
		setSize(getWidth(), getHeight() * 1.3f);

		// SET THE POSSIBLE SPEECH OF ENEMY
		textAlternatives.add("K�T");
		textAlternatives.add("O�lum oynamasan�za burada!");
		textAlternatives.add("Keserim topunu!");

		// SET THE ENEMY MOVEMENT BEHAVIOUR
		setBehaviourMovement(new MovementStand(this));
	}

}
