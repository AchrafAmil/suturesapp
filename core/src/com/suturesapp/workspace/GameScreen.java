package com.suturesapp.workspace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.suturesapp.workspace.tools.CloseTool;
import com.suturesapp.workspace.tools.DndTool;
import com.suturesapp.workspace.tools.Tool;
import com.suturesapp.workspace.utils.Constants;
import com.suturesapp.workspace.tools.TrashTool;

/**
 * TEST TEST
 */
public class GameScreen implements Screen, SettingTool {
    private final GameStage stage;
    private final com.suturesapp.workspace.HudStage hudStage;


    public GameScreen(){
        stage = new GameStage();
        hudStage = new com.suturesapp.workspace.HudStage(this);
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
                stage.setTool(new com.suturesapp.workspace.tools.CutTool(world, camera, stage.groundBody));
                break;
            case Close:
                stage.setTool(new CloseTool(world, camera, stage.groundBody));
                break;
            case Connect:
                stage.setTool(new com.suturesapp.workspace.tools.ConnectTool(world, camera, stage.groundBody));
                break;
            case Move:
                stage.setTool(new DndTool(world, camera, stage.groundBody));
                break;
            case Trash:
                stage.setTool(new TrashTool(world, camera, stage.groundBody));
                break;
            case Tumor:
                stage.setTool(new com.suturesapp.workspace.tools.TumorTool(world, camera, stage.groundBody));
        }
    }

    @Override
    public void load() {
        stage.load();
    }

    Vector2 tmpVec = new Vector2();
    @Override
    public void showPathology() {
        tmpVec.set(TrashTool.TRASH_POSITION_X-Constants.CAMERA_INITIAL_TRANSLATION.x, TrashTool.TRASH_POSITION_Y-Constants.CAMERA_INITIAL_TRANSLATION.y);
        stage.camera.translate(tmpVec);
        stage.camera.zoom = Constants.INITIAL_ZOOM;
        stage.camera.update();
        hudStage.showPathologyBoxButtons();
        setTool(Tool.Tools.Move);
    }

    @Override
    public void showWorkspace() {
        tmpVec.set(-TrashTool.TRASH_POSITION_X+Constants.CAMERA_INITIAL_TRANSLATION.x, -TrashTool.TRASH_POSITION_Y+Constants.CAMERA_INITIAL_TRANSLATION.y);
        stage.camera.translate(tmpVec);
        stage.camera.zoom = Constants.INITIAL_ZOOM;
        stage.camera.update();
        hudStage.showWorkspaceButtons();
        setTool(Tool.Tools.Move);
    }
}
