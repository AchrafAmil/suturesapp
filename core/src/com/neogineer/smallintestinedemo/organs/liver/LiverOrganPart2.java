package com.neogineer.smallintestinedemo.organs.liver;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.utils.Utils;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 08/11/16.
 */
public class LiverOrganPart2 extends LiverOrganPart{

    public static final String ID = "2";

    public static final float BASE_WIDTH = 139;
    public static final float BASE_HEIGHT = 333;

    public static final float VERTICES_SCALE = 6.92f;

    public LiverOrganPart2(World world, OrthographicCamera camera, Organ callback, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, scale, position, rotation);
        setupBody("LiverOrganPart.json",ID);
    }

    @Override
    protected void attachFixture(BodyEditorLoader loader, String id, FixtureDef fix) {
        loader.attachFixture(body, "base"+id,fix, VERTICES_SCALE*scale, 1, 1  );
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
        return this.VERTICES_SCALE;
    }
}
