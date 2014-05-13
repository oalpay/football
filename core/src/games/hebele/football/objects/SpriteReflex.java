package games.hebele.football.objects;

import games.hebele.football.helpers.GameEvent;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * position sprite according to physical position of the body
 * 
 * @author osman
 * 
 */
public class SpriteReflex implements Stepper {
	private Body body;
	private Sprite sprite;

	public SpriteReflex(Body body, Sprite sprite) {
		this.body = body;
		this.sprite = sprite;
	}

	public void step(float delta,ArrayList<GameEvent> events) {
		Vector2 bodyPosition = body.getPosition();
		this.sprite.setPosition(bodyPosition.x - this.sprite.getWidth() / 2,
				bodyPosition.y - this.sprite.getHeight() / 2);
		this.sprite.setRotation(body.getAngle() * MathUtils.radDeg);
	}

	public Body getBody() {
		return body;
	}

	public Sprite getSprite() {
		return sprite;
	}

}
