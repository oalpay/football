package games.hebele.football.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

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
    
    private Label gameOverBubble, gameWonBubble;
    private Image gameEndCurtain;
    private ImageButton btnMenu, btnRestart;
    
	public HUD(Stage stage, final Player player){
		
		this.player=player;
		this.stage=stage;
		
		initHUD();
	}
	
	public void initHUD(){
        initTouchPad();
        initControllers();
        initButtons();
        initGameEndScreens();
	}
	
	public void initTouchPad(){
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
	
	public void initGameEndScreens(){
		TextureAtlas textureAtlas= new TextureAtlas("data/touchpad.atlas");

		NinePatch patch = new NinePatch(textureAtlas.findRegion("panelbg"),28,28,28,28);
		NinePatchDrawable pD = new NinePatchDrawable(patch);

		BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/bookantiqua_white_64.fnt"));
		
		
		LabelStyle style = new LabelStyle();
		style.background=pD;
		style.font=font;
		
		gameOverBubble = new Label("GAME OVER!",style);
		gameWonBubble = new Label("VICTORY!",style);
		

		gameOverBubble.setSize(stage.getWidth()*0.6f, stage.getHeight()*0.7f);
		gameWonBubble.setSize(stage.getWidth()*0.6f, stage.getHeight()*0.7f);
		
		//gameOverBubble.pack();
		//gameWonBubble.pack();
		
		gameOverBubble.setPosition(stage.getWidth()/2 - gameOverBubble.getWidth()/2, stage.getHeight()/2 - gameOverBubble.getHeight()/2);
		gameWonBubble.setPosition(stage.getWidth()/2 - gameWonBubble.getWidth()/2, stage.getHeight()/2 - gameWonBubble.getHeight()/2);
		
		
		gameEndCurtain = new Image(skin,"perde");
		gameEndCurtain.setSize(stage.getWidth(), stage.getHeight());

		stage.addActor(gameEndCurtain);
		stage.addActor(gameOverBubble);
		stage.addActor(gameWonBubble);
		
		gameEndCurtain.setVisible(false);
		gameOverBubble.setVisible(false);
		gameWonBubble.setVisible(false);
		
		
		
		btnRestart = new ImageButton(skin,"restart");
		
		float btnSize=100;
		float btnX=gameOverBubble.getX() + gameOverBubble.getWidth()/2;
		float btnY=gameOverBubble.getY();
		
		btnRestart.setSize(btnSize, btnSize);
		btnRestart.setPosition(btnX+10, btnY+btnSize);
		
		stage.addActor(btnRestart);
		btnRestart.setVisible(false);
		
		btnRestart.addListener(new InputListener(){
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y,int pointer, int button) {
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y,int pointer, int button) {
				GameController.resetGame();
				initHUD();
				((Game)(Gdx.app.getApplicationListener())).setScreen(new PlayScreen());
			}
		});
		
		
		btnMenu = new ImageButton(skin,"menu");
		
		btnMenu.setSize(btnSize, btnSize);
		btnMenu.setPosition(btnX-btnSize, btnY+btnSize);
		
		stage.addActor(btnMenu);
		btnMenu.setVisible(false);
		
		btnMenu.addListener(new InputListener(){
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y,int pointer, int button) {
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y,int pointer, int button) {
				GameController.resetGame();
				initHUD();
				((Game)(Gdx.app.getApplicationListener())).setScreen(new LevelSelectionScreen());
			}
		});
	}
	
    //GAME OVER
    public void showGameOver(){
		btnMenu.setVisible(true);
    	btnRestart.setVisible(true);
    	gameEndCurtain.setVisible(true);
    	gameOverBubble.setVisible(true);
    }
    //-------------------------------------------------
	
    //GAME WON - GRATS
    public void showGameWon(){
		gameWonBubble.setVisible(true);
    }
    //-------------------------------------------------

}
