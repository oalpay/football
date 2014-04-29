package games.hebele.football.objects;

import games.hebele.football.Variables;
import games.hebele.football.helpers.Assets;

import java.util.List;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.utils.Array;

public class Player extends Sprite {

	private World world;
	private Body playerBody, groundBody;
	private Fixture playerFixture, playerSensorFixture;
	
	private Ball playerBall;
	private RopeJoint ballRope;
	
	public Vector2 playerMovement;
    public float playerSpeed=8f;
	

	private TextureAtlas playerAtlas;
	private Animation playerAnimation;
	private float runTime=0;
	private int playerTowards;  
	private boolean jump=false;
	
	private boolean isGrounded=false;
	
	private int numFeetContacts=0;
	
	public boolean holdState=true;
	public boolean isIdle=true;
	public boolean towardsRight=true;
	public boolean isWalking=false;
	
	private boolean walkingRight=false;
	private boolean walkingLeft=false;
	
	private boolean preparePickBall=false;
	
	private float width, height;
	
	public Player(World world){
		this.world=world;
		
		playerBall= new Ball(world, this);
		
		height=1;
		width=1;
		
		
		playerMovement = new Vector2(0,0);
		
		
		initPlayerBody();
		initPlayerBallJoint();
		
		
		//LOAD ASSETS-----------------------------
		TextureAtlas textureAtlas = Assets.manager.get(Assets.footballPack, TextureAtlas.class);
		//BACKGROUND IMAGE
		playerAnimation = new Animation(0.2f, textureAtlas.findRegions("male_runner_1"));
		playerAnimation.setPlayMode(PlayMode.LOOP);
		setRegion(playerAnimation.getKeyFrame(0.2f));
		setSize(width,height*1.5f);
		//texturebg = textureAtlas.findRegion("bg1");

		setFlip(true, false);
		//flip(true,false);
	}
	
	public void removeBallRope(){
		world.destroyJoint(ballRope);
	}
	
	public void initPlayerBallJoint(){
		RopeJointDef ropeBallDef = new RopeJointDef();
		ropeBallDef.bodyA = playerBody;
		ropeBallDef.bodyB = playerBall.getBody();
		ropeBallDef.maxLength = Variables.BALL_ROPE_LENGTH;
		
		ropeBallDef.localAnchorA.set(0,0f);
		ropeBallDef.localAnchorB.set(0,0);
		ropeBallDef.collideConnected = true;
		
		ballRope = (RopeJoint) world.createJoint(ropeBallDef);
	}
	
	public void initPlayerBody(){
		//PLAYER SHAPE
		//body def
		BodyDef bodyDef = new BodyDef();
		bodyDef.type=BodyType.DynamicBody;
		bodyDef.position.set(2, 4);
		
		//ball shape
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2, height/2);
		

		
		//fixture def
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape=shape;
		fixtureDef.density=.5f;
		fixtureDef.friction=0.3f;
		fixtureDef.restitution=0f;
		
		playerBody = this.world.createBody(bodyDef);
		playerFixture = playerBody.createFixture(fixtureDef);
	//	playerBody.createFixture(fixtureDef).setUserData("player");
		
		shape.dispose();
		
		CircleShape circle = new CircleShape();		
		circle.setRadius(width/2.1f);
		circle.setPosition(new Vector2(0,-height/4));
		//fixture def
		fixtureDef = new FixtureDef();
		fixtureDef.shape=circle;
		//fixtureDef.density=.5f;
		//fixtureDef.friction=0.3f;
		//fixtureDef.restitution=0f;
		

		playerSensorFixture = playerBody.createFixture(fixtureDef);
		playerSensorFixture.setUserData("playerSensor");
		playerSensorFixture.setSensor(true);
		
		circle.dispose();
		
		playerFixture.setUserData("player");
		playerBody.setBullet(false);
		playerBody.setFixedRotation(true);
		playerBody.setUserData(this);
		
		/*
		CircleShape circle = new CircleShape();		
		circle.setRadius(width/2);
		circle.setPosition(new Vector2(0, -height/4));
		playerSensorFixture = playerBody.createFixture(circle, 0);		
		playerSensorFixture.setSensor(true);
		circle.dispose();*/
		
