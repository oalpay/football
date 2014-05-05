package games.hebele.football.objects.enemies;

import games.hebele.football.objects.Player;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class EnemyStanding extends Enemy {

	public EnemyStanding(World world, Stage stage, Player player, float posX, float posY) {
		super(world, stage, player, posX, posY);
		
		initDefaultBehaviour(ENEMY_STATE.IDLE);
	}
	
	@Override
	public void calmDown() {
		stopTalking();
		setState(ENEMY_STATE.RETURNHOME);
	}

}
