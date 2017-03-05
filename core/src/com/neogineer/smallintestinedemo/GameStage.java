package com.neogineer.smallintestinedemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.IntArray;
import com.neogineer.smallintestinedemo.organs.OrganPartDefinition;
import com.neogineer.smallintestinedemo.organs.OrgansHolder;
import com.neogineer.smallintestinedemo.organs.abdominalwall.AbdominalWallOrganPart;
import com.neogineer.smallintestinedemo.organs.appendix.AppendixOrganPart;
import com.neogineer.smallintestinedemo.organs.esophagus.EsophagusOrganPart;
import com.neogineer.smallintestinedemo.organs.rectum.RectumOrganPart;
import com.neogineer.smallintestinedemo.organs.rope.ColonOrganPart;
import com.neogineer.smallintestinedemo.tools.CloseTool;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.tools.CutTool;
import com.neogineer.smallintestinedemo.organs.stomach.StomachOrganPart;
import com.neogineer.smallintestinedemo.tools.DndTool;

import net.dermetfan.gdx.physics.box2d.kryo.serializers.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by neogineer on 30/08/16.
 */
public class GameStage extends Stage{

    private float TIME_STEP = 1 / 100f;
    private float accumulator = 0f;

    World world ;
    boolean stop = false ;

    Body groundBody = null;

    OrthographicCamera camera;
    InputMultiplexer inputMultiplexer;
    private Box2DDebugRenderer renderer;

    private OrgansHolder organsHolder = new OrgansHolder();

    public Kryo kryo = new Kryo();

    public GameStage(){
        world = new World(new Vector2(0,0), true);
        OrgansHolder.world = world;

        setupKryo();

        setupGround();

        setupCamera();

        organsHolder.start(this);

        setupInputMultiplexer();

        renderer = new Box2DDebugRenderer();




    }

    private void setupKryo() {
        kryo.register(Array.class, new Serializer<Array>() {
            {
                setAcceptsNull(true);
            }

            private Class genericType;

            public void setGenerics (Kryo kryo, Class[] generics) {
                if (generics != null && kryo.isFinal(generics[0])) genericType = generics[0];
                else genericType = null;
            }

            public void write (Kryo kryo, Output output, Array array) {
                int length = array.size;
                output.writeInt(length, true);
                if (length == 0) {
                    genericType = null;
                    return;
                }
                if (genericType != null) {
                    Serializer serializer = kryo.getSerializer(genericType);
                    genericType = null;
                    for (Object element : array)
                        kryo.writeObjectOrNull(output, element, serializer);
                } else {
                    for (Object element : array)
                        kryo.writeClassAndObject(output, element);
                }
            }

            public Array read (Kryo kryo, Input input, Class<Array> type) {
                Array array = new Array();
                kryo.reference(array);
                int length = input.readInt(true);
                array.ensureCapacity(length);
                if (genericType != null) {
                    Class elementClass = genericType;
                    Serializer serializer = kryo.getSerializer(genericType);
                    genericType = null;
                    for (int i = 0; i < length; i++)
                        array.add(kryo.readObjectOrNull(input, elementClass, serializer));
                } else {
                    for (int i = 0; i < length; i++)
                        array.add(kryo.readClassAndObject(input));
                }
                return array;
            }
        });

        kryo.register(IntArray.class, new Serializer<IntArray>() {
            {
                setAcceptsNull(true);
            }

            public void write (Kryo kryo, Output output, IntArray array) {
                int length = array.size;
                output.writeInt(length, true);
                if (length == 0) return;
                for (int i = 0, n = array.size; i < n; i++)
                    output.writeInt(array.get(i), true);
            }

            public IntArray read (Kryo kryo, Input input, Class<IntArray> type) {
                IntArray array = new IntArray();
                kryo.reference(array);
                int length = input.readInt(true);
                array.ensureCapacity(length);
                for (int i = 0; i < length; i++)
                    array.add(input.readInt(true));
                return array;
            }
        });

        kryo.register(FloatArray.class, new Serializer<FloatArray>() {
            {
                setAcceptsNull(true);
            }

            public void write (Kryo kryo, Output output, FloatArray array) {
                int length = array.size;
                output.writeInt(length, true);
                if (length == 0) return;
                for (int i = 0, n = array.size; i < n; i++)
                    output.writeFloat(array.get(i));
            }

            public FloatArray read (Kryo kryo, Input input, Class<FloatArray> type) {
                FloatArray array = new FloatArray();
                kryo.reference(array);
                int length = input.readInt(true);
                array.ensureCapacity(length);
                for (int i = 0; i < length; i++)
                    array.add(input.readFloat());
                return array;
            }
        });

        kryo.register(Color.class, new Serializer<Color>() {
            public Color read (Kryo kryo, Input input, Class<Color> type) {
                Color color = new Color();
                Color.rgba8888ToColor(color, input.readInt());
                return color;
            }

            public void write (Kryo kryo, Output output, Color color) {
                output.writeInt(Color.rgba8888(color));
            }
        });

        kryo.register(ChainShape.class, ChainShapeSerializer.instance);
        kryo.register(PolygonShape.class, PolygonShapeSerializer.instance);
        kryo.register(CircleShape.class, CircleShapeSerializer.instance);
        kryo.register(EdgeShape.class, EdgeShapeSerializer.instance);



        kryo.register(StomachOrganPart.class, new Serializer<com.neogineer.smallintestinedemo.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.neogineer.smallintestinedemo.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.neogineer.smallintestinedemo.organs.OrganPart read(Kryo kryo, Input input, Class<com.neogineer.smallintestinedemo.organs.OrganPart> type) {
                OrganPartDefinition opDef = kryo.readObject(input, OrganPartDefinition.class);
                return new StomachOrganPart(opDef);
            }
        });

