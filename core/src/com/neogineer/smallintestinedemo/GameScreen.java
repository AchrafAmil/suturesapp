package com.neogineer.smallintestinedemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.neogineer.smallintestinedemo.utils.Constants;

/**
 * Created by neogineer on 30/08/16.
 */
public class GameScreen implements Screen {
    private final GameStage stage;


    public GameScreen(){
        stage = new GameStage();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.camera.setToOrtho(false, Constants.VIEWPORT_HEIGHT *width/(float)height, Constants.VIEWPORT_HEIGHT);
        //stage.getBatch().getProjectionMatrix()
        //        .setToOrtho2D(0, 0, Constants.VIEWPORT_HEIGHT *width/(float)height, Constants.VIEWPORT_HEIGHT);
        Gdx.app.log("camera zoom",stage.camera.zoom+"");
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
    }
}
