package games.hebele.football.objects;

import games.hebele.football.Variables;
import games.hebele.football.helpers.Assets;
import games.hebele.football.helpers.BasicGameEvent;
import games.hebele.football.helpers.GameEvent;
import games.hebele.football.helpers.GameEventManager;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

public class Player extends InputMover implements Actress {

	private World world;
	private Body playerBody;
	private Fixture playerFixture, playerSensorFixture;
	private Sprite sprite;
	private Ball playerBall;
	private Animation playerAnimation;

	private float width, height;

	private SpriteReflex reflex;
	private RopeJoint leftRope;
	private RopeJoint rightRope;
	private boolean hasBall = true;
	private GameEventManager eventManager;

	public Player(World world, Ball ball, GameEventManager eventManager) {
		this.world = world;
		this.sprite = new Sprite();
		this.eventManager = eventManager;

		playerBall = ball;

		height = 1;
		width = 1;

		initPlayerBody();

		// LOAD ASSETS-----------------------------
		TextureAtlas textureAtlas = Assets.manager.get(Assets.footballPack,
				TextureAtlas.class);
		// BACKGROUND IMAGE
		playerAnimation = new Animation(0.2f,
				textureAtlas.findRegions("male_runner_1"));
		playerAnimation.setPlayMode(PlayMode.LOOP);
		this.sprite.setRegion(playerAnimation.getKeyFrame(0.2f));
		this.sprite.setSize(width, height * 1.5f);
		// texturebg = textureAtlas.findRegion("bg1");

		this.sprite.setFlip(true, false);
		// flip(true,false);

		this.reflex = new SpriteWalkerReflex(playerBody, this.sprite,
				playerAnimation);
	}

	public void initPlayerBody() {
		// PLAYER SHAPE
		// body def
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(2, 4);

		// ball shape
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 4, height / 2);

		// fixture def
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1f;
		fixtureDef.friction = 0.3f;
		fixtureDef.restitution = 0f;
		fixtureDef.filter.categoryBits = Variables.PLAYER_CATEGOTY;

		playerBody = this.world.createBody(bodyDef);
		playerFixture = playerBody.createFixture(fixtureDef);
		// playerBody.createFixture(fixtureDef).setUserData("player");

		shape.dispose();

		CircleShape circle = new CircleShape();
		circle.setRadius(width / 2.1f);
		circle.setPosition(new Vector2(0, -height / 4));
		// fixture def
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		// fixtureDef.density=.5f;
		// fixtureDef.friction=0.3f;
		// fixtureDef.restitution=0f;

		playerSensorFixture = playerBody.createFixture(fixtureDef);
		playerSensorFixture.setUserData("playerSensor");
		playerSensorFixture.setSensor(true);

		circle.dispose();

		playerFixture.setUserData("player");
		playerBody.setBullet(false);
		playerBody.setFixedRotation(true);
		playerBody.setUserData(this);

		// -----PLAYER SHAPE

