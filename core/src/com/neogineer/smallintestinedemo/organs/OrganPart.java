package com.neogineer.smallintestinedemo.organs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.neogineer.smallintestinedemo.organs.stomach.StomachOrganPart;
import com.neogineer.smallintestinedemo.utils.Constants;
import com.neogineer.smallintestinedemo.utils.Utils;

import java.util.ArrayList;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 24/10/16.
 */
public abstract class OrganPart extends Actor implements Connectable {

    public String identifier = "";

    public Body body;

    protected World mWorld;
    OrthographicCamera camera;
    protected Sprite baseSprite ;
    protected Sprite highlightedBaseSprite;
    protected boolean highlighted;
    public Vector2 origin ;
    Organ organCallback ;
    public float scale = 1f ;
    protected Vector2 position = new Vector2(8, 19);
    protected float rotation = 0;


    protected ArrayList<SuturePoint> suturePoints = new ArrayList<SuturePoint>();

    protected ArrayList<OpenableSide> openableSides = new ArrayList<OpenableSide>();


    public OrganPart(World world, OrthographicCamera camera, Organ callback, String identifier, float scale, Vector2 position, float rotation ){
        this.mWorld = world;
        this.camera = camera;
        this.organCallback = callback;
        this.identifier = identifier;
        this.scale = scale;
        if(position!=null)
            this.position = position;
        this.rotation = rotation;
    }

    public OrganPart(World world, OrthographicCamera camera, Organ callback, float scale, OrganPartDefinition opDef){
        this(world, camera, callback, opDef.getIdentifier(), scale, opDef.position, opDef.angle);
    }

    public OrganPart(OrganPartDefinition opDef){
        this(OrgansHolder.world, OrgansHolder.camera, OrgansHolder.organFromName(opDef.getOrganName()),
                Utils.scaleFromName(opDef.getOrganName()), opDef );
    }


    /**
     * Called directly after object initialization.
     * This will use the OrganPart fields' values to load and create the appropriate body shape and sprite.
     * @param path Json file containing this organ's organParts definitions.
     */
    protected void setupBody(String path){
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal(path));
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.DynamicBody;
        //if(this instanceof SmallIntestineOrganPart)
        //    if(((SmallIntestineOrganPart)this).id==145)
        //        bDef.type = BodyDef.BodyType.KinematicBody;         // just while developing
        if(this.getClass().getSimpleName().equals("DuodenumOrganPart") && this.identifier.equals("1"))
            bDef.type = BodyDef.BodyType.KinematicBody;         // just while developing
        bDef.position.set(this.position);
        bDef.angle = this.rotation;
        body = mWorld.createBody(bDef);
        body.setUserData(this);

        FixtureDef fix = new FixtureDef();
        fix.density = 0.1f;
        fix.friction = 0f;
        fix.restitution = 0.5f;

        origin = loader.getOrigin("base"+identifier, 1).cpy();

        Texture baseSpriteTexture = new Texture(Gdx.files.internal(loader.getImagePath("base"+identifier) ));
        baseSprite = new Sprite(baseSpriteTexture);

        loadHighlightedSprite(loader, identifier);
        
