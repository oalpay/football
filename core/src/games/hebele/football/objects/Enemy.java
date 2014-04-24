package games.hebele.football.objects;

import games.hebele.football.Variables;

import com.badlogic.gdx.Gdx;
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

public class Enemy extends Sprite {

	private World world;
	private Player player;
	
	private Stage stage;
	
	private Body body;
	private Fixture fixture;
	
	private Label textBubble;
	
	private float height, width;
	
	private boolean isDead=false;
	
	private Vector2 targetLeft, targetRight;
	
	private float moveSpeed=2f;
	private float curSpeed;
	
	public enum ENEMY_STATE{
		IDLE, PATROL, FOLLOW, ATTACK 
	}

	private ENEMY_STATE state=ENEMY_STATE.PATROL;
	
	public Enemy(World world, Stage stage, Player player, float posX, float posY){
		this.world=world;
		this.stage=stage;
		this.player=player;
		
		height=1;
		width=1;
		
		curSpeed=moveSpeed;
		
		setSize(width,height);
		
		TextureAtlas playerAtlas= new TextureAtlas("data/player.pack");

		setRegion(playerAtlas.findRegion("p2_walk01"));
		
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
		int rnd = MathUtils.random(1,3);
		if(rnd==1) textBubble.setText("Keserim Topunu!");
		else if(rnd==2) textBubble.setText("Oðlum oynamasanýza burada!");
		else if(rnd==3) textBubble.setText("Camlarý kýracaksýn, ben de kafaný kýracaðým!");
		
		//textBubble.setSize(100, 100);
		//textBubble.setWrap(true);
		
		textBubble.pack();
	}
	
	public void updateBubblePosition(){
		
		float Virtual_Width=Variables.PIXEL_SIZE*Variables.PIXEL_PER_WIDTH/Variables.PIXEL_TO_METER;
		
		float bubbleX = Variables.VIRTUAL_STAGE_WIDTH * (body.getPosition().x - player.getBody().getPosition().x)/Virtual_Width;
		
		textBubble.setPosition(Variables.VIRTUAL_STAGE_WIDTH/2 + bubbleX - textBubble.getWidth()/2, Variables.VIRTUAL_STAGE_HEIGHT*0.6f);
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
		
		textBubble.setVisible(true);
		
		updateBubblePosition();
	}
	
	public void statePatrol(){
		
		textBubble.setVisible(false);
		
		Vector2 curPos = body.getPosition();
		Vector2 curVel = body.getLinearVelocity();
		
		float moveGap=0.3f;
		
		if(curSpeed<=0 && curPos.x<=targetLeft.x + moveGap) curSpeed=moveSpeed;
		else if(curSpeed>0 && curPos.x>=targetRight.x - moveGap) curSpeed=-moveSpeed;
		
		body.setLinearVelocity(curSpeed, curVel.y);
	}
	
	public void update(float delta, float runTime){
		
		switch(state){
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
		}

		
		//update SPRITE Position
		setPosition(body.getPosition().x-width/2,body.getPosition().y-height/2);
	}
	
	public void setState(ENEMY_STATE state){
		this.state=state;
		
		switch(state){
		default:
		case IDLE:
			break;
		case PATROL:
			
			break;
		case FOLLOW:
			updateBubbleText();
			break;
		case ATTACK:
			
			break;
		}
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
	
}
