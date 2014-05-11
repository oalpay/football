package games.hebele.football.helpers;

public class BasicGameEvent implements GameEvent{
	public static String BALL_KICKED = "BALL_KICKED";
	
	private String type;

	public BasicGameEvent(String type){
		this.type = type;
	}

	@Override
	public String getType() {
		return type;
	}

}
