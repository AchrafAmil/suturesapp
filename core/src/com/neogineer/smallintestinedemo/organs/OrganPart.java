package com.neogineer.smallintestinedemo.organs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.neogineer.smallintestinedemo.GameStage;
import com.neogineer.smallintestinedemo.utils.Utils;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import java.util.ArrayList;
import java.util.List;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 24/10/16.
 */
public abstract class OrganPart extends Actor implements Connectable {

    public Body body;

    World mWorld;
    OrthographicCamera camera;
    Sprite baseSprite ;
    Sprite highlightedBaseSprite;
    boolean highlighted;
    Vector2 baseSpriteOrigin ;
    Organ organCallback ;
    float scale = 1f ;
    Vector2 position = new Vector2(8, 19);
    float rotation = 0;


    protected ArrayList<SuturePoint> suturePoints = new ArrayList<SuturePoint>();

    protected ArrayList<OpenableSide> openableSides = new ArrayList<OpenableSide>();


    public OrganPart(World world, OrthographicCamera camera, Organ callback, float scale, Vector2 position, float rotation ){
        this.mWorld = world;
        this.camera = camera;
        this.organCallback = callback;
        this.scale = scale;
        if(position!=null)
            this.position = position;
        this.rotation = rotation;
    }

    void setupBody(String path){
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal(path));
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.DynamicBody;
        //if(this.position.y==20)
        //    bDef.type = BodyDef.BodyType.KinematicBody;         // just while developing
        bDef.position.set(this.position);
        bDef.angle = this.rotation;
        body = mWorld.createBody(bDef);
        body.setUserData(this);

        FixtureDef fix = new FixtureDef();
        fix.density = 0.01f;
        fix.friction = 0.6f;
        fix.restitution = 0.1f;

        Texture baseSpriteTexture = new Texture(Gdx.files.internal( loader.getImagePath("base") ));
        baseSprite = new Sprite(baseSpriteTexture);

        loader.attachFixture(body, "base",fix, 1f, Utils.getPixelPerMeter().x * scale, Utils.getPixelPerMeter().y * scale);
        baseSpriteOrigin = loader.getOrigin("base", getWidth()).cpy();
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

        Vector3 bodyPixelPos = camera.project(new Vector3(body.getPosition().x, body.getPosition().y, 0));

        float w = scale * getWidth()/camera.zoom;
        float h = scale * getHeight()/camera.zoom;

        baseSprite.setSize(w,h);
        baseSprite.setOrigin(w/2, h/2);
        baseSprite.setPosition(bodyPixelPos.x -w/2, bodyPixelPos.y - h/2);
        baseSprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        /*Gdx.app.log("values"," body.getPosition: "+body.getPosition()+" foo: "+bodyPixelPos
                +" baseSpriteOrigin: "+baseSpriteOrigin
                +" body Origin: "+ getOriginX() +" "+getOriginY());*/
        baseSprite.draw(batch);
    }

    private void drawOpenableSides(Batch batch){
        for(OpenableSide side: openableSides){

            Sprite sprite = side.getSprite();
            if(sprite==null)
                continue;

            Vector3 bodyPixelPos = camera.project(new Vector3(body.getPosition().x, body.getPosition().y, 0));

            float w = scale * getWidth()/camera.zoom;
            float h = scale * getHeight()/camera.zoom;

            sprite.setSize(w,h);
            sprite.setOrigin(w/2, h/2);
            sprite.setPosition(bodyPixelPos.x -w/2, bodyPixelPos.y - h/2);
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

            Vector3 spPixelPos = camera.project(new Vector3(spWorldCoord.x, spWorldCoord.y, 0));

            float w = scale * SuturePoint.IMG_SIZE_W/camera.zoom;
            float h = scale * SuturePoint.IMG_SIZE_H/camera.zoom;

            sprite.setSize(w,h);

            // it's gonna rotate around this origin, let it be the suturePoint's center.
            sprite.setOrigin(w/2, h/2);

            // same as origin, here expressed in global screen coordinates (not relative, not local)
            sprite.setPosition(spPixelPos.x -w/2, spPixelPos.y - h/2);

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
        return
                new Vector2(scale * getWidth() / ( Utils.getPixelPerMeter().x),
                            scale * getHeight() / ( Utils.getPixelPerMeter().y));
    }

    /**
     * @return width in pixels
     */
    public abstract float getWidth();

    /**
     * @return height in pixels
     */
    public abstract float getHeight();

    @Override
    public boolean addSuturePoint(SuturePoint sp) {
        this.correctAnchorAndRotation(sp);
        sp.setRelatedOrganPart(this);
        this.suturePoints.add(sp);
        return true;
    }

    @Override
    public boolean deleteSuturePoint(SuturePoint sp) {
        this.suturePoints.remove(sp);

        if(this instanceof Openable){
            Vector2 vec = body.getWorldPoint(new Vector2(sp.getLocalCoord().x, sp.getLocalCoord().y));
            ( (Openable) this).open(vec.x, vec.y);
        }

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

    @Override
    public boolean connectionIntent(Vector2 point) {
        return true;
    }

    @Override
    public SuturePoint correctAnchorAndRotation(SuturePoint suturePoint) {
        return suturePoint;
    }

    @Override
    public void correctJointDef(RevoluteJointDef def) {
    }
}
