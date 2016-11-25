package com.neogineer.smallintestinedemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.neogineer.smallintestinedemo.organs.OrgansHolder;
import com.neogineer.smallintestinedemo.organs.SmallIntestine;
import com.neogineer.smallintestinedemo.organs.liver.Liver;
import com.neogineer.smallintestinedemo.organs.stomach.Stomach;
import com.neogineer.smallintestinedemo.tools.CloseTool;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.tools.CutTool;
import com.neogineer.smallintestinedemo.tools.DndTool;
import com.neogineer.smallintestinedemo.tools.Tool;
import com.neogineer.smallintestinedemo.utils.Constants;

import org.iforce2d.Jb2dJson;

/**
 * Created by neogineer on 30/08/16.
 */
public class GameStage extends Stage{

    private final float TIME_STEP = 1 / 300f;
    private float accumulator = 0f;

    World world ;
    boolean stop = false ;

    Body groundBody = null;

    OrthographicCamera camera;
    InputMultiplexer inputMultiplexer;
    private Box2DDebugRenderer renderer;

    private OrgansHolder organsHolder = new OrgansHolder();


    public GameStage(){
        world = new World(new Vector2(0,0), true);

        setupGround();

        setupCamera();

        //organsHolder.smallIntestine = new SmallIntestine(world, camera);
        //addActor(organsHolder.smallIntestine);
        //organsHolder.liver = new Liver(world, camera);
        //addActor(organsHolder.liver);

        organsHolder.stomach = new Stomach(world, camera);
        addActor(organsHolder.stomach);

        setupInputMultiplexer();

        renderer = new Box2DDebugRenderer();

    }

    private void setupCamera(){
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.zoom += 3f;
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();
    }

    private void setupInputMultiplexer(){
        this.inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexer.addProcessor(this);
        setTool(new DndTool(world, camera, groundBody));
    }

    @Override
    public void act(float delta) {
        super.act(delta);


        Array<Body> bodies = new Array<Body>(world.getBodyCount());
        world.getBodies(bodies);

        for (Body body : bodies) {
            update(body);
        }

        Array<Actor> actors = getActors();
        for (Actor a : actors) {
            a.act(delta);
        }

        // Fixed time step
        accumulator += delta;

        while (accumulator >= delta) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }
    }

    private void update(Body body){
        if(stop){
            body.setLinearVelocity(0,0);
            body.setAngularVelocity(0);
        }

    }



    public void setTool(Tool tool){
        if(inputMultiplexer.size()>=2)
            this.inputMultiplexer.removeProcessor(inputMultiplexer.size()-1);
        this.inputMultiplexer.addProcessor(tool);

        if(tool instanceof CutTool)
            organsHolder.highlight();
        else
            organsHolder.unhighlight();
    }



    @Override
    public void draw() {
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.draw();
        //renderer.render(world, camera.combined);
    }

    private void setupGround(){
        // next we create a static ground platform. This platform
        // is not moveable and will not react to any influences from
        // outside. It will however influence other bodies. First we
        // create a PolygonShape that holds the form of the platform.
        // it will be 100 meters wide and 2 meters high, centered
        // around the origin
        PolygonShape groundPoly = new PolygonShape();
        groundPoly.setAsBox(2, 10);

        // next we create the body for the ground platform. It's
        // simply a static body.
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set(200,15);
        groundBody = world.createBody(groundBodyDef);

        // finally we add a fixture to the body using the polygon
        // defined above. Note that we have to dispose PolygonShapes
        // and CircleShapes once they are no longer used. This is the
        // only time you have to care explicitly for memory management.
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundPoly;
        fixtureDef.filter.groupIndex = 0;
        groundBody.createFixture(fixtureDef);
        groundPoly.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        stop = false;
        if(screenX<180 && screenY<180)
            this.keyDown(0);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        stop = true;
        return super.touchUp(screenX, screenY, pointer, button);
    }

    int clicks = 0 ;
    @Override
    public boolean keyDown(int keyCode) {
        clicks++;
        switch (clicks%4){
            case 0:
                setTool(new DndTool(world, camera, groundBody));
                Gdx.app.log("keyDown","DND tool");
                break;
            case 1:
                setTool(new CutTool(world, camera));
                Gdx.app.log("keyDown", "Cut tool");
                break;
            case 2:
                setTool(new CloseTool(world, camera));
                Gdx.app.log("keyDown", "Close tool");
                break;
            case 3:
                setTool(new ConnectTool(world, camera));
                Gdx.app.log("keyDown","Connect Tool");
                break;
        }

        return super.keyDown(keyCode);
    }
}
