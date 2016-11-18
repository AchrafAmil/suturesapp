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
import com.neogineer.smallintestinedemo.utils.Constants;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 08/11/16.
 */
public class LiverOrganPart1 extends LiverOrganPart{

    public static final String ID = "1" ;

    public static final float BASE_WIDTH = 159;
    public static final float BASE_HEIGHT = 226;

    // TODO: 09/11/16  clarify this
    public static final float VERTICES_SCALE = 8f;

    public LiverOrganPart1(World world, OrthographicCamera camera, Organ callback, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, scale, position, rotation);
        setupBody("LiverOrganPart.json",ID);
    }

    @Override
    protected void attachFixture(BodyEditorLoader loader, String id, FixtureDef fix) {
        super.attachFixture(loader, id, fix);
        loader.attachFixture(body, "base"+id,fix, getVerticesScale()*scale, 1, 1 );
    }

    @Override
    public float getWidth() {
        return BASE_WIDTH;
    }

    @Override
    public float getHeight() {
        return BASE_HEIGHT;
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public float getVerticesScale() {
        return Constants.PLATFORM_SCALE*this.VERTICES_SCALE;
    }
}
