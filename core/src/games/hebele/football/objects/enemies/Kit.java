package games.hebele.football.objects.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import games.hebele.football.objects.Player;

public class Kit extends Enemy {

	public Kit(World world, Stage stage, Player player, float posX, float posY) {
		super(world, stage, player, posX, posY);
		
		//SET ENEMY ANIMATION (FROM TEXTURE ATLAS)
		setEnemyAnimation("kit");
		
		//CHANGES THE IMAGE SIZE (NOT THE BOX2D OBJECT)
		setSize(getWidth(), getHeight()*1.3f);
		
		//SET THE POSSIBLE SPEECH OF ENEMY
		textAlternatives.add("KÝT");
		textAlternatives.add("Oðlum oynamasanýza burada!");
		textAlternatives.add("Keserim topunu!");
		
		//SET THE ENEMY MOVEMENT BEHAVIOUR
		setBehaviourMovement(new MovementStand(this));
	}

}
