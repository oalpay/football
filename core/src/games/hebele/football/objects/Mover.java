package games.hebele.football.objects;

import games.hebele.football.helpers.GameEvent;

import java.util.ArrayList;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Move physical objects bodies
 * 
 * @author osman
 * 
 */
public abstract class Mover implements Stepper {
	@Override
	public void step(float delta, ArrayList<GameEvent> events) {
		Vector2 vel = getBody().getLinearVelocity();
		Vector2 desiredVel = new Vector2(vel);
		Set<Direction> directions = this.getDirections();
		if (directions.contains(Direction.LEFT)
				&& directions.contains(Direction.RIGHT)) {
			desiredVel.x = 0;
		} else if (directions.contains(Direction.LEFT)) {
			desiredVel.x = -getLeftSpeed();
			movingLeft();
		} else if (directions.contains(Direction.RIGHT)) {
			desiredVel.x = getRightSpeed();
			movingRight();
		} else {
			desiredVel.x = 0;
		}
		if (directions.contains(Direction.UP) && this.canJump()) {
			desiredVel.y = getJumpSpeed();
		}
		Vector2 velChange = desiredVel.sub(vel);
		Vector2 impulse = velChange.scl(getBody().getMass());
		getBody().applyLinearImpulse(impulse, getBody().getWorldCenter(), true);
	}

	public abstract Body getBody();

	public abstract float getLeftSpeed();

	public abstract float getRightSpeed();

	public abstract float getJumpSpeed();

	
	public abstract boolean canJump();

	/**
	 * called at each step to decide which direction to move
	 * 
	 * @return
	 */
	public abstract Set<Direction> getDirections();

	/**
	 * called when moving toward right
	 */
	protected void movingRight() {

	}

	/**
	 * called when moving left
	 */
	protected void movingLeft() {

	}

}
