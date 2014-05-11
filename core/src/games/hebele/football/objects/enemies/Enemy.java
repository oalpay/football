package games.hebele.football.objects.enemies;

import java.util.ArrayList;

import games.hebele.football.Variables;
import games.hebele.football.helpers.Assets;
import games.hebele.football.helpers.GameEvent;
import games.hebele.football.objects.Actress;
import games.hebele.football.objects.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class Enemy extends Sprite implements Actress {

	private World world;
	private Player player;

	private Stage stage;

	private Body body;
	private Fixture fixture;

	private Skin skin;
	public TextureAtlas textureAtlas;
	public Animation enemyAnimation;

	private BehaviourMovement IMovement;

	public Array<String> textAlternatives = new Array<String>();
	private Label textBubble, debugState;
	private boolean debugStateLabel = true; // SHOW STATE LABELS UNDER ENEMIES

	private int distanceToPlayer;

	private float height, width;

	private boolean isDead = false;

	private Vector2 targetHome, targetLeft, targetRight;

	private float moveSpeed = 2f;
	private float curSpeed;

	public enum ENEMY_STATE {
		IDLE, PATROL, FOLLOW, ATTACK, RETURNHOME
	}

	private ENEMY_STATE defaultBehaviour;
	private ENEMY_STATE currentState;
	private float runTime;

	public Enemy(World world, Stage stage, Player player, float posX, float posY) {
		this.world = world;
		this.stage = stage;
		this.player = player;

		height = 1;
		width = 1;

		curSpeed = moveSpeed;

		setSize(width, height);

		// GET THE TEXTURE ATLAS OF ALL ENEMIES
		// THE RELEVANT REGIONS IS SET AT THE SUB ENEMY CLASS
		textureAtlas = Assets.manager.get(Assets.enemyPack, TextureAtlas.class);
		skin = Assets.manager.get(Assets.skin, Skin.class);

		// PLAYER SHAPE
		// body def
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(posX, posY);

		// ball shape
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2, height / 2);

		// fixture def
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = .5f;
		fixtureDef.friction = 0.3f;
		fixtureDef.restitution = 0f;
		fixtureDef.filter.maskBits &= ~Variables.CAGE_CATEGOTY;

		body = this.world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);

		shape.dispose();

		// fixture def
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = .5f;
		fixtureDef.friction = 0.3f;
		fixtureDef.restitution = 0f;

		fixture.setUserData("enemy");
		body.setFixedRotation(true);
		body.setUserData(this);

		initTextBubble();
		if (debugStateLabel)
			initDebugStateLabel();
	}

	public void resetBehaviour() {
		currentState = defaultBehaviour;
	}

	public void calmDown() {
		stopTalking();
		IMovement.stateCalmDown();
	}

	public void initDefaultBehaviour(ENEMY_STATE state) {
		defaultBehaviour = state;
		resetBehaviour();
	}

	public void warnEnemy() {
		currentState = ENEMY_STATE.FOLLOW;
		updateBubbleText();
		startTalking();
	}

	public void startTalking() {
		textBubble.setVisible(true);
	}

	public void stopTalking() {
		textBubble.setVisible(false);
	}

	public void initDebugStateLabel() {
		debugState = new Label("", skin, "debug");
		stage.addActor(debugState);
	}

	public void updateDebugStatePosition() {
		debugState.setText("" + currentState + "\nDist: " + distanceToPlayer);
		debugState.pack();

		float Virtual_Width = Variables.TILE_SIZE
				* Variables.TILES_PER_SCREENWIDTH / Variables.PIXEL_TO_METER;
		float Virtual_Height = Virtual_Width * Variables.VIRTUAL_STAGE_HEIGHT
				/ Variables.VIRTUAL_STAGE_WIDTH;
		float bubbleX = Variables.VIRTUAL_STAGE_WIDTH
				* (body.getPosition().x - player.getBody().getPosition().x)
				/ Virtual_Width;
		float bubbleY = Variables.VIRTUAL_STAGE_HEIGHT
				* (body.getPosition().y - player.getBody().getPosition().y)
				/ Virtual_Height;

		debugState.setPosition(Variables.VIRTUAL_STAGE_WIDTH / 2 + bubbleX
				- debugState.getWidth() / 2, Variables.VIRTUAL_STAGE_HEIGHT / 2
				+ bubbleY - debugState.getHeight() * 2f);
	}

	public void initTextBubble() {
		textBubble = new Label("", skin, "textbubble");
		stage.addActor(textBubble);
		textBubble.setVisible(false);
	}

	public void updateBubbleText() {
		textBubble.setText(textAlternatives.get(MathUtils
				.random(textAlternatives.size - 1)));
		textBubble.pack();
	}

	public void updateBubblePosition() {
		float Virtual_Width = Variables.TILE_SIZE
				* Variables.TILES_PER_SCREENWIDTH / Variables.PIXEL_TO_METER;
		float bubbleX = Variables.VIRTUAL_STAGE_WIDTH
				* (body.getPosition().x - player.getBody().getPosition().x)
				/ Virtual_Width;
		textBubble.setPosition(Variables.VIRTUAL_STAGE_WIDTH / 2 + bubbleX
				- textBubble.getWidth() / 2,
				Variables.VIRTUAL_STAGE_HEIGHT * 0.6f);
	}

	public void calculateDistanceToPlayer() {
		distanceToPlayer = (int) (Math.sqrt(Math.pow((getX() - player.getX()),
				2) + Math.pow((getY() - player.getY()), 2)) * Variables.PIXEL_TO_METER);
	}

	public void act() {
		body.setLinearVelocity(curSpeed, 0);
		updateBubblePosition();
	}

	public void setState(ENEMY_STATE state) {
		this.currentState = state;
	}

	public void die() {
		debugState.setVisible(false);
		textBubble.setVisible(false);
		isDead = true;
	}

	public boolean isFlaggedForDelete() {
		return isDead;
	}

	public void setTargets(Vector2 v1, Vector2 v2) {

		if (v1.x < v2.x) {
			targetLeft = v1;
			targetRight = v2;
		} else {
			targetLeft = v2;
			targetRight = v1;
		}
	}

	public Vector2 getTargetLeft() {
		return targetLeft;
	}

	public Vector2 getTargetRight() {
		return targetRight;
	}

	public void setHome(Vector2 home) {
		targetHome = new Vector2(home);
	}

	public Vector2 getHome() {
		return targetHome;
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

	public void setBehaviourMovement(BehaviourMovement bm) {
		IMovement = bm;
	}

	public void setSpeedX(float x) {
		curSpeed = x;
	}

	public float getSpeedX() {
		return curSpeed;
	}

	public float getPlayerX() {
		return player.getX();
	}

	public void walkRight() {
		curSpeed = moveSpeed;
	}

	public void walkLeft() {
		curSpeed = -moveSpeed;
	}

	public void stopVelocity() {
		body.setLinearVelocity(0, 0);
	}

	public void setEnemyAnimation(String enemyName) {
		enemyAnimation = new Animation(0.1f,
				textureAtlas.findRegions(enemyName));
		enemyAnimation.setPlayMode(PlayMode.LOOP);
		setRegion(enemyAnimation.getKeyFrame(0.2f));
	}
	
	@Override
	public void step(float delta, ArrayList<GameEvent> events) {
		// CHECK THE DISTANCE WITH THE PLAYER
		calculateDistanceToPlayer();

		switch (currentState) {

		default:
		case IDLE:
			curSpeed = 0;
			break;
		case PATROL:
			IMovement.statePatrol();
			break;
		case FOLLOW:
			IMovement.stateFollow();
			break;
		case RETURNHOME:
			IMovement.stateReturnHome();
			break;
		/*
		 * case ATTACK: stateAttack(); break;
		 */
		}

		// DEBUG STATE LABEL
		if (debugStateLabel)
			updateDebugStatePosition();

		// update SPRITE Position
		setPosition(body.getPosition().x - width / 2, body.getPosition().y
				- height / 2);
		runTime += delta;
		// update SPRITE animation and direction
		setRegion(enemyAnimation.getKeyFrame(runTime));
		if (curSpeed >= 0)
			setFlip(false, false);
		else
			setFlip(true, false);

		// ACT
		act();
	}
	
	@Override
	public boolean isDead() {
		return isFlaggedForDelete();
	}
	@Override
	public void destroy() {
		this.world.destroyBody(body);
	}
}
