package games.hebele.football.objects.enemies;

import games.hebele.football.objects.Player;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class EnemyPatrolling extends Enemy {

	public EnemyPatrolling(World world, Stage stage, Player player, float posX, float posY) {
		super(world, stage, player, posX, posY);
		
		initDefaultBehaviour(ENEMY_STATE.PATROL);
	}
	
	@Override
	public void calmDown() {
		super.calmDown();
		resetBehaviour();
	}

}