        kryo.register(RectumOrganPart.class, new Serializer<com.neogineer.smallintestinedemo.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.neogineer.smallintestinedemo.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.neogineer.smallintestinedemo.organs.OrganPart read(Kryo kryo, Input input, Class<com.neogineer.smallintestinedemo.organs.OrganPart> type) {
                OrganPartDefinition opDef = kryo.readObject(input, OrganPartDefinition.class);
                return new RectumOrganPart(opDef);
            }
        });

        kryo.register(com.neogineer.smallintestinedemo.organs.duedenum.DuodenumOrganPart.class, new Serializer<com.neogineer.smallintestinedemo.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.neogineer.smallintestinedemo.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.neogineer.smallintestinedemo.organs.OrganPart read(Kryo kryo, Input input, Class<com.neogineer.smallintestinedemo.organs.OrganPart> type) {
                OrganPartDefinition opDef = kryo.readObject(input, OrganPartDefinition.class);
                return new com.neogineer.smallintestinedemo.organs.duedenum.DuodenumOrganPart(opDef);
            }
        });

        kryo.register(EsophagusOrganPart.class, new Serializer<com.neogineer.smallintestinedemo.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.neogineer.smallintestinedemo.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.neogineer.smallintestinedemo.organs.OrganPart read(Kryo kryo, Input input, Class<com.neogineer.smallintestinedemo.organs.OrganPart> type) {
                OrganPartDefinition opDef = kryo.readObject(input, OrganPartDefinition.class);
                return new EsophagusOrganPart(opDef);
            }
        });

        kryo.register(com.neogineer.smallintestinedemo.organs.liver.LiverOrganPart.class, new Serializer<com.neogineer.smallintestinedemo.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.neogineer.smallintestinedemo.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.neogineer.smallintestinedemo.organs.OrganPart read(Kryo kryo, Input input, Class<com.neogineer.smallintestinedemo.organs.OrganPart> type) {
                OrganPartDefinition opDef = kryo.readObject(input, OrganPartDefinition.class);
                return new com.neogineer.smallintestinedemo.organs.liver.LiverOrganPart(opDef);
            }
        });

        kryo.register(com.neogineer.smallintestinedemo.organs.rope.SmallIntestineOrganPart.class, new Serializer<com.neogineer.smallintestinedemo.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.neogineer.smallintestinedemo.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.neogineer.smallintestinedemo.organs.OrganPart read(Kryo kryo, Input input, Class<com.neogineer.smallintestinedemo.organs.OrganPart> type) {
                OrganPartDefinition opDef = kryo.readObject(input, OrganPartDefinition.class);
                return new com.neogineer.smallintestinedemo.organs.rope.SmallIntestineOrganPart(opDef);
            }
        });

        kryo.register(AppendixOrganPart.class, new Serializer<com.neogineer.smallintestinedemo.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.neogineer.smallintestinedemo.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.neogineer.smallintestinedemo.organs.OrganPart read(Kryo kryo, Input input, Class<com.neogineer.smallintestinedemo.organs.OrganPart> type) {
                OrganPartDefinition opDef = kryo.readObject(input, OrganPartDefinition.class);
                return new AppendixOrganPart(opDef);
            }
        });

        kryo.register(ColonOrganPart.class, new Serializer<com.neogineer.smallintestinedemo.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.neogineer.smallintestinedemo.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.neogineer.smallintestinedemo.organs.OrganPart read(Kryo kryo, Input input, Class<com.neogineer.smallintestinedemo.organs.OrganPart> type) {
                OrganPartDefinition opDef = kryo.readObject(input, OrganPartDefinition.class);
                return new ColonOrganPart(opDef);
            }
        });

        kryo.register(AbdominalWallOrganPart.class, new Serializer<com.neogineer.smallintestinedemo.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.neogineer.smallintestinedemo.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.neogineer.smallintestinedemo.organs.OrganPart read(Kryo kryo, Input input, Class<com.neogineer.smallintestinedemo.organs.OrganPart> type) {
                OrganPartDefinition opDef = kryo.readObject(input, OrganPartDefinition.class);
                return new AbdominalWallOrganPart(opDef);
            }
        });



    }

    private void setupCamera(){
        camera = new OrthographicCamera(com.neogineer.smallintestinedemo.utils.Constants.VIEWPORT_WIDTH, com.neogineer.smallintestinedemo.utils.Constants.VIEWPORT_HEIGHT);
        camera.zoom = com.neogineer.smallintestinedemo.utils.Constants.INITIAL_ZOOM;
        //camera.position.set(camera.zoom * camera.viewportWidth/ 2.0f, camera.zoom * camera.viewportHeight/ 2.0f, 0);
        camera.update();
        OrgansHolder.camera = camera;
    }

    private void setupInputMultiplexer(){
        this.inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexer.addProcessor(this);
        setTool(new DndTool(world, camera,groundBody));
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



    public void setTool(com.neogineer.smallintestinedemo.tools.Tool tool){
        ArrayList<InputProcessor> processorsToRemove = new ArrayList<>();
        int size = inputMultiplexer.getProcessors().size;
        for(int i = 0; i<size; i++){
            InputProcessor ip = inputMultiplexer.getProcessors().get(i);
            if(ip instanceof com.neogineer.smallintestinedemo.tools.Tool || ip instanceof GestureDetector)
                processorsToRemove.add(ip);
        }
        for(InputProcessor ip : processorsToRemove){
            inputMultiplexer.removeProcessor(ip);
        }

        this.inputMultiplexer.addProcessor(tool);

        if(tool instanceof CutTool)
            organsHolder.highlight();
        else
            organsHolder.unhighlight();

        if(tool instanceof DndTool)
            this.inputMultiplexer.addProcessor(new GestureDetector((DndTool)tool));
    }



    @Override
    public void draw() {
        Gdx.gl.glClearColor(233/255f, 232/255f, 231/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.draw();
        if(com.neogineer.smallintestinedemo.utils.Constants.ENABLE_DEBUG)
            renderer.render(world, camera.combined);
    }

    private void setupGround(){
        // next we create a static ground platform. This platform
        // is not moveable and will not react to any influences from
        // outside. It will however influence other bodies. First we
        // create a PolygonShape that holds the form of the platform.
        // it will be 100 meters wide and 2 meters high, centered
        // around the origin
        PolygonShape groundPoly = new PolygonShape();
        groundPoly.setAsBox(200, 10);

        // next we create the body for the ground platform. It's
        // simply a static body.
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set(2000,0);
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
        //if(screenX<180 && screenY<180)
        //    this.keyDown(0);
        return super.touchDown(screenX, screenY, pointer, button);

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        stop = true;
        return super.touchUp(screenX, screenY, pointer, button);
    }

    int clicksA = 0 ;
    int clicksB = 0 ;
    @Override
    public boolean keyDown(int keyCode) {

        switch (keyCode){
            case 29:
                // switch tools
                this.clickedA();
                break;
            case 30:
                // instead of switching tools, we'll now save/load game states (always for debug purpose)
                this.clickedB();
                break;
            case 40:
                load();
                break;
            case 47:
                save();
                break;
            case 81:
                camera.zoom-=0.25f;
                camera.update();
                break;
            case 69:
                camera.zoom+=0.25f;
                camera.update();
                break;
            case 19:
                camera.translate(0,5f);
                camera.update();
                break;
            case 20:
                camera.translate(0,-5f);
                camera.update();
                break;
            case 21:
                camera.translate(-5f,0);
                camera.update();
                break;
            case 22:
                camera.translate(5f,0);
                camera.update();
                break;
            case 129:
                com.neogineer.smallintestinedemo.utils.Constants.ENABLE_DEBUG = !com.neogineer.smallintestinedemo.utils.Constants.ENABLE_DEBUG;
                break;
            default:
                Gdx.app.log("Keydown", "not handled, code: "+keyCode);
                break;
        }

        return super.keyDown(keyCode);
    }

    private void clickedA(){
        clicksA++;

        switch (clicksA%4){
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
    }

    private void clickedB(){
        clicksB++;
        if(clicksB %2==1){
            save();
        }else{
            load();
        }
    }

    public void save(){
        try {
            Output output = new Output(new FileOutputStream("kryo_save.bin"));
            organsHolder.saveState(kryo, output);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            accumulator=0;
        }
    }

    public void load(){
        try {
            Input input = new Input(Gdx.files.internal("kryo_save.bin").read());
            organsHolder.loadState(kryo, input);
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            accumulator=0;
        }
    }
}
