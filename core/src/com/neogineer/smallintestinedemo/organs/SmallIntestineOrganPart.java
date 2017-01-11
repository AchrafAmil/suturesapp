package com.neogineer.smallintestinedemo.organs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
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
        super(world, camera, callback, "", scale, position, rotation);
        this.id = id;
        setupBody("SmallIntestineOrganPart.json");
        setupOpenableSides("SmallIntestineOrganPart.json");
    }

    public SmallIntestineOrganPart(OrganPartDefinition opDef) {
        super(opDef);
        this.id = opDef.smallIntestineId;
        setupBody("SmallIntestineOrganPart.json");
        setupOpenableSides("SmallIntestineOrganPart.json");
    }
//
//    /**
//     * default rotation = 0
//     */
//    public SmallIntestineOrganPart(World world, OrthographicCamera camera, Organ callback,int id, float scale, Vector2 position) {
//        this(world, camera, callback, id, scale, position, 0);
//    }

    @Override
    protected void loadHighlightedSprite(BodyEditorLoader loader, String identifier) {
        // no highlighted state.
    }


    void setupOpenableSides(String path){

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal(path));

        try{
            int ind=0;
            // TODO: 20/11/16 Review & refactor this
            while (true){
                BodyEditorLoader.PolygonModel pm = loader.getInternalModel().rigidBodies.get("closed"+ind).polygons.get(0);
                float[] vertices = new float[pm.vertices.size()*2];

                int i = 0;
                for( Vector2 vec : pm.vertices){
                    vertices[i++] = vec.x;
                    vertices[i++] = vec.y;
                }

                Polygon polygon = new Polygon(vertices);
                polygon.scale(getVerticesScale()*scale);
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
        return this.suturePoints.size() < MAX_SUTURE_POINTS && (isVeryMiddle() || isVeryEdge());


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
            if(side.state== OpenableSide.State.OPEN ){
                vec.y = (dims.y/2) * ((i==0)? 1:(-1)) ;
                float xStep = this
                        .getVertex(this.origin.x + this.origin.x * 2 * HORIZONTAL_SUTUREPOINT_POSITION, 0).x;
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
                    float xStep = this
                            .getVertex(this.origin.x + this.origin.x * 2 * HORIZONTAL_SUTUREPOINT_POSITION, 0).x;

                    float oldX = suturePoint.getLocalCoord().x ;
                    suturePoint.getLocalCoord().x = (oldX>0)? xStep:-xStep;

                    if(i==0){
                        // up openable side
                        suturePoint.getLocalCoord().y = this
                                .getVertex(0,this.origin.y + this.origin.y * 2 * SmallIntestine.JOINT_OFFSET_PERCENT ).y;
                    }else{
                        // down openable side
                        suturePoint.getLocalCoord().y = - this
                                .getVertex(0,this.origin.y + this.origin.y * 2 * SmallIntestine.JOINT_OFFSET_PERCENT ).y;
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
        //SmallIntestine doesn't highlight.
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

    public boolean isVeryMiddle(){
        for(SuturePoint sp: suturePoints){
            if(!(((SmallIntestineOrganPart)sp.getTheOtherOrganPart()).isMiddle()))
                return false;
        }
        return true;
    }

    public boolean isVeryEdge(){
        return (hasAnOpenSide() || endSuturePointsCount()>=1);
    }

    public boolean isMiddle(){
        int count=0;
        for(SuturePoint sp: suturePoints){
            if(sp.getLocalCoord().x==0)
                count++;
        }
        return count==2;

        // check if neighbors are also SmallIntestine.
//        for(int i=0; i<suturePoints.size(); i++ ){
//            SuturePoint sp = suturePoints.get(i);
//            if(!(sp.getTheOtherOrganPart() instanceof SmallIntestineOrganPart))
//                return false;
//            else{
//                SmallIntestineOrganPart theOtherOp = (SmallIntestineOrganPart) sp.getTheOtherOrganPart();
//                SuturePoint sp2 = theOtherOp.getSuturePoints().get(i);
//                if(theOtherOp.hasEndSuturePoints()
//                        && sp2.getTheOtherOrganPart()!=this)
//                    return false;
//                if(!( sp2.getTheOtherOrganPart() instanceof SmallIntestineOrganPart))
//                    return false;
//            }
//
//
//        }
//        return true;
    }

    public boolean hasAnOpenSide(){
        try {
            return openableSides.get(0).state== OpenableSide.State.OPEN
                    || openableSides.get(1).state== OpenableSide.State.OPEN ;
        }catch (NullPointerException npe){
            return false;
        }
    }

    public int endSuturePointsCount(){
        int count = 0 ;
        for(SuturePoint sp: suturePoints){
            // neither on X nor Y axes
            if(sp.getLocalCoord().x!=0 && sp.getLocalCoord().y!=0)
                count++;
        }
        return count;
    }

}
