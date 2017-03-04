package com.suturesapp.workspace.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;
import com.suturesapp.workspace.organs.rope.RopeOrganPart;
import com.suturesapp.workspace.organs.duedenum.Duodenum;
import com.suturesapp.workspace.organs.stomach.Stomach;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by neogineer on 27/10/16.
 */
public class Utils {

    static Vector3 tmpVec3 = new Vector3();
    public static Vector3 cameraPosition(OrthographicCamera camera){
        return tmpVec3.set(camera.position.x-Constants.VIEWPORT_HEIGHT *Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight()*camera.zoom/2, camera.position.y-Constants.VIEWPORT_HEIGHT*camera.zoom/2,0);
    }

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
        for(int i=0; i<joints.length(); i++){
            if(joints.isNull(i))
                joints.remove(i);
        }
        return joints;
    }

    public static float scaleFromName(String name){
        switch (name){
            case "AbdominalWall":
                return com.suturesapp.workspace.organs.abdominalwall.AbdominalWall.SCALE;
            case "Liver":
                return com.suturesapp.workspace.organs.liver.Liver.SCALE;
            case "Stomach":
                return Stomach.SCALE;
            case "Rectum":
                return com.suturesapp.workspace.organs.rectum.Rectum.SCALE;
            case "Duodenum":
                return Duodenum.SCALE;
            case "Esophagus":
                return com.suturesapp.workspace.organs.esophagus.Esophagus.SCALE;
            case "SmallIntestine":
                return com.suturesapp.workspace.organs.rope.SmallIntestine.getSCALE();
            case "Appendix":
                return com.suturesapp.workspace.organs.appendix.Appendix.SCALE;
            case "Colon":
                return com.suturesapp.workspace.organs.rope.Colon.getSCALE();
            default:
                return 1;
        }
    }

    public static com.suturesapp.workspace.organs.SuturePointDef jointToSpDef(Joint j){
        com.suturesapp.workspace.organs.SuturePointDef spDef = new com.suturesapp.workspace.organs.SuturePointDef();
        com.suturesapp.workspace.organs.OrganPart orgA = ((com.suturesapp.workspace.organs.OrganPart)j.getBodyA().getUserData());
        com.suturesapp.workspace.organs.OrganPart orgB = ((com.suturesapp.workspace.organs.OrganPart)j.getBodyB().getUserData());

        spDef.setOrganA(orgA.getFullIdentifier());
        spDef.setOrganB(orgB.getFullIdentifier());



        spDef.setAnchorA(new Vector2(orgA.body.getLocalPoint(j.getAnchorA()).x,orgA.body.getLocalPoint(j.getAnchorA()).y)) ;
        spDef.setAnchorB(new Vector2(orgB.body.getLocalPoint(j.getAnchorB()).x,orgB.body.getLocalPoint(j.getAnchorB()).y));

        spDef.setAvisible(((com.suturesapp.workspace.organs.SuturePoint)j.getUserData()).isVisible());
        // TODO: 31/12/16 find a way to get the real value
        spDef.setBvisible(false);

        if(orgA instanceof RopeOrganPart)
            spDef.idA = ((RopeOrganPart)orgA).id;
        if(orgB instanceof RopeOrganPart)
            spDef.idB = ((RopeOrganPart)orgB).id;


        return spDef;
    }

    /**
     * @param joints Will be <b> modified </b> !
     */
    public static Array<Joint> sortJoints(final Array<Joint> joints){
        // TODO: 31/12/16 make this even better (smarter sorting)
        Array<Joint> sortedJoints = new Array<>();
        Joint[] intesJoints = new Joint[com.suturesapp.workspace.organs.rope.SmallIntestine.getLENGTH() +1];
        int count=0;
        for(Joint j : joints){
            if((j.getBodyB().getUserData()) instanceof com.suturesapp.workspace.organs.rope.SmallIntestineOrganPart){
                count++;
                intesJoints[( (com.suturesapp.workspace.organs.rope.SmallIntestineOrganPart) j.getBodyB().getUserData()).id] = j ;
                joints.removeValue(j, true);
            }
        }

        for(int i = 0; i< com.suturesapp.workspace.organs.rope.SmallIntestine.getLENGTH(); i++){
            if(intesJoints[i]==null)
                continue;
            sortedJoints.add(intesJoints[i]);
        }

        sortedJoints.addAll(joints);

        return sortedJoints;
    }
    /**
     * true if - At least one of the fixtures contains the specified point.
     */
    private static Vector2 tmpVec = new Vector2();
    public static boolean fixturesContains(Array<Fixture> fixtures, float x, float y){
        return fixturesContains(fixtures, tmpVec.set(x,y));
    }

    /**
     * true if - At least one of the fixtures contains the specified point.
     */
    public static boolean fixturesContains(Array<Fixture> fixtures, Vector2 vec){
        for(Fixture fix : fixtures){
            if(fix.testPoint(vec))
                return true;
        }
        return false;
    }

    public static int getRegion(Vector2 localVec){
        float regionWidth = com.suturesapp.workspace.organs.OrgansHolder.organFromName("SmallIntestine").organParts.get("0").getDimensions().x/2;
        if(localVec.x<regionWidth && localVec.x>-regionWidth){
            if(localVec.x>0){
                return (localVec.y>0)? 1:2;
            }else{
                return (localVec.y>0)? 3:4;
            }
        }else{
            if(localVec.x>regionWidth){
                return (localVec.y>0)? 5:6;
            }else{
                return (localVec.y<0)? 7:8;
            }
        }
    }

    public static boolean regionsMatch(int regionA, int regionB){
        return !(   //doesn't match (refuse suturing) when :
                    regionA==regionB || regionA+regionB==5
                );
    }

    public static void vectorZeroPrecision(Vector2 vec){
        if(Math.abs(vec.x)<0.0001f)
            vec.x = 0;
        if(Math.abs(vec.y)<0.0001f)
            vec.y = 0;
    }

    public static float zoomToFitInterval(float zoom){
        if(zoom>Constants.MAX_ZOOM)
            return Constants.MAX_ZOOM;
        if(zoom< Constants.MIN_ZOOM)
            return Constants.MIN_ZOOM;
        return zoom;
    }

    public static double fitClosestWellKnowAngle(float angle){
        float degree = (float) Math.toDegrees(angle);
        if(degree>-195 && degree<-165)
            return Math.toRadians(-180);
        if(degree>-105 && degree<-75)
            return Math.toRadians(-90);
        if(degree>-15 && degree<15)
            return Math.toRadians(0);
        if(degree>75 && degree<105)
            return Math.toRadians(90);
        if(degree>165 && degree<195)
            return Math.toRadians(180);
        if(degree>265 && degree<285)
            return Math.toRadians(270);
        if(degree>345 && degree<375)
            return Math.toRadians(360);

        return angle;
    }
}
