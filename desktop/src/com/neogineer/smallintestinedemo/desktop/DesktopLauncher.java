package com.neogineer.smallintestinedemo.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.neogineer.smallintestinedemo.SmallIntestineDemoGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new SmallIntestineDemoGame(), config);

		Gdx.app.log("screen size", ""+config.width+" "+config.height);
	}
}
