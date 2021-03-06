package com.suturesapp.workspace.organs;

import com.badlogic.gdx.Gdx;
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
import com.suturesapp.workspace.organs.abdominalconnector.AbdominalConnectorOrganPart;
import com.suturesapp.workspace.organs.abdominalwall.AbdominalWallOrganPart;
import com.suturesapp.workspace.organs.appendix.AppendixOrganPart;
import com.suturesapp.workspace.organs.bileduct.BileDuctOrganPart;
import com.suturesapp.workspace.organs.duedenum.DuodenumOrganPart;
import com.suturesapp.workspace.organs.esophagus.EsophagusOrganPart;
import com.suturesapp.workspace.organs.gallbladder.GallbladderOrganPart;
import com.suturesapp.workspace.organs.pancreas.PancreasOrganPart;
import com.suturesapp.workspace.organs.rectum.RectumOrganPart;
import com.suturesapp.workspace.organs.spleen.SpleenOrganPart;
import com.suturesapp.workspace.organs.stomach.StomachOrganPart;
import com.suturesapp.workspace.utils.Constants;
import com.suturesapp.workspace.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 24/10/16.
 */
public abstract class OrganPart extends Actor implements Connectable {

    public static int INSTANCES = 0;

    public String identifier = "";

    public Body body;

    protected World mWorld;
    OrthographicCamera camera;
    protected Sprite baseSprite ;
    protected Sprite highlightedBaseSprite;
    protected boolean highlighted;
    public Vector2 origin ;
    public com.suturesapp.workspace.organs.Organ organCallback ;
    public float scale = 1f ;
    protected Vector2 position = new Vector2(8, 19);
    protected float rotation = 0;
    private int globalOrder ;


    protected ArrayList<com.suturesapp.workspace.organs.SuturePoint> suturePoints = new ArrayList<>();

    protected ArrayList<OpenableSide> openableSides = new ArrayList<>();

    protected ArrayList<com.suturesapp.workspace.organs.Tumor> tumors = new ArrayList<>();

    protected List<OpenableSide.State> openableSidesStatesBuffer;


    public OrganPart(World world, OrthographicCamera camera, com.suturesapp.workspace.organs.Organ callback, String identifier, float scale, Vector2 position, float rotation ){
        this.mWorld = world;
        this.camera = camera;
        this.organCallback = callback;
        this.identifier = identifier;
        this.scale = scale;
        if(position!=null)
            this.position = position;
        this.rotation = rotation;
        this.setGlobalOrder(INSTANCES++);
    }

    public OrganPart(World world, OrthographicCamera camera, com.suturesapp.workspace.organs.Organ callback, float scale, com.suturesapp.workspace.organs.OrganPartDefinition opDef){
        this(world, camera, callback, opDef.getIdentifier(), scale, opDef.position, opDef.angle);
        this.openableSidesStatesBuffer = opDef.openableSidesStates;
    }

    public OrganPart(com.suturesapp.workspace.organs.OrganPartDefinition opDef){
        this(OrgansHolder.world, OrgansHolder.camera, OrgansHolder.organFromName(opDef.getOrganName()),
                Utils.scaleFromName(opDef.getOrganName()), opDef );
    }

    public boolean loadBufferedOpenableSides(){
        if(this.openableSidesStatesBuffer==null || openableSides.size()==0)
            return false;

        for(OpenableSide.State state: openableSidesStatesBuffer){
            OpenableSide side = openableSides.get(openableSidesStatesBuffer.indexOf(state));
            side.setState(state);
        }
        return true;
    }


    /**
     * Called directly after object initialization.
     * This will use the OrganPart fields' values to load and create the appropriate body shape and sprite.
     * @param path Json file containing this organ's organParts definitions.
     */
    protected void setupBody(String path){
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal(path));
        BodyDef bDef = new BodyDef();
        bDef.type = getOganBodyType();
        bDef.position.set(this.position);
        bDef.angle = this.rotation;
        body = mWorld.createBody(bDef);
        body.setUserData(this);

        FixtureDef fix = new FixtureDef();
        fix.density = this.getOrganDensity();

        fix.friction = 0f;
        fix.restitution = 0.5f;
        fix.filter.categoryBits = this.getCategory();
        fix.filter.maskBits = this.getMask();

