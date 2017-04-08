package com.suturesapp.workspace;

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
import com.suturesapp.workspace.organs.OpenableSide;
import com.suturesapp.workspace.organs.abdominalconnector.AbdominalConnectorOrganPart;
import com.suturesapp.workspace.organs.bileduct.BileDuctOrganPart;
import com.suturesapp.workspace.organs.duedenum.DuodenumOrganPart;
import com.suturesapp.workspace.organs.esophagus.EsophagusOrganPart;
import com.suturesapp.workspace.organs.gallbladder.GallbladderOrganPart;
import com.suturesapp.workspace.organs.pancreas.PancreasOrganPart;
import com.suturesapp.workspace.organs.rope.ColonOrganPart;
import com.suturesapp.workspace.tools.CloseTool;
import com.suturesapp.workspace.tools.DndTool;
import com.suturesapp.workspace.tools.Tool;
import com.suturesapp.workspace.utils.Constants;

import net.dermetfan.gdx.physics.box2d.kryo.serializers.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by neogineer on 30/08/16.
 */
public class GameStage extends Stage {

    private float TIME_STEP = 1 / 100f;
    private float accumulator = 0f;

    World world ;
    boolean stop = false ;

    Body groundBody = null;

    OrthographicCamera camera;
    InputMultiplexer inputMultiplexer;
    private Box2DDebugRenderer renderer;

    private com.suturesapp.workspace.organs.OrgansHolder organsHolder = new com.suturesapp.workspace.organs.OrgansHolder();

    public Kryo kryo = new Kryo();

    public GameStage(){
        world = new World(new Vector2(0,0), true);
        com.suturesapp.workspace.organs.OrgansHolder.world = world;

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



        kryo.register(com.suturesapp.workspace.organs.stomach.StomachOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new com.suturesapp.workspace.organs.stomach.StomachOrganPart(opDef);
            }
        });

        kryo.register(com.suturesapp.workspace.organs.rectum.RectumOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new com.suturesapp.workspace.organs.rectum.RectumOrganPart(opDef);
            }
        });

        kryo.register(DuodenumOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new DuodenumOrganPart(opDef);
            }
        });

        kryo.register(EsophagusOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new EsophagusOrganPart(opDef);
            }
        });

        kryo.register(com.suturesapp.workspace.organs.liver.LiverOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new com.suturesapp.workspace.organs.liver.LiverOrganPart(opDef);
            }
        });

        kryo.register(com.suturesapp.workspace.organs.rope.SmallIntestineOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new com.suturesapp.workspace.organs.rope.SmallIntestineOrganPart(opDef);
            }
        });

        kryo.register(com.suturesapp.workspace.organs.appendix.AppendixOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new com.suturesapp.workspace.organs.appendix.AppendixOrganPart(opDef);
            }
        });

        kryo.register(ColonOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new ColonOrganPart(opDef);
            }
        });

        kryo.register(com.suturesapp.workspace.organs.abdominalwall.AbdominalWallOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new com.suturesapp.workspace.organs.abdominalwall.AbdominalWallOrganPart(opDef);
            }
        });

        kryo.register(com.suturesapp.workspace.organs.spleen.SpleenOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new com.suturesapp.workspace.organs.spleen.SpleenOrganPart(opDef);
            }
        });

        kryo.register(PancreasOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new PancreasOrganPart(opDef);
            }
        });

        kryo.register(BileDuctOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new BileDuctOrganPart(opDef);
            }
        });

        kryo.register(GallbladderOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new GallbladderOrganPart(opDef);
            }
        });

        kryo.register(AbdominalConnectorOrganPart.class, new Serializer<com.suturesapp.workspace.organs.OrganPart>() {
            @Override
            public void write(Kryo kryo, Output output, com.suturesapp.workspace.organs.OrganPart op) {
                kryo.writeObject(output, op.getOPDef());
            }

            @Override
            public com.suturesapp.workspace.organs.OrganPart read(Kryo kryo, Input input, Class<com.suturesapp.workspace.organs.OrganPart> type) {
                com.suturesapp.workspace.organs.OrganPartDefinition opDef = kryo.readObject(input, com.suturesapp.workspace.organs.OrganPartDefinition.class);
                return new AbdominalConnectorOrganPart(opDef);
            }
        });

        kryo.register(OpenableSide.State.class, new Serializer<OpenableSide.State>() {
            @Override
            public void write(Kryo kryo, Output output, OpenableSide.State state) {
                kryo.writeObject(output, state);
            }

            @Override
            public OpenableSide.State read(Kryo kryo, Input input, Class<OpenableSide.State> type) {
                OpenableSide.State state = kryo.readObject(input,OpenableSide.State.class);
                if(state==null)
                    return OpenableSide.State.NORMAL;
                else
                    return state;
            }
        });



    }

    private void setupCamera(){
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.zoom = Constants.INITIAL_ZOOM;
        //camera.position.set(camera.zoom * camera.viewportWidth/ 2.0f, camera.zoom * camera.viewportHeight/ 2.0f, 0);
        camera.update();
        com.suturesapp.workspace.organs.OrgansHolder.camera = camera;
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



    public void setTool(Tool tool){
        ArrayList<InputProcessor> processorsToRemove = new ArrayList<>();
        int size = inputMultiplexer.getProcessors().size;
        for(int i = 0; i<size; i++){
            InputProcessor ip = inputMultiplexer.getProcessors().get(i);
            if(ip instanceof Tool || ip instanceof GestureDetector)
                processorsToRemove.add(ip);
        }
        for(InputProcessor ip : processorsToRemove){
            inputMultiplexer.removeProcessor(ip);
        }

        this.inputMultiplexer.addProcessor(new GestureDetector(tool));
        this.inputMultiplexer.addProcessor(tool);

        if(tool instanceof com.suturesapp.workspace.tools.CutTool)
            organsHolder.highlight();
        else
            organsHolder.unhighlight();

        //if(tool instanceof DndTool)
        //    this.inputMultiplexer.addProcessor(new GestureDetector((DndTool)tool));
    }



    @Override
    public void draw() {
        Gdx.gl.glClearColor(233/255f, 232/255f, 231/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.draw();
        if(Constants.ENABLE_DEBUG)
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
                Constants.ENABLE_DEBUG = !Constants.ENABLE_DEBUG;
                break;
            default:
                Gdx.app.log("Keydown", "not handled, code: "+keyCode);
                return false;
        }

        return true;
    }

    private void clickedA(){
        clicksA++;

        switch (clicksA%4){
            case 0:
                setTool(new DndTool(world, camera, groundBody));
                Gdx.app.log("keyDown","DND tool");
                break;
            case 1:
                setTool(new com.suturesapp.workspace.tools.CutTool(world, camera, groundBody));
                Gdx.app.log("keyDown", "Cut tool");
                break;
            case 2:
                setTool(new CloseTool(world, camera, groundBody));
                Gdx.app.log("keyDown", "Close tool");
                break;
            case 3:
                setTool(new com.suturesapp.workspace.tools.ConnectTool(world, camera, groundBody));
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