        attachFixture(loader, identifier, fix);

    }

    /**
     * called from setupBody after loading the base sprite.
     * OrganParts implement this to load the appropriate highlighted sprite.
     */
    protected abstract void loadHighlightedSprite(BodyEditorLoader loader, String identifier);


    protected void attachFixture(BodyEditorLoader loader, String identifier, FixtureDef fix) {
        loader.attachFixture(body, "base"+ identifier,fix, getVerticesScale()*scale, 1, 1);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if(camera!=null){
            drawBaseSprite(batch);
            drawOpenableSides(batch);
            drawSuturePoints(batch);
        }
    }

    private void drawBaseSprite(Batch batch){
        Sprite baseSprite = this.getBaseSprite();
        batch.setProjectionMatrix(camera.combined);
        float someScale = 0.1f;

        float w = scale * (baseSprite.getTexture().getWidth())/2 *someScale;
        float h = scale * (baseSprite.getTexture().getHeight())/2 *someScale;

        Vector3 bodyPixelPos = camera.project(new Vector3(body.getPosition().x, body.getPosition().y, 0))
                .scl(someScale*camera.viewportHeight/(Gdx.graphics.getHeight()/10f/camera.zoom)).sub(w/2, h/2, 0);

        baseSprite.setSize(w,h);
        baseSprite.setOrigin(w/2, h/2);
        baseSprite.setPosition(bodyPixelPos.x, bodyPixelPos.y);
        baseSprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        baseSprite.draw(batch);
    }

    private void drawOpenableSides(Batch batch){
        for(OpenableSide side: openableSides){

            Sprite sprite = side.getSprite();
            if(sprite==null)
                continue;


            batch.setProjectionMatrix(camera.combined);
            float someScale = 0.1f;

            float w = scale * (baseSprite.getTexture().getWidth())/2 *someScale;
            float h = scale * (baseSprite.getTexture().getHeight())/2 *someScale;


            Vector3 bodyPixelPos = camera.project(new Vector3(body.getPosition().x, body.getPosition().y, 0))
                    .scl(someScale*camera.viewportHeight/(Gdx.graphics.getHeight()/10f/camera.zoom)).sub(w/2, h/2, 0);

            sprite.setSize(w,h);
            sprite.setOrigin(w/2, h/2);
            sprite.setPosition(bodyPixelPos.x, bodyPixelPos.y);
            sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

            side.draw(batch);
        }
    }

    private void drawSuturePoints(Batch batch){
        for(SuturePoint sp : suturePoints){

            Sprite sprite = sp.getSprite();
            if(sprite==null)
                continue;

            Vector2 spWorldCoord = body.getWorldPoint(sp.getLocalCoord());


            batch.setProjectionMatrix(camera.combined);
            float someScale = 0.1f;

            float w = scale * (sprite.getTexture().getWidth())/2f *someScale;
            float h = scale * (sprite.getTexture().getHeight())/2f *someScale;

            Vector3 spPixelPos = camera.project(new Vector3(spWorldCoord.x, spWorldCoord.y, 0))
                    .scl(someScale*camera.viewportHeight/(Gdx.graphics.getHeight()/10f/camera.zoom)).sub(w/2, h/2, 0);


            sprite.setSize(w,h);

            // it's gonna rotate around this origin, let it be the suturePoint's center.
            sprite.setOrigin(w/2, h/2);

            // same as origin, here expressed in global screen coordinates (not relative, not local)
            sprite.setPosition(spPixelPos.x, spPixelPos.y);

            sprite.setRotation((body.getAngle() + sp.getRotation()) * MathUtils.radiansToDegrees);

            sprite.draw(batch);
        }
    }

    /**
     * checks if highlighting is ON.
     * @return either base sprite or highlighted base sprite.
     */
    private Sprite getBaseSprite(){
        return (highlighted? highlightedBaseSprite:baseSprite);
    }

    /**
     * override this to either set hightlighted or ignore it.
     * @return isHighlighted();
     */
    public abstract boolean setHighlighted(boolean highlighted);

    public boolean isHighlighted(){
        return highlighted;
    }

    /**
     * @return dims in meters from view port & zooming.
     */
    public Vector2 getDimensions(){
        return getVertex(origin.x*2, origin.y*2).scl(2);
    }

    /**
     * get a vertex local coordinates
     * @param x,y relative coordinates as in the json file. No scale.
     * @return coordinates scaled and relative to origin.
     */
    public Vector2 getVertex(double x, double y){
        return
                new Vector2( (float)(x-this.origin.x)*getVerticesScale()*scale , (float)(y-this.origin.y)*getVerticesScale()*scale) ;
    }

    public float getVerticesScale(){
        return getHeight()/ origin.y / Constants.VERTICES_SCALE_FACTOR;
    }

    /**
     * @return width in pixels
     */
    public float getWidth(){
        return this.baseSprite.getWidth();
    };

    /**
     * @return height in pixels
     */
    public float getHeight(){
        return this.baseSprite.getHeight();
    };

    public String getIdentifier(){
        return this.identifier;
    }

    @Override
    public boolean addSuturePoint(SuturePoint sp) {
        this.correctAnchorAndRotation(sp);
        sp.setRelatedOrganPart(this);
        this.suturePoints.add(sp);

        if(this instanceof Openable){
            try {
                ((Openable)this).getOpenableSide(sp.getLocalCoord().x, sp.getLocalCoord().y).normal();
            }catch (NullPointerException npe){
            }
        }

        return true;
    }

    @Override
    public boolean deleteSuturePoint(SuturePoint sp) {
        this.suturePoints.remove(sp);

        if(this instanceof Openable){
            Vector2 vec = body.getWorldPoint(new Vector2(sp.getLocalCoord().x, sp.getLocalCoord().y));
            ( (Openable) this).open(vec.x, vec.y);
        }

        this.body.applyLinearImpulse(new Vector2(sp.getLocalCoord().x*10*scale,sp.getLocalCoord().y*10*scale), body.getWorldCenter(),true);

        return true;
    }

    @Override
    public boolean deleteSuturePoint(RevoluteJoint joint) {
        for(SuturePoint sp: this.suturePoints){
            if(sp.getRelatedJoint()==joint){
                return this.deleteSuturePoint(sp);
            }
        }
        return false;
    }

    @Override
    public ArrayList<SuturePoint> getSuturePoints() {
        return this.suturePoints;
    }

    /**
     * called from connectTool, to check if we can connect at this point.
     * @param point
     * @return
     */
    @Override
    public boolean connectionIntent(Vector2 point) {
        return false;
    }

    /**
     * called from addSuturePoint
     * @param suturePoint
     * @return
     */
    @Override
    public SuturePoint correctAnchorAndRotation(SuturePoint suturePoint) {
        return suturePoint;
    }

    /**
     * called from connectTool
     * @param def
     */
    @Override
    public void correctJointDef(RevoluteJointDef def) {
    }

    /**
     * @return a small representation of this organPart state. can be used to clone or store.
     */
    public OrganPartDefinition getOPDef(){
        OrganPartDefinition opDef = new OrganPartDefinition();
        opDef.position = this.body.getPosition().cpy();
        opDef.angle = this.body.getAngle();
        opDef.fullIdentifier = this.getClass().getSimpleName()+this.getIdentifier();
        if(this instanceof SmallIntestineOrganPart)
            opDef.smallIntestineId = ((SmallIntestineOrganPart)this).id;

        for(OpenableSide os : openableSides){
            opDef.openableSidesStates.add(os.getState());
        }

        return opDef;
    }

    public void destroy(){
        mWorld.destroyBody(body);
        baseSprite.getTexture().dispose();
    }

    public String getFullIdentifier(){
        return this.getClass().getSimpleName()+identifier;
    }

    /**
     * Returns the closest vector on the body's edge.
     */
    public Vector2 pushToEdge(Vector2 vec){
        Vector2 vec1 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec2 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec3 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec4 = this.body.getWorldPoint(vec).cpy();

        Array<Fixture> fixtures = this.body.getFixtureList();

        float step = 0.01f;
        for(float i=0; i<200; i+=step){
            if(!Utils.fixturesContains(fixtures,vec1.x+=step, vec1.y)){
                vec1.x-=step;
                return this.body.getLocalPoint(vec1).cpy();
            }
            if(!Utils.fixturesContains(fixtures,vec2.x-=step, vec2.y)){
                vec2.x+=step;
                return this.body.getLocalPoint(vec2).cpy();
            }
            if(!Utils.fixturesContains(fixtures,vec3.x, vec3.y+=step)){
                vec3.y-=step;
                return this.body.getLocalPoint(vec3).cpy();
            }
            if(!Utils.fixturesContains(fixtures,vec4.x, vec4.y-=step)){
                vec4.y+=step;
                return this.body.getLocalPoint(vec4).cpy();
            }
        }
        return null;
    }
}