package com.neogineer.smallintestinedemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.neogineer.smallintestinedemo.utils.NativePlatform;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public class AndroidLauncher extends AndroidApplication implements NativePlatform {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new SmallIntestineDemoGame(this), config);
	}

	@Override
	public void saveScreenshot(FileHandle fileHandle) {
		Pixmap pixmap = getScreenshot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		OutputStream stream = fileHandle.write(false);
		savePNG(pixmapToIntArray(pixmap), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), stream);
	}

	public Pixmap getScreenshot(int x, int y, int w, int h, boolean flipY ) {

		Gdx.gl.glPixelStorei( GL20.GL_PACK_ALIGNMENT, 1 );

		final Pixmap pixmap = new Pixmap( w, h, Pixmap.Format.RGBA8888 );
		ByteBuffer pixels = pixmap.getPixels();
		Gdx.gl.glReadPixels( x, y, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels );

		final int numBytes = w * h * 4;
		byte[] lines = new byte[numBytes];
		if ( flipY ) {
			final int numBytesPerLine = w * 4;
			for ( int i = 0; i < h; i++ ) {
				pixels.position( (h - i - 1) * numBytesPerLine );
				pixels.get( lines, i * numBytesPerLine, numBytesPerLine );
			}
			pixels.clear();
			pixels.put( lines );
		} else {
			pixels.clear();
			pixels.get( lines );
		}

		return pixmap;
	}

	public int[] pixmapToIntArray( Pixmap pixmap ) {
		int w = pixmap.getWidth();
		int h = pixmap.getHeight();

		int dest = 0;
		int[] raw = new int[w * h];
		for ( int y = 0; y < h; y++ ) {
			for ( int x = 0; x < w; x++ ) {
				int rgba = pixmap.getPixel( x, y );
				raw[dest++] = 0xFF000000 | ( rgba >> 8 );
			}
		}
		return raw;
	}

	public void savePNG( int[] colors, int width, int height, OutputStream stream ) {
		Bitmap bitmap = Bitmap.createBitmap( colors, width, height, Bitmap.Config.ARGB_8888 );
		bitmap = Utils.flip(bitmap, Utils.Direction.VERTICAL);
		bitmap.compress( Bitmap.CompressFormat.PNG, 100, stream );

		Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.suturesapp.peditor.peditor");
		startActivity( LaunchIntent );


	}

}
