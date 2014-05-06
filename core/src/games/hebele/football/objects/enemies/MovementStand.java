package games.hebele.football.objects.enemies;

import games.hebele.football.objects.enemies.Enemy.ENEMY_STATE;

public class MovementStand implements BehaviourMovement {
	
	private Enemy enemy;
	
	public MovementStand(Enemy e){
		enemy=e;
		enemy.initDefaultBehaviour(ENEMY_STATE.IDLE);
	}
	
	@Override
	public void stateFollow() {
		
		float speedX = enemy.getSpeedX();
		float posX = enemy.getPosition().x;
		float playerX = enemy.getPlayerX();
		
		if(speedX<=0 && posX <= playerX) enemy.walkRight();
		else if(speedX>=0 && posX >= playerX) enemy.walkLeft();
		
	}

	@Override
	public void statePatrol() {}

	@Override
	public void stateReturnHome() {
		
		float speedX = enemy.getSpeedX();
		float posX = enemy.getPosition().x;
		float homeX = enemy.getHome().x;
		
		float moveGap=0.3f;
		
		//STOP ONCE YOU ARRIVE HOME
		if(Math.abs(posX - homeX) < moveGap){
			enemy.setState(ENEMY_STATE.IDLE);
			enemy.stopVelocity();
			return;
		}
		
		if(speedX<=0 && posX <= homeX + moveGap) enemy.walkRight();
		else if(speedX>0 && posX >= homeX - moveGap) enemy.walkLeft();
	}

	//RETURN TO HOME WHEN CALMING DOWN
	@Override
	public void stateCalmDown() {
		enemy.setState(ENEMY_STATE.RETURNHOME);
	}
	
}