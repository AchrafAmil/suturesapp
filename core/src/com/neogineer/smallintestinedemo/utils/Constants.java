package com.neogineer.smallintestinedemo.utils;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by neogineer on 30/08/16.
 */
public class Constants {

    // This will be our viewport measurements while working with the debug renderer (N.B: meters)
    public static final int VIEWPORT_WIDTH = 32;
    public static final int VIEWPORT_HEIGHT = 24;

    public static final int SMALL_INTESTINE_MIN_CONNECTION_ID_DIFFERENCE = 5 ;

    public static final float VERTICES_SCALE_FACTOR = 40;


    /**
     * ORGANS SCALES AND POSITIONS
     */

    public static final float GLOBAL_SCALE = 1f;
    public static final float LIVER_SCALE = 1f *GLOBAL_SCALE;
    public static final float STOMACH_SCALE = 1f *GLOBAL_SCALE;
    public static final float DUODENUM_SCALE = 1f *GLOBAL_SCALE;
    public static final float SMALLINTESTINE_SCALE = 0.15f *GLOBAL_SCALE;

    public static final Vector2 LIVER_POSITION = new Vector2(10,80);
    public static final Vector2 STOMACH_POSITION = new Vector2(48, 60);
    public static final Vector2 DUODENUM_POSITION = new Vector2(40,40);
    public static final Vector2 SMALLINTESTINE_LEFT_POSITION = new Vector2(55,25);
    public static final Vector2 SMALLINTESTINE_RIGHT_POSITION = new Vector2(100,0);
}
