package games.hebele.football.helpers;

import games.hebele.football.Variables;
import games.hebele.football.objects.Ball;
import games.hebele.football.objects.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;

public class InputHandler implements InputProcessor {

	private Player player;
	private Body ballBody, playerBody;
	private RopeJoint ropeBall;
	private Ball ball;
	
	public InputHandler(Player player){
		this.player=player;
		this.ballBody=player.getBallBody();
		this.ropeBall=player.getBallRope();
		this.ball=player.getBall();
		
		playerBody=player.getBody();
	}
	
	
	public void checkPressedInput(float delta){
		
		
		if(Gdx.input.isKeyPressed(Keys.A)) {
			player.walkLeft();
		}
		 
		if(Gdx.input.isKeyPressed(Keys.D)) {
			player.walkRight();
		}
		
		if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {
			//player.stop();
		}
		
	}
	
	
	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
		case Keys.W:
			player.jump();
			break;
		case Keys.A:
			player.turnToLeft();
			break;
		case Keys.D:
			player.turnToRight();
	}
	return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch(keycode) {
		case Keys.A:
		case Keys.D:
			player.stop();
	}
	return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		//player.kickBall();
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//player.pickBall();
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
