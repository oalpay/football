package games.hebele.football.objects;

import java.util.ArrayList;

import games.hebele.football.Variables;
import games.hebele.football.helpers.Assets;
import games.hebele.football.helpers.GameEvent;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Ball extends Sprite implements Actress {
	
	public enum State{
		PICKED, KICKED
	}

	private Body body;
	private Fixture fixture;
	private TextureAtlas textureAtlas;
	private boolean isKicked = false;
	private SpriteReflex reflex;
	private World world;

	public Ball(World world) {
		this.world = world;
		float size = 0.5f;

		setSize(size, size);
		setOrigin(size / 2, size / 2);

		// BALL SHAPE
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(2.5f, 5);

		CircleShape circle = new CircleShape();
		circle.setRadius(size / 2);

		// fixture def
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.3f;
		fixtureDef.friction = 0.8f;
		fixtureDef.restitution = 0.4f;
		fixtureDef.filter.maskBits &= ~Variables.PLAYER_CATEGOTY;

		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
		fixture.setUserData("ball");
		body.setBullet(true);

		circle.dispose();

		textureAtlas = Assets.manager.get(Assets.footballPack,
				TextureAtlas.class);
		setRegion(textureAtlas.findRegion("ball"));

		this.reflex = new SpriteReflex(body, this);
	}

	public Body getBody() {
		return body;
	}

	public void setKicked() {
		isKicked = true;
		Filter old = fixture.getFilterData();
		old.maskBits &= ~Variables.CAGE_CATEGOTY;
		fixture.setFilterData(old);
	}

	public void picked() {
		Filter old = fixture.getFilterData();
		old.maskBits |= Variables.CAGE_CATEGOTY;
		fixture.setFilterData(old);
	}

	public void hold() {
		isKicked = false;
	}

	public boolean isKicked() {
		return isKicked;
	}

	@Override
	public void step(float delta, ArrayList<GameEvent> events) {
		reflex.step(delta, events);
	}

	@Override
	public void draw(Batch batch) {
		super.draw(batch);
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public void destroy() {
		this.world.destroyBody(body);
	}
}
