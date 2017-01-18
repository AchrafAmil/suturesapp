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

    public static boolean ENABLE_DEBUG = true;

    public static final boolean VERBOSE_GESTURE = true;

    public static final float INITIAL_ZOOM = 6f;

    /**
     * ORGANS SCALES AND POSITIONS
     */

    public static final float GLOBAL_SCALE = 1f;
    public static final float LIVER_SCALE = 1f *GLOBAL_SCALE;
    public static final float SMALLINTESTINE_SCALE = 0.15f *GLOBAL_SCALE;
    public static final float APPENDIX_SCALE = 0.23f *GLOBAL_SCALE;
    public static final float STOMACH_SCALE = 1f *GLOBAL_SCALE;
    public static final float DUODENUM_SCALE = 1f *GLOBAL_SCALE;
    public static final float ESOPHAGUS_SCALE = 1f *GLOBAL_SCALE;

    // TODO: 14/01/17 make positions relative so that changing the global scale doesn't affect the external joints.
    public static final Vector2 LIVER_POSITION = new Vector2(14,59);
    public static final Vector2 SMALLINTESTINE_LEFT_POSITION = new Vector2(51,29.5f);
    public static final Vector2 SMALLINTESTINE_RIGHT_POSITION = new Vector2(242,-8.58f);
    public static final Vector2 APPENDIX_POSITION = SMALLINTESTINE_RIGHT_POSITION.cpy().add(10,1); //new Vector2(20, 15);
    public static final Vector2 STOMACH_POSITION = new Vector2(47, 59.2f);
    public static final Vector2 DUODENUM_POSITION = new Vector2(40,40);
    public static final Vector2 ESOPHAGUS_POSITION = new Vector2(48, 68);



    // if you ever change it, think about the appendix-smallintestine external joint json file.
    public static final int SMALLINTESTINE_LENGTH = 280;
}
