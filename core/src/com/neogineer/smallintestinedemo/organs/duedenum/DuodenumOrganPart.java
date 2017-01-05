package com.neogineer.smallintestinedemo.organs.duedenum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
 * Created by neogineer on 26/11/16.
 */
public class DuodenumOrganPart extends OrganPart implements Openable {


    public DuodenumOrganPart(World world, OrthographicCamera camera, Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("DuodenumOrganPart.json");
        setupOpenableSides("DuodenumOrganPart.json");
    }

    public DuodenumOrganPart(OrganPartDefinition opDef) {
        super(opDef);
        setupBody("DuodenumOrganPart.json");
        setupOpenableSides("DuodenumOrganPart.json");
    }

    @Override
    protected void loadHighlightedSprite(BodyEditorLoader loader, String identifier) {
        // TODO: 26/11/16 implement highlighting
    }

    void setupOpenableSides(String path){

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal(path));

        try{
            int ind=0;
            // TODO: 04/01/17 Review & refactor this
            // this actually try loading all possible openablesides (doesn't know their number) till it gets an error (catched)
            while (true){
                BodyEditorLoader.PolygonModel pm = loader.getInternalModel().rigidBodies.get(identifier+"open"+ind).polygons.get(0);
                float[] vertices = new float[pm.vertices.size()*2];

                int i = 0;
                for( Vector2 vec : pm.vertices){
                    vertices[i++] = vec.x;
                    vertices[i++] = vec.y;
                }

                Polygon polygon = new Polygon(vertices);
                polygon.scale(getVerticesScale()*scale);
                Vector2 origin = loader.getOrigin(identifier+"open"+ind, 1).cpy();
                polygon.setOrigin(origin.x, origin.y);

                Texture openSpriteTexture = new Texture(Gdx.files.internal( loader.getImagePath(identifier+"open"+ind) ));
                Texture closedSpriteTexture = new Texture( Gdx.files.internal( loader.getImagePath(identifier+"closed"+ind)));
                Sprite openSprite = new Sprite(openSpriteTexture);
                Sprite closedSprite = new Sprite(closedSpriteTexture);
                openableSides.add( new OpenableSide(openSprite, closedSprite, polygon) );
                ind++;
            }
        }catch (RuntimeException re){
        }


    }

    @Override
    public boolean setHighlighted(boolean highlighted) {
        // TODO: 26/11/16 implement highlighting
        return false;
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
        Gdx.app.log("getOpenableSide"," in ("+coord.x+" "+coord.y+")");
        for(OpenableSide side : openableSides ){
            if(side.contains(coord.x, coord.y))
                return side;
        }
        Gdx.app.log("getOpenableSide","nothing found");
        return null;
//  if some openableside.contains doesn't return true
//        if(coord.y>0){
//            return openableSides.get(0);
//        }else{
//            return openableSides.get(1);
//        }
    }

    @Override
    public List<OpenableSide> getOpenableSides() {
        return this.openableSides;
    }

    @Override
    public boolean connectionIntent(Vector2 point) {
        return true;
    }

    @Override
    public void correctJointDef(RevoluteJointDef def) {
        Vector2 vec = (this.body==def.bodyA)? def.localAnchorA:def.localAnchorB;

        vec.set(pushToEdge(vec));
    }

    @Override
    public boolean addSuturePoint(SuturePoint sp) {
        super.addSuturePoint(sp);
        try {
            this.getOpenableSide(sp.getLocalCoord().x, sp.getLocalCoord().y).normal();
        }catch (NullPointerException npe){
        }
        return true;
    }
}
