package games.hebele.football.helpers;

public class GameController {
	
	private static GameState currentState = GameState.GAME_RUNNING;
	
	public enum GameState{
		GAME_RUNNING,
		GAME_PAUSED,
		GAME_OVER,
		GAME_WIN,
		GAME_IDLE
	}
	
	public static void setGameState(GameState state){
		currentState=state;
	}
	
	public static GameState getGameState(){
		return currentState;
	}
	
	public static boolean isGameRunning(){
		return currentState==GameState.GAME_RUNNING;
	}

}