        origin = loader.getOrigin("base"+identifier, 1).cpy();

        String imgPath = loader.getImagePath("base"+identifier);

        /*AssetManager manager = SmallIntestineDemoGame.getAssets();
        manager.load(imgPath,Texture.class);
        manager.finishLoading();
        Texture baseSpriteTexture = manager.get(imgPath,Texture.class);
        baseSprite = new Sprite(baseSpriteTexture);*/
        baseSprite = new Sprite(new Texture(imgPath));


        loadHighlightedSprite(loader, identifier);
        
        attachFixture(loader, identifier, fix);

    }

    private BodyDef.BodyType getOganBodyType(){
        if(this instanceof AbdominalWallOrganPart
                || this instanceof SpleenOrganPart
                || (this instanceof EsophagusOrganPart && identifier.equals("1"))
                || (this instanceof StomachOrganPart && identifier.equals("1"))
                || (this instanceof DuodenumOrganPart && identifier.equals("1"))
                || (this instanceof RectumOrganPart && identifier.equals("6"))
                || (this instanceof BileDuctOrganPart && identifier.equals("4"))
                || (this instanceof BileDuctOrganPart && identifier.equals("5"))
                || (this instanceof AbdominalConnectorOrganPart && identifier.equals("4"))
                || (this instanceof AbdominalConnectorOrganPart && identifier.equals("5"))
                || (this instanceof AbdominalConnectorOrganPart && identifier.equals("7"))
                || (this instanceof AbdominalConnectorOrganPart && identifier.equals("8"))

                )
            return BodyDef.BodyType.KinematicBody;
        else
            return BodyDef.BodyType.DynamicBody;
    }

    private float getOrganDensity() {
        if(this instanceof com.suturesapp.workspace.organs.rope.RopeOrganPart)
            return 0.5f;
        if(this instanceof GallbladderOrganPart)
            return 0.02f;
        if(this instanceof EsophagusOrganPart)
            return 1.5f;
        if(this instanceof PancreasOrganPart)
            return 1.5f;
        if(this instanceof BileDuctOrganPart)
            return 1.2f;
        if(this instanceof RectumOrganPart)
            switch(identifier){
                case "6":
                    return 3f;
                case "5":
                    return 3f;
                case "4":
                case "2":
                    return 0.5f;
                case "1":
                    return 0.5f;
                case "3":
                    return 0.5f;
                default:
                    break;
            }

        if(this instanceof AbdominalConnectorOrganPart)
            return 0.01f;

        return 0.1f;
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
        //moved to the the Organ ─ Now the Organ$draw() takes care of calling each OrganPart$draw()
        /*
        this.draw(Batch batch);
        }*/
    }

    public void draw(Batch batch){
        if(camera!=null){
            drawBaseSprite(batch);
            drawOpenableSides(batch);
            drawSuturePoints(batch);
            drawTumors(batch);
        }
    }

    Vector3 bodyPixelPos;
    Vector3 tmpVec = new Vector3();
    private void drawBaseSprite(Batch batch){
        Sprite baseSprite = this.getBaseSprite();
        batch.setProjectionMatrix(camera.combined);
        float someScale = 0.1f;

        float w = scale * (baseSprite.getTexture().getWidth())/2 *someScale;
        float h = scale * (baseSprite.getTexture().getHeight())/2 *someScale;

        bodyPixelPos = camera.project(tmpVec.set(
                body.getPosition().x, body.getPosition().y, 0).add(Utils.cameraPosition(camera)))
                .scl(camera.viewportHeight/(Gdx.graphics.getHeight()/camera.zoom))
                .sub(w/2, h/2, 0);

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


            bodyPixelPos = camera.project(tmpVec.set(
                    body.getPosition().x, body.getPosition().y, 0).add(Utils.cameraPosition(camera)))
                    .scl(camera.viewportHeight/(Gdx.graphics.getHeight()/camera.zoom))
                    .sub(w/2, h/2, 0);

            sprite.setSize(w,h);
            sprite.setOrigin(w/2, h/2);
            sprite.setPosition(bodyPixelPos.x, bodyPixelPos.y);
            sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

            side.draw(batch);
        }
    }

    Vector3 spPixelPos;
    Vector2 spWorldCoord;
    private void drawSuturePoints(Batch batch){
        for(com.suturesapp.workspace.organs.SuturePoint sp : suturePoints){

            Sprite sprite = sp.getSprite();
            if(sprite==null)
                continue;

            spWorldCoord = body.getWorldPoint(sp.getLocalCoord());


            batch.setProjectionMatrix(camera.combined);
            float someScale = 0.1f;

            float w = Constants.SMALLINTESTINE_SCALE * (sprite.getTexture().getWidth())/2f *someScale;
            float h = Constants.SMALLINTESTINE_SCALE * (sprite.getTexture().getHeight())/2f *someScale;

            spPixelPos = camera.project(tmpVec.set(spWorldCoord.x, spWorldCoord.y, 0)
                    .add(Utils.cameraPosition(camera)))
                    .scl(camera.viewportHeight/(Gdx.graphics.getHeight()/camera.zoom)).sub(w/2, h/2, 0);


            sprite.setSize(w,h);

            // it's gonna rotate around this origin, let it be the suturePoint's center.
            sprite.setOrigin(w/2, h/2);

            // same as origin, here expressed in global screen coordinates (not relative, not local)
            sprite.setPosition(spPixelPos.x, spPixelPos.y);

            sprite.setRotation((body.getAngle() + sp.getRotation()) * MathUtils.radiansToDegrees);

            sprite.draw(batch);
        }
    }

    public void drawTumors(Batch batch){
        for(com.suturesapp.workspace.organs.Tumor tumor : tumors){

            Sprite sprite = tumor.sprite;

            spWorldCoord = body.getWorldPoint(tumor.localCoord);


            batch.setProjectionMatrix(camera.combined);
            float someScale = 0.1f * tumor.scale;

            float w = Constants.SMALLINTESTINE_SCALE * (sprite.getTexture().getWidth())/2f *someScale;
            float h = Constants.SMALLINTESTINE_SCALE * (sprite.getTexture().getHeight())/2f *someScale;

            spPixelPos = camera.project(tmpVec.set(spWorldCoord.x, spWorldCoord.y, 0)
                    .add(Utils.cameraPosition(camera)))
                    .scl(camera.viewportHeight/(Gdx.graphics.getHeight()/camera.zoom)).sub(w/2, h/2, 0);


            sprite.setSize(w,h);

            // it's gonna rotate around this origin, let it be the suturePoint's center.
            sprite.setOrigin(w/2, h/2);

            // same as origin, here expressed in global screen coordinates (not relative, not local)
            sprite.setPosition(spPixelPos.x, spPixelPos.y);

            sprite.setRotation((body.getAngle()) * MathUtils.radiansToDegrees);

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

    /**
     * inverse of getVertex.
     * jsonCoordinatesFromVertex(getVertex(X)) = X
     */
    public Vector2 jsonCoordinatesFromVertex(Vector2 vec){
        return
                new Vector2( ((vec.x/getVerticesScale()/scale)+this.origin.x) , ((vec.y/getVerticesScale()/scale)+this.origin.y)) ;
    }

    public float getVerticesScale(){
        return getHeight()/ origin.y / Constants.VERTICES_SCALE_FACTOR;
    }

    /**
     * @return width in pixels
     */
    private float spriteWidth=-1;
    public float getWidth(){
        if(spriteWidth==-1)
            spriteWidth = this.baseSprite.getWidth();
        return spriteWidth;
    };

    /**
     * @return height in pixels
     */
    private float spriteHeight=-1;
    public float getHeight(){
        if(spriteHeight==-1)
            spriteHeight = this.baseSprite.getHeight();
        return spriteHeight;
    };

    public String getIdentifier(){
        return this.identifier;
    }

    @Override
    public boolean addSuturePoint(com.suturesapp.workspace.organs.SuturePoint sp) {
        this.correctAnchorAndRotation(sp);
        sp.setRelatedOrganPart(this);
        this.suturePoints.add(sp);

        if(this instanceof com.suturesapp.workspace.organs.Openable){
            try {
                Vector2 vec = body.getWorldPoint(new Vector2(sp.getLocalCoord().x, sp.getLocalCoord().y));
                ( (com.suturesapp.workspace.organs.Openable) this).normal(vec.x, vec.y);
            }catch (NullPointerException npe){
            }
        }

        return true;
    }

    @Override
    public boolean deleteSuturePoint(com.suturesapp.workspace.organs.SuturePoint sp) {
        this.suturePoints.remove(sp);
        if(sp.getTheOtherOrganPart() instanceof AbdominalConnectorOrganPart)
            return true;

        if(this instanceof com.suturesapp.workspace.organs.Openable){
            Vector2 vec = body.getWorldPoint(new Vector2(sp.getLocalCoord().x, sp.getLocalCoord().y));
            ( (com.suturesapp.workspace.organs.Openable) this).open(vec.x, vec.y);
        }

        this.body.applyLinearImpulse(new Vector2(sp.getLocalCoord().x*10*scale,sp.getLocalCoord().y*10*scale), body.getWorldCenter(),true);

        return true;
    }

    @Override
    public boolean deleteSuturePoint(RevoluteJoint joint) {
        for(com.suturesapp.workspace.organs.SuturePoint sp: this.suturePoints){
            if(sp.getRelatedJoint()==joint){
                return this.deleteSuturePoint(sp);
            }
        }
        return false;
    }

    @Override
    public ArrayList<com.suturesapp.workspace.organs.SuturePoint> getSuturePoints() {
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
    public com.suturesapp.workspace.organs.SuturePoint correctAnchorAndRotation(com.suturesapp.workspace.organs.SuturePoint suturePoint) {
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
    public com.suturesapp.workspace.organs.OrganPartDefinition getOPDef(){
        com.suturesapp.workspace.organs.OrganPartDefinition opDef = new com.suturesapp.workspace.organs.OrganPartDefinition();
        opDef.position = this.body.getPosition().cpy();
        opDef.angle = this.body.getAngle();
        opDef.fullIdentifier = this.getClass().getSimpleName()+this.getIdentifier();
        if(this instanceof com.suturesapp.workspace.organs.rope.RopeOrganPart)
            opDef.ropeId = ((com.suturesapp.workspace.organs.rope.RopeOrganPart)this).id;

        for(OpenableSide os : openableSides){
            opDef.openableSidesStates.add(os.getState());
        }

        return opDef;
    }

    public void destroy(){
        mWorld.destroyBody(body);
        baseSprite.getTexture().dispose();
        if(highlightedBaseSprite!=null)
            highlightedBaseSprite.getTexture().dispose();
        for(OpenableSide os : openableSides)
            os.free();
        for(com.suturesapp.workspace.organs.SuturePoint sp : suturePoints)
            sp.free();
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
        Vector2 vec5 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec6 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec7 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec8 = this.body.getWorldPoint(vec).cpy();

        Array<Fixture> fixtures = this.body.getFixtureList();

        float step = 0.01f;
        for(float i=0; i<200; i+=step){
            if(!Utils.fixturesContains(fixtures,vec1.x+=step, vec1.y)){
                //vec1.x-=step;
                return this.body.getLocalPoint(vec1).cpy();
            }
            if(!Utils.fixturesContains(fixtures,vec2.x-=step, vec2.y)){
                //vec2.x+=step;
                return this.body.getLocalPoint(vec2).cpy();
            }
            if(!Utils.fixturesContains(fixtures,vec3.x, vec3.y+=step)){
                //vec3.y-=step;
                return this.body.getLocalPoint(vec3).cpy();
            }
            if(!Utils.fixturesContains(fixtures,vec4.x, vec4.y-=step)){
                //vec4.y+=step;
                return this.body.getLocalPoint(vec4).cpy();
            }
            if(!Utils.fixturesContains(fixtures,vec5.x+=step, vec5.y+=step)){

                return this.body.getLocalPoint(vec5).cpy();
            }
            if(!Utils.fixturesContains(fixtures,vec6.x-=step, vec6.y-=step)){

                return this.body.getLocalPoint(vec6).cpy();
            }
            if(!Utils.fixturesContains(fixtures,vec7.x-=step, vec7.y+=step)){

                return this.body.getLocalPoint(vec7).cpy();
            }
            if(!Utils.fixturesContains(fixtures,vec8.x+=step, vec8.y-=step)){

                return this.body.getLocalPoint(vec8).cpy();
            }
        }
        return null;
    }

    public Vector2 pushToEdge(Vector2 vec, boolean toTheRight){
        Vector2 vec1 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec2 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec3 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec4 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec5 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec6 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec7 = this.body.getWorldPoint(vec).cpy();
        Vector2 vec8 = this.body.getWorldPoint(vec).cpy();

        Array<Fixture> fixtures = this.body.getFixtureList();

        float step = 0.01f;
        for(float i=0; i<200; i+=step){
            if(toTheRight){
                if(!Utils.fixturesContains(fixtures,vec1.x+=step, vec1.y)){
                    //vec1.x-=step;
                    return this.body.getLocalPoint(vec1).cpy();
                }
            }else{
                if(!Utils.fixturesContains(fixtures,vec1.x-=step, vec1.y)){
                    //vec1.x-=step;
                    return this.body.getLocalPoint(vec1).cpy();
                }
            }
        }
        return null;
    }

    public OpenableSide getClosestOpenableSide(Vector2 vec){
        Vector2 vec1 = vec.cpy();
        Vector2 vec2 = vec.cpy();
        Vector2 vec3 = vec.cpy();
        Vector2 vec4 = vec.cpy();
        Vector2 vec5 = vec.cpy();
        Vector2 vec6 = vec.cpy();
        Vector2 vec7 = vec.cpy();
        Vector2 vec8 = vec.cpy();

        OpenableSide side = null;

        Array<Fixture> fixtures = this.body.getFixtureList();

        float step = 0.05f;
        for(float i=0; i<200; i+=step){
            if(((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec1.x+=step, vec1.y)!=null){
                side = ((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec1.x, vec1.y);
                break;
            }
            if(((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec2.x, vec2.y)!=null){
                side = ((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec2.x, vec2.y);
                break;
            }
            if(((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec3.x, vec3.y+=step)!=null){
                side = ((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec3.x, vec3.y);
                break;
            }
            if(((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec4.x, vec4.y-=step)!=null){
                side = ((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec4.x, vec4.y);
                break;
            }
            if(((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec5.x+=step, vec5.y+=step)!=null){
                side = ((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec5.x, vec5.y);
                break;
            }
            if(((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec6.x-=step, vec6.y-=step)!=null){
                side = ((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec6.x, vec6.y);
                break;
            }
            if(((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec7.x-=step, vec7.y+=step)!=null){
                side = ((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec7.x, vec7.y);
                break;
            }
            if(((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec8.x+=step, vec8.y-=step)!=null){
                side = ((com.suturesapp.workspace.organs.Openable) this).getOpenableSide(vec8.x, vec8.y);
                break;
            }
        }

        return side;
    }
    
    public boolean close(float x, float y){
        if(!(this instanceof com.suturesapp.workspace.organs.Openable))
            return false;

        OpenableSide side = ((com.suturesapp.workspace.organs.Openable)this).getOpenableSide(x, y);
        if(side==null){
            side = getClosestOpenableSide(new Vector2(x, y));
        }


        if(side!=null)
            if(side.getState() == OpenableSide.State.OPEN){
                side.close();
                return true;
            }
        return false;
    }

    public abstract short getCategory();

    public abstract short getMask();

    public int getGlobalOrder() {
        return globalOrder;
    }

    public void setGlobalOrder(int globalOrder) {
        this.globalOrder = globalOrder;
    }

    public void addTumor(com.suturesapp.workspace.organs.Tumor tumor){
        tumor.scale = Utils.tumorScale(this);
        this.tumors.add(tumor);
        Gdx.app.log("Tumor","tumor with scale "+tumor.scale+" added to "+this);
    }

    public boolean syncToken = false;
    public void destroyAllSuturePoints(){
        syncToken=true;
        for (com.suturesapp.workspace.organs.SuturePoint sp:
             suturePoints) {
            sp.getTheOtherOrganPart().deleteSuturePoint(sp.getRelatedJoint());
            mWorld.destroyJoint(sp.getRelatedJoint());
        }
        suturePoints.clear();

        syncToken=false;
    }
}