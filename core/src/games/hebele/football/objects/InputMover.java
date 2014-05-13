package games.hebele.football.objects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

/**
 * Move bodies according to inputs
 * 
 * @author osman
 * 
 */
public abstract class InputMover extends Mover {
	private Set<Direction> hudDirections = new HashSet<Direction>();
	private static Set<Direction> horizontalSet = new HashSet<Direction>(
			Arrays.asList(Direction.LEFT, Direction.RIGHT));
	private static Set<Direction> verticalSet = new HashSet<Direction>(
			Arrays.asList(Direction.UP));

	@Override
	public Set<Direction> getDirections() {
		Set<Direction> keyboard = checkKeyboard();
		if (!keyboard.isEmpty()) {
			return keyboard;
		} else {
			return hudDirections;
		}
	}

	public Set<Direction> checkKeyboard() {
		Set<Direction> keyboardDirections = new HashSet<Direction>();
		if (Gdx.input.isKeyPressed(Keys.A)) {
			keyboardDirections.add(Direction.LEFT);
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			keyboardDirections.add(Direction.RIGHT);
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			keyboardDirections.add(Direction.UP);
		}
		return keyboardDirections;
	}

	public void keepMovingLeft() {
		hudDirections.remove(Direction.RIGHT);
		hudDirections.add(Direction.LEFT);
	}

	public void keepMovingRight() {
		hudDirections.remove(Direction.LEFT);
		hudDirections.add(Direction.RIGHT);
	}

	public void keepMovingUp() {
		hudDirections.add(Direction.UP);
	}

	public void stopMovingHorizontal() {
		hudDirections.removeAll(horizontalSet);
	}

	public void stopMovingVertical() {
		hudDirections.removeAll(verticalSet);
	}
}
