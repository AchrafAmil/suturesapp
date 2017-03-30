package com.neogineer.smallintestinedemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by neogineer on 30/03/17.
 */
public class SplashScreen implements Screen {
    SpriteBatch batch;
    Sprite splashImg;

    public SplashScreen(){
        batch = new SpriteBatch();
        splashImg = new Sprite(new Texture(Gdx.files.internal("connectButton.png")));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        batch.begin();
        splashImg.draw(batch);
        batch.end();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        splashImg.getTexture().dispose();
        batch.dispose();
    }
}
