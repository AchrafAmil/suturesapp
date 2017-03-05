package com.suturesapp.smallintestinedemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.suturesapp.smallintestinedemo.tools.CloseTool;
import com.suturesapp.smallintestinedemo.tools.ConnectTool;
import com.suturesapp.smallintestinedemo.tools.CutTool;
import com.suturesapp.smallintestinedemo.tools.DndTool;
import com.suturesapp.smallintestinedemo.tools.Tool;
import com.suturesapp.smallintestinedemo.utils.Constants;
import com.suturesapp.smallintestinedemo.tools.TrashTool;

/**
 * Created by neogineer on 30/08/16.
 */
public class GameScreen implements Screen, SettingTool {
    private final com.suturesapp.smallintestinedemo.GameStage stage;
    private final com.suturesapp.smallintestinedemo.HudStage hudStage;


    public GameScreen(){
        stage = new com.suturesapp.smallintestinedemo.GameStage();
        hudStage = new com.suturesapp.smallintestinedemo.HudStage(this);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        com.suturesapp.smallintestinedemo.SmallIntestineDemoGame.getAssets().update();
        stage.draw();
        stage.act(delta);
        hudStage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.camera.setToOrtho(false, Constants.VIEWPORT_HEIGHT *width/(float)height, Constants.VIEWPORT_HEIGHT);
        //stage.getBatch().getProjectionMatrix()
        //        .setToOrtho2D(0, 0, Constants.VIEWPORT_HEIGHT *width/(float)height, Constants.VIEWPORT_HEIGHT);

        hudStage.getViewport().update(width, height, true);
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

    @Override
    public void setTool(Tool.Tools tool) {
        OrthographicCamera camera = stage.camera;
        World world = stage.world;

        switch (tool){
            case Cut:
                stage.setTool(new CutTool(world,camera));
                break;
            case Close:
                stage.setTool(new CloseTool(world, camera));
                break;
            case Connect:
                stage.setTool(new ConnectTool(world, camera));
                break;
            case Move:
                stage.setTool(new DndTool(world, camera, stage.groundBody));
                break;
            case Trash:
                stage.setTool(new TrashTool(world, camera));
                break;
        }
    }

    @Override
    public void load() {
        stage.load();
    }
}