		//-----PLAYER SHAPE
	}
	
	public void update(float delta, float runTime){
		
		playerBall.update(delta);
		
		Vector2 vel = playerBody.getLinearVelocity();
		
		// cap max velocity on x		
		if(Math.abs(vel.x) > Variables.playerSpeed) {			
			vel.x = Math.signum(vel.x) * Variables.playerSpeed;
			playerBody.setLinearVelocity(vel.x, vel.y);
		}
		
		//WALK DEPENDING ON HUD WALK BUTTONS
		if(walkingRight) walkRight();
		else if(walkingLeft) walkLeft();
		//else if(!jump && isGrounded()) stop();
		//----------------------------------
		
		
		if(jump) {			
			jump = false;	
		//	System.out.println("jump before: " + playerBody.getLinearVelocity());
		//	playerBody.setTransform(getX(), getY() + 0.01f, 0);
			playerBody.setLinearVelocity(playerBody.getLinearVelocity().x,0);
			playerBody.applyLinearImpulse(0, playerSpeed*0.5f, getX(), getY(),true);			
		//	System.out.println("jump, " + playerBody.getLinearVelocity());				
		}
		
		//update SPRITE and Position
		//TextureRegion walkRegion=playerAnimation.getKeyFrame(runTime);

		if(isWalking) setRegion(playerAnimation.getKeyFrame(runTime));	
		if(towardsRight) setFlip(false, false);
		else setFlip(true, false);
		
		setPosition(playerBody.getPosition().x-width/2,playerBody.getPosition().y-height/2);
	}
	
	public Body getBody(){
		return playerBody;
	}
	
	public boolean isHoldState(){
		return holdState;
	}

	public void setHoldState(boolean bool){
		holdState=bool;
	}
	
	public boolean isIdle(){
		return isIdle;
	}

	public void setIdle(boolean bool){
		isIdle=bool;
	}
	
	public boolean isGrounded(){
		return numFeetContacts>0;
	}
	
	public void setGrounded(boolean bool){
		isGrounded=bool;
	}
	
	public void setJump(boolean bool){
		jump=bool;
	}
	
	public void setFriction(float friction){
		playerFixture.setFriction(friction);
	}
	
	public float getFriction(){
		return playerFixture.getFriction();
	}
	
	public Ball getBall(){
		return playerBall;
	}
	
	public Body getBallBody(){
		return playerBall.getBody();
	}
	
	public RopeJoint getBallRope(){
		return ballRope;
	}
	
	public void increaseFeetContacts(){
		numFeetContacts++;
	}
	
	public void decreaseFeetContacts(){
		numFeetContacts--;
	}
	
	public void kickBall(float xPercent, float yPercent){
		if(!playerBall.isKicked() && ((towardsRight && xPercent>0) || (!towardsRight && xPercent<0)) ){
			
			preparePickBall=false;
			holdState=false;
			removeBallRope();
			playerBall.setKicked();
			
			//ropeBall.setMaxLength(10f);

			float kickForceX = Variables.playerKickPower * xPercent;
			float kickForceY = Variables.playerKickPower * yPercent;
			
			System.out.println(kickForceX+" , "+kickForceY);
			
			Vector2 ballCenter = playerBall.getBody().getWorldCenter();
			
			//playerBall.getBody().setTransform(playerBall.getBody().getPosition(), 0);
			//playerBall.getBody().applyForceToCenter(kickForceX, kickForceY,true);
			playerBall.getBody().applyLinearImpulse(kickForceX, kickForceY, ballCenter.x, ballCenter.y, true);
			//ballBody.applyLinearImpulse(50, 0.1f, ballBody.getPosition().x, ballBody.getPosition().y, true);
		}
	}
	
	public void pickBall(){
		if(playerBall.isKicked()){
			preparePickBall=false;
			holdState=true;
			initPlayerBallJoint();
			
			playerBall.getBody().setLinearVelocity(0, 0);
			
			float ballPosition=getWidth();
			if(!towardsRight) ballPosition*=-1;
			
			playerBall.getBody().setTransform(playerBody.getPosition().x+ballPosition, playerBall.getBody().getPosition().y, playerBall.getBody().getTransform().getRotation());
			playerBall.hold();
		}
	}
	
	public void prepareForPickingBall(){
		preparePickBall=true;
	}
	
	public boolean isPreparePickBall(){
		return preparePickBall;
	}
	
	public void turnToRight(){			
		isWalking=true;
		towardsRight=true;	
		playerMovement.x=playerSpeed;
		setFriction(0);
		
		//get ball to right
		if(!playerBall.isKicked())
			playerBall.getBody().setTransform(playerBody.getPosition().x+getWidth(), playerBall.getBody().getPosition().y, playerBall.getBody().getTransform().getRotation());
	
		setFlip(false, false);
		
	}
	
	public void turnToLeft(){
		isWalking=true;
		towardsRight=false;
		playerMovement.x=-playerSpeed;
		setFriction(0);
		
		//get ball to left
		if(!playerBall.isKicked())
			playerBall.getBody().setTransform(playerBody.getPosition().x-getWidth(), playerBall.getBody().getPosition().y, playerBall.getBody().getTransform().getRotation());
		
		setFlip(true, false);
	}
	
	public void walkRight(){
		setIdle(false);
		playerBody.setLinearVelocity(Variables.playerSpeed, playerBody.getLinearVelocity().y);
	}
	
	public void walkLeft(){
		setIdle(false);
		playerBody.setLinearVelocity(-Variables.playerSpeed, playerBody.getLinearVelocity().y);
	}
	
	public void stop(){
		setIdle(true);

		isWalking=false;
		playerMovement.x=0;	
		
		if(isGrounded()){ 
			playerBody.setLinearVelocity(0, 0);
			setFriction(100);
		};
		
		setRegion(playerAnimation.getKeyFrame(0.2f));
	}
	
	public void jump(){
		if(isGrounded()) setJump(true);
	}
	
	public void setWalkingLeft(boolean bool){
		if(walkingLeft!=bool){
			walkingLeft=bool;
			if(walkingLeft) turnToLeft();
			else stop();
		}
	}
	
	public void setWalkingRight(boolean bool){
		if(walkingRight!=bool){
			walkingRight=bool;
			if(walkingRight) turnToRight();
			else stop();
		}
	}
}
