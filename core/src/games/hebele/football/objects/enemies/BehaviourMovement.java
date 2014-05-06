package games.hebele.football.objects.enemies;

public interface BehaviourMovement {
	public void stateFollow();
	public void statePatrol();
	public void stateReturnHome();
	public void stateCalmDown();
}


