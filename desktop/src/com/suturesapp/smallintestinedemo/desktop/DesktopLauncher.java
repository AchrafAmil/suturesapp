package com.neogineer.smallintestinedemo.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.neogineer.smallintestinedemo.utils.NativePlatform;

public class DesktopLauncher{
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new com.neogineer.smallintestinedemo.SmallIntestineDemoGame(new NativePlatform() {
			@Override
			public void saveScreenshot(FileHandle fileHandle) {
			}

			@Override
			public void showMessage(String msg) {

			}
		}), config);

		Gdx.app.log("screen size", ""+config.width+" "+config.height);
	}
}
