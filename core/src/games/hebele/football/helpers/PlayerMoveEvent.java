package games.hebele.football.helpers;

import games.hebele.football.objects.Direction;

import java.util.Set;

public class PlayerMoveEvent implements GameEvent {
	public static String TYPE = "PlayerMoveEvent";
	private Set<Direction> direction;
	
	public PlayerMoveEvent(Set<Direction> direction ){
		this.direction = direction;
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
	public Set<Direction> getDirection(){
		return this.direction;
	}

}
