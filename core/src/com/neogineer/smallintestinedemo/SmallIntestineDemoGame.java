package com.neogineer.smallintestinedemo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class SmallIntestineDemoGame extends Game {

	public AssetManager assets;
	public com.neogineer.smallintestinedemo.utils.NativePlatform nativePlatform;

	public SmallIntestineDemoGame(com.neogineer.smallintestinedemo.utils.NativePlatform nativePlatform){
		this.nativePlatform = nativePlatform;
	}


	@Override
	public void create() {
		//setScreen(new SplashScreen());
		GameScreen mainScreen = new GameScreen();
		Gdx.app.log("Game","setScreen(mainScreen)");
		setScreen(mainScreen);
	}

	public static AssetManager getAssets() {
		AssetManager manager = ((SmallIntestineDemoGame) Gdx.app.getApplicationListener()).assets;
		if(manager==null) {
			((SmallIntestineDemoGame) Gdx.app.getApplicationListener()).setupAssets();
			return getAssets();
		}
		manager.update();
		return ((SmallIntestineDemoGame) Gdx.app.getApplicationListener()).assets;
	}

	public void setupAssets(){
		this.assets = new AssetManager();
		Texture.setAssetManager(assets);
	}

}
