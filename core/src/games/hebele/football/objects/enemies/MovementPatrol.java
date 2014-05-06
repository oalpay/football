package games.hebele.football.objects.enemies;

import games.hebele.football.objects.enemies.Enemy.ENEMY_STATE;



public class MovementPatrol implements BehaviourMovement {

	private Enemy enemy;
	
	public MovementPatrol(Enemy e){
		enemy=e;
		enemy.initDefaultBehaviour(ENEMY_STATE.PATROL);
	}
	
	@Override
	public void stateFollow() {

		float speedX = enemy.getSpeedX();
		float posX = enemy.getPosition().x;
		float playerX = enemy.getPlayerX();
		
		if(speedX<=0 && posX <= playerX) enemy.walkRight();
		else if(speedX>0 && posX >= playerX) enemy.walkLeft();
		
	}

	@Override
	public void statePatrol() {
		
		float speedX = enemy.getSpeedX();
		float posX = enemy.getPosition().x;
		float targetLeftX  = enemy.getTargetLeft().x;
		float targetRightX = enemy.getTargetRight().x;
		float moveGap=0.3f;
		
		if(speedX<=0 && posX <= targetLeftX + moveGap) enemy.walkRight();
		else if(speedX>0 && posX >= targetRightX - moveGap) enemy.walkLeft();
		
	}

	//PATROLLING ENEMY NEVER RETURNS HOME
	@Override
	public void stateReturnHome() {}

	
	//CONTINUE PATROLLING AFTER CALMING DOWN 
	@Override
	public void stateCalmDown() {
		enemy.setState(ENEMY_STATE.PATROL);
	}
	
}