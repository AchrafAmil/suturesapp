package com.suturesapp.workspace;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.suturesapp.workspace.utils.NativePlatform;

public class SmallIntestineDemoGame extends Game {

	public AssetManager assets;
	public NativePlatform nativePlatform;

	public SmallIntestineDemoGame(NativePlatform nativePlatform){
		this.nativePlatform = nativePlatform;
	}


	@Override
	public void create() {
		setScreen(new GameScreen());
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
