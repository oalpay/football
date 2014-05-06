package games.hebele.football.helpers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {

	
	public static final AssetManager manager = new AssetManager();
	
	public static final String footballPack ="data/football.atlas";
	public static final String enemyPack ="data/enemies.pack";
	
	public static final String skin ="data/football.json";
	
	public static void load(){
		manager.load(footballPack, TextureAtlas.class);
		manager.load(enemyPack, TextureAtlas.class);
		manager.load(skin,Skin.class);
	}
	
	public static void dispose(){
		manager.dispose();
	}
}
