package games.hebele.football.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Ball extends Sprite {

	private World world;
	private Player player;
	private Body body;
	
	private TextureAtlas textureAtlas;
	
	private boolean isKicked=false;
	private float rollTime;
	
	public Ball(World world, Player player){
		this.world=world;
		this.player=player;
		
		float size=0.5f;
		
		setSize(size, size);
		setOrigin(size/2, size/2);
		
		//BALL SHAPE
		//body def
		BodyDef bodyDef = new BodyDef();
		bodyDef.type=BodyType.DynamicBody;
		bodyDef.position.set(4, 5);
		
		CircleShape circle = new CircleShape();		
		circle.setRadius(size/2);
		
		//fixture def
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape=circle;
		fixtureDef.density=0.3f;
		fixtureDef.friction=0.8f;
		fixtureDef.restitution=0.4f;
		
		
		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef).setUserData("ball");		
		
		circle.dispose();
		
		textureAtlas= new TextureAtlas("data/football.pack");
		setRegion(textureAtlas.findRegion("ball"));
	}
	
	public void update(float delta){
		updateBallSpeed();
		if(!isKicked)
			body.setTransform(body.getPosition().x, player.getBody().getPosition().y-player.getHeight()/4, body.getTransform().getRotation());
		else{
			rollTime+=delta;
			if(rollTime>2){
				Vector2 bV = body.getLinearVelocity();
				body.setLinearVelocity(bV.x * 0.7f , bV.y);
			}
		}
		//update SPRITE Position and Rotation
		setPosition(body.getPosition().x-getWidth()/2,body.getPosition().y-getHeight()/2);
		setRotation(body.getAngle()*MathUtils.radDeg);
	}
	
	public void updateBallSpeed(){
		if(player.isHoldState()){
			float maxSpeed=8;
			float xS=body.getLinearVelocity().x;
			float yS=body.getLinearVelocity().y;
			if(xS>3) xS=3;
			else if(xS<-3) xS=-3;
			if(yS>maxSpeed) yS=maxSpeed;
			
			body.setLinearVelocity(xS, yS);
		}
	}
	
	public Body getBody(){
		return body;
	}
	
	public void setKicked(){
		rollTime=0;
		isKicked=true;
	}
	
	public void hold(){
		isKicked=false;
	}
	
	public boolean isKicked(){
		return isKicked;
	}
}
