package games.hebele.football.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * animate sprite according to the velocity of the body
 * @author osman
 *
 */
public class SpriteWalkerReflex extends SpriteReflex{
	private Direction playerDirection = Direction.RIGHT;
	private Vector2 previousVelocity = new Vector2(0,0);
	private float currentAnimationState = 0;
	private Animation walkAnimation;

	public SpriteWalkerReflex(Body body, Sprite sprite, Animation walkAnimation) {
		super(body, sprite);
		this.walkAnimation = walkAnimation;
	}

	public void step(float delta) {
		currentAnimationState += delta;
		Vector2 currentVelocity = this.getBody().getLinearVelocity();
		if (currentVelocity.x != 0) {
			this.getSprite().setRegion( walkAnimation.getKeyFrame(currentAnimationState));
			if(currentVelocity.x > 0){
				playerDirection = Direction.RIGHT;
			}else{
				playerDirection = Direction.LEFT;
			}
		}
		if(playerDirection == Direction.RIGHT){
			this.getSprite().setFlip(false, false);
		}else{
			this.getSprite().setFlip(true, false);
		}
		previousVelocity.set(currentVelocity);
		super.step(delta);
	}
}
