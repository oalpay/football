package games.hebele.football.helpers;

import games.hebele.football.objects.Ball;
import games.hebele.football.objects.enemies.Enemy;
import games.hebele.football.objects.enemies.Enemy.ENEMY_STATE;
import games.hebele.football.objects.Player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.utils.Array;

public class ContactHelper  implements ContactFilter, ContactListener {

	private World world;
	private Player player;
	private Ball ball;
	private Body ballBody;
	//private RopeJoint ropeBall;
	
	public ContactHelper(World world, Player player, Body ballBody){
		this.world=world;
		this.player=player;
		this.ballBody=ballBody;
		this.ball=player.getBall();
		//this.ropeBall=ropeBall;
	}
	
	
	//WARN ENEMIES WHEN PLAYER STEP ON THEIR GROUND OR LEAVE
	public void warnEnemies(Fixture groundFixture, ENEMY_STATE state){
		Array<Contact> contactList = world.getContactList();
		for(int i = 0; i < contactList.size; i++) {
			Contact contact = contactList.get(i);
			
			String dataA=contact.getFixtureA().getUserData().toString();
			String dataB=contact.getFixtureB().getUserData().toString();
			
			if(contact.isTouching()){
				if(groundFixture.equals(contact.getFixtureA()) && dataB.equals("enemy") 
				|| groundFixture.equals(contact.getFixtureB()) && dataA.equals("enemy")){
					
					Enemy e =(Enemy) contact.getFixtureB().getBody().getUserData();
					
					if(dataA.equals("ground"))
						e =(Enemy) contact.getFixtureB().getBody().getUserData();
					
					if(state == ENEMY_STATE.FOLLOW)
						e.warnEnemy();
					else
						e.calmDown();
				}
			}
		}
	}
	
	@Override
	public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
		/*
		if(fixtureA.getBody().getUserData() instanceof Enemy){
			Enemy e = (Enemy)fixtureA.getBody().getUserData();
			if(e.isFlaggedForDelete())
				return false;
		}
		
		if(fixtureB.getBody().getUserData() instanceof Enemy){
			Enemy e = (Enemy)fixtureB.getBody().getUserData();
			if(e.isFlaggedForDelete())
				return false;
		}*/
		
		
		
		String dataA=fixtureA.getUserData().toString();
		String dataB=fixtureB.getUserData().toString();
		
		if(checkIfContact(dataA,dataB,"ground","player")){	
				return player.getBody().getLinearVelocity().y <= 0;
		}
		return true;
	}

	@Override
	public void beginContact(Contact contact) {

		String dataA=contact.getFixtureA().getUserData().toString();
		String dataB=contact.getFixtureB().getUserData().toString();
		
		//BALL & PLAYER
		if(checkIfContact(dataA,dataB,"ball","playerSensor")){	
			player.prepareForPickingBall();
		}
		
		
		//PLAYER & ENEMY
		if(checkIfContact(dataA,dataB,"player","enemy")){	
			GameController.GameOver();
		}
		
		//BALL & ENEMY
		if(checkIfContact(dataA,dataB,"ball","enemy")){	
			
			Enemy enemyHit = null;
			
			if(dataB.equals("enemy")){ 
				enemyHit = (Enemy)contact.getFixtureB().getBody().getUserData();
				
			}
			else{ 
				enemyHit = (Enemy)contact.getFixtureA().getBody().getUserData();

			}	
			if(ball.isKicked()) enemyHit.die();
			else{ 
				//System.out.println("GAME OVER");
				GameController.GameOver();
			}
		}
		
		//ENEMY & GROUND
		if(checkIfContact(dataA,dataB,"ground","enemy")){	
			
			Enemy enemy = null;
			
			if(dataB.equals("enemy")) {
				enemy = (Enemy)contact.getFixtureB().getBody().getUserData();
				Vector2 v1=new Vector2();
				Vector2 v2=new Vector2();
				
				((ChainShape)contact.getFixtureA().getShape()).getVertex(0, v1);
				((ChainShape)contact.getFixtureA().getShape()).getVertex(1, v2);
				
				enemy.setHome(enemy.getPosition());
				enemy.setTargets(v1,v2);
				
			}
			else{ 
				enemy = (Enemy)contact.getFixtureA().getBody().getUserData();

				//System.out.println(((ChainShape)contact.getFixtureB().getShape()).getVertexCount());
			}
			//enemyHit.die();
		}
		
		//PLAYER & GROUND
		if(checkIfContact(dataA,dataB,"ground","player")){	
			Fixture ground = null;
			
			if(dataB.equals("ground")) {
				ground=contact.getFixtureB();
			}
			else{ 
				ground=contact.getFixtureA();
			}
			
			warnEnemies(ground,ENEMY_STATE.FOLLOW);

			//System.out.println("warn: FOLLOW");
		}
		
		
		//PLAYERSENSOR & BALL
		if(checkIfContact(dataA,dataB,"playerSensor","ball")){	
			//USING GAME CONTROLLER TO UPDATE THE BALL BEFORE BOX2D STEP
			GameController.fixBallPosition=true;
		}
		
		//CHECK PLAYER FEET
		if(checkIfContact(dataA,dataB,"playerSensor","ALL","ball")){
			//System.out.println("PLAYER GROUNDED");
			//player.increaseFeetContacts();
		}
	}

	@Override
	public void endContact(Contact contact) {
		
		String dataA="";
		String dataB="";
		
		if(contact.getFixtureA()!=null && contact.getFixtureB()!=null){
			dataA=contact.getFixtureA().getUserData().toString();
			dataB=contact.getFixtureB().getUserData().toString();
		}
		/*
		if((dataA.equals("ball") && dataB.equals("player"))|| (dataB.equals("ball") && dataA.equals("player"))){
			//if(player.isHoldState()) ballBody.setLinearVelocity(ballBody.getLinearVelocity().x/10, ballBody.getLinearVelocity().y);
		}
		*/
		//player.setHoldState(!player.isHoldState());
		
		//PLAYER & GROUND
		if(checkIfContact(dataA,dataB,"ground","player")){	
				
				Fixture ground = null;
				
				if(dataB.equals("ground")) {
					ground=contact.getFixtureB();
				}else{ 
					ground=contact.getFixtureA();
				}
				
				warnEnemies(ground,ENEMY_STATE.PATROL);
				
				//System.out.println("warn: PATROL");
		}
		
		
		//CHECK PLAYER FEET
		if(checkIfContact(dataA,dataB,"playerSensor","ALL","ball")){
			//System.out.println("PLAYER LEFT THE GROUND");
			//player.decreaseFeetContacts();
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		String dataA=contact.getFixtureA().getUserData().toString();
		String dataB=contact.getFixtureB().getUserData().toString();
		
		if(checkIfContact(dataA,dataB,"ground","player")){	
			//if(player.isIdle()) contact.setFriction(100);
			//else contact.setFriction(0);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}
	
	
	
	//CHECK CONTACT
	public boolean checkIfContact(String A, String B, String targetA, String targetB){
		if(targetB.equals("ALL") && (A.equals(targetA) || B.equals(targetA)))
			return true;
		
		if((A.equals(targetA) && B.equals(targetB)) || (B.equals(targetA) && A.equals(targetB)))
			return true;
		
		return false;
	}
	
	//CHECK IF TARGETS CONTACT BUT NEITHER OF THEM IS THE EXCEPTION
	public boolean checkIfContact(String A, String B, String targetA, String targetB, String exception){
		if(!A.equals(exception) && !B.equals(exception))
			return checkIfContact(A,B,targetA,targetB);
		
		return false;
	}

}
