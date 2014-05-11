package games.hebele.football.objects;

import games.hebele.football.helpers.GameEvent;

import java.util.ArrayList;

public interface Stepper {
	void step(float delta,ArrayList<GameEvent> events);
}
