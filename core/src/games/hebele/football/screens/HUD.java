package games.hebele.football.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import games.hebele.football.helpers.Assets;
import games.hebele.football.helpers.GameController;
import games.hebele.football.helpers.GameController.GameState;
import games.hebele.football.objects.Player;

public class HUD {
	private Player player;
	private Stage stage;
	
    private Touchpad touchpad;
    private Skin skin;
    
    private ImageButton btnController, btnPlay;
    
    private float lastKnobPercentX, lastKnobPercentY;
    
    private float controllerWidth, controllerHeight;
    
    private Image gameEndCurtain;
    private ImageButton btnMenu, btnRestart;
    
    private boolean isGameRunning;
    
	public HUD(Stage stage, final Player player){
		
		this.player=player;
		this.stage=stage;
		
		skin = Assets.manager.get(Assets.skin,Skin.class);
		
		initHUD();
	}
	
	public void initHUD(){
		isGameRunning=true;
        initTouchPad();
        initControllers();
        initButtons();
        initGameEndScreens();
	}
	
	public void initTouchPad(){
		
        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, skin);
        
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
				//System.out.println("left");
			}
			else if(x>controllerWidth*2/3){ //GO TO RIGHT
				player.setWalkingLeft(false);
				player.setWalkingRight(true);
				//System.out.println("right");
			}
			else{ //STOP AT X AXIS
			//	player.setWalkingLeft(false);
			//	player.setWalkingRight(false);
			}
			
			if(y>controllerHeight*0.6f){
				player.jump();
				//System.out.println("up");
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
		
		gameEndCurtain = new Image(skin,"perde");
		gameEndCurtain.setSize(stage.getWidth(), stage.getHeight());

		stage.addActor(gameEndCurtain);
		gameEndCurtain.setVisible(false);
		
		btnRestart = new ImageButton(skin,"restart");
		btnMenu = new ImageButton(skin,"menu");

		
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
    	
    	if(isGameRunning){
    		
    		isGameRunning=false;

    		float btnSize=100;
    		
    		Table endScreenTable = new Table(skin);
    		endScreenTable.setBackground("panelbg");
    		endScreenTable.setSize(stage.getWidth()/2, stage.getHeight()/2);
    		endScreenTable.setPosition(stage.getWidth()/2 - endScreenTable.getWidth()/2, stage.getHeight()/2 - endScreenTable.getHeight()/2);
    		
    		endScreenTable.add(new Label("GAME OVER!",skin,"ending")).colspan(2).padBottom(btnSize/2).row();
    		endScreenTable.add(btnMenu).size(btnSize);
    		endScreenTable.add(btnRestart).size(btnSize);
    		
    		stage.addActor(endScreenTable);
    		gameEndCurtain.setVisible(true);
    	}
    }
    //-------------------------------------------------
	
    //GAME WON - GRATS
    public void showGameWon(){
    	if(isGameRunning){
    		isGameRunning=false;
    		
    	}
    }
    //-------------------------------------------------

}
