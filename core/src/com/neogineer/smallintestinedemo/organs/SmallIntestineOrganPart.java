package com.neogineer.smallintestinedemo.organs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.neogineer.smallintestinedemo.utils.Utils;

import java.util.List;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 24/10/16.
 */
public class SmallIntestineOrganPart extends OrganPart implements Openable{

    public static final float BASE_WIDTH = 273;
    public static final float BASE_HEIGHT = 139;
    private static final int MAX_SUTURE_POINTS = 3 ;
    private static final float HORIZONTAL_SUTUREPOINT_POSITION = 0.18f;

    public int id;



    public SmallIntestineOrganPart(World world, OrthographicCamera camera, Organ callback,int id, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, scale, position, rotation);
        this.id = id;
        setupBody("SmallIntestineOrganPart.json","");
        setupOpenableSides("SmallIntestineOrganPart.json");
    }

    /**
     * default rotation = 0
     */
    public SmallIntestineOrganPart(World world, OrthographicCamera camera, Organ callback,int id, float scale, Vector2 position) {
        this(world, camera, callback, id, scale, position, 0);
    }

    @Override
    protected void attachFixture(BodyEditorLoader loader, String id, FixtureDef fix) {
        loader.attachFixture(body, "base"+id,fix, 0.67f, Utils.getPixelPerMeter().x * scale, Utils.getPixelPerMeter().y * scale);
    }

    void setupOpenableSides(String path){

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal(path));

        try{
            int ind=0;
            while (true){
                BodyEditorLoader.PolygonModel pm = loader.getInternalModel().rigidBodies.get("open"+ind).polygons.get(0);
                float[] vertices = new float[pm.vertices.size()*2];

                int i = 0;
                for( Vector2 vec : pm.vertices){
                    vertices[i++] = vec.x;
                    vertices[i++] = vec.y;
                }

                Polygon polygon = new Polygon(vertices);
                polygon.scale(1.5f);
                Vector2 origin = loader.getOrigin("open"+ind, 1).cpy();
                polygon.setOrigin(origin.x, origin.y);

                Texture openSpriteTexture = new Texture(Gdx.files.internal( loader.getImagePath("open"+ind) ));
                Texture closedSpriteTexture = new Texture( Gdx.files.internal( loader.getImagePath("closed"+ind)));
                Sprite openSprite = new Sprite(openSpriteTexture);
                Sprite closedSprite = new Sprite(closedSpriteTexture);
                openableSides.add( new OpenableSide(openSprite, closedSprite, polygon) );
                ind++;
            }
        }catch (RuntimeException re){
        }


    }

    @Override
    public boolean connectionIntent(Vector2 point) {
        return (this.suturePoints.size()<MAX_SUTURE_POINTS);
    }

    @Override
    public boolean close(float x, float y) {
        try {
            this.getOpenableSide(x,y).close();
            return true;
        }catch (NullPointerException npe){
            return false;
        }
    }

    @Override
    public boolean open(float x, float y) {
        try {
            this.getOpenableSide(x,y).open();
            return true;
        }catch (NullPointerException npe){
            return false;
        }
    }

    @Override
    public boolean normal(float x, float y) {
        try {
            this.getOpenableSide(x,y).normal();
            return true;
        }catch (NullPointerException npe){
            return false;
        }
    }

    @Override
    public OpenableSide getOpenableSide(float x, float y) {
        Vector2 coord = body.getLocalPoint(new Vector2(x, y));
        Gdx.app.log(" in ("+coord.x+" "+coord.y+")","");
        for(OpenableSide side : openableSides ){
            if(side.contains(coord.x, coord.y))
                return side;
        }

        // TODO: 31/10/16 some openableside.contains doesn't return true
        if(coord.y>0){
            return openableSides.get(0);
        }else{
            return openableSides.get(1);
        }
    }

    @Override
    public List<OpenableSide> getOpenableSides() {
        return this.openableSides;
    }

    @Override
    public void correctJointDef(RevoluteJointDef def) {

        Vector2 vec = (this.body==def.bodyA)? def.localAnchorA:def.localAnchorB;
        Vector2 dims = this.getDimensions();

        for(int i=0; i<openableSides.size(); i++){
            OpenableSide side = openableSides.get(i);
            if(side.state== OpenableSide.State.CLOSED || side.state== OpenableSide.State.OPEN ){
                vec.y = (dims.y/2) * ((i==0)? 1:(-1)) ;
                float xStep = BASE_WIDTH / (Utils.getPixelPerMeter().x )
                        * scale * HORIZONTAL_SUTUREPOINT_POSITION ;
                float oldX = vec.x ;
                vec.x = (oldX>0)? xStep:-xStep;
                return;
            }
        }
        vec.x = dims.x/2 * ((vec.x>0)? 1:(-1)) ;
        vec.y=0;

//        float xDiff = Math.max( Math.abs(dims.x/2-vec.x) , Math.abs(-dims.x/2-vec.x) );
//        float yDiff = Math.max( Math.abs(dims.y/2-vec.y) , Math.abs(-dims.y/2-vec.y) );
//
//        if(xDiff<yDiff){
//            vec.x = dims.x/2 * ((vec.x>0)? 1:(-1)) ;
//            vec.y=0;
//        }else{
//            vec.y = dims.y/2 * ((vec.y>0)? 1:(-1)) ;
//            vec.x=0;
//        }

    }

    @Override
    public SuturePoint correctAnchorAndRotation(SuturePoint suturePoint) {
        // first, let's decide if it's up/down or left/right (end or side).

        for(int i=0; i<openableSides.size(); i++){
            OpenableSide side = openableSides.get(i);
            if(side.state== OpenableSide.State.CLOSED || side.state== OpenableSide.State.OPEN ){
                // TODO: 31/10/16 : sometimes side.contains() is returning unexpected "false"
                if(true || side.contains(suturePoint.localCoord.x, suturePoint.localCoord.y)){
                    // we're suturing upon this side

                    // the distance between the center and the next suture point x position
                    float xStep = BASE_WIDTH / (Utils.getPixelPerMeter().x )
                            * scale * HORIZONTAL_SUTUREPOINT_POSITION ;
                    float oldX = suturePoint.getLocalCoord().x ;
                    suturePoint.getLocalCoord().x = (oldX>0)? xStep:-xStep;

                    if(i==0){
                        // up openable side
                        suturePoint.getLocalCoord().y = BASE_HEIGHT / (Utils.getPixelPerMeter().y )
                                                        * scale * SmallIntestine.JOINT_OFFSET_PERCENT;
                    }else{
                        // down openable side
                        suturePoint.getLocalCoord().y = - BASE_HEIGHT / (Utils.getPixelPerMeter().y )
                                * scale * SmallIntestine.JOINT_OFFSET_PERCENT;
                    }

                    suturePoint.setRotation((float) (Math.PI/2));
                }
            }
        }

        return suturePoint;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public boolean setHighlighted(boolean highlighted) {
        //SmallIntestine doesn't hightligh.
        //this.highlighted = highlighted;
        return this.isHighlighted();
    }

    @Override
    public float getWidth() {
        return BASE_WIDTH;
    }

    @Override
    public float getHeight() {
        return BASE_HEIGHT;
    }

}
