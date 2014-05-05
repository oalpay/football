package games.hebele.football.objects.enemies;

import games.hebele.football.Variables;
import games.hebele.football.helpers.Assets;
import games.hebele.football.objects.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;

public class Enemy extends Sprite {

	private World world;
	private Player player;
	
	private Stage stage;
	
	private Body body;
	private Fixture fixture;
	
	public TextureAtlas textureAtlas;
	public Animation enemyAnimation;
	
	
	public Array<String> textAlternatives = new Array<String>();
	private Label textBubble;
	
	private float height, width;
	
	private boolean isDead=false;
	
	private Vector2 targetHome, targetLeft, targetRight;
	
	private float moveSpeed=2f;
	private float curSpeed;
	
	public enum ENEMY_STATE{
		IDLE, PATROL, FOLLOW, ATTACK, RETURNHOME 
	}

	private ENEMY_STATE defaultBehaviour;
	private ENEMY_STATE currentState;
	
	public Enemy(World world, Stage stage, Player player, float posX, float posY){
		this.world=world;
		this.stage=stage;
		this.player=player;
		
		height=1;
		width=1;
		
		curSpeed=moveSpeed;
		
		setSize(width,height);
		
		//GET THE TEXTURE ATLAS OF ALL ENEMIES
		//THE RELEVANT REGIONS IS SET AT THE SUB ENEMY CLASS
		textureAtlas = Assets.manager.get(Assets.enemyPack, TextureAtlas.class);
		
		//TextureAtlas playerAtlas= new TextureAtlas("data/player.pack");
		//setRegion(playerAtlas.findRegion("p2_walk01"));
		
		//PLAYER SHAPE
		//body def
		BodyDef bodyDef = new BodyDef();
		bodyDef.type=BodyType.DynamicBody;
		bodyDef.position.set(posX, posY);
		
		//ball shape
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2, height/2);
		

		
		//fixture def
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape=shape;
		fixtureDef.density=.5f;
		fixtureDef.friction=0.3f;
		fixtureDef.restitution=0f;
		
		body = this.world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
		

		shape.dispose();
		
		//fixture def
		fixtureDef = new FixtureDef();
		fixtureDef.shape=shape;
		fixtureDef.density=.5f;
		fixtureDef.friction=0.3f;
		fixtureDef.restitution=0f;
		
		
		fixture.setUserData("enemy");
		body.setFixedRotation(true);
		body.setUserData(this);
		
		initTextBubble();
	}
	
	public void resetBehaviour(){
		currentState = defaultBehaviour;
	}
	
	public void calmDown(){
		stopTalking();
	}
	
	public void initDefaultBehaviour(ENEMY_STATE state){
		defaultBehaviour=state;
		resetBehaviour();
	}
	
	public void warnEnemy(){
		currentState = ENEMY_STATE.FOLLOW;
		updateBubbleText();
		startTalking();
	}
	
	public void startTalking(){
		textBubble.setVisible(true);
	}
	
	public void stopTalking(){
		textBubble.setVisible(false);
	}
	
	public void initTextBubble(){
		//9PATCH TEST
		TextureAtlas textureAtlas= new TextureAtlas("data/football.pack");

		NinePatch patch = new NinePatch(textureAtlas.findRegion("textbubble"),28,28,28,28);
		NinePatchDrawable pD = new NinePatchDrawable(patch);

		BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/bookantiqua_white_32.fnt"));
		
		
		LabelStyle style = new LabelStyle();
		style.background=pD;
		style.font=font;
		
		textBubble = new Label("Keserim Topunu!",style);
		
		stage.addActor(textBubble);
		textBubble.setVisible(false);
		//-------------
	}
	
	public void updateBubbleText(){
		textBubble.setText(textAlternatives.get(MathUtils.random(textAlternatives.size-1)));
		textBubble.pack();
	}
	
	public void updateBubblePosition(){
		
		float Virtual_Width=Variables.TILE_SIZE*Variables.TILES_PER_SCREENWIDTH/Variables.PIXEL_TO_METER;
		
		float bubbleX = Variables.VIRTUAL_STAGE_WIDTH * (body.getPosition().x - player.getBody().getPosition().x)/Virtual_Width;
		
		textBubble.setPosition(Variables.VIRTUAL_STAGE_WIDTH/2 + bubbleX - textBubble.getWidth()/2, Variables.VIRTUAL_STAGE_HEIGHT*0.6f);
	}
	
	public void stateReturnHome(){
		Vector2 curPos = body.getPosition();
		Vector2 curVel = body.getLinearVelocity();

		float moveGap=0.3f;
		
		if(Math.abs(curPos.x - targetHome.x) < moveGap){
			resetBehaviour();
			body.setLinearVelocity(0, 0);
			return;
		}
		
		
		if(curSpeed<=0 && curPos.x<=targetHome.x + moveGap) curSpeed=moveSpeed;
		else if(curSpeed>0 && curPos.x>=targetHome.x - moveGap) curSpeed=-moveSpeed;
		
		body.setLinearVelocity(curSpeed, curVel.y);
	}
	
	public void stateAttack(){
		
	}
	
	public void stateFollow(){
		
		
		Vector2 curPos = body.getPosition();
		Vector2 curVel = body.getLinearVelocity();
		
		Vector2 playerPos = player.getBody().getPosition();
		
		if(curSpeed<=0 && curPos.x<=playerPos.x) curSpeed=moveSpeed;
		else if(curSpeed>0 && curPos.x>=playerPos.x) curSpeed=-moveSpeed;
		
		body.setLinearVelocity(curSpeed, curVel.y);
		
		updateBubblePosition();
	}
	
	public void statePatrol(){

		Vector2 curPos = body.getPosition();
		Vector2 curVel = body.getLinearVelocity();
		
		float moveGap=0.3f;
		
		if(curSpeed<=0 && curPos.x<=targetLeft.x + moveGap) curSpeed=moveSpeed;
		else if(curSpeed>0 && curPos.x>=targetRight.x - moveGap) curSpeed=-moveSpeed;
		
		body.setLinearVelocity(curSpeed, curVel.y);
	}
	
	public void update(float delta, float runTime){
		
		switch(currentState){
		default:
		case IDLE:
			break;
		case PATROL:
			statePatrol();
			break;
		case FOLLOW:
			stateFollow();
			break;
		case ATTACK:
			stateAttack();
			break;
		case RETURNHOME:
			stateReturnHome();
			break;
		}

		
		//update SPRITE Position
		setPosition(body.getPosition().x-width/2,body.getPosition().y-height/2);
		
		//update SPRITE animation and direction
		//if(isWalking) 
		setRegion(enemyAnimation.getKeyFrame(runTime));	
		if(curSpeed >=0 ) setFlip(false, false);
		else setFlip(true, false);
	}
	
	public void setState(ENEMY_STATE state){
		this.currentState=state;
	}
	
	public void die(){
		textBubble.setVisible(false);
		isDead=true;
	}
	
	public boolean isFlaggedForDelete(){
		return isDead;
	}
	
	public void setTargets(Vector2 v1, Vector2 v2){
		
		if(v1.x < v2.x){
			targetLeft=v1;
			targetRight=v2;	
		}else{
			targetLeft=v2;
			targetRight=v1;
		}
	}
	
	public void setHome(Vector2 home){
		targetHome = new Vector2(home);
	}
	
	public Vector2 getPosition(){
		return body.getPosition();
	}
	
}
