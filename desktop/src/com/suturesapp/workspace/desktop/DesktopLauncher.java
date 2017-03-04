package com.suturesapp.workspace.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.suturesapp.workspace.SmallIntestineDemoGame;
import com.suturesapp.workspace.utils.NativePlatform;

public class DesktopLauncher{
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new SmallIntestineDemoGame(new NativePlatform() {
			@Override
			public void saveScreenshot(FileHandle fileHandle) {
			}
		}), config);

		Gdx.app.log("screen size", ""+config.width+" "+config.height);
	}
}
