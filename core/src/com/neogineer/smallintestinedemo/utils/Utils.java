package com.neogineer.smallintestinedemo.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by neogineer on 27/10/16.
 */
public class Utils {

    /**
     * Multiply it by camera.zoom then use it to scale between meters and pixels.
     */
    public static Vector2 getPixelPerMeter(){
        final float x = (float) Gdx.graphics.getWidth() / Constants.VIEWPORT_WIDTH;
        final float y =  (float) Gdx.graphics.getHeight() / Constants.VIEWPORT_HEIGHT;
        return new Vector2(x,y);
    }

    public static float getDistance(final Vector2 vec1,final Vector2 vec2){
        float distanceX = vec1.x - vec2.x;
        float distanceY = vec1.y - vec2.y;
        float distance = (float) Math.sqrt(distanceX*distanceX + distanceY*distanceY);
        return distance;
    }

    /**
     * reads the json file (from given path) and returns the JSONArray inside with the key "joints".
     * @param path json file path.
     */
    public static JSONArray loadJoints(String path){
        String str = "";
        try {
            InputStream is = Gdx.files.internal(path).read();
            byte[] data = new byte[10000];
            is.read(data);
            str = new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject data = new JSONObject(str);
        JSONArray joints = data.getJSONArray("joints");
        return joints;
    }

}
