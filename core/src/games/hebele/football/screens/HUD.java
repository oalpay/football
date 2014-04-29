package games.hebele.football.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import games.hebele.football.helpers.GameController;
import games.hebele.football.helpers.GameController.GameState;
import games.hebele.football.objects.Player;

public class HUD {
	private Player player;
	private Stage stage;
	
    private Touchpad touchpad;
    private TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    
    private Skin skin;
    
    private Drawable touchBackground;
    private Drawable touchKnob;
    
    private ImageButton btnController, btnPlay;
    
    private float lastKnobPercentX, lastKnobPercentY;
    
    private float controllerWidth, controllerHeight;
    
	public HUD(Stage stage, final Player player){
		
		this.player=player;
		this.stage=stage;
		
		//TOUCHPAD
		TextureAtlas touchpadAtlas = new TextureAtlas(Gdx.files.internal("data/touchpad.atlas"));
		
		skin = new Skin(Gdx.files.internal("data/touchpad.json"),touchpadAtlas);
		
        //Create a touchpad skin    
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.addRegions(touchpadAtlas);
        touchpadStyle = new TouchpadStyle();
        
        //Create Drawable's from TouchPad skin
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        
        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadStyle);
        
        float touchPadSize=250;
        float touchPadGap=10;
        //setBounds(x,y,width,height)
        touchpad.setBounds(stage.getWidth()-touchPadSize-touchPadGap, touchPadGap, touchPadSize, touchPadSize);

        
        
        touchpad.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y,int pointer, int button) {
				
				//System.out.println(lastKnobPercentX+" , "+lastKnobPercentY);
				
				//CHECK IF GAME IS RUNNING BEFORE KICKING THE BALL
				if(GameController.isGameRunning())
					player.kickBall(lastKnobPercentX, lastKnobPercentY);
				
				super.touchUp(event, x, y, pointer, button);
			}
			
			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				lastKnobPercentX=touchpad.getKnobPercentX();
				lastKnobPercentY=touchpad.getKnobPercentY();
				//System.out.println(getKnobPercentX()+" , "+getKnobPercentY());
				super.touchDragged(event, x, y, pointer);
			}
		});
        
        stage.addActor(touchpad);      
        
        initControllers();
        initButtons();
	}
	
	public void initButtons(){
		btnPlay = new ImageButton(skin,"play");
		
		float btnSize=100;
		btnPlay.setSize(btnSize, btnSize);
		btnPlay.setPosition(stage.getWidth()-btnSize*1.5f, stage.getHeight()-btnSize*1.5f);
		
		stage.addActor(btnPlay);
		
		btnPlay.addListener(new InputListener(){
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y,int pointer, int button) {
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y,int pointer, int button) {
				if(GameController.isGameRunning()) GameController.setGameState(GameState.GAME_PAUSED);
				else GameController.setGameState(GameState.GAME_RUNNING);
			}
		});
	}
	
	public void initControllers(){
		
		btnController = new ImageButton(skin,"controller");
		
		float btnRatio = btnController.getHeight() / btnController.getWidth();
		controllerWidth=300;
		controllerHeight=controllerWidth*btnRatio;
		float btnGap=20;
		
		btnController.setPosition(btnGap, btnGap);
		btnController.setSize(controllerWidth, controllerHeight);
		
		stage.addActor(btnController);
		
		
		btnController.addListener(new InputListener(){
			
			@Override
			public void touchDragged(InputEvent event, float x, float y,int pointer) {

				checkMovementControllerInput(x,y);		
				super.touchDragged(event, x, y, pointer);
			}
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y,int pointer, int button) {

				checkMovementControllerInput(x,y);
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y,int pointer, int button) {
				//System.out.println("touchup");
				player.setWalkingLeft(false);
				player.setWalkingRight(false);
			//	player.stop();
				super.touchUp(event, x, y, pointer, button);
			}
		});
		
	}
	
	private void checkMovementControllerInput(float x, float y){
		if(x>=0 && y>=0 && x<=controllerWidth && y<=controllerHeight){
			//System.out.println("touchDragged: "+x+" , "+y);
			
			if(x<controllerWidth/3){ //GO TO LEFT
				player.setWalkingRight(false);
				player.setWalkingLeft(true);
				System.out.println("left");
			}
			else if(x>controllerWidth*2/3){ //GO TO RIGHT
				player.setWalkingLeft(false);
				player.setWalkingRight(true);
				System.out.println("right");
			}
			else{ //STOP AT X AXIS
			//	player.setWalkingLeft(false);
			//	player.setWalkingRight(false);
			}
			
			if(y>controllerHeight/2){
				player.jump();
				System.out.println("up");
			}
		}
	}
	
	public Stage getStage(){
		return stage;
	}
	
	public float getKnobPercentX(){
		return touchpad.getKnobPercentX();
	}
	
	public float getKnobPercentY(){
		return touchpad.getKnobPercentY();
	}

}
