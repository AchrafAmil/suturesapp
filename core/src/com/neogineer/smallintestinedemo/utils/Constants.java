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

    public static final float MAX_ZOOM = 6.5f;
    public static final float MIN_ZOOM = 1f;
    public static final int CAMERA_X_LIMIT_RIGHT = 70;
    public static final int CAMERA_X_LIMIT_LEFT = -5;
    public static final int CAMERA_Y_LIMIT_TOP = 70;
    public static final int CAMERA_Y_LIMIT_BUTTOM = -5;
    public static final Vector2 CAMERA_INITIAL_TRANSLATION = new Vector2(5, -45);

    public static final float VERTICES_SCALE_FACTOR = 40;

    public static boolean ENABLE_DEBUG = false;

    public static final boolean VERBOSE_GESTURE = true;

    public static final float INITIAL_ZOOM = 6;

    /**
     * ORGANS SCALES AND POSITIONS
     */

    public static final float GLOBAL_SCALE = 1f;
    public static final float ABDOMINALWALL_SCALE = 1.52f *GLOBAL_SCALE;
    public static final float ESOPHAGUS_SCALE = 1f *GLOBAL_SCALE;
    public static final float BILEDUCT_SCALE = 1f *GLOBAL_SCALE;
    public static final float GALLBLADDER_SCALE = 1f *GLOBAL_SCALE;
    public static final float PANCREAS_SCALE = 1f *GLOBAL_SCALE;
    public static final float DUODENUM_SCALE = 1f *GLOBAL_SCALE;
    public static final float RECTUM_SCALE = 0.23f *GLOBAL_SCALE;
    public static final float STOMACH_SCALE = 1f *GLOBAL_SCALE;
    public static final float COLON_SCALE = 0.23f *GLOBAL_SCALE;
    public static final float APPENDIX_SCALE = 0.23f *GLOBAL_SCALE;
    public static final float SMALLINTESTINE_SCALE = 0.15f *GLOBAL_SCALE;
    public static final float LIVER_SCALE = 1f *GLOBAL_SCALE;

    // ROPEs
    public static final Vector2 SMALLINTESTINE_RIGHT_POSITION = new Vector2(165,10-(165-150f)/4.95f);
    public static final Vector2 SMALLINTESTINE_LEFT_POSITION = new Vector2(51,29.5f);
    public static final Vector2 COLON_RIGHT_POSITION = SMALLINTESTINE_RIGHT_POSITION.cpy().add(2,0);
    public static final Vector2 COLON_LEFT_POSITION = new Vector2(83,-51);

    // TODO: 14/01/17 make positions relative so that changing the global scale doesn't affect the external joints.
    public static final Vector2 ABDOMINALWALL_POSITION = new Vector2(40,31);
    public static final Vector2 ESOPHAGUS_POSITION = new Vector2(46, 68);
    public static final Vector2 BILEDUCT_POSITION = new Vector2(38,47);
    public static final Vector2 GALLBLADDER_POSITION = new Vector2(38,47);
    public static final Vector2 PANCREAS_POSITION = new Vector2(38,47);
    public static final Vector2 DUODENUM_POSITION = new Vector2(40,41.3f);
    public static final Vector2 RECTUM_POSITION = COLON_LEFT_POSITION.cpy().add(-9,-6);
    public static final Vector2 STOMACH_POSITION = new Vector2(47, 59.2f);
    public static final Vector2 APPENDIX_POSITION = SMALLINTESTINE_RIGHT_POSITION.cpy().add(5,3);
    public static final Vector2 LIVER_POSITION = new Vector2(17,64);

    // Tumor scale
    public static final float TUMOR_SCALE = 5f;
    public static final float ABDOMINALWALL_TUMOR_SCALE = 0.05f*TUMOR_SCALE;
    public static final float ESOPHAGUS_TUMOR_SCALE = 0.2f*TUMOR_SCALE;
    public static final float BILEDUCT_TUMOR_SCALE = 0.2f*TUMOR_SCALE;
    public static final float GALLBLADDER_TUMOR_SCALE = 0.2f*TUMOR_SCALE;
    public static final float PANCREAS_TUMOR_SCALE = 0.2f*TUMOR_SCALE;
    public static final float DUODENUM_TUMOR_SCALE = 0.2f*TUMOR_SCALE;
    public static final float RECTUM_TUMOR_SCALE = 0.3f*TUMOR_SCALE;
    public static final float STOMACH_TUMOR_SCALE = 0.5f*TUMOR_SCALE;
    public static final float COLON_TUMOR_SCALE = 0.3f*TUMOR_SCALE;
    public static final float APPENDIX_TUMOR_SCALE = 0.15f*TUMOR_SCALE;
    public static final float SMALLINTESTINE_TUMOR_SCALE = 0.2f*TUMOR_SCALE;
    public static final float LIVER_TUMOR_SCALE = 0.5f*TUMOR_SCALE;



    // if you ever change it, think about the appendix-smallintestine external joint json file.
    public static final int SMALLINTESTINE_LENGTH = 160;

    // if you ever change it, think about the appendix-smallintestine external joint json file.
    public static final int COLON_LENGTH = 145;



    /*
    COLLISION FILTERING
     */

    //categories : one different category per organ :
    public static final int CATEGORY_ABDOMINALWALL = 1;
    public static final int CATEGORY_ESOPHAGUS = 2;
    public static final int CATEGORY_BILEDUCT = 4;
    public static final int CATEGORY_GALLBLADDER = 8;
    public static final int CATEGORY_PANCREAS = 16;
    public static final int CATEGORY_DUODENUM = 32;
    public static final int CATEGORY_RECTUM = 64;
    public static final int CATEGORY_STOMACH = 128;
    public static final int CATEGORY_COLON = 256;
    public static final int CATEGORY_APPENDIX = 512;
    public static final int CATEGORY_SMALLINTESTINE = 1024;
    public static final int CATEGORY_LIVER = 2048;
    public static final int CATEGORY_SPLEEN = 4096;

    //filtering mask (collides with...)
    public static final int MASK_ABDOMINALWALL = 0 ; //CATEGORY_ABDOMINALWALL | CATEGORY_ESOPHAGUS | CATEGORY_DUODENUM
            //| CATEGORY_RECTUM | CATEGORY_STOMACH | CATEGORY_COLON | CATEGORY_APPENDIX | CATEGORY_SMALLINTESTINE | CATEGORY_SPLEEN | CATEGORY_LIVER ;
    public static final int MASK_ESOPHAGUS = CATEGORY_ABDOMINALWALL | CATEGORY_ESOPHAGUS | CATEGORY_SPLEEN | CATEGORY_LIVER;
    public static final int MASK_BILEDUCT = CATEGORY_ABDOMINALWALL | CATEGORY_BILEDUCT | CATEGORY_GALLBLADDER | CATEGORY_SPLEEN | CATEGORY_LIVER;
    public static final int MASK_GALLBLADDER = CATEGORY_ABDOMINALWALL | CATEGORY_GALLBLADDER | CATEGORY_BILEDUCT | CATEGORY_SPLEEN | CATEGORY_LIVER;
    public static final int MASK_PANCREAS = CATEGORY_ABDOMINALWALL | CATEGORY_PANCREAS | CATEGORY_DUODENUM | CATEGORY_SPLEEN | CATEGORY_LIVER;
    public static final int MASK_DUODENUM = CATEGORY_ABDOMINALWALL | CATEGORY_DUODENUM | CATEGORY_SPLEEN | CATEGORY_LIVER;
    public static final int MASK_RECTUM = CATEGORY_ABDOMINALWALL | CATEGORY_RECTUM | CATEGORY_COLON | CATEGORY_SMALLINTESTINE | CATEGORY_SPLEEN | CATEGORY_LIVER;
    public static final int MASK_STOMACH = CATEGORY_ABDOMINALWALL | CATEGORY_STOMACH | CATEGORY_SPLEEN | CATEGORY_LIVER;
    public static final int MASK_COLON = CATEGORY_ABDOMINALWALL | CATEGORY_COLON | CATEGORY_APPENDIX | CATEGORY_RECTUM | CATEGORY_SPLEEN | CATEGORY_LIVER;
    public static final int MASK_APPENDIX = CATEGORY_ABDOMINALWALL | CATEGORY_COLON | CATEGORY_APPENDIX | CATEGORY_SMALLINTESTINE | CATEGORY_SPLEEN | CATEGORY_LIVER;
    public static final int MASK_SMALLINTESTINE = CATEGORY_ABDOMINALWALL | CATEGORY_APPENDIX | CATEGORY_SMALLINTESTINE | CATEGORY_RECTUM | CATEGORY_SPLEEN | CATEGORY_LIVER;
    public static final int MASK_LIVER = CATEGORY_ABDOMINALWALL | CATEGORY_ESOPHAGUS | CATEGORY_DUODENUM | CATEGORY_RECTUM | CATEGORY_STOMACH | CATEGORY_COLON
                                        | CATEGORY_APPENDIX | CATEGORY_SMALLINTESTINE | CATEGORY_SPLEEN | CATEGORY_LIVER;

}
