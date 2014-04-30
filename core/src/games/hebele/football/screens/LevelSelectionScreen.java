package games.hebele.football.screens;

import games.hebele.football.Variables;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class LevelSelectionScreen implements Screen {

	private Stage stage;
	private OrthographicCamera camera;
	
	private Skin skin;
	private Table screenTable;
	
	private float Virtual_Height, Virtual_Width;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(178/255f, 213/255f, 238/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		stage.getSpriteBatch().setProjectionMatrix(camera.combined);
		stage.act(delta);
		stage.draw();
	}



	@Override
	public void show() {
		
		Virtual_Width=1000;
		Virtual_Height=Virtual_Width*Gdx.graphics.getHeight()/Gdx.graphics.getWidth();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Virtual_Width, Virtual_Height);
		
		stage = new Stage(new ExtendViewport(Virtual_Width, Virtual_Height));
		
		skin = new Skin(Gdx.files.internal("data/touchpad.json"));
		screenTable = new Table();
		//screenTable.setPosition(Virtual_Width/2, Virtual_Height/2);
		screenTable.setFillParent(true);
		
		float btnWidth=130;
		float btnHeight=170;
		
		screenTable.defaults().size(btnWidth, btnHeight);
		screenTable.defaults().padBottom(btnHeight/3);
		screenTable.defaults().padRight(btnWidth/3);
		
		
		stage.addActor(screenTable);
		
		
		int[] dummyLevels = { 9,
			    3, 1, 2,
			    3, 0, 0, 
			    0, 0, 0, 
			    0 
			};
		
		int curLevel = 5;
		
		int numLevel=dummyLevels.length;
		int levelPerRow=5;
		
		float starSize=btnWidth/2.5f;
		
		for(int i=1; i<numLevel;  i++){
			
			if(i%levelPerRow == 1) screenTable.row();
			
			int levelPoint = dummyLevels[i];
			final boolean locked = (levelPoint==0 && i!=curLevel);
			
			String panelType ="panel_green";
			if(locked) panelType ="panel_grey";
			if(i == curLevel) panelType ="panel_blue"; 
			
			ImageTextButton btn = new ImageTextButton(""+i,skin,panelType);
			btn.row();
			
			Image star1, star2, star3;
			
			
			
			if(levelPoint>0){
				star1=new Image(skin,"star_empty");
				star2=new Image(skin,"star_full");
				star3=new Image(skin,"star_empty");
				if(levelPoint>1) star1=new Image(skin,"star_full");
				if(levelPoint>2) star3=new Image(skin,"star_full");
				
				btn.add(star1).size(starSize);
				btn.add(star2).padTop(starSize/2).padLeft(-starSize/2).size(starSize);
				btn.add(star3).padLeft(-starSize/2).size(starSize);
				
				star3.toBack();
			}
			
			if(locked){
				btn.add(new Image(skin,"lock")).size(113/2, 150/2).center();
			}
			
			btn.addListener(new InputListener(){
				
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					return true;
				}
				
				@Override
				public void touchUp(InputEvent event, float x, float y,int pointer, int button) {
					if(!locked) ((Game)(Gdx.app.getApplicationListener())).setScreen(new PlayScreen());
				}
				
				
			});
			
			screenTable.add(btn);
		}
		
		Gdx.input.setInputProcessor(stage);
		
	}

	
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
