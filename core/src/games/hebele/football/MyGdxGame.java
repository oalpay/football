package games.hebele.football;

import games.hebele.football.helpers.Assets;
import games.hebele.football.screens.LevelSelectionScreen;
import games.hebele.football.screens.PlayScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class MyGdxGame extends Game {

	@Override
	public void create() {
		
		Assets.load();
		Assets.manager.finishLoading();
		
		Variables.VIRTUAL_STAGE_HEIGHT = Variables.VIRTUAL_STAGE_WIDTH * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
		
		setScreen(new LevelSelectionScreen());
		//setScreen(new PlayScreen());
	}
	
	@Override
	public void dispose() {
		super.dispose();
		Assets.dispose();
	}
}
	
