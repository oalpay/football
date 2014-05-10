package games.hebele.football.objects;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Move bodies according to inputs
 * @author osman
 *
 */
public abstract class InputMover extends Mover {


	@Override
	public Set<Direction> getDirections(){
		Set<Direction> directions = new HashSet<Direction>();
		if(Gdx.input.isKeyPressed(Keys.A)) {
			directions.add(Direction.LEFT);
		}
		if(Gdx.input.isKeyPressed(Keys.D)) {
			directions.add(Direction.RIGHT);
		}
		if(Gdx.input.isKeyPressed(Keys.W)) {
			directions.add(Direction.UP);
		}
		return directions;
	}
}
