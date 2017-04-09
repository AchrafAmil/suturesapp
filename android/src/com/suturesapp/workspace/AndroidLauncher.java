package com.suturesapp.workspace;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.suturesapp.workspace.utils.NativePlatform;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public class AndroidLauncher extends AndroidApplication implements AndroidFragmentApplication.Callbacks, NativePlatform {

	View view;
	RelativeLayout rl;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		setContentView(R.layout.launcher_android);
		final AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		view = initializeForView(new com.suturesapp.workspace.SmallIntestineDemoGame(AndroidLauncher.this), config);

		rl = (RelativeLayout) findViewById(R.id.splashRL);
		RelativeLayout splashImg = (RelativeLayout) findViewById(R.id.splashImage);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		rl.addView(view, params);
		splashImg.bringToFront();
		hideSystemUI();

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	// This snippet hides the system bars.
	private void hideSystemUI() {
		// Set the IMMERSIVE flag.
		// Set the content to appear under the system bars so that the content
		// doesn't resize when the system bars hide and show.
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
						| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
						| View.SYSTEM_UI_FLAG_IMMERSIVE);
	}

	// This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
	private void showSystemUI() {
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	@Override
	public void saveScreenshot(FileHandle fileHandle) {
		Pixmap pixmap = getScreenshot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		OutputStream stream = fileHandle.write(false);
		savePNG(pixmapToIntArray(pixmap), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), stream);
	}

	@Override
	public void showMessage(final String msg) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(AndroidLauncher.this, msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void loadingFinished() {
		this.runOnUiThread(new Runnable() {
			public void run() {
				rl.removeView(findViewById(R.id.splashImage));
				new AlertDialog.Builder(AndroidLauncher.this, R.style.AlertDialogStyle)
						.setTitle("Disclaimer")
						.setMessage(getString(R.string.disclaimer_text))
						.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// continue with delete
							}
						})
						.setIcon(R.drawable.ic_launcher)
						.show();
			}
		});
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

		Intent intent = getPackageManager().getLaunchIntentForPackage("com.suturesapp.peditor.peditor");
		// TODO: 05/03/17 don't hardcode that!
		intent.putExtra("imagePath","/Sutures/pica.png");
		//intent.putExtra("bitmap",bitmap);		// to heavy
		startActivity( intent );


	}

}