		initPlayerBaseFixture();
		createRopeJoint();
	}

	public void initPlayerBaseFixture() {
		FixtureDef cageBottomDef = new FixtureDef();
		PolygonShape cageBottomShape = new PolygonShape();
		float[] verticesBottom = { 1.2f, -0.7f, -1.2f, -0.7f, -1.2f, -0.8f };
		cageBottomShape.set(verticesBottom);
		cageBottomDef.shape = cageBottomShape;
		cageBottomDef.restitution = 0f;
		cageBottomDef.friction = 1f;
		cageBottomDef.filter.categoryBits = Variables.CAGE_CATEGOTY;
		cageBottomDef.density = 0f;
		Fixture ballCageBottomFixture = playerBody.createFixture(cageBottomDef);
		ballCageBottomFixture.setUserData("cage");
	}

	public void createRopeJoint() {
		RopeJointDef ropeLeftDef = new RopeJointDef();
		ropeLeftDef.bodyA = playerBody;
		ropeLeftDef.bodyB = playerBall.getBody();
		ropeLeftDef.localAnchorA.set(-1, -height / 2);
		ropeLeftDef.localAnchorB.set(0, 0);
		ropeLeftDef.collideConnected = true;
		ropeLeftDef.maxLength = 2f;
		leftRope = (RopeJoint) world.createJoint(ropeLeftDef);

		RopeJointDef ropeRightDef = new RopeJointDef();
		ropeRightDef.bodyA = playerBody;
		ropeRightDef.bodyB = playerBall.getBody();
		ropeRightDef.localAnchorA.set(1, -height / 2);
		ropeRightDef.localAnchorB.set(0, 0);
		ropeRightDef.collideConnected = true;
		ropeRightDef.maxLength = 2f;
		rightRope = (RopeJoint) world.createJoint(ropeRightDef);
	}

	public Body getBody() {
		return playerBody;
	}

	public Ball getBall() {
		return playerBall;
	}

	public Body getBallBody() {
		return playerBall.getBody();
	}

	public void kickBall(float xPercent, float yPercent) {
		if (((playerBall.getBody().getPosition().x > playerBody.getPosition().x && xPercent > 0) || (playerBall
				.getBody().getPosition().x < playerBody.getPosition().x && xPercent < 0))) {
			playerBall.setKicked();
			hasBall = false;
			leftRope.setMaxLength(100f);
			rightRope.setMaxLength(100f);
			
			float kickForceX = Variables.playerKickPower * xPercent;
			float kickForceY = Variables.playerKickPower * yPercent;
			Vector2 ballCenter = playerBall.getBody().getWorldCenter();
			playerBall.getBody().applyLinearImpulse(kickForceX, kickForceY,
					ballCenter.x, ballCenter.y, true);
			eventManager.notify(new BasicGameEvent(BasicGameEvent.BALL_KICKED));
		}
	}

	public void prepareForPickingBall() {
		if (hasBall) {
			return;
		}
		hasBall = true;
		playerBall.picked();
		leftRope.setMaxLength(0.6f);
		rightRope.setMaxLength(0.6f);
	}

	public float getX() {
		return this.sprite.getX();
	}

	public float getY() {
		return this.sprite.getY();
	}

	@Override
	public float getLeftSpeed() {
		return Variables.playerSpeed;
	}

	@Override
	public float getRightSpeed() {
		return Variables.playerSpeed;
	}

	@Override
	public float getJumpSpeed() {
		return Variables.playerJumpSpeed;
	}

	@Override
	public boolean canJump() {
		// check if touching ground
		for (Contact contact : world.getContactList()) {
			if (contact.getFixtureA().getUserData().equals("player")
					&& contact.getFixtureB().getUserData().equals("ground")) {
				return contact.getFixtureB().getBody().getPosition().y <= contact
						.getFixtureB().getBody().getPosition().y;
			} else if (contact.getFixtureB().getUserData().equals("player")
					&& contact.getFixtureA().getUserData().equals("ground")) {
				return contact.getFixtureA().getBody().getPosition().y <= contact
						.getFixtureB().getBody().getPosition().y;
			}
		}
		return false;
	}

	@Override
	public void movingLeft() {
		if (hasBall) {
			leftRope.setMaxLength(0.5f);
			rightRope.setMaxLength(2f);
		}
		super.movingLeft();
	}

	@Override
	public void movingRight() {
		if (hasBall) {
			leftRope.setMaxLength(2f);
			rightRope.setMaxLength(0.5f);
		}
		super.movingRight();
	}

	@Override
	public void draw(Batch batch) {
		this.sprite.draw(batch);
	}

	@Override
	public void step(float delta, ArrayList<GameEvent> events) {
		super.step(delta, events);// mover
		reflex.step(delta, events);
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public void destroy() {
		this.world.destroyBody(playerBody);
	}

}
