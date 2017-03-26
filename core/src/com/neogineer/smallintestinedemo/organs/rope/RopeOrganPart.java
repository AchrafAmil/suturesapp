package com.neogineer.smallintestinedemo.organs.rope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.neogineer.smallintestinedemo.organs.Openable;
import com.neogineer.smallintestinedemo.organs.OpenableSide;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.OrganPartDefinition;
import com.neogineer.smallintestinedemo.organs.SuturePoint;

import java.util.List;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 24/10/16.
 */
public abstract class RopeOrganPart extends OrganPart implements Openable {



    public int id;


    public RopeOrganPart(World world, OrthographicCamera camera, Organ callback, int id, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, "", scale, position, rotation);
        this.id = id;
    }

    public RopeOrganPart(OrganPartDefinition opDef) {
        super(opDef);
        this.id = opDef.ropeId;
    }

    public abstract int getMaxSuturePoints();
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
                BodyEditorLoader.PolygonModel pm = loader.getInternalModel().rigidBodies.get("open"+ind).polygons.get(0);
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
        return this.suturePoints.size() < getMaxSuturePoints() && (isVeryMiddle() || isVeryEdge());


    }

    @Override
    public boolean close(float x, float y) {
        try {
            try {
                this.getTheOpenOs().close();
            }catch (NullPointerException e){
                try {
                    ((RopeOrganPart)this.getSuturePoints().get(0).getTheOtherOrganPart()).getTheOpenOs().close();
                }catch (NullPointerException | ClassCastException ee){
                    try {
                        ((RopeOrganPart)this.getSuturePoints().get(1).getTheOtherOrganPart()).getTheOpenOs().close();
                    }catch (NullPointerException | ClassCastException eee){
                        return false;
                    }
                }
            }
            return true;
        }catch (IndexOutOfBoundsException ioobe){
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

        if(isVeryEdge()){
            boolean up = upSuturing();
            vec.y = (dims.y / 2) * ((up) ? 1 : (-1));
            float xStep = this
                    .getVertex(this.origin.x
                            + this.origin.x * 2 * getHorizontalSuturePointPosition(vec),
                            0).x;
            float oldX = vec.x;
            vec.x = (oldX > 0) ? xStep : -xStep;

        }else{
            // side suturing (x sign is enough to decide because we have ConnectTool.MAX_ACCEPTED_DISTANCE
            vec.x = dims.x/2 * ((vec.x>0)? 1:(-1)) ;
            vec.y=0;
        }

    }

    protected abstract float getHorizontalSuturePointPosition(Vector2 vec);

    @Override
    public SuturePoint correctAnchorAndRotation(SuturePoint suturePoint) {
        // first, let's decide if it's up/down or left/right (end or side).

        if(isVeryEdge()){
            //end suturing
            if(upSuturing()){
                suturePoint.getLocalCoord().y = this
                        .getVertex(0,this.origin.y + this.origin.y * 2 * SmallIntestine.getJointOffsetPercent()).y;
            }else{
                suturePoint.getLocalCoord().y = - this
                        .getVertex(0,this.origin.y + this.origin.y * 2 * SmallIntestine.getJointOffsetPercent()).y;
            }
            suturePoint.setRotation((float) (Math.PI/2));
        }
        //side suturing (nothing to do)

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


    public abstract boolean isVeryMiddle();

    public abstract boolean isVeryEdge();

    protected boolean isMiddle(){
        int count=0;
        for(SuturePoint sp: suturePoints){
            if(sp.getLocalCoord().x==0)
                count++;
        }
        return count==2;
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

    public OpenableSide getTheOpenOs(){
        for(OpenableSide os: openableSides){
            if(os.state== OpenableSide.State.OPEN)
                return os;
        }
        return null;
    }

    /**
     *@return Either 0 or 1 (or -1 if there is no open OS)
     */
    public int getTheOpenOsIndex(){
        try{
            return openableSides.indexOf(getTheOpenOs());
        }catch (NullPointerException npe){
            return -1;
        }
    }

    public SuturePoint getTheNoXYSp(){
        for(SuturePoint sp: suturePoints){
            // neither on X nor Y axes
            if(sp.getLocalCoord().x!=0 && sp.getLocalCoord().y!=0)
                return sp;
        }
        return null;
    }

    private boolean upSuturing() {
        if(hasAnOpenSide())
            return getTheOpenOsIndex()==0;
        if(endSuturePointsCount()>=1)
            return getTheNoXYSp().getLocalCoord().y>0;
        // not supposed to get here, to-do: throw SuturesException
        return true;
    }

    /**
     * @return the region that should be sutured with another organ's region
     * @param region the other organ's region.
     */
    public Vector2 getSuturableRegion(int region){

        switch (region){
            case 1:
                if(upSuturing())
                    return getRegion(2);
                else
                    return getRegion(3);
            case 2:
                if(upSuturing())
                    return getRegion(1);
                else
                    return getRegion(4);
            case 3:
                if(upSuturing())
                    return getRegion(1);
                else
                    return getRegion(4);
            case 4:
                if(upSuturing())
                    return getRegion(2);
                else
                    return getRegion(3);
            case 5:
            case 8:
                // TODO: 25/03/17
            case 6:
            case 7:
                // TODO: 25/03/17
            default:
                return null;
        }

    }

    public Vector2 getRegion(int region){
        Vector2 vec = new Vector2(0.01f,0.01f);
        boolean up = upSuturing();
        vec.y = this
                .getVertex(0,this.origin.y + this.origin.y * 2 * SmallIntestine.getJointOffsetPercent()).y
                * ((up) ? 1 : (-1));
        float xStep = this
                .getVertex(this.origin.x
                                + this.origin.x * 2 * getHorizontalSuturePointPosition(vec),
                        0).x;
        vec.x = (region==1 || region==3)? +xStep:-xStep;

        return vec;
    }

}
