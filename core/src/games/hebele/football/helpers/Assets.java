package games.hebele.football.helpers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {

	
	public static final AssetManager manager = new AssetManager();
	
	public static final String footballPack ="data/football.pack";
	public static final String playerPack ="data/player.pack";
	public static final String touchpadPack ="data/touchpad.pack";
	
	public static final String skin ="skin/touchpad.json";
	
	public static void load(){
		manager.load(footballPack, TextureAtlas.class);
		manager.load(playerPack, TextureAtlas.class);
		
		//manager.load(skin,Skin.class);
	}
	
	public static void dispose(){
		manager.dispose();
	}
}
