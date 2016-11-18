package com.neogineer.smallintestinedemo.organs.liver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.utils.Utils;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 08/11/16.
 */
public abstract class LiverOrganPart extends OrganPart {

    public LiverOrganPart(World world, OrthographicCamera camera, Organ callback, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, scale, position, rotation);
    }

    @Override
    public boolean setHighlighted(boolean highlighted) {
        return this.highlighted = highlighted;
    }

    @Override
    protected void attachFixture(BodyEditorLoader loader, String id, FixtureDef fix) {
        Texture texture = new Texture(Gdx.files.internal( loader.getImagePath("base_highlighted"+id) ));
        highlightedBaseSprite = new Sprite(texture);
    }

    public Vector2 getVertex(double x, double y){
        return
                new Vector2( (float)(x-this.origin.x)*getVerticesScale()*scale , (float)(y-this.origin.y)*getVerticesScale()*scale) ;
    }


    public abstract String getID();

    public abstract float getVerticesScale();

}
