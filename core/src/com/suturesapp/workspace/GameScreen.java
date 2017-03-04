package com.suturesapp.workspace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.suturesapp.workspace.tools.TrashTool;

/**
 * Created by neogineer on 30/08/16.
 */
public class GameScreen implements Screen, SettingTool {
    private final GameStage stage;
    private final HudStage hudStage;


    public GameScreen(){
        stage = new GameStage();
        hudStage = new HudStage(this);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        SmallIntestineDemoGame.getAssets().update();
        stage.draw();
        stage.act(delta);
        hudStage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.camera.setToOrtho(false, com.suturesapp.workspace.utils.Constants.VIEWPORT_HEIGHT *width/(float)height, com.suturesapp.workspace.utils.Constants.VIEWPORT_HEIGHT);
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
    public void setTool(com.suturesapp.workspace.tools.Tool.Tools tool) {
        OrthographicCamera camera = stage.camera;
        World world = stage.world;

        switch (tool){
            case Cut:
                stage.setTool(new com.suturesapp.workspace.tools.CutTool(world,camera));
                break;
            case Close:
                stage.setTool(new com.suturesapp.workspace.tools.CloseTool(world, camera));
                break;
            case Connect:
                stage.setTool(new com.suturesapp.workspace.tools.ConnectTool(world, camera));
                break;
            case Move:
                stage.setTool(new com.suturesapp.workspace.tools.DndTool(world, camera, stage.groundBody));
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
