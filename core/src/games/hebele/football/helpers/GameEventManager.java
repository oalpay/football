package games.hebele.football.helpers;

import java.util.ArrayList;

public class GameEventManager {
	private ArrayList<GameEvent> events = new ArrayList<GameEvent>();
	
	public void notify(GameEvent event){
		events.add(event);
	}
	
	public ArrayList<GameEvent> getAndClean(){
		ArrayList<GameEvent> pastEvents = new ArrayList<GameEvent>(events);
		events.clear();
		return pastEvents;
	}
	
}
