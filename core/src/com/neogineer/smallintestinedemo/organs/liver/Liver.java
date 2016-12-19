package com.neogineer.smallintestinedemo.organs.liver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.stomach.StomachOrganPart;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.utils.Constants;
import com.neogineer.smallintestinedemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by neogineer on 08/11/16.
 */
public class Liver extends Organ {

    public static final float SCALE = Constants.LIVER_SCALE;

    public static final Vector2 POSITION = Constants.LIVER_POSITION;

    private static final Vector2 STEP = new Vector2(2.5f*SCALE, 10*SCALE);

    public static final int SIZE = 8 ;


    public Liver(World world, OrthographicCamera camera) {
        super(world, camera);

        for(int i=1; i<=SIZE; i++){
            OrganPart part = new LiverOrganPart(world, camera, this, ""+i, SCALE, getOrganPartPosition(i), 0);
            organParts.put(part.getIdentifier(), part);
            addActor(part);
        }

        JSONArray joints = Utils.loadJoints("liver_initial_joints.json");

        for(int i=0; i<joints.length(); i++){
            JSONObject joint = (JSONObject) joints.get(i);
            ConnectTool tool = new ConnectTool(world, camera);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            LiverOrganPart organA = (LiverOrganPart) this.organParts.get(joint.getString("organA"));
            LiverOrganPart organB = (LiverOrganPart) this.organParts.get(joint.getString("organB"));

            connector.organA = organA;
            connector.organB = organB;

            connector.anchorA = organA.getVertex(joint.getDouble("anchorAx"), joint.getDouble("anchorAy"));
            connector.anchorB = organB.getVertex(joint.getDouble("anchorBx"), joint.getDouble("anchorBy"));

            connector.makeConnection(false);

        }

    }

    @Override
    public void loadState(Kryo kryo, Input input){
        this.free();

        for(int i=1; i<=SIZE; i++){
            OrganPart op = kryo.readObject(input, LiverOrganPart.class);
            Gdx.app.log("loadState","creating "+op+op.getIdentifier());
            organParts.put(op.getIdentifier(), op);
            addActor(op);
        }
    }

    @Override
    public float getScale() {
        return SCALE;
    }

    private static Vector2 getOrganPartPosition(int identifier){
        switch (identifier){
            case 1:
                return new Vector2(POSITION.x, POSITION.y );
            case 2:
                return new Vector2(POSITION.x,POSITION.y - STEP.y);
            case 3:
                return new Vector2(POSITION.x+STEP.x, POSITION.y);
            case 4:
                return new Vector2(POSITION.x+STEP.x, POSITION.y-STEP.y);
            case 5:
                return new Vector2(POSITION.x+2*STEP.x, POSITION.y);
            case 6:
                return new Vector2(POSITION.x+2*STEP.x, POSITION.y-STEP.y);
            case 7:
                return new Vector2(POSITION.x+3*STEP.x,POSITION.y-0.75f*STEP.y);
            case 8:
                return new Vector2(POSITION.x+4*STEP.x , POSITION.y-0.75f*STEP.y);
            default:
                return null;
        }
    }

}
