package games.hebele.football.helpers;

public class GameController {
	
	private static GameState currentState = GameState.GAME_RUNNING;
	
	public enum GameState{
		GAME_RUNNING,
		GAME_PAUSED,
		GAME_OVER,
		GAME_WON,
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
	
	public static boolean isGameOver(){
		return currentState==GameState.GAME_OVER;
	}
	
	public static boolean isGameWon(){
		return currentState==GameState.GAME_WON;
	}
	
	public static boolean isGamePaused(){
		return currentState==GameState.GAME_PAUSED;
	}
	
	public static void GameOver(){
		currentState=GameState.GAME_OVER;
	}
	
	public static void GameWon(){
		currentState=GameState.GAME_WON;
	}
	
	public static void resetGame(){
		currentState=GameState.GAME_RUNNING;
	}
}
